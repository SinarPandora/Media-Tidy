package warma.desktop.media.tidy.models.data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Author: sinar
 * 2022/8/31 21:23
 */
@Data
@TableName("profile")
public class Profile {
    @TableId(type = IdType.AUTO)
    private Long id;
    @NotBlank(message = "请提供配置项名称")
    private String name;
    @NotNull(message = "请提供配置项激活状态")
    private Boolean active;
}
