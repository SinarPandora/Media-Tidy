package warma.desktop.media.tidy.models;

import lombok.Data;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import javax.validation.constraints.NotNull;

/**
 * Author: sinar
 * 2022/9/1 15:46
 */
@Data
public class FileUploadRequest {
    @NotNull(message = "必须提供上传文件")
    private FileUpload file;
}
