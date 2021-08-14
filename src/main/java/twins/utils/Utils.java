package twins.utils;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.stereotype.Service;

import twins.data.ItemEntity;
import twins.data.ItemType;
import twins.data.UserRole;

@Service
public class Utils {
	private final String DELIMITER = "@@";
	private final int ID_POSITION = 0;
	private final int SPACE_POSITION = 1;

	public void assertValidRole(String role) {
		for (UserRole ur : UserRole.values()) {
			if (ur.name().equals(role))
				return;
		}
		throw new BadRequestException("Invalid role");
	}

	public void assertNull(Object obj) {
		if (obj == null)
			throw new BadRequestException("null object");
	}

	public void assertEmptyString(String str) {
		if (str.equals(""))
			throw new BadRequestException("Empty string");
	}

	/**
	 * Creates a unique ID
	 * 
	 * @param springApplicationName - application name
	 * @return uniqueID + delimiter + springApplicationName
	 */
	public String createUniqueID(String springApplicationName) {
		return UUID.randomUUID().toString() + DELIMITER + springApplicationName;
	}

	/**
	 * extracts the space from an ID
	 * 
	 * @param id a unique ID
	 * @return springApplicationName
	 */
	public String extractSpace(String id) {
		return id.split(DELIMITER)[SPACE_POSITION];
	}

	/**
	 * extracts the id from an ID
	 * 
	 * @param id a unique ID
	 * @return id
	 */
	public String extractId(String id) {
		return id.split(DELIMITER)[ID_POSITION];
	}

	/**
	 * extracts space and email of a user from their ID
	 * 
	 * @param id an ID in format of space+delimiter+email
	 * @return array of Strings in the shape of [space, email]
	 */
	public String[] extractSpaceEmail(String id) {
		return id.split(DELIMITER);
	}

	/**
	 * combines the user's space and email/id to create a unique ID
	 * 
	 * @param space user's applicationSpace
	 * @param email user's email/id
	 * @return space + delimiter+email/id
	 */
	public String combineID(String space, String parameter) {
		return parameter + DELIMITER + space;
	}

	public void assertValidEmail(String email) {
		String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		if (!email.matches(regex))
			throw new BadRequestException("Invalid email");
	}

	public void assertProperRole(String currentRole, String... requiredRoles) {
		for (String requiredRole : requiredRoles) {
			if (currentRole.equals(requiredRole)) {
				return;
			}
		}
		throw new ForbiddenException(
				"Required role to be " + Arrays.toString(requiredRoles) + " but was " + currentRole);
	}

	
	public void assertItemType(String type) {

		for (ItemType itemType : ItemType.values()) {
			if (itemType.name().equals(type)) {
				return;
			}
		}
		throw new ForbiddenException(
				"Required itemType to be " + Arrays.toString(ItemType.values()) + " but was " + type);
	}

	/**
	 * Calculate distance between two points in latitude and longitude taking into
	 * account height difference. If you are not interested in height difference
	 * pass 0.0. Uses Haversine method as its base.
	 * 
	 * @param startPoint Contains lat/lng of point A
	 * @param endPoint   Contains lat/lng of point B
	 * @return Distance in Meters
	 */
	public double calculateDistance(ItemEntity startPoint, ItemEntity endPoint) {
		double lat1 = startPoint.getLat(), lat2 = endPoint.getLat();
		double lng1 = startPoint.getLng(), lng2 = endPoint.getLng();
		final int R = 6371; // Radius of the earth

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lng2 - lng1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		distance = Math.pow(distance, 2);

		return Math.sqrt(distance);
	}
}
