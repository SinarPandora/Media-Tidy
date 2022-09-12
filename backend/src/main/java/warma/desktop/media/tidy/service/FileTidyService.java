package warma.desktop.media.tidy.service;

import io.smallrye.mutiny.Uni;
import io.vertx.core.file.OpenOptions;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.file.AsyncFile;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jboss.resteasy.reactive.RestQuery;
import warma.desktop.media.tidy.mapper.MediaFileMapper;
import warma.desktop.media.tidy.mapper.TagMapper;
import warma.desktop.media.tidy.models.TidyRequest;
import warma.desktop.media.tidy.models.data.MediaFile;
import warma.desktop.media.tidy.models.data.TidyHistory;
import warma.desktop.media.tidy.system.exception.ApiException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Author: sinar
 * 2022/8/31 21:19
 */
@Slf4j
@ApplicationScoped
@SuppressWarnings("CdiInjectionPointsInspection")
public class FileTidyService {
    private static final OpenOptions READ_ONLY = new OpenOptions().setCreate(false).setWrite(false);

    @Inject
    HistoryService historyService;

    @Inject
    MediaFileMapper mediaFileRepo;

    @Inject
    TagMapper tagRepo;

    @Inject
    Vertx vertx;

    /**
     * 整理
     *
     * @param request 整理请求
     */
    @Transactional
    public void tidy(TidyRequest request) {
        val file = mediaFileRepo.selectById(request.getFileId());
        if (file == null) throw new ApiException("整理失败，目标文件不存在");
        val tag = tagRepo.selectById(request.getTagId());
        if (tag == null) throw new ApiException("整理失败，目标标签不存在");
        Path newFilePath;
        try {
            newFilePath = Path.of(
                    tag.getFolder(),
                    request.getNewName() != null && !request.getNewName().isBlank()
                            ? request.getNewName()
                            : file.getFilename()
            );
        } catch (InvalidPathException e) {
            throw new ApiException("新路径或文件名称不合法");
        }
        if (Files.exists(newFilePath)) {
            throw new ApiException("整理失败，目标文件夹下已存在同名文件");
        }
        val tempName = UUID.randomUUID().toString();
        val tempFilePath = Path.of(file.getFolder(), tempName);
        try {
            Files.move(Path.of(file.getFolder(), file.getFilename()), tempFilePath);
        } catch (IOException e) {
            throw new ApiException("整理失败，无法创建临时文件");
        }
        if (request.getTagId() != null) {
            this.moveTo(file, request.getTagId());
        }
        if (request.getNewName() != null && !request.getNewName().isBlank()) {
            this.rename(file, request.getNewName());
        }
        mediaFileRepo.updateById(file);
        try {
            Files.move(tempFilePath, newFilePath);
        } catch (IOException e) {
            throw new ApiException("整理失败，无法移动到目标文件夹，请尝试手动移动文件：" + tempFilePath.toAbsolutePath());
        }
    }

    /**
     * 移动文件到指定标签文件夹
     *
     * @param file  媒体文件对象
     * @param tagId 标签 ID
     */
    private void moveTo(MediaFile file, long tagId) {
        val tag = tagRepo.selectById(tagId);
        if (tag == null) {
            throw new ApiException("目标标签不存在");
        }
        val history = TidyHistory.ofMove(
                file.getId(),
                file.getTagId(),
                file.getTagId() == null ? file.getSourceId() : null,
                file.getFolder(),
                tag.getId(),
                tag.getFolder()
        );
        historyService.record(history);
        file.setTagId(tagId);
        file.setFolder(tag.getFolder());
    }

    /**
     * 重命名文件
     *
     * @param file    媒体文件对象
     * @param newName 新名称
     */
    private void rename(MediaFile file, String newName) {
        val history = TidyHistory.ofRename(
                file.getId(),
                file.getFilename(),
                newName
        );
        historyService.record(history);
        file.setFilename(newName);
    }

    /**
     * 文件是否存在
     *
     * @param fileId 文件 ID
     * @return 是否存在
     */
    public boolean exist(long fileId) {
        val file = this.mediaFileRepo.selectById(fileId);
        if (file == null) throw new ApiException("指定文件不在索引中，请选择其他文件");
        return Files.exists(Paths.get(file.getFolder(), file.getFilename()));
    }
}
