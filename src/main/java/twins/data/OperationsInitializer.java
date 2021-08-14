package twins.data;

import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import twins.boundaries.Creator;
import twins.boundaries.OperationBoundary;
import twins.boundaries.UserId;
import twins.logic.ExtendedOperationsService;

//@Component
public class OperationsInitializer implements CommandLineRunner {
	private ExtendedOperationsService opertionsService;

	@Autowired
	public OperationsInitializer(ExtendedOperationsService opertionsService) {
		this.opertionsService = opertionsService;
	}

	@Override
	public void run(String... args) throws Exception {
		// create 50 users
		IntStream.range(0, 20) // Stream<Integer>
				.mapToObj(i -> {
					OperationBoundary operation = new OperationBoundary();
					operation.setType("");
					operation.setInvokedBy(new Creator(new UserId("2021b.shelly.fainberg", "nastiaHaMalka@gmail.com")));
					return operation;
				}).forEach(this.opertionsService::invokeOperation);

	}

}
