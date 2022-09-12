package warma.desktop.media.tidy.lifecycle;

import io.quarkus.runtime.StartupEvent;
import lombok.extern.slf4j.Slf4j;
import warma.desktop.media.tidy.cache.Settings;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Author: sinar
 * 2022/9/4 12:13
 */
@Slf4j
@ApplicationScoped
public class Initializer {
    @Inject
    Settings settings;

    /**
     * 初始化应用程序
     *
     * @param ev 初始化事件
     */
    public void onStart(@Observes StartupEvent ev) {
        log.info("正在初始化缓存");
        settings.getAll();
        log.info("初始化完毕");
    }
}
