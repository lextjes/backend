package alexandre.blended.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String type, String attribute, String value){
        super(String.format("No %s with %s = %s could be found.\n", type, attribute, value));
    }
}
