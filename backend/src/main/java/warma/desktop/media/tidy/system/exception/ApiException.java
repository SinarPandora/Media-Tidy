package warma.desktop.media.tidy.system.exception;

import java.io.Serial;
import java.io.Serializable;

/**
 * Author: sinar
 * 2022/9/8 22:00
 */
public class ApiException extends RuntimeException implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public ApiException() {
        super();
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    protected ApiException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
