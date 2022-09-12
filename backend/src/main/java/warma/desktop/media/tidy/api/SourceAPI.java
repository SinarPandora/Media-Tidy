package warma.desktop.media.tidy.api;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;
import warma.desktop.media.tidy.models.data.Source;
import warma.desktop.media.tidy.service.SourceService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Author: sinar
 * 2022/9/2 00:08
 */
@Slf4j
@Path("source")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SourceAPI {
    @Inject
    SourceService service;

    /**
     * 获取当前配置项下全部源
     *
     * @return 源列表
     */
    @GET
    @Path("all")
    public List<Source> getAll() {
        return service.getAll();
    }

    /**
     * 添加源
     *
     * @param sources 源列表
     */
    @POST
    @Path("")
    public Response add(List<Source> sources) {
        service.add(sources);
        return Response.ok().build();
    }

    /**
     * 更新别名
     *
     * @param sourceId 源 ID
     * @param name     别名
     */
    @PUT
    @Path("alias/{sourceId}")
    public Response alias(@RestPath long sourceId, @RestQuery String name) {
        service.alias(sourceId, name);
        return Response.ok().build();
    }

    /**
     * 删除源
     *
     * @param ids 源 ID 列表
     */
    @DELETE
    @Path("")
    public Response remove(List<Long> ids) {
        service.remove(ids);
        return Response.ok().build();
    }
}
