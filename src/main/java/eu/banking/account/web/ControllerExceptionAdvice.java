package eu.banking.account.web;

import eu.banking.account.api.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.apache.commons.lang3.ObjectUtils.isEmpty;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice(basePackages = "eu.banking.account.web")
public class ControllerExceptionAdvice {

	@ExceptionHandler(Exception.class)
	@ResponseStatus(INTERNAL_SERVER_ERROR)
	public Collection<ResponseMessage> handle(Exception ex) {
		log.error("Unexpected exception take place while trying to process application request", ex);

		return Collections.singletonList(ResponseMessage.builder()
				.message(StringUtils.isNoneBlank(ex.getMessage()) ? ex.getMessage() : "Unexpected system exception take place during processing request")
				.timestamp(ZonedDateTime.now())
				.build());
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(BAD_REQUEST)
	public Collection<ResponseMessage> handle(MethodArgumentNotValidException ex) {
		return buildMessages(ex.getBindingResult());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(BAD_REQUEST)
	public Collection<ResponseMessage> handle(IllegalArgumentException ex) {
		log.error("IllegalArgumentException exception take place while trying to process application request",
				ex.getMessage());
		ResponseMessage message = ResponseMessage.builder()
				.message(ex.getMessage())
				.timestamp(ZonedDateTime.now()).build();
		return Collections.singletonList(message);
	}

	private Collection<ResponseMessage> buildMessages(BindingResult result) {
		Collection<ResponseMessage> messages = new ArrayList<>();

		var validationErrors = result.getFieldErrors();
		validationErrors.forEach(validationError -> {
			messages.add(ResponseMessage.builder()
					.message(validationError.getField() + " : " + validationError.getDefaultMessage())
					.timestamp(ZonedDateTime.now())
					.build());
		});
		var objectErrors = result.getGlobalErrors();
		if (isEmpty(objectErrors)) return messages;

		objectErrors.forEach(validationError -> {
			messages.add(ResponseMessage.builder()
					.message(validationError.getDefaultMessage())
					.timestamp(ZonedDateTime.now())
					.build());
		});

		return messages;
	}
}
