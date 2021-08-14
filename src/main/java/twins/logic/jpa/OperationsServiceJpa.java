package twins.logic.jpa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import twins.boundaries.OperationBoundary;
import twins.data.FilterType;
import twins.data.ItemEntity;
import twins.data.ItemType;
import twins.data.Node;
import twins.data.OperationEntity;
import twins.data.OperationType;
import twins.data.UserEntity;
import twins.data.UserRole;
import twins.data.dao.ItemsDao;
import twins.data.dao.OperationsDao;
import twins.data.dao.UserDao;
import twins.logic.ExtendedOperationsService;
import twins.logic.converters.ItemsEntityConverterImplementation;
import twins.logic.converters.OpertionsEntityConverterImplementation;
import twins.utils.AttributeNames;
import twins.utils.ForbiddenException;
import twins.utils.NotFoundException;
import twins.utils.Utils;

@Service
public class OperationsServiceJpa implements ExtendedOperationsService {
	private OpertionsEntityConverterImplementation entityConverter;
	private ItemsEntityConverterImplementation itemEntityconverter;
	private String springApplicationName;
	private ItemsDao itemsDao;
	private OperationsDao operationsDao;
	private UserDao userDao;
	private Utils utils;
	private JmsTemplate jmsTemplate;
	private ObjectMapper jackson;

	public OperationsServiceJpa() {
		this.jackson = new ObjectMapper();
	}

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setItemDao(ItemsDao itemsDao) {
		this.itemsDao = itemsDao;
	}

	@Autowired
	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Autowired
	public void setOperationsDao(OperationsDao operationsDao) {
		this.operationsDao = operationsDao;
	}

	@Autowired
	public void setEntityConverter(OpertionsEntityConverterImplementation entityConverter) {
		this.entityConverter = entityConverter;
	}

	@Autowired
	public void setItemEntityconverter(ItemsEntityConverterImplementation itemEntityconverter) {
		this.itemEntityconverter = itemEntityconverter;
	}

	@Autowired
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	@Value("${spring.application.name:defaultName}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}

	@Override
	@Transactional
	public Object invokeOperation(OperationBoundary operation) {
		utils.assertNull(operation);
		utils.assertNull(operation.getType());
		utils.assertNull(operation.getInvokedBy());
		utils.assertNull(operation.getInvokedBy().getUserId());
		utils.assertNull(operation.getInvokedBy().getUserId().getEmail());
		utils.assertNull(operation.getInvokedBy().getUserId().getSpace());
		utils.assertNull(operation.getItem());
		utils.assertNull(operation.getItem().getItemId());
		utils.assertNull(operation.getItem().getItemId().getId());
		utils.assertNull(operation.getItem().getItemId().getSpace());

		String userId = utils.combineID(operation.getInvokedBy().getUserId().getSpace(),
				operation.getInvokedBy().getUserId().getEmail());
		UserEntity user = userDao.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found: " + userId));

		if (operation.getType().equals(OperationType.GET_REPORTS.name())) {
			utils.assertProperRole(user.getRole(), UserRole.ADMIN.name());
			user.setRole(UserRole.PLAYER.name());
		}
		
		utils.assertProperRole(user.getRole(), UserRole.PLAYER.name());

		String itemId = utils.combineID(operation.getItem().getItemId().getSpace(),
				operation.getItem().getItemId().getId());

		Optional<ItemEntity> optional = this.itemsDao.findById(itemId);
		if (!optional.isPresent())
			throw new NotFoundException("Could not find item by id " + itemId);

		ItemEntity item = optional.get();
		if (!item.isActive()) {
			throw new ForbiddenException("Players cannot request inactive items with itemId " + itemId);
		}

		OperationEntity oe = this.entityConverter.fromBoundary(operation);
		String newId = utils.createUniqueID(springApplicationName);
		oe.setOperationId(newId);
		oe.setCreatedTimestamp(new Date());
		Object returnValue = handleOperations(oe);
		
		if (operation.getType().equals(OperationType.GET_REPORTS.name())) {
			user.setRole(UserRole.ADMIN.name());
		}
		
		this.operationsDao.save(oe);
		return returnValue;
	}

	@Override
	@Transactional
	public OperationBoundary invokeAsynchronousOperation(OperationBoundary operation) {
		utils.assertNull(operation);
		utils.assertNull(operation.getType());
		utils.assertNull(operation.getInvokedBy());
		utils.assertNull(operation.getInvokedBy().getUserId());
		utils.assertNull(operation.getInvokedBy().getUserId().getEmail());
		utils.assertNull(operation.getInvokedBy().getUserId().getSpace());
		utils.assertNull(operation.getItem());
		utils.assertNull(operation.getItem().getItemId());
		utils.assertNull(operation.getItem().getItemId().getId());
		utils.assertNull(operation.getItem().getItemId().getSpace());

		String userId = utils.combineID(operation.getInvokedBy().getUserId().getSpace(),
				operation.getInvokedBy().getUserId().getEmail());
		UserEntity user = userDao.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found: " + userId));
		utils.assertProperRole(user.getRole(), UserRole.PLAYER.name());

		String itemId = utils.combineID(operation.getItem().getItemId().getSpace(),
				operation.getItem().getItemId().getId());

		Optional<ItemEntity> optional = this.itemsDao.findById(itemId);
		if (!optional.isPresent())
			throw new NotFoundException("Could not find item by id " + itemId);

		ItemEntity item = optional.get();
		if (!item.isActive()) {
			throw new ForbiddenException("Players cannot request inactive items with itemId " + itemId);
		}

		OperationEntity oe = this.entityConverter.fromBoundary(operation);
		String newId = utils.createUniqueID(springApplicationName);
		oe.setOperationId(newId);
		oe.setCreatedTimestamp(new Date());
		this.operationsDao.save(oe);
		return HandleAsyncOperations(oe);
		// return this.entityConverter.toBoundary(oe);
	}

	@Override
	@Transactional(readOnly = true)
	public List<OperationBoundary> getAllOperations(String adminEmail, String adminSpace) {
		throw new RuntimeException("Deprecated method");
	}

	@Override
	@Transactional
	public void deleteAllOperations(String adminEmail, String adminSpace) {
		utils.assertNull(adminSpace);
		utils.assertNull(adminEmail);
		utils.assertValidEmail(adminEmail);

		String userId = utils.combineID(adminSpace, adminEmail);
		UserEntity admin = userDao.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found: " + userId));
		utils.assertProperRole(admin.getRole(), UserRole.ADMIN.name());

		this.operationsDao.deleteAll();
	}

	@Override
	public List<OperationBoundary> getAllOperations(String adminEmail, String adminSpace, int page, int size) {
		utils.assertNull(adminSpace);
		utils.assertNull(adminEmail);
		utils.assertValidEmail(adminEmail);

		String userId = utils.combineID(adminSpace, adminEmail);
		UserEntity admin = userDao.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found: " + userId));
		utils.assertProperRole(admin.getRole(), UserRole.ADMIN.name());

		return this.operationsDao.findAll(PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "operationId"))
				.stream().map(this.entityConverter::toBoundary).collect(Collectors.toList());
	}

