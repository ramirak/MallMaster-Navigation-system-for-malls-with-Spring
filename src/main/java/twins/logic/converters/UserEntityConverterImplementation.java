package twins.logic.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import twins.boundaries.UserBoundary;
import twins.boundaries.UserId;
import twins.data.UserEntity;
import twins.utils.Utils;

@Component
public class UserEntityConverterImplementation implements EntityConverter<UserEntity, UserBoundary> {
	private Utils utils;

	@Autowired
	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Override
	public UserBoundary toBoundary(UserEntity entity) {
		UserBoundary ub = new UserBoundary();
		ub.setAvatar(entity.getAvatar());
		ub.setRole(entity.getRole());
		String[] userIDInfo = utils.extractSpaceEmail(entity.getId());
		ub.setUserId(new UserId(userIDInfo[1], userIDInfo[0]));
		ub.setUsername(entity.getUsername());
		return ub;
	}

	@Override
	public UserEntity fromBoundary(UserBoundary boundary) {
		UserEntity userEntity = new UserEntity();
		userEntity.setAvatar(boundary.getAvatar());
		userEntity.setRole(boundary.getRole());
		userEntity.setUsername(boundary.getUsername());
		return userEntity;
	}

}
