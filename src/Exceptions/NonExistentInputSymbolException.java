package Exceptions;
/**
 * Created by Camilo Tobar on 14/04/2018.
 */
@SuppressWarnings("serial")
public class NonExistentInputSymbolException extends Exception {

    public NonExistentInputSymbolException(String message){
        super(message);
    }
}
