package warma.desktop.media.tidy.models;

import lombok.Data;

import java.util.List;

/**
 * Author: sinar
 * 2022/9/3 20:40
 */
@Data
public class FuzzySearchRequest {
    private String keyword;
    private List<Long> tagIds;
    private List<Long> sourceIds;
}
