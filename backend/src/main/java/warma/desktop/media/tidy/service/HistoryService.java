package warma.desktop.media.tidy.service;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import warma.desktop.media.tidy.mapper.MediaFileMapper;
import warma.desktop.media.tidy.mapper.TidyHistoryMapper;
import warma.desktop.media.tidy.models.data.TidyHistory;
import warma.desktop.media.tidy.system.exception.ApiException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

/**
 * Author: sinar
 * 2022/9/2 00:23
 */
@Slf4j
@ApplicationScoped
@SuppressWarnings("CdiInjectionPointsInspection")
public class HistoryService {
    @Inject
    TidyHistoryMapper tidyHisRepo;

    @Inject
    MediaFileMapper mediaFileRepo;

    /**
     * 获取全部历史
     *
     * @return 全部历史
     */
    public List<TidyHistory> getAll() {
        return tidyHisRepo.selectList(null);
    }

    /**
     * 记录历史
     *
     * @param history 历史记录
     */
    public void record(TidyHistory history) {
        tidyHisRepo.insert(history);
    }

    /**
     * 清理全部
     */
    public void cleanAll() {
        tidyHisRepo.delete(null);
    }

    /**
     * 撤回更改
     *
     * @param historyId 历史 ID
     */
    @Transactional
    public void revert(long historyId) {
        val history = tidyHisRepo.selectById(historyId);
        if (history == null) {
            throw new ApiException("历史记录不存在");
        }
        val file = mediaFileRepo.selectById(history.getFileId());
        val fromPath = Path.of(file.getFolder(), file.getFilename());
        Path toPath;
        if (TidyHistory.Action.MOVE.equals(history.getAction())) {
            toPath = Path.of(history.getOriginFolder(), file.getFilename());
        } else if (TidyHistory.Action.RENAME.equals(history.getAction())) {
            toPath = Path.of(file.getFolder(), history.getOriginFilename());
        } else {
            throw new ApiException("不支持的回滚操作");
        }
        if (Files.exists(toPath)) {
            throw new ApiException("回滚失败，同名文件已存在");
        }
        try {
            Files.move(fromPath, toPath);
        } catch (IOException e) {
            throw new ApiException("回滚失败，无法移动文件");
        }
        history.setCreateAt(new Date());
        history.setRevert(!history.getRevert());
        tidyHisRepo.updateById(history);
    }
}
