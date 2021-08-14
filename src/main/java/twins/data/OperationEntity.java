package twins.data;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;

@Entity
@Table(name = "Operations")
public class OperationEntity {
	private String operationType;
	private Date createdTimestamp;
	private String creatorSpace;
	@Email
	private String creatorEmail;
	private String itemId;
	
	private String operationId;
	
	private String operationAttributes;

	public OperationEntity() {
	}

	public String getOperationType() {
		return operationType;
	}

	public void setOperationType(String type) {
		this.operationType = type;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}
	
	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	
	@Lob
	public String getOperationAttributes() {
		return operationAttributes;
	}
	
	public void setOperationAttributes(String operationAttributes) {
		this.operationAttributes = operationAttributes;
	}

	public String getCreatorSpace() {
		return creatorSpace;
	}

	public void setCreatorSpace(String creatorSpace) {
		this.creatorSpace = creatorSpace;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String email) {
		this.creatorEmail = email;
	}
	
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	@Id
	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(String opertionId) {
		this.operationId = opertionId;
	}

}
