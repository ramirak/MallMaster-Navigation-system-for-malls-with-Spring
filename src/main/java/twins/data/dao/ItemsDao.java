package twins.data.dao;


import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import twins.data.ItemEntity;

public interface ItemsDao extends PagingAndSortingRepository<ItemEntity, String> {	
	
	public List<ItemEntity> findAllByParents_itemId (
			@Param("originalItemId") String originalItemId,
			Pageable pageable);
	
	public List<ItemEntity> findAllByChildren_itemId (
			@Param("originalItemId") String originalItemId,
			Pageable pageable);
	
	public List<ItemEntity> findAllByActive(
			@Param("active") boolean active,
			Pageable pageable);
	
	public List<ItemEntity> findAllByActiveAndParents_itemId (
			@Param("active") boolean active,
			@Param("originalItemId") String originalItemId,
			Pageable pageable);
	
	public List<ItemEntity> findAllByActiveAndChildren_itemId (
			@Param("active") boolean active,
			@Param("originalItemId") String originalItemId,
			Pageable pageable);
	
	public List<ItemEntity> findAllByActiveAndItemType(
			@Param("active") boolean active,
			@Param("itemType") String itemType,
			Pageable pageable);
	
	public List<ItemEntity> findAllByActiveAndItemType(
			@Param("active") boolean active,
			@Param("itemType") String itemType);
	
}
