package presentation.General;

public class UserCancelledException extends RuntimeException {
    
    public UserCancelledException() {
        super("Operation cancelled by user");
    }
    
    public UserCancelledException(String message) {
        super(message);
    }
}

