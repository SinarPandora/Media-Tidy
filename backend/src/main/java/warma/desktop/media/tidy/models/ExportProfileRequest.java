package warma.desktop.media.tidy.models;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Author: sinar
 * 2022/9/12 00:33
 */
@Data
public class ExportProfileRequest {
    @NotBlank(message = "请提供导出地址")
    private String path;
}
