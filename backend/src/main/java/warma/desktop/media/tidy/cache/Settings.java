package warma.desktop.media.tidy.cache;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import warma.desktop.media.tidy.mapper.SettingMapper;
import warma.desktop.media.tidy.models.data.Setting;
import warma.desktop.media.tidy.system.exception.ApiException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author: sinar
 * 2022/9/1 20:02
 */
@Slf4j
@ApplicationScoped
@SuppressWarnings("CdiInjectionPointsInspection")
public class Settings {
    @Inject
    SettingMapper settingRepo;

    /**
     * 获取配置值
     *
     * @param key 配置键
     * @return 配置值
     */
    public String get(String key) {
        val setting = this.getAll().get(key);
        if (setting == null) throw new ApiException("配置项不存在！");
        return setting.getValue();
    }

    /**
     * 获取数字类型的配置值
     *
     * @param key 配置键
     * @return 配置值
     */
    public Long getNumber(String key) {
        try {
            return Long.parseLong(this.get(key));
        } catch (NumberFormatException e) {
            throw new ApiException("配置项格式不正确");
        }
    }

    /**
     * 获取全部配置
     *
     * @return 全部配置
     */
    @CacheResult(cacheName = "settings")
    public Map<String, Setting> getAll() {
        val settings = settingRepo
                .selectList(null).stream()
                .collect(Collectors.toMap(Setting::getKey, Function.identity()));
        log.info("系统配置读取完毕");
        return settings;
    }

    /**
     * 更新配置
     *
     * @param key   配置键
     * @param value 配置值
     * @return 更新后的配置
     */
    @CacheInvalidate(cacheName = "settings")
    public Setting update(String key, String value) {
        val setting = this.getAll().get(key);
        if (Setting.Type.NUMBER.equals(setting.getValueType())) {
            try {
                Double.parseDouble(value);
            } catch (NumberFormatException e) {
                log.warn("配置格式不正确：{} -> {}", key, value);
                throw new ApiException("值格式不正确");
            }
        }
        setting.setValue(value);
        settingRepo.updateById(setting);
        return setting;
    }

    public static final class Key {
        public static final String CURRENT_PROFILE = "CURRENT_PROFILE";
    }
}
