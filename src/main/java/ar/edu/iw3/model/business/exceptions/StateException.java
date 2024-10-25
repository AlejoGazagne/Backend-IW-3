package ar.edu.iw3.model.business.exceptions;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StateException extends Exception {
	private static final long serialVersionUID = 1L;

	@Builder
	public StateException(String message, Throwable ex) {
		super(message, ex);
	}
	@Builder
	public StateException(String message) {
		super(message);
	}
	@Builder
	public StateException(Throwable ex) {
		super(ex.getMessage(), ex);
	}
}