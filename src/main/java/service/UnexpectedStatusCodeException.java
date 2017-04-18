package service;

public class UnexpectedStatusCodeException extends Exception {

    private String message;

    UnexpectedStatusCodeException(String message) {
        this.message = message;
    }

    @Override
    public final String toString() {
        return message;
    }
}
