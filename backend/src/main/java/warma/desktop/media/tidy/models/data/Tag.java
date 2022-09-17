package warma.desktop.media.tidy.models.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * Author: sinar
 * 2022/8/31 21:04
 */
@Data
@NoArgsConstructor
@TableName("tag")
public class Tag {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotBlank(message = "请提供标签名")
    private String name;
    private String description;
    // Source folder 和 Tag folder 不能重复
    @NotBlank(message = "请提供文件夹地址")
    private String folder;
    private Boolean notIndex;
    private Date updateAt;
    private Long profileId;
    private Integer menuOrder;
}
