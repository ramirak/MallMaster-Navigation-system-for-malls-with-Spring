package twins.logic;

import java.util.List;

import twins.boundaries.Id;
import twins.boundaries.ItemBoundary;

public interface ExtendedItemsService extends ItemsService {

	public void bindItem(String parentId, Id childIdBoundary, String userSpace, String email, String itemSpace);

	public List<ItemBoundary> getChildren(String userSpace, String userEmail, String itemSpace, String itemId, int page, int size);

	public List<ItemBoundary> getAllItem(String userSpace, String userEmail, int page, int size);

	public List<ItemBoundary> getParents(String userSpace, String userEmail, String itemSpace, String itemId, int page, int size);
}
