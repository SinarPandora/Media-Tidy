package warma.desktop.media.tidy.models.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * Author: sinar
 * 2022/8/31 21:09
 */
@Data
@TableName("source_folder")
public class Source {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotBlank(message = "请提供文件夹路径")
    private String filepath;
    private String alias;
    private Date updateAt;
    private Boolean active;
    private Long profileId;
}
