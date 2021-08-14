package twins.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import twins.boundaries.Id;
import twins.boundaries.ItemBoundary;
import twins.logic.ExtendedItemsService;

@RestController
public class DigitalItemsController {
	private ExtendedItemsService extendedItemsService;
	private final String pageSize = "10";

	@Autowired
	public void setItemsService(ExtendedItemsService extendedItemsService) {
		this.extendedItemsService = extendedItemsService;
	}

	public DigitalItemsController() {
	}

	@RequestMapping(path = "/twins/items/{userSpace}/{userEmail}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary createNewItem(@RequestBody ItemBoundary item, @PathVariable("userSpace") String space,
			@PathVariable("userEmail") String email) {
		return extendedItemsService.createItem(space, email, item);
	}

	@RequestMapping(path = "/twins/items/{userSpace}/{userEmail}/{itemSpace}/{itemId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void updateItem(@RequestBody ItemBoundary item, @PathVariable("userSpace") String userSpace,
			@PathVariable("userEmail") String email, @PathVariable("itemSpace") String itemSpace,
			@PathVariable("itemId") String itemId) {
		extendedItemsService.updateItem(userSpace, email, itemSpace, itemId, item);
	}

	@RequestMapping(path = "/twins/items/{userSpace}/{userEmail}/{itemSpace}/{itemId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary getItem(@PathVariable("userSpace") String userSpace, @PathVariable("userEmail") String email,
			@PathVariable("itemSpace") String itemSpace, @PathVariable("itemId") String itemId) {
		return extendedItemsService.getSpecificItem(userSpace, email, itemSpace, itemId);
	}

	@RequestMapping(path = "/twins/items/{userSpace}/{userEmail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary[] getAllItems(@PathVariable("userSpace") String userSpace,
			@PathVariable("userEmail") String email,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = pageSize) int size) {
		return extendedItemsService.getAllItem(userSpace, email, page, size).toArray(new ItemBoundary[0]);
	}

	@RequestMapping(path = "/twins/items/{userSpace}/{userEmail}/{itemSpace}/{itemId}/children", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void bindItem(@RequestBody Id itemIdBoundary, @PathVariable("userSpace") String userSpace,
			@PathVariable("userEmail") String email, @PathVariable("itemSpace") String itemSpace,
			@PathVariable("itemId") String itemId) {
		// TODO: No use of path variables
		extendedItemsService.bindItem(itemId, itemIdBoundary, userSpace, email, itemSpace);
	}

	@RequestMapping(path = "/twins/items/{userSpace}/{userEmail}/{itemSpace}/{itemId}/children", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary[] getAllChildren(@PathVariable("userSpace") String userSpace,
			@PathVariable("userEmail") String userEmail, @PathVariable("itemSpace") String itemSpace,
			@PathVariable("itemId") String itemId,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = pageSize) int size) {
		// TODO: No use of path variables
		return extendedItemsService.getChildren(userSpace,userEmail,itemSpace, itemId, page, size).toArray(new ItemBoundary[0]);
	}

	@RequestMapping(path = "/twins/items/{userSpace}/{userEmail}/{itemSpace}/{itemId}/parents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ItemBoundary[] getParents(@PathVariable("userSpace") String userSpace,
			@PathVariable("userEmail") String userEmail, @PathVariable("itemSpace") String itemSpace,
			@PathVariable("itemId") String itemId,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page,
			@RequestParam(name = "size", required = false, defaultValue = pageSize) int size) {
		// TODO: No use of path variables
		return extendedItemsService.getParents(userSpace,userEmail,itemSpace,itemId, page, size).toArray(new ItemBoundary[0]);
	}

}
