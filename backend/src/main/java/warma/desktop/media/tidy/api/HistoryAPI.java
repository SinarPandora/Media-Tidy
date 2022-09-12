package warma.desktop.media.tidy.api;

import org.jboss.resteasy.reactive.RestPath;
import warma.desktop.media.tidy.models.data.TidyHistory;
import warma.desktop.media.tidy.service.HistoryService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Author: sinar
 * 2022/9/4 12:56
 */
@Path("history")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HistoryAPI {
    @Inject
    HistoryService service;

    /**
     * 获取全部历史记录
     *
     * @return 全部历史记录
     */
    @GET
    @Path("all")
    public List<TidyHistory> getAll() {
        return service.getAll();
    }

    /**
     * 回滚历史
     *
     * @param historyId 历史 ID
     */
    @PUT
    @Path("{historyId}/revert")
    public Response revert(@RestPath long historyId) {
        service.revert(historyId);
        return Response.ok().build();
    }
}
