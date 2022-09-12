package warma.desktop.media.tidy.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Author: sinar
 * 2022/9/2 00:27
 */
@Path("system")
@Produces(MediaType.TEXT_PLAIN)
public class SystemAPI {
    /**
     * 心跳 API
     *
     * @return true
     */
    @GET
    @Path("heartbeat")
    public boolean heartbeat() {
        return true;
    }
}
