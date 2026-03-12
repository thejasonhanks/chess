package service;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String msg) {
        super(msg);
    }
    public BadRequestException() {
        super("Bad request");
    }
}
