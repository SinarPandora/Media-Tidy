package warma.desktop.media.tidy.api;

import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.MultipartForm;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;
import warma.desktop.media.tidy.models.ExportProfileRequest;
import warma.desktop.media.tidy.models.FileUploadRequest;
import warma.desktop.media.tidy.models.ImportProfileRequest;
import warma.desktop.media.tidy.models.ImportableProfile;
import warma.desktop.media.tidy.models.data.Profile;
import warma.desktop.media.tidy.service.ProfileService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Author: sinar
 * 2022/9/1 00:52
 */
@Slf4j
@Path("/profile")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProfileAPI {
    @Inject
    ProfileService service;

    /**
     * 获取全部配置项
     *
     * @return 全部配置项
     */
    @GET
    @Path("all")
    public List<Profile> getAll() {
        return service.getAll();
    }

    /**
     * 切换到指定配置项
     *
     * @param profileId 配置项 ID
     */
    @GET
    @Path("switch/{profileId}")
    public Response switchTo(@RestPath long profileId) {
        service.switchTo(profileId);
        return Response.ok().build();
    }

    /**
     * 更新/保存配置项
     *
     * @param profile 配置项
     * @return 更新后结果
     */
    @POST
    @Path("")
    public Profile persist(@Valid Profile profile) {
        return service.persist(profile);
    }

    /**
     * 调整配置项激活状态
     *
     * @param status    状态
     * @param profileId 配置项 ID
     */
    @PUT
    @Path("active")
    @Valid
    public Response active(@RestQuery @NotNull Boolean status, @RestQuery @NotNull Long profileId) {
        service.active(profileId, status);
        return Response.ok().build();
    }

    /**
     * 直接删除配置项
     *
     * @param profileId 配置项 ID
     */
    @DELETE
    @Path("{profileId}")
    public Response delete(@RestPath long profileId) {
        service.delete(profileId);
        return Response.ok().build();
    }

    /**
     * 导出配置
     *
     * @param request 配置导出请求
     */
    @POST
    @Path("export")
    @Valid
    public Response export(ExportProfileRequest request) {
        service.exportOut(request);
        return Response.ok().build();
    }

    /**
     * 导入配置
     *
     * @param request 导入请求
     */
    @POST
    @Path("import")
    public Response importIn(ImportProfileRequest request) {
        service.importIn(request);
        return Response.ok().build();
    }
}
