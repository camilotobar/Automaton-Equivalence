package Exceptions;
/**
 * Created by Camilo Tobar on 14/04/2018.
 */
@SuppressWarnings("serial")
public class NonExistentStateException extends Exception {

    public NonExistentStateException(String message){
        super(message);
    }
}
