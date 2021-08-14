package twins.logic.mockups;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import twins.boundaries.ItemBoundary;
import twins.data.ItemEntity;
import twins.logic.ItemsService;
import twins.logic.converters.ExtendedEntityConverter;
import twins.utils.Utils;

//@Service
public class ItemsServiceMockup implements ItemsService {
	private Map<String, ItemEntity> items;
	private ExtendedEntityConverter<ItemEntity, ItemBoundary> entityConverter;
	private String springApplicationName;
	private Utils utils;

	public ItemsServiceMockup() {
		this.setItems(Collections.synchronizedMap(new HashMap<>()));
	}

	@Autowired
	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Autowired
	public void setEntityConverter(ExtendedEntityConverter<ItemEntity, ItemBoundary> entityConverter) {
		this.entityConverter = entityConverter;
	}

	@Value("${spring.application.name:defaultName}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}

	@Override
	public ItemBoundary createItem(String userSpace, String userEmail, ItemBoundary item) {
		utils.assertNull(item);
		ItemEntity entity = this.entityConverter.fromBoundary(item);
		String id = utils.createUniqueID(springApplicationName);
		entity.setCreatorEmail(userEmail);
		entity.setItemId(id);
		entity.setCreatedTimestamp(new Date());
		items.put(id, entity);
		return this.entityConverter.toBoundary(entity);
	}

	@Override
	public ItemBoundary updateItem(String userSpace, String userEmail, String itemSpace, String itemId,
			ItemBoundary update) {

		utils.assertNull(update);

		ItemEntity existing = this.items.get(itemId);
		if (existing != null) {
			boolean dirty = false;

			if (update.getActive() != null) {
				existing.setActive(update.getActive());
				dirty = true;
			}
			if (update.getItemAttributes() != null) {
				existing.setItemAttributes(entityConverter.mapToJSON(update.getItemAttributes()));
				dirty = true;
			}
			if (update.getLocation() != null) {
				if (update.getLocation().getLat() != null) {
					existing.setLat(update.getLocation().getLat());
					dirty = true;
				}
				if (update.getLocation().getLng() != null) {
					existing.setLng(update.getLocation().getLng());
					dirty = true;
				}
			}
			if (update.getName() != null) {
				existing.setItemName(update.getName());
				dirty = true;

			}
			if (update.getType() != null) {
				existing.setItemType(update.getType());
				dirty = true;
			}
			if (dirty) {
				this.items.put(itemId, existing);

			}
			ItemBoundary rv = this.entityConverter.toBoundary(existing);
			return rv;
		} else {
			throw new RuntimeException("Could not find item by id " + itemId); // NullPointerExcption
		}
	}

	@Override
	public List<ItemBoundary> getAllItem(String userSpace, String userEmail) {
		return this.items.values().stream().map(this.entityConverter::toBoundary).collect(Collectors.toList());
	}

	@Override
	public ItemBoundary getSpecificItem(String userSpace, String userEmail, String itemSpace, String itemId) {

		ItemEntity entity = this.items.get(itemId);
		if (entity == null)
			throw new RuntimeException();
		return this.entityConverter.toBoundary(entity);
	}

	@Override
	public void deleteAllItems(String adminSpace, String adminEmail) {
		utils.assertNull(adminSpace);
		utils.assertNull(adminEmail);
		items.clear();
	}

	public Map<String, ItemEntity> getItems() {
		return items;
	}

	public void setItems(Map<String, ItemEntity> items) {
		this.items = items;
	}
}
