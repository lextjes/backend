package alexandre.blended.exception;

public class AlreadyExistsException extends RuntimeException {

    public AlreadyExistsException(String type, String attribute, String value){
        super(String.format("An %s with %s = %s already exists.\n", type, attribute, value));
    }
}