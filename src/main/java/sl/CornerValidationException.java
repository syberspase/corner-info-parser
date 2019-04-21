package sl;

/**
 * General Exception class for corner information validation.
 * @author Shep Liu(syberspase@gmail.com)
 *
 */
public class CornerValidationException extends Exception {
    private static final long serialVersionUID = 6403680596979998323L;

    public CornerValidationException(Throwable cause) {
        super(cause);
    }
}
