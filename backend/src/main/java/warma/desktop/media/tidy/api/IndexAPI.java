package warma.desktop.media.tidy.api;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestPath;
import warma.desktop.media.tidy.models.FuzzySearchRequest;
import warma.desktop.media.tidy.models.data.MediaFile;
import warma.desktop.media.tidy.service.IndexService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Author: sinar
 * 2022/9/3 20:24
 */
@Slf4j
@Path("index")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IndexAPI {
    @Inject
    IndexService service;

    /**
     * 手动重构索引
     */
    @POST()
    @Path("rebuild")
    public Response rebuild() {
        service.build();
        return Response.ok().build();
    }

    /**
     * 获取全部索引
     *
     * @return 未整理文件
     */
    @GET
    @Path("untidy")
    public List<MediaFile> getUntidy() {
        return service.getUntidy();
    }

    /**
     * 获取标签下的文件
     *
     * @param tagId 标签 ID
     * @return 标签下的文件
     */
    @GET
    @Path("tag/{tagId}")
    public List<MediaFile> getByTag(@RestPath long tagId) {
        return service.getByTag(tagId);
    }

    /**
     * 获取指定源下的文件
     *
     * @param sourceId 源 ID
     * @return 源下的文件
     */
    @GET
    @Path("source/{sourceId}")
    public List<MediaFile> getBySource(@RestPath long sourceId) {
        return service.getBySource(sourceId);
    }

    /**
     * 手动删除全部索引
     */
    @DELETE
    @Path("all")
    public Response cleanUp() {
        service.cleanAll();
        return Response.ok().build();
    }

    /**
     * 模糊搜索
     *
     * @param request 搜索请求
     * @return 搜索结果
     */
    @POST
    @Path("search")
    public List<MediaFile> search(FuzzySearchRequest request) {
        return service.search(request);
    }
}
