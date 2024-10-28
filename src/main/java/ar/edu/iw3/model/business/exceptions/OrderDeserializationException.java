package ar.edu.iw3.model.business.exceptions;

public class OrderDeserializationException extends RuntimeException {
    public OrderDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }
    public OrderDeserializationException(String message) {
        super(message);
    }
    public OrderDeserializationException(Throwable cause) {
        super(cause);
    }
}