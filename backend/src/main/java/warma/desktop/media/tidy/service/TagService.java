package warma.desktop.media.tidy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.val;
import warma.desktop.media.tidy.cache.Settings;
import warma.desktop.media.tidy.mapper.TagMapper;
import warma.desktop.media.tidy.models.data.Source;
import warma.desktop.media.tidy.models.data.Tag;
import warma.desktop.media.tidy.system.exception.ApiException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Validator;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: sinar
 * 2022/8/31 21:19
 */
@ApplicationScoped
@SuppressWarnings("CdiInjectionPointsInspection")
public class TagService {
    @Inject
    Settings settings;

    @Inject
    TagMapper tagRepo;

    @Inject
    SourceService sourceService;

    @Inject
    IndexService indexService;

    @Inject
    Validator validator;

    /**
     * 获取当前配置项下全部标签
     *
     * @return 当前配置项下全部标签
     */
    public List<Tag> getAll() {
        val profileId = settings.getNumber(Settings.Key.CURRENT_PROFILE);
        return tagRepo.selectList(new QueryWrapper<Tag>()
                .eq("profile_id", profileId)
                .orderByAsc("menu_order")
                .orderByDesc("id"));
    }

    /**
     * 重新排序
     *
     * @param orderedIds 有序的 ID 列表
     */
    @Transactional
    public void reorder(List<Long> orderedIds) {
        if (!orderedIds.isEmpty()) {
            for (int idx = 0; idx < orderedIds.size(); idx++) {
                // Transaction issue, only update in loop using SQL is ok
                tagRepo.updateOrder(orderedIds.get(idx), idx);
            }
        }
    }

    /**
     * 添加/更新标签
     *
     * @param tags 标签列表
     */
    @Transactional
    public void persist(List<Tag> tags) {
        val profileId = settings.getNumber(Settings.Key.CURRENT_PROFILE);
        this.validateBaseInfo(tags, profileId);
        this.validatePath(tags);
        val group = tags.stream().collect(Collectors.groupingBy(it -> it.getId() == null));
        List<Tag> needAdd = group.get(Boolean.TRUE) == null ? Collections.emptyList() : group.get(Boolean.TRUE);
        List<Tag> needUpdate = group.get(Boolean.FALSE) == null ? Collections.emptyList() : group.get(Boolean.FALSE);
        this.validateName(needAdd, needUpdate);
        if (!needAdd.isEmpty()) tagRepo.batchInsert(needAdd);
        if (!needUpdate.isEmpty()) tagRepo.batchUpdate(needUpdate);
    }

    /**
     * 校验基本信息
     *
     * @param tags      待更新的标签
     * @param profileId 配置项 ID
     */
    private void validateBaseInfo(Collection<Tag> tags, Long profileId) {
        for (Tag tag : tags) {
            val errors = validator.validate(tag);
            if (!errors.isEmpty()) {
                val message = new ArrayList<>(errors).get(0).getMessage();
                throw new ApiException("更新失败：" + message);
            }
            val tagDir = new File(tag.getFolder());
            if (tagDir.isFile()) {
                throw new ApiException("更新失败：" + tag.getFolder() + "不是文件夹");
            } else if (!tagDir.exists() && !tagDir.mkdir()) {
                throw new ApiException("更新失败：无法在" + tag.getFolder() + "下创建文件夹");
            }
            tag.setProfileId(profileId);
        }
    }

    /**
     * 校验名称
     *
     * @param needAdd    待添加的标签
     * @param needUpdate 待更新的标签
     */
    private void validateName(Collection<Tag> needAdd, Collection<Tag> needUpdate) {
        val namePair = this.getAll().stream().collect(Collectors.toMap(Tag::getId, Tag::getName));
        for (Tag tag : needUpdate) {
            if (!namePair.containsKey(tag.getId())) {
                throw new ApiException("更新的标签不存在：" + tag.getId() + " - " + tag.getName());
            }
            namePair.put(tag.getId(), tag.getName());
        }
        val existNames = namePair.values();
        val newNames = needAdd.stream().map(Tag::getName).collect(Collectors.toSet());
        val total = existNames.size() + newNames.size();
        newNames.addAll(existNames);
        if (total > newNames.size()) {
            throw new ApiException("更新失败，存在同名标签");
        }
    }

    /**
     * 校验路径
     *
     * @param tags 待更新的标签
     */
    private void validatePath(Collection<Tag> tags) {
        val tagDirs = tags.stream().map(Tag::getFolder).collect(Collectors.toSet());
        val sourceDirs = sourceService.getAll().stream().map(Source::getFilepath).collect(Collectors.toSet());
        for (String tagDir : tagDirs) {
            if (sourceDirs.contains(tagDir)) {
                throw new ApiException("更新失败：标签路径不能与源相同：" + tagDir);
            }
        }
    }

    /**
     * 删除标签
     *
     * @param ids 标签 ID 列表
     */
    @Transactional
    public void remove(List<Long> ids) {
        indexService.cleanByTag(ids);
        tagRepo.deleteBatchIds(ids);
    }
}
