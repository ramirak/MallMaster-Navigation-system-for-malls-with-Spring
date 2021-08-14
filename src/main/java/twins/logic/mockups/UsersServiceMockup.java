package twins.logic.mockups;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import twins.boundaries.UserBoundary;
import twins.data.UserEntity;
import twins.logic.UsersService;
import twins.logic.converters.EntityConverter;
import twins.utils.Utils;

//@Service
public class UsersServiceMockup implements UsersService {
	private Map<String, UserEntity> users;
	private Utils utils;
	private EntityConverter<UserEntity, UserBoundary> entityConverter;

	public UsersServiceMockup() {
		this.users = Collections.synchronizedMap(new HashMap<>());
	}

	@Autowired
	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Autowired
	public void setEntityConverter(EntityConverter<UserEntity, UserBoundary> entityConverter) {
		this.entityConverter = entityConverter;
	}

	@Override
	public UserBoundary createUser(UserBoundary user) {
		utils.assertNull(user.getRole());
		utils.assertValidRole(user.getRole());
		utils.assertNull(user);
		utils.assertNull(user.getUserId());
		utils.assertNull(user.getUserId().getSpace());
		utils.assertNull(user.getUserId().getEmail());
		utils.assertValidEmail(user.getUserId().getEmail());
		UserEntity entity = this.entityConverter.fromBoundary(user);
		String id = utils.combineID(user.getUserId().getSpace(), user.getUserId().getEmail());
		users.put(id, entity);
		return this.entityConverter.toBoundary(entity);
	}

	@Override
	public UserBoundary login(String userSpace, String userEmail) {
		String id = utils.combineID(userSpace, userEmail);
		UserEntity entity = users.get(id);
		utils.assertNull(entity);
		return this.entityConverter.toBoundary(entity);
	}

	@Override
	public UserBoundary updateUser(String userSpace, String userEmail, UserBoundary update) {
		utils.assertNull(update);
		boolean dirty = false;

		String id = utils.combineID(userSpace, userEmail);
		UserEntity entity = users.get(id);
		utils.assertNull(entity);
		if (update.getAvatar() != null) {
			dirty = true;
			entity.setAvatar(update.getAvatar());
		}
		if (update.getRole() != null) {
			utils.assertValidRole(update.getRole());
			dirty = true;
			entity.setRole(update.getRole());
		}
		if (update.getUsername() != null) {
			dirty = true;
			entity.setUsername(update.getUsername());
		}
		if (dirty) {
			users.put(id, entity);
		}
		return this.entityConverter.toBoundary(entity);
	}

	@Override
	public List<UserBoundary> getAllUsers(String adminSpace, String adminEmail) {
		utils.assertNull(adminSpace);
		utils.assertNull(adminEmail);
		// Check if admin stub
		return this.users.values().stream().map(this.entityConverter::toBoundary).collect(Collectors.toList());
	}

	@Override
	public void deleteAllUsers(String adminSpace, String adminEmail) {
		utils.assertNull(adminSpace);
		utils.assertNull(adminEmail);
		// Check if admin stub
		users.clear();
	}

	public Map<String, UserEntity> getUsers() {
		return users;
	}

	public void setUsers(Map<String, UserEntity> users) {
		this.users = users;
	}
}
