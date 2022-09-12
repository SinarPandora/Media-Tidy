package warma.desktop.media.tidy.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Author: sinar
 * 2022/9/8 22:01
 */
@Data
@AllArgsConstructor
public class ErrorMessage {
    private String message;
    private boolean known;
}