	@Transactional
	private OperationBoundary HandleAsyncOperations(OperationEntity operation) {
		if (operation.getOperationType().equals(OperationType.ADD_REPORT.name())) {
			try {
				String opJson = this.jackson.writeValueAsString(operation);
				this.jmsTemplate // API for MOM (ActiveMQ)
						.send("ReportInbox", session -> session.createTextMessage(opJson));
				// return response to client without waiting for processing to end by MOM
				return this.entityConverter.toBoundary(operation);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	private Object handleOperations(OperationEntity operation) {
		Map<String, Object> attributes = entityConverter.JSONToMap(operation.getOperationAttributes());

		if (operation.getOperationType().equals(OperationType.GET_DIRECTIONS.name())) {
			String currentPointId = utils.combineID(operation.getCreatorSpace(), operation.getItemId());
			String endPointId = utils.combineID(operation.getCreatorSpace(),
					(String) attributes.get(AttributeNames.OTHER_ITEM_ID));
			ItemEntity startPoint = itemsDao.findById(currentPointId)
					.orElseThrow(() -> new NotFoundException("Starting point not found: " + currentPointId));

			ItemEntity endPoint = itemsDao.findById(endPointId)
					.orElseThrow(() -> new NotFoundException("Ending point not found: " + endPointId));
			return aStar(new Node(startPoint, null, null), new Node(endPoint, null, null)).stream()
					.map(itemEntityconverter::toBoundary).collect(Collectors.toList());

		} else if (operation.getOperationType().equals(OperationType.FILTER.name())) {

			List<ItemEntity> items = itemsDao.findAllByActiveAndItemType(true, ItemType.STORE.name(),
					PageRequest.of((int) attributes.get(AttributeNames.PAGE), (int) attributes.get(AttributeNames.SIZE),
							Direction.DESC, "itemName", "itemId"));
			return filterItems(items, attributes.get(AttributeNames.FILTER),
					(String) attributes.get(AttributeNames.FILTER_BY));

		} else if (operation.getOperationType().equals(OperationType.ADD_TO_FAVORITE.name())) {
			String itemId = utils.combineID(operation.getCreatorSpace(), operation.getItemId());
			ItemEntity item = itemsDao.findById(itemId)
					.orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
			utils.assertProperRole(item.getItemType(), ItemType.STORE.name());

			String userId = utils.combineID(operation.getCreatorSpace(), operation.getCreatorEmail());
			UserEntity user = userDao.findById(userId)
					.orElseThrow(() -> new NotFoundException("User not found: " + userId));

			if (!RemoveIfFav(userId, item)) {
				ItemEntity itemAsUser;
				Map<String, Object> itemAttr;
				Optional<ItemEntity> userItemOptional = itemsDao.findById(userId);
				if (userItemOptional.isPresent()) {
					itemAsUser = userItemOptional.get();
					itemAttr = entityConverter.JSONToMap(itemAsUser.getItemAttributes());
				} else {
					itemAsUser = new ItemEntity();
					itemAttr = new HashMap<>();
				}
				itemAttr.put(AttributeNames.USER_ROLE, user.getRole());
				itemAttr.put(AttributeNames.USER_AVATAR, user.getAvatar());
				String itemAttrStr = entityConverter.mapToJSON(itemAttr);
				itemAsUser.setItemAttributes(itemAttrStr);
				itemAsUser.setItemId(userId);
				itemAsUser.setItemName(user.getUsername());
				itemAsUser.setItemType(ItemType.PERSON.name());
				itemAsUser.setCreatorEmail(operation.getCreatorEmail());
				itemAsUser.setCreatedTimestamp(new Date());
				itemAsUser.setActive(true);
				itemAsUser.addChild(item);
				itemsDao.save(itemAsUser);
				itemsDao.save(item);
			}
			return item;

		} else if (operation.getOperationType().equals(OperationType.GET_FAVORITES.name())) {
			String userId = utils.combineID(operation.getCreatorSpace(), operation.getCreatorEmail());
			userDao.findById(userId).orElseThrow(() -> new NotFoundException("User not found: " + userId));
			Optional<ItemEntity> opt = itemsDao.findById(userId);
			if (opt.isPresent()) {
				return itemsDao.findAllByParents_itemId(userId,
						PageRequest.of((int) attributes.get(AttributeNames.PAGE),
								(int) attributes.get(AttributeNames.SIZE), Direction.DESC, "itemName", "itemId"));
			}
			return null;
		} else if (operation.getOperationType().equals(OperationType.GET_REPORTS.name())) {
			return itemsDao.findAllByActiveAndItemType(true, ItemType.REPORT.name(),
					PageRequest.of((int) attributes.get(AttributeNames.PAGE), (int) attributes.get(AttributeNames.SIZE),
							Direction.DESC, "itemName", "itemId"));
		}
		return null;
	}

	private boolean RemoveIfFav(String userId, ItemEntity item) {
		Optional<ItemEntity> userAsItemOpt = itemsDao.findById(userId);
		if (userAsItemOpt.isPresent()) {
			ItemEntity userItem = userAsItemOpt.get();
			if (userItem.getChildren().contains(item)) {
				userItem.removeChild(item);
				itemsDao.save(userItem);
				itemsDao.save(item);
				return true;
			}
		}
		return false;
	}

	private List<ItemEntity> filterItems(List<ItemEntity> items, Object filter, String filterType) {

		List<ItemEntity> filteredItems = new ArrayList<>();
		FilterType type = FilterType.valueOf(filterType);

		switch (type) {
		case STORE_NAME:
			for (ItemEntity item : items) {
				String storeName = (String) filter.toString().toLowerCase();
				if (item.getItemName().toLowerCase().contains(storeName)) {
					filteredItems.add(item);
				}
			}
			break;
		case STORE_TYPE:
			for (ItemEntity item : items) {
				Map<String, Object> attributes = entityConverter.JSONToMap(item.getItemAttributes());
				String wantedFilterType = (String) filter.toString().toLowerCase();
				if (attributes.get(AttributeNames.STORE_TYPE).toString().toLowerCase().equals(wantedFilterType)) {
					filteredItems.add(item);
				}
			}
			break;
		case FLOOR:
			for (ItemEntity item : items) {
				Map<String, Object> attributes = entityConverter.JSONToMap(item.getItemAttributes());
				if (attributes.get(AttributeNames.FLOOR).equals((int) filter)) {
					filteredItems.add(item);
				}
			}
			break;
		}

		return filteredItems;
	}

	private List<ItemEntity> aStar(Node start, Node target) {
		PriorityQueue<Node> closedList = new PriorityQueue<>();
		PriorityQueue<Node> openList = new PriorityQueue<>();
		start.setDistance(start.calculateDistance(start, target));
		openList.add(start);

		while (!openList.isEmpty()) {
			Node n = openList.peek();
			n.setDistance(n.calculateDistance(n, target));

			if (n.equals(target)) {
				return restorePath(n);
			}
			for (Node m : n.getNeighbors()) {
				if (!openList.contains(m) && !closedList.contains(m)) {
					m.setParent(n);
					openList.add(m);
				}
			}
			openList.remove(n);
			closedList.add(n);
		}
		throw new NotFoundException("Path from " + start.getItem().getItemName() + " to "
				+ target.getItem().getItemName() + " does not exist.");
	}

	private List<ItemEntity> restorePath(Node n) {
		List<ItemEntity> path = new ArrayList<ItemEntity>();
		while (n.getParent() != null) {
			System.err.println("Adding to path" + n.getItem().getItemId());
			path.add(n.getItem());
			n = n.getParent();
		}
		System.err.println("Adding to path" + n.getItem().getItemId());
		path.add(n.getItem());
		Collections.reverse(path);
		System.err.println("Path length is " + path.size());
		return path;
	}
}
