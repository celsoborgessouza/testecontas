package br.com.contas.utils.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.contas.service.exception.ServiceException;

public class Response {
	

	public static ResponseEntity<?> exception(Exception e) {
		Payload<?> response = Payload.Error("Sistem indisponível",e);
		return  new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public static ResponseEntity<?> service(ServiceException e) {
		Payload<?> response = Payload.Error(e.getMessage(),e);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public static <T> ResponseEntity<?> ok(List<T> lista) {
		Payload<?> response = Payload.OK(lista);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public static <T> ResponseEntity<?> ok(String msg, List<T> lista) {
		Payload<?> response = Payload.OK(msg, lista);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	public static <T>  ResponseEntity<T> ok(T object) {
		T response = (T) Payload.OK(object);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	public static <T> ResponseEntity<T> ok(String msg, T object) {
		T response = (T) Payload.OK(msg, object);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	public static <T> ResponseEntity<T> service(String msg, Throwable e) {
		T response = (T) Payload.Error(msg, e);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@SuppressWarnings("unchecked")
	public static <T> ResponseEntity<T> exception(String msg, Throwable e) {
		T response = (T) Payload.Error(msg, e);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
