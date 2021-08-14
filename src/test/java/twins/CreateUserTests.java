package twins;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Email;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import net.bytebuddy.utility.RandomString;
import testUtils.Finals;
import twins.boundaries.UserBoundary;
import twins.utils.BadRequestException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CreateUserTests {
	private int port;
	private RestTemplate restTemplate;
	private String baseURL;
	private String applicationSpace;

	@Value("${spring.application.name:missingName}")
	public void setSpringApplicatioName(String applicationSpace) {
		this.applicationSpace = applicationSpace;
	}

	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.baseURL = "http://localhost:" + this.port + "/twins/";
	}

	@Test
	public void testContext() throws Exception {
		// Do nothing
	}

	@AfterEach
	public void teardown() {
		String adminEmail = "admin@test.com";
		String deleteURL = baseURL + "admin/users/" + applicationSpace + "/" + adminEmail;
		this.restTemplate.delete(deleteURL);
	}

	public Map<String, Object> createNewUserBoundary(String email, String avatar, String role, String username) {
		Map<String, Object> newUserDetails = new HashMap<>();
		newUserDetails.put(Finals.newUserDetails.email, email);
		newUserDetails.put(Finals.newUserDetails.avatar, avatar);
		newUserDetails.put(Finals.newUserDetails.role, role);
		newUserDetails.put(Finals.newUserDetails.username, username);
		return newUserDetails;
	}

	@Test
	public void createValidUser() throws Exception {
		// GIVEN: Server is running
		// Do nothing
		// WHEN: post with
		// {
		// "email":"test@demo.com",
		// "role":"PLAYER",
		// "username":"tester001",
		// "avatar":"TEST AVATAR"
		// }
		String createURL = baseURL + "users/";
		String email = "test@demo.com";
		String avatar = "TEST AVATAR";
		String role = "PLAYER";
		String username = "tester001";
		Map<String, Object> newUserDetails = createNewUserBoundary(email, avatar, role, username);
		UserBoundary actualUser = this.restTemplate.postForObject(createURL, newUserDetails, UserBoundary.class);

		// THEN: Server creates a user
		// AND: the user has an ID that is not null
		// AND: the userBoundary contains the newUserDetails info
		// AND: the userBoundary contains the application space
		assertValidUser(actualUser, email, avatar, role, username);
	}

	public void assertValidUser(UserBoundary actualUser, String email, String avatar, String role, String username)
			throws Exception {
		// User not null
		assertThat(actualUser).isNotNull();

		// User id is not null
		assertThat(actualUser.getUserId()).isNotNull();

		// User fields are valid
		assertThat(actualUser.getAvatar()).matches(avatar);
		assertThat(actualUser.getUsername()).matches(username);
		assertThat(actualUser.getRole()).matches(role);
		assertThat(actualUser.getUserId().getEmail()).matches(email);

		// User space is the application space
		assertThat(actualUser.getUserId().getSpace()).matches(applicationSpace);
	}

	@Test
	public void createUserInvalidEmail() throws Exception {
		// GIVEN: Server is running
		// Do nothing
		// WHEN: post with
		// {
		// "email":"invalidEmail",
		// "role":"PLAYER",
		// "username":"tester001",
		// "avatar":"TEST AVATAR"
		// }
		String createURL = baseURL + "users/";
		String email = "invalidEmail";
		String avatar = "TEST AVATAR";
		String role = "PLAYER";
		String username = "tester001";
		Map<String, Object> newUserDetails = createNewUserBoundary(email, avatar, role, username);
		// THEN: The server will throw an exception for bad request(400)
		assertThatThrownBy(() -> {
			this.restTemplate.postForObject(createURL, newUserDetails, UserBoundary.class);
		}).isInstanceOf(HttpClientErrorException.BadRequest.class);
	}

	@Test
	public void createEmptyUser() throws Exception {
		// GIVEN: Server is running
		// Do nothing

		// WHEN: post with
		// {}
		String createURL = baseURL + "users/";
		Map<String, Object> newUserDetails = new HashMap<>();

		// THEN: Server will throw bad request
		assertThatThrownBy(() -> {
			this.restTemplate.postForObject(createURL, newUserDetails, UserBoundary.class);
		}).isInstanceOf(HttpClientErrorException.BadRequest.class);
	}

	@Test
	public void createManyUsers() throws Exception {
		// GIVEN: Server is running
		// Do nothing

		int times = 100;
		String createURL = baseURL + "users/";

		// WHEN: post {times} with random valid info
		for (int i = 0; i < times; i++) {
			String randomString = RandomString.make(5);
			String email = randomString + i + "@gmail.com";
			String avatar = randomString + i;
			String role = "PLAYER";
			String username = randomString + i + i;
			Map<String, Object> newUserDetails = createNewUserBoundary(email, avatar, role, username);
			UserBoundary actualUser = this.restTemplate.postForObject(createURL, newUserDetails, UserBoundary.class);
			
			// THEN: for every user -> Server will create a user
			// AND: the user has an ID that is not null
			// AND: the userBoundary contains the newUserDetails info
			// AND: the userBoundary contains the application space
			assertValidUser(actualUser, email, avatar, role, username);
		}

	}
	
}

// TODO Once we can validate if users exist or not - create a test to add two of the same user
