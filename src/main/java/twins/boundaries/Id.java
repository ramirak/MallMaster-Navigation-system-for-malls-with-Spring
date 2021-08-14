package twins.boundaries;

public class Id {
	private String space;
	private String id;
	
	public Id() {
		super();
	}
	
	public Id(String space, String id) {
		super();
		this.space = space;
		this.id = id;
	}

	public String getSpace() {
		return space;
	}
	public void setSpace(String space) {
		this.space = space;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
