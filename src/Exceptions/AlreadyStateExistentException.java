package Exceptions;
/**
 * Created by Camilo Tobar on 14/04/2018.
 */
@SuppressWarnings("serial")
public class AlreadyStateExistentException extends Exception{

    public AlreadyStateExistentException(String message){
        super(message);
    }
}
