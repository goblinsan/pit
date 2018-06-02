package pit.webapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pit.errors.GameError;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(GameError.class)
    public ResponseEntity handleException(GameError e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }
}