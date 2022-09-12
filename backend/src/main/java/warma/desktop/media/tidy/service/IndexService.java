package warma.desktop.media.tidy.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import warma.desktop.media.tidy.cache.Settings;
import warma.desktop.media.tidy.mapper.MediaFileMapper;
import warma.desktop.media.tidy.mapper.SourceMapper;
import warma.desktop.media.tidy.mapper.SqliteSequenceMapper;
import warma.desktop.media.tidy.mapper.TagMapper;
import warma.desktop.media.tidy.models.FuzzySearchRequest;
import warma.desktop.media.tidy.models.data.MediaFile;
import warma.desktop.media.tidy.models.data.Source;
import warma.desktop.media.tidy.models.data.SqliteSequence;
import warma.desktop.media.tidy.models.data.Tag;
import warma.desktop.media.tidy.system.exception.ApiException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Author: sinar
 * 2022/8/31 21:19
 */
@Slf4j
@ApplicationScoped
@SuppressWarnings("CdiInjectionPointsInspection")
public class IndexService {
    private static final Set<String> ACCEPT_EXTENSIONS = Set.of("mp3", "wav", "wmv", "flac", "m4a", "aac", "ogg", "mp4");

    @Inject
    Settings settings;

    @Inject
    MediaFileMapper mediaFileRepo;

    @Inject
    SourceMapper sourceRepo;

    @Inject
    TagMapper tagRepo;

    @Inject
    SqliteSequenceMapper sqliteSequenceRepo;

    @Inject
    HistoryService historyService;

    /**
     * 构建索引
     *
     */
    @Transactional
    public void build() {
        historyService.cleanAll();
        this.cleanAll();
        val profileId = settings.getNumber(Settings.Key.CURRENT_PROFILE);
        val sources = sourceRepo.selectList(new QueryWrapper<Source>()
                .eq("profile_id", profileId)
                .eq("active", true));
        sources.forEach(folder -> load(folder.getFilepath(), folder.getId(), null));
        val tags = tagRepo.selectList(new QueryWrapper<Tag>()
                .eq("profile_id", profileId)
                .eq("not_index", false));
        tags.forEach(tag -> load(tag.getFolder(), null, tag.getId()));
    }

    /**
     * 从硬盘加载索引
     *
     * @param filePath 文件夹路径
     * @param tagId    标签
     */
    @Transactional
    public void load(String filePath, Long sourceId, Long tagId) {
        try (val walks = Files.walk(Path.of(filePath))) {
            val indexes = walks
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(file -> Optional.of(file.getName())
                            .filter(f -> f.contains("."))
                            .map(f -> f.substring(file.getName().lastIndexOf(".") + 1).toLowerCase())
                            .filter(ACCEPT_EXTENSIONS::contains)
                            .isPresent())
                    .map(file -> new MediaFile(file.getName(), file.getParent(), sourceId, tagId))
                    .collect(Collectors.toList());
            if (indexes.size() == 0) {
                if (sourceId != null) {
                    sourceRepo.deleteById(sourceId);
                }
                return;
            };
            val affect = mediaFileRepo.batchInsert(indexes);
            if (affect != indexes.size()) {
                log.warn("索引加载不全，预期数量：{}，实际数量：{}", indexes.size(), affect);
            }
        } catch (IOException e) {
            throw new ApiException(e);
        }
    }

    /**
     * 获取索引
     *
     * @return 文件索引内容
     */
    public List<MediaFile> getUntidy() {
        return mediaFileRepo.selectList(new QueryWrapper<MediaFile>()
                .isNull("tag_id"));
    }

    /**
     * 通过标签 ID 加载
     *
     * @param tagId 标签 ID
     * @return 索引内容
     */
    public List<MediaFile> getByTag(long tagId) {
        return mediaFileRepo.selectList(new QueryWrapper<MediaFile>()
                .eq("tag_id", tagId));
    }

    /**
     * 通过源 ID 加载
     *
     * @param sourceId 源 ID
     * @return 索引内容
     */
    public List<MediaFile> getBySource(long sourceId) {
        return mediaFileRepo.selectList(new QueryWrapper<MediaFile>()
                .eq("source_id", sourceId));
    }

    /**
     * 清理文件索引
     */
    @Transactional
    public void cleanAll() {
        mediaFileRepo.delete(null);
        // 重置索引
        sqliteSequenceRepo.delete(new QueryWrapper<SqliteSequence>()
                .eq("name", "media_file"));
    }

    /**
     * 通过标签清空索引
     *
     * @param tagIds 标签 ID 列表
     */
    public void cleanByTag(Collection<Long> tagIds) {
        mediaFileRepo.delete(new QueryWrapper<MediaFile>()
                .in("tag_id", tagIds));
    }

    /**
     * 通过源文件夹清空索引
     *
     * @param sourceIds 源文件夹 ID 列表
     */
    public void cleanBySource(Collection<Long> sourceIds) {
        mediaFileRepo.delete(new QueryWrapper<MediaFile>()
                .in("source_id", sourceIds));
    }

    /**
     * 模糊搜索
     *
     * @param request 搜索请求
     * @return 搜索结果
     */
    public List<MediaFile> search(FuzzySearchRequest request) {
        var cond = new QueryWrapper<MediaFile>();
        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            cond = cond.like("name", "%" + request.getKeyword() + "%");
        }
        if (request.getSourceIds() != null && !request.getSourceIds().isEmpty()) {
            cond = cond.in("source_id", request.getSourceIds());
        }
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            cond = cond.in("tag_id", request.getTagIds());
        }
        return mediaFileRepo.selectList(cond);
    }
}
