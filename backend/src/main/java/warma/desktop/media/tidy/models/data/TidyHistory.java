package warma.desktop.media.tidy.models.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;

import java.util.Date;

/**
 * Author: sinar
 * 2022/8/31 21:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tidy_history")
public class TidyHistory {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long fileId;
    private Long originTagId; // 标签移动
    private Long originSourceId; // 源移动
    private String originFolder;
    private String originFilename;
    private Long tidiedTagId;
    private String tidiedFolder;
    private String tidiedFilename;
    private Date createAt;
    private String action;
    private Boolean revert;

    public static final class Action {
        public static final String MOVE = "MOVE";
        public static final String RENAME = "RENAME";
    }

    /**
     * 静态构造方法：创建移动历史
     */
    public static TidyHistory ofMove(Long fileId, Long originTagId, Long originSourceId, String originFolder, Long tidiedTagId, String tidiedFolder) {
        val history = new TidyHistory();
        history.fileId = fileId;
        history.originTagId = originTagId;
        history.originSourceId = originSourceId;
        history.originFolder = originFolder;
        history.tidiedTagId = tidiedTagId;
        history.tidiedFolder = tidiedFolder;
        history.action = Action.MOVE;
        return history;
    }

    /**
     * 静态构造方法：创建重命名历史
     */
    public static TidyHistory ofRename(Long fileId, String originFilename, String tidiedFilename) {
        val history = new TidyHistory();
        history.fileId = fileId;
        history.originFilename = originFilename;
        history.tidiedFilename = tidiedFilename;
        history.action = Action.RENAME;
        return history;
    }
}
