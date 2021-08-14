package twins.data;

import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import twins.boundaries.UserBoundary;
import twins.boundaries.UserId;
import twins.logic.ExtendedUsersService;

//@Component
public class UserInitializer implements CommandLineRunner {
	private ExtendedUsersService usersService;

	@Autowired
	public UserInitializer(ExtendedUsersService usersService) {
		this.usersService = usersService;
	}

	@Override
	public void run(String... args) throws Exception {
		// create 50 users
		IntStream.range(0, 20) // Stream<Integer>
				.mapToObj(i -> {
					UserBoundary user = new UserBoundary();
					user.setUserId(new UserId("2021b.shelly.fainberg", "nastia" + i + "HaMalka@gmail.com"));
					user.setRole("PLAYER");
					return user;
				}).forEach(this.usersService::createUser);
	}

}
