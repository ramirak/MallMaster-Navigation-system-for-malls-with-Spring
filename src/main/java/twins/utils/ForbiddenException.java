package twins.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException{

	private static final long serialVersionUID = -2681662732363735492L;
	
	public ForbiddenException() {
	}

	public ForbiddenException(String message) {
		super(message);
	}

	public ForbiddenException(Throwable cause) {
		super(cause);
	}

	public ForbiddenException(String message, Throwable cause) {
		super(message, cause);
	}

}
