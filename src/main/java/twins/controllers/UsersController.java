package twins.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import twins.boundaries.NewUserDetails;
import twins.boundaries.UserBoundary;
import twins.boundaries.UserId;
import twins.logic.UsersService;

@RestController
public class UsersController {
	private UsersService userService;
	private String applicationSpace;

	@Value("${spring.application.name:missingName}")
	public void setSpringApplicatioName(String applicationSpace) {
		this.applicationSpace = applicationSpace;
	}

	@Autowired
	public void setUserService(UsersService userService) {
		this.userService = userService;
	}

	public UsersService getUserService() {
		return userService;
	}

	@RequestMapping(path = "/twins/users", 
			method = RequestMethod.POST, 
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary createUser(@RequestBody NewUserDetails details) {
		UserBoundary user = new UserBoundary();
		user.setAvatar(details.getAvatar());
		user.setRole(details.getRole());
		user.setUsername(details.getUsername());
		user.setUserId(new UserId(this.applicationSpace, details.getEmail()));
		return userService.createUser(user);
	}

	@RequestMapping(path = "/twins/users/login/{userSpace}/{userEmail}", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary login(@PathVariable("userSpace") String space, @PathVariable("userEmail") String email) {
		return userService.login(space, email);
	}

	@RequestMapping(path = "/twins/users/{userSpace}/{userEmail}", 
			method = RequestMethod.PUT, 
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public void update(@PathVariable("userSpace") String space, @PathVariable("userEmail") String email,
			@RequestBody UserBoundary user) {
		userService.updateUser(space, email, user);
	}
}