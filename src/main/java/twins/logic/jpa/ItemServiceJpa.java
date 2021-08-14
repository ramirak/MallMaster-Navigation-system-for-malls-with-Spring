package twins.logic.jpa;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import twins.boundaries.Id;
import twins.boundaries.ItemBoundary;
import twins.data.ItemEntity;
import twins.data.UserEntity;
import twins.data.UserRole;
import twins.data.dao.ItemsDao;
import twins.data.dao.UserDao;
import twins.logic.ExtendedItemsService;
import twins.logic.converters.EntityConverter;
import twins.logic.converters.ExtendedEntityConverter;
import twins.utils.ForbiddenException;
import twins.utils.NotFoundException;
import twins.utils.Utils;

@Service
public class ItemServiceJpa implements ExtendedItemsService {
	private ExtendedEntityConverter<ItemEntity, ItemBoundary> entityConverter;
	private String springApplicationName;
	private ItemsDao itemsDao;
	private UserDao userDao;
	private Utils utils;

	public ItemServiceJpa() {
	}

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setItemsDao(ItemsDao itemsDao) {
		this.itemsDao = itemsDao;
	}

	@Autowired
	public void setEntityConverter(ExtendedEntityConverter<ItemEntity, ItemBoundary> entityConverter) {
		this.entityConverter = entityConverter;
	}

	@Autowired
	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Value("${spring.application.name:defaultName}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}

	@Override
	@Transactional
	public ItemBoundary createItem(String userSpace, String userEmail, ItemBoundary item) {
//		if (!userSpace.equals(this.springApplicationName))
//			throw new RuntimeException();
		utils.assertNull(item);
		utils.assertNull(item.getType());
		utils.assertNull(item.getName());

		String userId = utils.combineID(userSpace, userEmail);
		UserEntity manager = userDao.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found: " + userId));
		utils.assertProperRole(manager.getRole(), UserRole.MANAGER.name());
		utils.assertItemType(item.getType());
			
		ItemEntity entity = this.entityConverter.fromBoundary(item);
		String id = utils.createUniqueID(springApplicationName);
		entity.setCreatorEmail(userEmail);
		entity.setItemId(id);
		entity.setCreatedTimestamp(new Date());
		this.itemsDao.save(entity);
		return this.entityConverter.toBoundary(entity);
	}

	@Override
	@Transactional
	public ItemBoundary updateItem(String userSpace, String userEmail, String itemSpace, String itemId,
			ItemBoundary update) {
		utils.assertNull(update);
		utils.assertNull(update.getType());
		utils.assertNull(update.getName());
		
		String userId = utils.combineID(userSpace, userEmail);
		UserEntity manager = userDao.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found: " + userId));
		utils.assertProperRole(manager.getRole(), UserRole.MANAGER.name());
		utils.assertItemType(update.getType());

//		if (!userSpace.equals(this.springApplicationName) || !itemSpace.equals(this.springApplicationName))
//			throw new RuntimeException();
		Optional<ItemEntity> existing = this.itemsDao.findById(itemId);
		if (existing.isPresent()) {
			ItemEntity entity = existing.get();
			if (update.getActive() != null) {
				entity.setActive(update.getActive());
			}
			if (update.getItemAttributes() != null) {
				entity.setItemAttributes(entityConverter.mapToJSON(update.getItemAttributes()));
			}
			if (update.getLocation() != null) {
				if (update.getLocation().getLat() != null) {
					entity.setLat(update.getLocation().getLat());
				}
				if (update.getLocation().getLng() != null) {
					entity.setLng(update.getLocation().getLng());
				}
			}
			if (update.getName() != null) {
				entity.setItemName(update.getName());
			}
			if (update.getType() != null) {
				entity.setItemType(update.getType());
			}
			entity = this.itemsDao.save(entity);
			return this.entityConverter.toBoundary(entity);
		} else {
			throw new NotFoundException("Could not find item by id " + itemId);
		}
	}

	@Override
	@Transactional(readOnly = true)
	@Deprecated
	public List<ItemBoundary> getAllItem(String userSpace, String userEmail) {
		throw new RuntimeException("Deprecated method");
//		if (!userSpace.equals(this.springApplicationName))
//			throw new RuntimeException();
//		Iterable<ItemEntity> allItemsEntities = this.itemsDao.findAll();
//		return StreamSupport.stream(allItemsEntities.spliterator(), false).map(this.entityConverter::toBoundary)
//				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemBoundary> getAllItem(String userSpace, String userEmail, int page, int size) {
		utils.assertNull(userEmail);
		utils.assertNull(userSpace);
		
		
		String userId = utils.combineID(userSpace, userEmail);
		UserEntity user = userDao.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found: " + userId));
		utils.assertProperRole(user.getRole(), UserRole.PLAYER.name(), UserRole.MANAGER.name());
		

		if (user.getRole() == UserRole.PLAYER.name()) {
			return this.itemsDao
					.findAllByActive(true, PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "itemId"))
					.stream().map(this.entityConverter::toBoundary).collect(Collectors.toList());
		}

		return this.itemsDao.findAll(PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "itemId"))
				.getContent().stream().map(this.entityConverter::toBoundary).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public ItemBoundary getSpecificItem(String userSpace, String userEmail, String itemSpace, String itemId) {

		utils.assertNull(userEmail);
		utils.assertNull(userSpace);
		utils.assertNull(itemSpace);
		utils.assertNull(itemId);

		String userId = utils.combineID(userSpace, userEmail);
		UserEntity user = userDao.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found: " + userId));
		utils.assertProperRole(user.getRole(), UserRole.PLAYER.name(), UserRole.MANAGER.name());

		Optional<ItemEntity> optional = this.itemsDao.findById(itemId);
		if (!optional.isPresent())
			throw new NotFoundException("Could not find item by id " + itemId);

		ItemEntity item = optional.get();
		if (user.getRole().equals(UserRole.PLAYER.name())) {
			if (item.isActive()) {
				return this.entityConverter.toBoundary(item);
			} else {
				throw new ForbiddenException("Players cannot request inactive items with itemId " + itemId);
			}
		}

		return this.entityConverter.toBoundary(optional.get());
	}

