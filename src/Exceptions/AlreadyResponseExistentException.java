package Exceptions;
/**
 * Created by Camilo Tobar on 14/04/2018.
 */
@SuppressWarnings("serial")
public class AlreadyResponseExistentException extends Exception {

    public AlreadyResponseExistentException(String message) {
        super(message);
    }
}
