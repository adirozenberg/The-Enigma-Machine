package exceptions;

public class InvalidInputException extends Throwable {
    public InvalidInputException(String errorMessage) {
        super(errorMessage);
    }
}
