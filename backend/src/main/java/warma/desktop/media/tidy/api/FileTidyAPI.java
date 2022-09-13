package warma.desktop.media.tidy.api;

import io.quarkus.vertx.web.Route;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.FileSystemAccess;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.mutiny.core.file.AsyncFile;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestPath;
import warma.desktop.media.tidy.models.TidyRequest;
import warma.desktop.media.tidy.service.FileTidyService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Author: sinar
 * 2022/9/2 00:22
 */
@Slf4j
@Path("tidy")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FileTidyAPI {
    @Inject
    FileTidyService service;

    /**
     * 文件是否存在
     *
     * @param fileId 文件 ID
     * @return 是否存在
     */
    @GET
    @Path("exist/{fileId}")
    public boolean exist(long fileId) {
        return service.exist(fileId);
    }

    /**
     * 预览本地文件
     *
     */
    @SuppressWarnings("unused")
    @Route(path = "tidy/preview/*", methods = Route.HttpMethod.GET)
    public void preview(RoutingContext rc) {
        StaticHandler.create(FileSystemAccess.ROOT, "").handle(rc);
    }

    /**
     * 整理文件
     *
     * @param request 整理文件请求
     */
    @PUT
    @Path("")
    public Response tidy(TidyRequest request) {
        service.tidy(request);
        return Response.ok().build();
    }
}
