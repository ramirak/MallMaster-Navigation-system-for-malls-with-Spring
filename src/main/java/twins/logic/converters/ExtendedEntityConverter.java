package twins.logic.converters;

import java.util.Map;

public interface ExtendedEntityConverter<E, B> extends EntityConverter<E, B> {
	
	public String mapToJSON (Map<String, Object> value);
	
	public Map<String, Object> JSONToMap (String json); 

}
