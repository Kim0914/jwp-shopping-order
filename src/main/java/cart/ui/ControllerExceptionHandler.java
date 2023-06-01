package cart.ui;

import cart.dto.Response;
import cart.dto.ResultResponse;
import cart.exception.AuthenticationException;
import cart.exception.NumberRangeException;
import cart.exception.ShoppingOrderException;
import cart.exception.UnauthorizedAccessException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception e) {
        logger.error(e.getMessage());
        return ResponseEntity.internalServerError()
                .body(new Response("서버에 알 수 없는 문제가 발생했습니다."));
    }

    @ExceptionHandler(ShoppingOrderException.class)
    public ResponseEntity<Response> handleShoppingOrderException(ShoppingOrderException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response(e.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Response> handlerAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new Response("인증이 실패했습니다."));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<Response> handleUnauthorizedAccessException(UnauthorizedAccessException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new Response(e.getMessage()));
    }

    @ExceptionHandler(NumberRangeException.class)
    public ResponseEntity<Response> handleNumberRangeException(NumberRangeException e) {
        Map<String, String> validation = new HashMap<>();
        validation.put(e.getField(), e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ResultResponse<>("잘못된 요청입니다.", validation));
    }
}
