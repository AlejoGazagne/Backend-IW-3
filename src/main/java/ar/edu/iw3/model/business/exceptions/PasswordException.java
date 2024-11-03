package ar.edu.iw3.model.business.exceptions;

import lombok.Builder;

public class PasswordException extends Exception {
   private static final long serialVersionUID = 1L;

	@Builder
	public PasswordException(String message, Throwable ex) {
		super(message, ex);
	}
	@Builder
	public PasswordException(String message) {
		super(message);
	}
	@Builder
	public PasswordException(Throwable ex) {
		super(ex.getMessage(), ex);
	} 
}
