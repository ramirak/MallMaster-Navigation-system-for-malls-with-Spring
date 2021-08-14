package twins.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "Items")
public class ItemEntity {
	private String itemId;
	private String itemType;
	private String itemName;
	private boolean active;
	private Date createdTimestamp;
	@Email
	private String creatorEmail;
	private double lat;
	private double lng;
	private String itemAttributes;
	private Set<ItemEntity> parents;
	private Set<ItemEntity> children;

	public ItemEntity() {
		parents = new HashSet<>();
		children = new HashSet<>();
	}

	@Id
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String id) {
		this.itemId = id;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String type) {
		this.itemType = type;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String name) {
		this.itemName = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String email) {
		this.creatorEmail = email;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	@Lob
	public String getItemAttributes() {
		return itemAttributes;
	}

	public void setItemAttributes(String itemAttributes) {
		this.itemAttributes = itemAttributes;
	}

	@JsonBackReference
	@ManyToMany(mappedBy = "children", fetch = FetchType.LAZY)
	public Set<ItemEntity> getParents() {
		return parents;
	}

	public void setParents(Set<ItemEntity> parents) {
		this.parents = parents;
	}

	@JsonBackReference
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "item_entity_children", joinColumns = @JoinColumn(name = "parent_id"), inverseJoinColumns = @JoinColumn(name = "children_id"))
	public Set<ItemEntity> getChildren() {
		return children;
	}

	public void setChildren(Set<ItemEntity> children) {
		this.children = children;
	}

	public void addChild(ItemEntity child) {
		this.children.add(child);
		child.parents.add(this);
	}

	public void removeChild(ItemEntity child) {
		// TODO remove throws exception if not found?
		this.children.remove(child);
		child.parents.remove(this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemEntity other = (ItemEntity) obj;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		return true;
	}
}
