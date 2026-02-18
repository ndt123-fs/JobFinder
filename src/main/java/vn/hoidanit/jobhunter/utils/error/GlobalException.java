package vn.hoidanit.jobhunter.utils.error;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import vn.hoidanit.jobhunter.domain.response.RestResponse;

@RestControllerAdvice
public class GlobalException {
	@ExceptionHandler(value = { BadCredentialsException.class, UsernameNotFoundException.class,
			EmailInvalidException.class, IdInvalidException.class, MissingRequestCookieException.class, NoSuchElementException.class })
	public ResponseEntity<RestResponse<Object>> handleException(Exception ex, WebRequest request) {

		RestResponse<Object> res = new RestResponse<Object>();
		res.setStatusCode(HttpStatus.BAD_REQUEST.value());
		res.setPath(request.getDescription(false).replace("uri=", ""));
		res.setMessage("Exception occurs....");
		res.setError(ex.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

	}

	// @ExceptionHandler(value = MethodArgumentNotValidException.class)
	// public ResponseEntity<RestResponse<Object>>
	// handleMethodNotValidException(Exception e, WebRequest request) {

	// RestResponse<Object> res = new RestResponse<Object>();
	// res.setStatusCode(HttpStatus.BAD_REQUEST.value());
	// res.setError(((MethodArgumentNotValidException) e).getBody().getDetail());
	// res.setPath(request.getDescription(false).replace("uri=", ""));
	// // xu ly cat chuoi
	// String message = e.getMessage();
	// int start = message.lastIndexOf("[");
	// int end = message.lastIndexOf("]");
	// message = message.substring(start + 1, end - 1);
	// res.setMessage(message);

	// return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
	// }
	@ExceptionHandler(value = NoResourceFoundException.class)
	public ResponseEntity<RestResponse<Object>> handleNotFoundException(NoResourceFoundException e,
			WebRequest request) {
		RestResponse<Object> res = new RestResponse<>();
		res.setStatusCode(HttpStatus.NOT_FOUND.value());
		res.setMessage("404 not found.URL may not exist !");
		res.setError(e.getMessage());
		res.setPath(request.getDescription(false).replace("uri=", ""));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
	}

	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<RestResponse<Object>> handleMethodNotValidException(MethodArgumentNotValidException e,
			WebRequest request) {
		// lay vaidation
		BindingResult result = e.getBindingResult();
		final List<FieldError> fieldErrors = result.getFieldErrors();

		RestResponse<Object> res = new RestResponse<Object>();
		res.setStatusCode(HttpStatus.BAD_REQUEST.value());
		res.setError(((MethodArgumentNotValidException) e).getBody().getDetail());
		res.setPath(request.getDescription(false).replace("uri=", ""));

		// List<String> errors = fieldErrors.stream().map(f ->
		// f.getDefaultMessage()).collect(Collectors.toList());
		// res.setMessage(errors.size() > 1 ? errors : errors.get(0));

		List<String> errors = new ArrayList<>();
		for (FieldError fieldError : fieldErrors) {
			errors.add(fieldError.getDefaultMessage());
		}
		res.setMessage(errors.size() > 1 ? errors : errors.get(0));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
	}

@ExceptionHandler(value = { StorageException.class })
	public ResponseEntity<RestResponse<Object>> handleUploadFileException(StorageException ex, WebRequest request) {

		RestResponse<Object> res = new RestResponse<Object>();
		res.setStatusCode(HttpStatus.BAD_REQUEST.value());
		res.setPath(request.getDescription(false).replace("uri=", ""));
		res.setMessage("Uploadfile Exception !!!....");
		res.setError(ex.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);

	}
	@ExceptionHandler(value = PermissionException.class)
	public ResponseEntity<RestResponse<Object>>handlePermissionException (PermissionException ex ){
		RestResponse<Object> res = new RestResponse<Object>();
		res.setStatusCode(HttpStatus.FORBIDDEN.value());
		res.setMessage("Forbidden !");
		res.setError(ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(res);
	}
}
