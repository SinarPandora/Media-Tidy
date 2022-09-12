package warma.desktop.media.tidy.models.data;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Author: sinar
 * 2022/9/1 16:56
 */
@Data
@TableName("sqlite_sequence")
public class SqliteSequence {
    private String name;
    private Long seq;
}
