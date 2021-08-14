package twins.boundaries;

public class Creator {
	private UserId userId;

	public Creator() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Creator(UserId userId) {
		this.userId = userId;
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}
	

}
