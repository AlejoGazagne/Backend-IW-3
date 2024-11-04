package ar.edu.iw3.model.business.exceptions;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TruckloadException extends Exception {
	private static final long serialVersionUID = 1L;

	@Builder
	public TruckloadException(String message, Throwable ex) {
		super(message, ex);
	}
	@Builder
	public TruckloadException(String message) {
		super(message);
	}
	@Builder
	public TruckloadException(Throwable ex) {
		super(ex.getMessage(), ex);
	}
}