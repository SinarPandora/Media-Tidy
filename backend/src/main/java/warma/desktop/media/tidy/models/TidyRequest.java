package warma.desktop.media.tidy.models;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Author: sinar
 * 2022/9/3 20:58
 */
@Data
public class TidyRequest {
    @NotNull(message = "必须提供文件 ID")
    private Long fileId;
    @NotNull(message = "必须提供标签 ID")
    private Long tagId;
    private String newName;
}
