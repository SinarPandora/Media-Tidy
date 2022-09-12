package warma.desktop.media.tidy.api;

import lombok.extern.slf4j.Slf4j;
import warma.desktop.media.tidy.models.data.Tag;
import warma.desktop.media.tidy.service.TagService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Author: sinar
 * 2022/9/3 01:12
 */
@Slf4j
@Path("tag")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TagAPI {
    @Inject
    TagService service;

    /**
     * 获取当前配置项下全部标签
     *
     * @return 全部标签
     */
    @GET
    @Path("all")
    public List<Tag> getAll() {
        return service.getAll();
    }

    /**
     * 重新排序
     *
     * @param orderedIds 有顺序的 ID 列表
     */
    @PUT
    @Path("order")
    public Response reorder(List<Long> orderedIds) {
        service.reorder(orderedIds);
        return Response.ok().build();
    }

    /**
     * 添加/更新标签
     *
     * @param tags 标签列表
     */
    @POST
    @Path("")
    public Response persist(List<Tag> tags) {
        service.persist(tags);
        return Response.ok().build();
    }

    /**
     * 删除标签
     *
     * @param ids 标签 ID 列表
     */
    @DELETE
    @Path("")
    public Response remove(List<Long> ids) {
        service.remove(ids);
        return Response.ok().build();
    }
}
