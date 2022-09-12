package warma.desktop.media.tidy.system.exception;

import warma.desktop.media.tidy.models.ErrorMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Author: sinar
 * 2022/9/8 21:59
 */
@Provider
public class ErrorHandler implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        exception.printStackTrace();
        if (exception instanceof ApiException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage(exception.getMessage(), true))
                    .build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorMessage(exception.getMessage(), false))
                    .build();
        }
    }
}
