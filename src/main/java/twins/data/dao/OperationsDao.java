package twins.data.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import twins.data.OperationEntity;

public interface OperationsDao extends PagingAndSortingRepository<OperationEntity,String>{

}
