package warma.desktop.media.tidy.models.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Author: sinar
 * 2022/8/31 21:26
 */
@Data
@TableName("setting")
public class Setting {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String key;
    private String value;
    private String valueType;
    private Boolean visible;

    public static final class Type {
        public static final String NUMBER = "N";
        public static final String TEXT = "S";
    }
}
