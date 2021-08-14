package twins.logic.converters;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import twins.boundaries.Creator;
import twins.boundaries.Id;
import twins.boundaries.ItemBoundary;
import twins.boundaries.Location;
import twins.boundaries.UserId;
import twins.data.ItemEntity;
import twins.utils.Utils;

@Component
public class ItemsEntityConverterImplementation implements ExtendedEntityConverter<ItemEntity, ItemBoundary> {
	private ObjectMapper jackson;
	private Utils utils;
	
		
	public ItemsEntityConverterImplementation() {
		this.jackson = new ObjectMapper();
	}

		@Autowired
	public void setUtils(Utils utils) {
		this.utils = utils;
	}
	
	@Override
	public ItemBoundary toBoundary(ItemEntity entity) {
		ItemBoundary ib = new ItemBoundary();
		ib.setActive(entity.isActive());
		String space = utils.extractSpace(entity.getItemId());
		ib.setCreatedBy(new Creator(new UserId(space, entity.getCreatorEmail())));
		ib.setCreatedTimestamp(entity.getCreatedTimestamp());
		ib.setItemAttributes(JSONToMap(entity.getItemAttributes()));
		ib.setItemId(new Id(space, utils.extractId(entity.getItemId())));
		Location location = new Location();
		location.setLat(entity.getLat());
		location.setLng(entity.getLng());
		ib.setLocation(location);
		ib.setName(entity.getItemName());
		ib.setType(entity.getItemType());
		return ib;
	}

	@Override
	public ItemEntity fromBoundary(ItemBoundary boundary) {
		ItemEntity itemEntity = new ItemEntity();
		if(boundary.getActive() != null) {
			itemEntity.setActive(boundary.getActive());
		}
		itemEntity.setCreatedTimestamp(boundary.getCreatedTimestamp());
		itemEntity.setItemAttributes(mapToJSON(boundary.getItemAttributes()));
		if(boundary.getLocation() != null) {
			itemEntity.setLat(boundary.getLocation().getLat());
			itemEntity.setLng(boundary.getLocation().getLng());
		}
		itemEntity.setItemName(boundary.getName());
		itemEntity.setItemType(boundary.getType());
		return itemEntity;
	}

	@Override
	public String mapToJSON (Map<String, Object> value) {
		try {
			return this.jackson.writeValueAsString(value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Map<String, Object> JSONToMap (String json){
		try {
			return this.jackson.readValue(json, Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
