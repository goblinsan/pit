package pit.errors;

import org.springframework.http.HttpStatus;

public class GameSecurityError extends RuntimeException {
    private final HttpStatus httpStatus;

    public GameSecurityError(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
