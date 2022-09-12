package warma.desktop.media.tidy.api;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;
import warma.desktop.media.tidy.cache.Settings;
import warma.desktop.media.tidy.models.ErrorMessage;
import warma.desktop.media.tidy.models.data.Setting;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: sinar
 * 2022/8/31 23:34
 */
@Slf4j
@Path("/setting")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SettingAPI {
    @Inject
    Settings settings;

    /**
     * 获取全部可见配置
     *
     * @return 全部可见配置
     */
    @GET
    @Path("all")
    public List<Setting> getAll() {
        return settings.getAll().values().stream()
                .filter(Setting::getVisible)
                .collect(Collectors.toList());
    }

    /**
     * 获取配置内容
     *
     * @param key 配置键
     * @return 配置对象
     */
    @GET
    @Path("{key}")
    public Response get(@RestPath String key) {
        val setting = settings.getAll().get(key);
        return setting == null ? Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorMessage("配置项不存在", true))
                .build()
                : Response.ok(setting).build();
    }

    /**
     * 更新配置内容
     *
     * @param key   配置键
     * @param value 配置值
     * @return 更新结果
     */
    @PUT
    @Path("{key}")
    public Response persist(@RestPath String key, @RestQuery String value) {
        return Response.ok(settings.update(key, value)).build();
    }
}
