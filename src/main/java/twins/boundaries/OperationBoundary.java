package twins.boundaries;

import java.util.Date;
import java.util.Map;

public class OperationBoundary {
	private Id operationId;
	private String type;
	private Item item;
	private Date createdTimestamp;
	private Creator invokedBy;
	private Map<String, Object> operationAttributes;
	public OperationBoundary() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Id getOperationId() {
		return operationId;
	}
	public void setOperationId(Id operationId) {
		this.operationId = operationId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}
	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	public Creator getInvokedBy() {
		return invokedBy;
	}
	public void setInvokedBy(Creator invokedBy) {
		this.invokedBy = invokedBy;
	}
	public Map<String, Object> getOperationAttributes() {
		return operationAttributes;
	}
	public void setOperationAttributes(Map<String, Object> operationAttributes) {
		this.operationAttributes = operationAttributes;
	}
	@Override
	public String toString() {
		return "OperationBoundary [operationId=" + operationId + ", type=" + type + ", item=" + item
				+ ", createdTimestamp=" + createdTimestamp + ", invokedBy=" + invokedBy + ", operationAttributes="
				+ operationAttributes + "]";
	}
}
