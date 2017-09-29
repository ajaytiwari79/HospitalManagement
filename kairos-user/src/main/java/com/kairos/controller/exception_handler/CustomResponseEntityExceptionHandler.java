package com.kairos.controller.exception_handler;

import com.kairos.custom_exception.*;
import com.kairos.response.dto.web.ResponseEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@ControllerAdvice
@Order(1)
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler  {

	private static final Logger logger = LoggerFactory.getLogger(CustomResponseEntityExceptionHandler.class);

	public CustomResponseEntityExceptionHandler() {
		super();
	}

	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
		List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
		List<FieldErrorDTO> errors = new ArrayList<FieldErrorDTO>(fieldErrors.size() + globalErrors.size());
		//  String error;
		for (FieldError fieldError : fieldErrors) {
			FieldErrorDTO error=new FieldErrorDTO(fieldError.getField(),fieldError.getDefaultMessage());
			errors.add(error);
		}
		for (ObjectError objectError : globalErrors) {
			FieldErrorDTO error=new FieldErrorDTO(objectError.getObjectName(),objectError.getDefaultMessage());
			errors.add(error);
		}

		ResponseEnvelope errorMessage = new ResponseEnvelope();
		errorMessage.setErrors(errors);
		errorMessage.setSuccess(false);

		return new ResponseEntity<Object>(errorMessage, headers, status);

	}


	@Override
	public ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		String unsupported = "Unsupported content type: " + ex.getContentType();
		String supported = "Supported content types: " + MediaType.toString(ex.getSupportedMediaTypes());
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return new ResponseEntity<Object>(errorMessage, headers, status);

	}

	@Override
	public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return new ResponseEntity<Object>(errorMessage, headers, status);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setPath(ex.getRequestURL());
		errorMessage.setMessage(ex.getMessage());
		return new ResponseEntity<Object>(errorMessage,HttpStatus.NOT_FOUND);
	}

	/**
	 * Customize the response for NoSuchRequestHandlingMethodException.
	 * <p>This method logs a warning and delegates to {@link #handleExceptionInternal}.
	 * @param ex the exception
	 * @param headers the headers to be written to the response
	 * @param status the selected response status
	 * @param request the current request
	 * @return a {@code ResponseEntity} instance
	 */
	@Override
	protected ResponseEntity<Object> handleNoSuchRequestHandlingMethod(NoSuchRequestHandlingMethodException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		pageNotFoundLogger.warn(ex.getMessage());

		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return handleExceptionInternal(ex, errorMessage, headers, status, request);
	}

	/**
	 * Customize the response for HttpRequestMethodNotSupportedException.
	 * <p>This method logs a warning, sets the "Allow" header, and delegates to
	 * {@link #handleExceptionInternal}.
	 * @param ex the exception
	 * @param headers the headers to be written to the response
	 * @param status the selected response status
	 * @param request the current request
	 * @return a {@code ResponseEntity} instance
	 */
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		pageNotFoundLogger.warn(ex.getMessage());

		Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
		if (!supportedMethods.isEmpty()) {
			headers.setAllow(supportedMethods);
		}
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return handleExceptionInternal(ex, errorMessage, headers, status, request);
	}

	/**
	 * Customize the response for HttpMediaTypeNotAcceptableException.
	 * <p>This method delegates to {@link #handleExceptionInternal}.
	 * @param ex the exception
	 * @param headers the headers to be written to the response
	 * @param status the selected response status
	 * @param request the current request
	 * @return a {@code ResponseEntity} instance
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return handleExceptionInternal(ex, errorMessage, headers, status, request);
	}

	/**
	 * Customize the response for MissingPathVariableException.
	 * <p>This method delegates to {@link #handleExceptionInternal}.
	 * @param ex the exception
	 * @param headers the headers to be written to the response
	 * @param status the selected response status
	 * @param request the current request
	 * @return a {@code ResponseEntity} instance
	 * @since 4.2
	 */
	@Override
	protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return handleExceptionInternal(ex, errorMessage, headers, status, request);
	}

	/**
	 * Customize the response for MissingServletRequestParameterException.
	 * <p>This method delegates to {@link #handleExceptionInternal}.
	 * @param ex the exception
	 * @param headers the headers to be written to the response
	 * @param status the selected response status
	 * @param request the current request
	 * @return a {@code ResponseEntity} instance
	 */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return handleExceptionInternal(ex, errorMessage, headers, status, request);
	}

	/**
	 * Customize the response for ServletRequestBindingException.
	 * <p>This method delegates to {@link #handleExceptionInternal}.
	 * @param ex the exception
	 * @param headers the headers to be written to the response
	 * @param status the selected response status
	 * @param request the current request
	 * @return a {@code ResponseEntity} instance
	 */
	@Override
	protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return handleExceptionInternal(ex, errorMessage, headers, status, request);
	}

	/**
	 * Customize the response for ConversionNotSupportedException.
	 * <p>This method delegates to {@link #handleExceptionInternal}.
	 * @param ex the exception
	 * @param headers the headers to be written to the response
	 * @param status the selected response status
	 * @param request the current request
	 * @return a {@code ResponseEntity} instance
	 */
	
	@Override
	protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return handleExceptionInternal(ex, errorMessage, headers, status, request);
	}


	/**
	 * Customize the response for HttpMessageNotWritableException.
	 * <p>This method delegates to {@link #handleExceptionInternal}.
	 * @param ex the exception
	 * @param headers the headers to be written to the response
	 * @param status the selected response status
	 * @param request the current request
	 * @return a {@code ResponseEntity} instance
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return handleExceptionInternal(ex, errorMessage, headers, status, request);
	}
	/**
	 * Customize the response for MissingServletRequestPartException.
	 * <p>This method delegates to {@link #handleExceptionInternal}.
	 * @param ex the exception
	 * @param headers the headers to be written to the response
	 * @param status the selected response status
	 * @param request the current request
	 * @return a {@code ResponseEntity} instance
	 */
	@Override
	protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return handleExceptionInternal(ex, errorMessage, headers, status, request);
	}

	/**
	 * Customize the response for BindException.
	 * <p>This method delegates to {@link #handleExceptionInternal}.
	 * @param ex the exception
	 * @param headers the headers to be written to the response
	 * @param status the selected response status
	 * @param request the current request
	 * @return a {@code ResponseEntity} instance
	 */
	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return handleExceptionInternal(ex, errorMessage, headers, status, request);
	}	


	// API

	// 400

	@ExceptionHandler({DataIntegrityViolationException.class})
	public ResponseEntity<Object> handleBadRequest(final Exception ex, final WebRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	// 403
	@ExceptionHandler({ AccessDeniedException.class,InvalidRequestException.class })
	public ResponseEntity<Object> handleAccessDeniedException(final Exception ex, final WebRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return new ResponseEntity<Object>(errorMessage, new HttpHeaders(), HttpStatus.FORBIDDEN);
	}


	// 409

	@ExceptionHandler({DuplicateDataException.class, DataAccessException.class,InvalidDataAccessApiUsageException.class })
	protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
		logger.error("error in user service ",ex);
		final String bodyOfResponse = "This should be application specific";
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	// 500

	@ExceptionHandler({ NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class,Exception.class,MessagingException.class })
	public ResponseEntity<Object> handleInternal(final Exception ex, final WebRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setMessage(ex.getMessage());
		return handleExceptionInternal(ex, errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}


	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler({DataNotFoundByIdException.class,AddressNotVerifiedByTomTom.class,
			ZipCodeNotFound.class,CitizenNotFoundException.class})
	@ResponseBody
	public ResponseEnvelope handleNotFound(RuntimeException ex,HttpServletRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setPath(request.getRequestURL().toString());
		errorMessage.setMessage(ex.getMessage());
		return  errorMessage;

	}

	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(value=ActionNotPermittedException.class)
	@ResponseBody
	public ResponseEnvelope actionNotPermittedExceptionHandler(ActionNotPermittedException ex,HttpServletRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setPath(request.getRequestURL().toString());
		errorMessage.setMessage(ex.getMessage());
		return  errorMessage;

	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(value = TaskDemandException.class)
	@ResponseBody
	public ResponseEnvelope taskDemandExceptionHandler(TaskDemandException ex,HttpServletRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setPath(request.getRequestURL().toString());
		errorMessage.setMessage(ex.getMessage());
		return  errorMessage;

	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(value = DataNotMatchedException.class)
	@ResponseBody
	public ResponseEnvelope dataNotMatchedExceptionHandler(RuntimeException ex,HttpServletRequest request) {
		logger.error("error in user service ",ex);
		ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setPath(request.getRequestURL().toString());
		errorMessage.setMessage(ex.getMessage());
		return  errorMessage;

	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(value = FlsCredentialException.class)
	@ResponseBody
	public ResponseEnvelope flsCredentialExceptionHandler(FlsCredentialException ex,HttpServletRequest request) {
		logger.error("error in user service ",ex);
		 ResponseEnvelope errorMessage=new ResponseEnvelope();
		errorMessage.setSuccess(false);
		errorMessage.setPath(request.getRequestURL().toString());
		errorMessage.setMessage(ex.getMessage());
		return  errorMessage;

	}

}
