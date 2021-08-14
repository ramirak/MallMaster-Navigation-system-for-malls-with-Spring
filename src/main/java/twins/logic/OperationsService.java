package twins.logic;

import java.util.List;

import twins.boundaries.OperationBoundary;

public interface OperationsService {

	public Object invokeOperation(OperationBoundary operation);

	public OperationBoundary invokeAsynchronousOperation(OperationBoundary operation);

	@Deprecated
	public List<OperationBoundary> getAllOperations(String adminEmail, String adminSpace);
	
	public void deleteAllOperations(String adminEmail, String adminSpace);
}
