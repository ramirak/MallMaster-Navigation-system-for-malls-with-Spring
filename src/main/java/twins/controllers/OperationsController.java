package twins.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import twins.boundaries.OperationBoundary;
import twins.logic.OperationsService;

@RestController
public class OperationsController {
	
	private OperationsService opertionService;
	
	public OperationsController(OperationsService opertionService) {
		this.opertionService = opertionService;
	}

	@RequestMapping (
			path = "/twins/operations",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE
			)
	public Object invoke(@RequestBody OperationBoundary operation) {
		return opertionService.invokeOperation(operation);
	}
	
	@RequestMapping (
			path = "/twins/operations/async",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE, 
			consumes = MediaType.APPLICATION_JSON_VALUE
			)
	public OperationBoundary asyncInvoke(@RequestBody OperationBoundary operation) {
		return opertionService.invokeAsynchronousOperation(operation);
	}
}
