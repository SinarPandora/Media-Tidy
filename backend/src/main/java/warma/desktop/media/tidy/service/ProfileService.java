package warma.desktop.media.tidy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import warma.desktop.media.tidy.cache.Settings;
import warma.desktop.media.tidy.mapper.ProfileMapper;
import warma.desktop.media.tidy.mapper.SourceMapper;
import warma.desktop.media.tidy.mapper.TagMapper;
import warma.desktop.media.tidy.mapper.TidyHistoryMapper;
import warma.desktop.media.tidy.models.ExportProfileRequest;
import warma.desktop.media.tidy.models.ImportProfileRequest;
import warma.desktop.media.tidy.models.ImportableProfile;
import warma.desktop.media.tidy.models.data.Profile;
import warma.desktop.media.tidy.models.data.Source;
import warma.desktop.media.tidy.models.data.Tag;
import warma.desktop.media.tidy.system.exception.ApiException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Author: sinar
 * 2022/9/1 01:03
 */
@Slf4j
@ApplicationScoped
@SuppressWarnings("CdiInjectionPointsInspection")
public class ProfileService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
    @Inject
    ProfileMapper profileRepo;

    @Inject
    SourceMapper sourceRepo;

    @Inject
    TagMapper tagRepo;

    @Inject
    TidyHistoryMapper tidyHistoryRepo;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    IndexService indexService;

    @Inject
    Settings settings;

    /**
     * 获取全部配置项
     *
     * @return 全部配置项
     */
    public List<Profile> getAll() {
        return profileRepo.selectList(null);
    }

    /**
     * 切换到对应配置项
     *
     * @param profileId 配置项 ID
     */
    @Transactional
    public void switchTo(long profileId) {
        val profileExist = profileRepo.exists(new QueryWrapper<Profile>()
                .eq("id", profileId));
        if (!profileExist) throw new ApiException("配置项不存在：ID " + profileId);
        settings.update(Settings.Key.CURRENT_PROFILE, String.valueOf(profileId));
        indexService.build();
    }

    /**
     * 保存配置项
     * *当ID不存在时添加
     * *ID存在时更新
     *
     * @param profile 配置项
     * @return 更新后的配置项
     */
    @Transactional
    public Profile persist(Profile profile) {
        if (profile.getId() == null) {
            profileRepo.insert(profile);
        } else if (profileRepo.updateById(profile) != 1) {
            throw new ApiException("更新失败，配置项不存在！");
        }
        return profile;
    }

    /**
     * 设置配置项激活状态
     *
     * @param profileId 配置项 ID
     * @param active    激活状态
     */
    @Transactional
    public void active(long profileId, boolean active) {
        val effect = profileRepo.update(null, new UpdateWrapper<Profile>()
                .eq("id", profileId)
                .set("active", active));
        if (effect != 1) {
            log.error("更新配置项失败，id：{}", profileId);
            throw new ApiException("更新配置项失败");
        }
    }

    /**
     * 导入配置
     *
     * @param req 文件上传请求
     */
    public void importIn(ImportProfileRequest req) {
        try {
            val content = Files.readString(Paths.get(req.getPath()));
            val iProfile = objectMapper.readValue(content, ImportableProfile.class);
            val profileExist = iProfile.getProfile().getId() != null && profileRepo.exists(new QueryWrapper<Profile>()
                    .eq("id", iProfile.getProfile().getId()));
            if (profileExist) replaceProfile(iProfile);
            else importAsNewProfile(iProfile);
        } catch (IOException e) {
            log.error("导入失败", e);
            throw new ApiException("导入失败，请重新上传");
        }
    }

    /**
     * 替换现有配置
     *
     * @param iProfile 导入的配置
     */
    private void replaceProfile(ImportableProfile iProfile) {
        val profile = iProfile.getProfile();
        val profileId = profile.getId();
        this.deleteRelated(profileId);
        // 导入
        profileRepo.updateById(profile);
        importSourceAndTag(iProfile, profileId);
    }

    /**
     * 导入为新配置
     *
     * @param iProfile 导入的配置
     */
    private void importAsNewProfile(ImportableProfile iProfile) {
        profileRepo.insert(iProfile.getProfile());
        val profileId = iProfile.getProfile().getId();
        importSourceAndTag(iProfile, profileId);
    }

    /**
     * 导入源和标签
     *
     * @param iProfile  导入的配置
     * @param profileId 配置 ID
     */
    private void importSourceAndTag(ImportableProfile iProfile, long profileId) {
        if (iProfile.getSources() != null && !iProfile.getSources().isEmpty()) {
            iProfile.getSources().forEach(it -> it.setProfileId(profileId));
            sourceRepo.batchInsert(iProfile.getSources());
        }
        if (iProfile.getTags() != null && !iProfile.getTags().isEmpty()) {
            iProfile.getTags().forEach(it -> it.setProfileId(profileId));
            tagRepo.batchInsert(iProfile.getTags());
        }
    }

    /**
     * 导出配置
     *
     * @param request 配置导出请求
     */
    public void exportOut(ExportProfileRequest request) {
        val profileId = settings.getNumber(Settings.Key.CURRENT_PROFILE);
        val profile = profileRepo.selectById(profileId);
        try {
            val timestamp = FORMATTER.format(LocalDateTime.now());
            val outFile = Paths.get(
                    request.getPath(), profile.getName() + " - " + timestamp + ".json"
            ).toFile();
            if (outFile.exists()) throw new ApiException("导出失败，文件已存在");
            if (!outFile.createNewFile()) throw new ApiException("导出失败，无法创建文件");
            val sources = sourceRepo.selectList(new QueryWrapper<Source>()
                    .eq("profile_id", profileId));
            val tags = tagRepo.selectList(new QueryWrapper<Tag>()
                    .eq("profile_id", profileId));
            try (val out = new FileOutputStream(outFile)) {
                objectMapper.writeValue(out, new ImportableProfile(profile, sources, tags));
            }
        } catch (IOException e) {
            log.error("导出失败", e);
            throw new ApiException("导出失败，请确认是否有目标文件夹的权限");
        }
    }

    /**
     * 删除依赖项
     *
     * @param profileId 配置项 ID
     */
    private void deleteRelated(long profileId) {
        if (settings.getNumber(Settings.Key.CURRENT_PROFILE).equals(profileId)) {
            indexService.cleanAll();
        }
        // 删除全部历史记录
        tidyHistoryRepo.delete(null);
        // 删除源文件夹
        sourceRepo.delete(new QueryWrapper<Source>()
                .eq("profile_id", profileId));
        // 删除标签
        tagRepo.delete(new QueryWrapper<Tag>()
                .eq("profile_id", profileId));
    }

    /**
     * 删除
     *
     * @param profileId 配置项 ID
     */
    @Transactional
    public void delete(long profileId) {
        this.deleteRelated(profileId);
        profileRepo.deleteById(profileId);
    }
}
