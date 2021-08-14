package twins.logic.jpa;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import twins.boundaries.UserBoundary;
import twins.data.UserEntity;
import twins.data.UserRole;
import twins.data.dao.UserDao;
import twins.logic.ExtendedUsersService;
import twins.logic.converters.EntityConverter;
import twins.utils.NotFoundException;
import twins.utils.Utils;

@Service
public class UsersServiceJpa implements ExtendedUsersService {

	private UserDao userDao;
	private Utils utils;
	private EntityConverter<UserEntity, UserBoundary> entityConverter;

	public UsersServiceJpa() {
	}

	@Autowired
	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Autowired
	public void setEntityConverter(EntityConverter<UserEntity, UserBoundary> entityConverter) {
		this.entityConverter = entityConverter;
	}

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	@Transactional
	public UserBoundary createUser(UserBoundary user) {
		utils.assertNull(user);
		utils.assertNull(user.getUserId());
		utils.assertNull(user.getUserId().getSpace());
		utils.assertNull(user.getRole());
		utils.assertValidRole(user.getRole());
		utils.assertNull(user.getUserId().getEmail());
		utils.assertValidEmail(user.getUserId().getEmail());

		UserEntity entity = this.entityConverter.fromBoundary(user);

		String id = utils.combineID(user.getUserId().getSpace(), user.getUserId().getEmail());
		entity.setId(id);

		entity = this.userDao.save(entity);

		return this.entityConverter.toBoundary(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public UserBoundary login(String userSpace, String userEmail) {

		utils.assertNull(userSpace);
		utils.assertNull(userEmail);
		utils.assertValidEmail(userEmail);

		String id = utils.combineID(userSpace, userEmail);
		Optional<UserEntity> optionalUser = this.userDao.findById(id);

		if (optionalUser.isPresent()) {
			UserEntity entity = optionalUser.get();
			return this.entityConverter.toBoundary(entity);
		} else {
			throw new NotFoundException("could not find user by email: " + userEmail);
		}
	}

	@Override
	@Transactional
	public UserBoundary updateUser(String userSpace, String userEmail, UserBoundary update) {

		utils.assertNull(update);
		utils.assertNull(userSpace);
		utils.assertNull(update.getRole());
		utils.assertValidRole(update.getRole());
		utils.assertNull(userEmail);
		utils.assertValidEmail(userEmail);

		String id = utils.combineID(userSpace, userEmail);
		Optional<UserEntity> existingOptional = this.userDao.findById(id);

		if (existingOptional.isPresent()) {
			UserEntity existing = existingOptional.get();

			if (update.getAvatar() != null) {
				existing.setAvatar(update.getAvatar());
			}
			if (update.getRole() != null) {
				utils.assertValidRole(update.getRole());
				existing.setRole(update.getRole());
			}
			if (update.getUsername() != null) {
				existing.setUsername(update.getUsername());
			}

			existing = this.userDao.save(existing);

			return this.entityConverter.toBoundary(existing);

		} else {
			throw new NotFoundException("could not find user by email: " + userEmail);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers(String adminSpace, String adminEmail) {
		throw new RuntimeException("Deprecated method");
//		utils.assertNull(adminSpace);
//		utils.assertNull(adminEmail);
//		utils.assertValidEmail(adminEmail);
//
//		Iterable<UserEntity> allEntities = this.userDao.findAll();
//
//		return StreamSupport.stream(allEntities.spliterator(), false).map(this.entityConverter::toBoundary)
//				.collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void deleteAllUsers(String adminSpace, String adminEmail) {
		utils.assertNull(adminSpace);
		utils.assertNull(adminEmail);
		utils.assertValidEmail(adminEmail);
		String userId = utils.combineID(adminSpace, adminEmail);
		UserEntity admin = userDao.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found: " + userId));
		utils.assertProperRole(admin.getRole(), UserRole.ADMIN.name());

		this.userDao.deleteAll();
	}

	@Override
	public List<UserBoundary> getAllUsers(String adminSpace, String adminEmail, int page, int size) {
		utils.assertNull(adminSpace);
		utils.assertNull(adminEmail);
		utils.assertValidEmail(adminEmail);

		String userId = utils.combineID(adminSpace, adminEmail);
		UserEntity admin = userDao.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found: " + userId));
		utils.assertProperRole(admin.getRole(), UserRole.ADMIN.name());

		return this.userDao.findAll(PageRequest.of(page, size, Direction.DESC, "id")).stream()
				.map(this.entityConverter::toBoundary).collect(Collectors.toList());
	}

}
