package service;

public class UnexpectedStatusCodeException extends Exception {

    private static final long serialVersionUID = 1L;
    private String message;

    UnexpectedStatusCodeException(String message) {
        this.message = message;
    }

    @Override
    public final String toString() {
        return message;
    }
}