	@Override
	@Transactional
	public void deleteAllItems(String adminSpace, String adminEmail) {
		utils.assertNull(adminSpace);
		utils.assertNull(adminEmail);
		utils.assertValidEmail(adminEmail);

		String userId = utils.combineID(adminSpace, adminEmail);
		UserEntity admin = userDao.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found: " + userId));
		utils.assertProperRole(admin.getRole(), UserRole.ADMIN.name());

		this.itemsDao.deleteAll();
	}

	@Override
	@Transactional
	public void bindItem(String parentId, Id childIdBoundary, String userSpace, String email, String itemSpace) {
		utils.assertNull(childIdBoundary);
		utils.assertNull(childIdBoundary.getId());
		utils.assertNull(childIdBoundary.getSpace());
		String userId = utils.combineID(userSpace, email);
		UserEntity user = this.userDao.findById(userId).orElseThrow(
				() -> new NotFoundException("User not found: " + userId));
		utils.assertProperRole(user.getRole(), UserRole.MANAGER.name());

		String parentKey = utils.combineID(userSpace, parentId);
		String childKey = utils.combineID(childIdBoundary.getSpace(), childIdBoundary.getId());

		ItemEntity parentItem = this.itemsDao.findById(parentKey)
				.orElseThrow(() -> new NotFoundException("could not find parent:" + parentKey));

		ItemEntity childItem = this.itemsDao.findById(childKey)
				.orElseThrow(() -> new NotFoundException("could not find child:" + childKey));
		parentItem.addChild(childItem);
		this.itemsDao.save(childItem);
		this.itemsDao.save(parentItem);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemBoundary> getChildren(String userSpace, String userEmail, String itemSpace, String itemId, int page,
			int size) {

		utils.assertNull(userEmail);
		utils.assertNull(userSpace);
		utils.assertNull(itemSpace);
		utils.assertNull(itemId);

		String combinedItemID = utils.combineID(itemSpace, itemId);
		String userId = utils.combineID(userSpace, userEmail);
		UserEntity user = userDao.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found: " + userId));
		utils.assertProperRole(user.getRole(), UserRole.PLAYER.name(), UserRole.MANAGER.name());

		if (user.getRole().equals(UserRole.PLAYER.name())) {
			return this.itemsDao
					.findAllByActiveAndParents_itemId(true, combinedItemID, PageRequest.of(page, size, Direction.DESC,
							"createdTimestamp", "itemId"))
					.stream().map(this.entityConverter::toBoundary).collect(Collectors.toList());
		}

		return this.itemsDao
				.findAllByParents_itemId(combinedItemID,
						PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "itemId"))
				.stream().map(this.entityConverter::toBoundary).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<ItemBoundary> getParents(String userSpace, String userEmail, String itemSpace, String itemId, int page,
			int size) {
		
		utils.assertNull(userEmail);
		utils.assertNull(userSpace);
		utils.assertNull(itemSpace);
		utils.assertNull(itemId);

		String combinedItemID = utils.combineID(itemSpace, itemId);
		String userId = utils.combineID(userSpace, userEmail);
		UserEntity user = userDao.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found: " + userId));
		utils.assertProperRole(user.getRole(), UserRole.PLAYER.name(), UserRole.MANAGER.name()); 

		if (user.getRole().equals(UserRole.PLAYER.name())) {
			return this.itemsDao
					.findAllByActiveAndChildren_itemId(true,combinedItemID,
							PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "itemId"))
					.stream().map(this.entityConverter::toBoundary).collect(Collectors.toList());
		}
		
		
		return this.itemsDao
				.findAllByChildren_itemId(itemId,
						PageRequest.of(page, size, Direction.DESC, "createdTimestamp", "itemId"))
				.stream().map(this.entityConverter::toBoundary).collect(Collectors.toList());


	}

	public EntityConverter<ItemEntity, ItemBoundary> getEntityConverter() {
		return entityConverter;
	}

	public String getSpringApplicationName() {
		return springApplicationName;
	}

}
