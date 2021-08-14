package twins.logic.mockups;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import twins.boundaries.OperationBoundary;
import twins.data.OperationEntity;
import twins.logic.OperationsService;
import twins.logic.converters.EntityConverter;
import twins.utils.Utils;

//@Service
public class OperationsServiceMockup implements OperationsService {

	private Map<String, OperationEntity> operations;
	private EntityConverter<OperationEntity, OperationBoundary> entityConverter;
	private String springApplicationName;
	private Utils utils;

	public OperationsServiceMockup() {
		this.operations = Collections.synchronizedMap(new HashMap<>());
	}

	@Autowired
	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	@Autowired
	public void setEntityConverter(EntityConverter<OperationEntity, OperationBoundary> entityConverter) {
		this.entityConverter = entityConverter;
	}

	@Value("${spring.application.name:defaultName}")
	public void setSpringApplicationName(String springApplicationName) {
		this.springApplicationName = springApplicationName;
	}

	@Override
	public Object invokeOperation(OperationBoundary operation) {
		utils.assertNull(operation);
		OperationEntity oe = this.entityConverter.fromBoundary(operation);
		String newId = utils.createUniqueID(springApplicationName);
		oe.setOperationId(newId);
		oe.setCreatedTimestamp(new Date());
		operations.put(newId, oe);
		return this.entityConverter.toBoundary(oe);
	}

	@Override
	public OperationBoundary invokeAsynchronousOperation(OperationBoundary operation) {
		utils.assertNull(operation);
		OperationEntity oe = this.entityConverter.fromBoundary(operation);
		String newId = utils.createUniqueID(springApplicationName);
		oe.setCreatedTimestamp(new Date());
		oe.setOperationId(newId);
		operations.put(newId, oe);
		return this.entityConverter.toBoundary(oe);
	}

	@Override
	public List<OperationBoundary> getAllOperations(String adminEmail, String adminSpace) {
		utils.assertNull(adminSpace);
		utils.assertNull(adminEmail);
		return this.operations.values().stream().map(this.entityConverter::toBoundary).collect(Collectors.toList());
	}

	@Override
	public void deleteAllOperations(String adminEmail, String adminSpace) {
		utils.assertNull(adminSpace);
		utils.assertNull(adminEmail);
		operations.clear();
	}

}
