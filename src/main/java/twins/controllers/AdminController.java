package twins.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import twins.boundaries.OperationBoundary;
import twins.boundaries.UserBoundary;
import twins.logic.ExtendedOperationsService;
import twins.logic.ExtendedUsersService;
import twins.logic.ItemsService;

@RestController
public class AdminController {
	private ExtendedUsersService extendedUsersService;
	private ItemsService itemsService;
	private ExtendedOperationsService extendedOperationsService;
	private final String pageSize = "10";

	public AdminController() {}

	@Autowired
	public void setExtendedUsersService(ExtendedUsersService extendedUsersService) {
		this.extendedUsersService = extendedUsersService;
	}

	@Autowired
	public void setItemsService(ItemsService itemsService) {
		this.itemsService = itemsService;
	}

	@Autowired
	public void setOperationsService(ExtendedOperationsService extendedOperationsService) {
		this.extendedOperationsService = extendedOperationsService;
	}

	@RequestMapping(path = "/twins/admin/users/{userSpace}/{userEmail}", method = RequestMethod.DELETE)
	public void deleteUsers(@PathVariable("userSpace") String adminSpace,
			@PathVariable("userEmail") String adminEmail) {
		extendedUsersService.deleteAllUsers(adminSpace, adminEmail);
	}

	@RequestMapping(path = "/twins/admin/items/{userSpace}/{userEmail}", method = RequestMethod.DELETE)
	public void deleteItems(@PathVariable("userSpace") String adminSpace,
			@PathVariable("userEmail") String adminEmail) {
		itemsService.deleteAllItems(adminSpace, adminEmail);
	}

	@RequestMapping(path = "/twins/admin/operations/{userSpace}/{userEmail}", method = RequestMethod.DELETE)
	public void deleteOperations(@PathVariable("userSpace") String adminSpace,
			@PathVariable("userEmail") String adminEmail) {
		extendedOperationsService.deleteAllOperations(adminEmail, adminSpace);
	}

	@RequestMapping(path = "/twins/admin/users/{userSpace}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] exportUsers(@PathVariable("userSpace") String adminSpace,
			@PathVariable("userEmail") String adminEmail,
			@RequestParam(name="page", required = false, defaultValue = "0") int page,
			@RequestParam(name="size", required = false, defaultValue = pageSize) int size) {
		return extendedUsersService.getAllUsers(adminSpace, adminEmail, page, size).toArray(new UserBoundary[0]);
	}

	@RequestMapping(path = "/twins/admin/operations/{userSpace}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public OperationBoundary[] exportOperations(@PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email,
			@RequestParam(name="page", required = false, defaultValue = "0") int page,
			@RequestParam(name="size", required = false, defaultValue = pageSize) int size) {
		return extendedOperationsService.getAllOperations(email, space, page, size).toArray(new OperationBoundary[0]);
	}
}
