package twins.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Node implements Comparable<Node> {
	private ItemEntity item;
	private Node parent;
	private List<Node> neighbors;
	private double distance;
	private Set<String> knownItems;
	
	private final String F1 = "FLOOR\":0";
	private final String F2 = "FLOOR\":1";

	public Node(ItemEntity item, Node creatorNode, Set<String> knownItems) {

		this.item = item;
		this.neighbors = new ArrayList<>();
		
		if(knownItems != null) {
			this.knownItems = knownItems;
		} else {
			this.knownItems = new HashSet<>();
		}
		
		if(item.getChildren().size() != 0 || item.getParents().size() != 0) {
			Stream.of(item.getChildren(), item.getParents()) //Stream<ItemEntity> parents and children
			.flatMap(x -> x.stream()) //Single stream
			.filter(neighbor-> ( (neighbor.getItemType().equals(ItemType.STORE.name())
								|| neighbor.getItemType().equals(ItemType.POINT.name())) && 
					(creatorNode == null
					|| !neighbor.equals(creatorNode.getItem()))
					&& !this.knownItems.contains(neighbor.getItemId()))
					) //Filter out invalid items
			.forEach(neighbor -> {
				this.knownItems.add(neighbor.getItemId());
				Node nodeToAdd = new Node(neighbor, this, this.knownItems);
				neighbors.add(nodeToAdd);}); //create new node for each neighbor and add it to my neighbors
		}
		this.parent = null;
	}

	public ItemEntity getItem() {
		return item;
	}

	public void setItem(ItemEntity item) {
		this.item = item;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public List<Node> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<Node> neighbors) {
		this.neighbors = neighbors;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public int compareTo(Node n) {
		return Double.compare(this.distance, n.distance);
	}

	public double calculateHeuristic(Node target) {
		return calculateDistance(this, target);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((item == null) ? 0 : item.hashCode());
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
		Node other = (Node) obj;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		return true;
	}

	/**
	 * Calculate distance between two points in latitude and longitude
	 * 
	 * @param startPoint Contains latitude/longitude of point A
	 * @param endPoint   Contains latitude/longitude of point B
	 * @return Distance in Meters
	 */
	public double calculateDistance(Node startPoint, Node endPoint) {
		if(startPoint.getItem().getItemAttributes().contains(F1) && endPoint.getItem().getItemAttributes().contains(F2)
				|| startPoint.getItem().getItemAttributes().contains(F2) && endPoint.getItem().getItemAttributes().contains(F1)) {
			return 0; // If there's a connection between floors - It's considered instant
		}
		double lat1 = startPoint.getItem().getLat(), lat2 = endPoint.getItem().getLat();
		double lng1 = startPoint.getItem().getLng(), lng2 = endPoint.getItem().getLng();
		final int R = 6371; // Radius of the earth

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lng2 - lng1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		distance = Math.pow(distance, 2);
		distance = Math.sqrt(distance);
		
		return distance;
	}
}