package tschuba.ez.booth.services;

/**
 * Exception thrown when requested data is not found.
 */
public class CheckoutException extends RuntimeException {
    public CheckoutException(String message) {
        super(message);
    }

    public CheckoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
