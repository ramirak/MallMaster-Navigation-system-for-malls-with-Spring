package twins.logic;

import java.util.List;

import twins.boundaries.OperationBoundary;

public interface ExtendedOperationsService extends OperationsService{
	public List<OperationBoundary> getAllOperations(String adminEmail, String adminSpace , int page, int size);
}
