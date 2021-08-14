package twins.boundaries;

import java.util.Date;
import java.util.Map;

public class ItemBoundary {
	private Id itemId;
	private String type;
	private String name;
	private Boolean active;
	private Date createdTimestamp;
	private Creator createdBy;
	private Location location;
	private Map<String, Object> itemAttributes;
	
	public ItemBoundary() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Creator getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Creator createdBy) {
		this.createdBy = createdBy;
	}
	public Id getItemId() {
		return itemId;
	}
	public void setItemId(Id itemId) {
		this.itemId = itemId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}
	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Map<String, Object> getItemAttributes() {
		return itemAttributes;
	}
	public void setItemAttributes(Map<String, Object> itemAttributes) {
		this.itemAttributes = itemAttributes;
	}
}
