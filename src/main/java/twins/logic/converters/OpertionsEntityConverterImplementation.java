package twins.logic.converters;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import twins.boundaries.Creator;
import twins.boundaries.Id;
import twins.boundaries.Item;
import twins.boundaries.OperationBoundary;
import twins.boundaries.UserId;
import twins.data.OperationEntity;
import twins.utils.Utils;

@Component
public class OpertionsEntityConverterImplementation implements ExtendedEntityConverter<OperationEntity, OperationBoundary> {
	private ObjectMapper jackson;	
	private Utils utils;
	
	public OpertionsEntityConverterImplementation() {
		this.jackson = new ObjectMapper();
	}
	
		@Autowired
	public void setUtils(Utils utils) {
		this.utils = utils;
	}
	
	@Override
	public OperationBoundary toBoundary(OperationEntity entity) {
		OperationBoundary ob = new OperationBoundary();
		ob.setCreatedTimestamp(entity.getCreatedTimestamp());
		ob.setInvokedBy(new Creator(new UserId(entity.getCreatorSpace(), entity.getCreatorEmail())));
		String space = utils.extractSpace(entity.getOperationId());
		ob.setItem(new Item(new Id(space, entity.getItemId())));
		ob.setOperationAttributes(this.JSONToMap(entity.getOperationAttributes()));
		ob.setType(entity.getOperationType());
		ob.setOperationId(new Id(space, entity.getOperationId()));
		return ob;
	}

	@Override
	public OperationEntity fromBoundary(OperationBoundary boundary) {
		OperationEntity oe = new OperationEntity();
		oe.setCreatedTimestamp(boundary.getCreatedTimestamp());
		if(boundary.getInvokedBy() != null) {
			if(boundary.getInvokedBy().getUserId() != null) {
				oe.setCreatorSpace(boundary.getInvokedBy().getUserId().getSpace());
				oe.setCreatorEmail(boundary.getInvokedBy().getUserId().getEmail());
			}
		}
		
		if(boundary.getItem() != null) {
			if(boundary.getItem().getItemId() != null ) {
				oe.setItemId(boundary.getItem().getItemId().getId());
			}
		}
		oe.setOperationAttributes(this.mapToJSON(boundary.getOperationAttributes()));
		oe.setOperationType(boundary.getType());
		return oe;
	}
	
	@Override
	public String mapToJSON(Map<String, Object> value) {
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
