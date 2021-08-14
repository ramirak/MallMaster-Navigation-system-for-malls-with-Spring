package twins.boundaries;

public class UserId {
	private String space;
	private String email;

	public UserId() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UserId(String space, String email) {
		this.space = space;
		this.email = email;
	}

	public String getSpace() {
		return space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public boolean equals(Object other) {
		UserId uid = (UserId) other;
		return this.getEmail().equals(uid.getEmail()) && this.getSpace().equals(uid.getSpace());
	}

}
