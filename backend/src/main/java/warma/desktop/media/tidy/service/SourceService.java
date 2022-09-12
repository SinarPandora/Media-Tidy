package warma.desktop.media.tidy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.val;
import warma.desktop.media.tidy.cache.Settings;
import warma.desktop.media.tidy.mapper.SourceMapper;
import warma.desktop.media.tidy.models.data.Source;
import warma.desktop.media.tidy.system.exception.ApiException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: sinar
 * 2022/8/31 21:19
 */
@ApplicationScoped
@SuppressWarnings("CdiInjectionPointsInspection")
public class SourceService {
    @Inject
    SourceMapper sourceRepo;

    @Inject
    IndexService indexService;

    @Inject
    Settings settings;

    /**
     * 获取全部源文件夹
     *
     * @return 源文件夹列表
     */
    public List<Source> getAll() {
        val profileId = settings.getNumber(Settings.Key.CURRENT_PROFILE);
        return sourceRepo.selectList(new QueryWrapper<Source>()
                .eq("profile_id", profileId)
                .orderByAsc("id"));
    }

    /**
     * 添加源（批量）
     *
     * @param sources 源对象
     */
    @Transactional
    public void add(List<Source> sources) {
        val profileId = settings.getNumber(Settings.Key.CURRENT_PROFILE);
        val allSource = sourceRepo.selectList(new QueryWrapper<Source>()
                        .eq("profile_id", profileId))
                .stream().map(Source::getFilepath)
                .collect(Collectors.toList());
        for (Source source : sources) {
            if (!allSource.contains(source.getFilepath())) {
                val sourceDir = new File(source.getFilepath());
                if (!sourceDir.exists()) {
                    throw new ApiException("文件夹不存在");
                } else if (!sourceDir.isDirectory()) {
                    throw new ApiException("源不是文件夹");
                }
                source.setProfileId(profileId);
                sourceRepo.insert(source);
                indexService.load(source.getFilepath(), source.getId(), null);
                allSource.add(source.getFilepath());
            } else {
                throw new ApiException("同名源已存在：" + source.getFilepath());
            }
        }
    }

    /**
     * 删除源（批量）
     *
     * @param ids 源 ID 列表
     */
    @Transactional
    public void remove(List<Long> ids) {
        indexService.cleanBySource(ids);
        sourceRepo.deleteBatchIds(ids);
    }

    /**
     * 更新别名
     *
     * @param sourceId 源 ID
     * @param name     别名
     */
    @Transactional
    public void alias(long sourceId, String name) {
        val source = sourceRepo.selectById(sourceId);
        if (source == null) throw new ApiException("指定源不存在");
        source.setAlias(name.trim());
        this.sourceRepo.updateById(source);
    }
}
