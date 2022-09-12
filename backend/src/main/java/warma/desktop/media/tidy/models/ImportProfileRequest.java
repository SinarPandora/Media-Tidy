package warma.desktop.media.tidy.models;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Author: sinar
 * 2022/9/12 00:39
 */
@Data
public class ImportProfileRequest {
    @NotBlank(message = "请提供文件地址")
    private String path;
}
