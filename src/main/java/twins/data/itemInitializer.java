package twins.data;

import java.util.Random;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import twins.boundaries.Id;
import twins.boundaries.ItemBoundary;
import twins.boundaries.UserBoundary;
import twins.boundaries.UserId;
import twins.logic.ExtendedItemsService;
import twins.logic.UsersService;
import twins.utils.Utils;

//@Component
public class itemInitializer implements CommandLineRunner {
	
	private ExtendedItemsService itemService;
	private UsersService usersService;
	private Utils utils;
	
	@Autowired
	public void setUsersService(UsersService usersService) {
		this.usersService = usersService;
	}
	
	@Autowired
	public void setUtils(Utils utils) {
		this.utils = utils;
	}
	
	@Autowired
	public itemInitializer(ExtendedItemsService itemService) {
		this.itemService = itemService;
	}


	@Override
	public void run(String... args) throws Exception {
		Random ran = new Random();
		
		UserBoundary user = new UserBoundary();
		user.setRole(UserRole.MANAGER.name());
		user.setUserId(new UserId("2021b.shelly.fainberg", "testMail@test.com"));
		usersService.createUser(user);
		
		// create 50 messages
		ItemBoundary original = new ItemBoundary(); 
		original.setName("original");
		original.setType("");
		original = this.itemService.createItem(user.getUserId().getSpace(), user.getUserId().getEmail(), original);
		final Id oringalId = original.getItemId();
		System.err.println("ORIGINAL KEY = " + oringalId);

		IntStream.range(0, 20) // Stream<Integer>
			.mapToObj(i->{
				ItemBoundary item = new ItemBoundary();
				item.setName(i + "");
				item.setType("");
				item.setActive(ran.nextBoolean());
				
		
				return item;
			})
			.forEach(item -> {
				item = this.itemService.createItem(user.getUserId().getSpace(), user.getUserId().getEmail(), item);
				this.itemService.bindItem(item.getItemId().getId(), oringalId, user.getUserId().getSpace(), user.getUserId().getEmail(), user.getUserId().getSpace());
			});
	}
}
