package client.exceptions;
/**
 * The StopServerException class is a custom exception that is thrown when there is a request to stop the server.
 * It extends the Exception class and provides a constructor to set the exception message.
 */

public class StopServerException extends Exception{
    public StopServerException(String message){
        super(message);
    }
}
