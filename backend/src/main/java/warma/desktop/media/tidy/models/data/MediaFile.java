package warma.desktop.media.tidy.models.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: sinar
 * 2022/8/31 20:11
 */
@Data
@NoArgsConstructor
@TableName("media_file")
public class MediaFile {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String filename;
    private String folder;
    private Long sourceId;
    private Long tagId;

    public MediaFile(String filename, String folder, Long sourceId, Long tagId) {
        this.filename = filename;
        this.folder = folder;
        this.sourceId = sourceId;
        this.tagId = tagId;
    }
}
