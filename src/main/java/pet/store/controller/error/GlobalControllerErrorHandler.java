package pet.store.controller.error;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j	
public class GlobalControllerErrorHandler {
	
	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public Map<String, String> handleNoSuchElementException(
			NoSuchElementException ex) {
		log.error("GlobalControllerErrorHandler>>NoSuchElementException : "+ex.getMessage());
		Map<String, String> errMap = Map.of(
			    "timestamp",ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME),
			    "message", ex.toString()
			);
		return errMap;
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, String> handleException(
			Exception ex) {
		log.error("GlobalControllerErrorHandler>>Exception : "+ ex);
		Map<String, String> errMap = Map.of(
			    "message", ex.toString(),"timestamp",ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)
			);
		return errMap;
	}

}
