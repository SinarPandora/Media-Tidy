package warma.desktop.media.tidy.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import warma.desktop.media.tidy.models.data.Profile;
import warma.desktop.media.tidy.models.data.Source;
import warma.desktop.media.tidy.models.data.Tag;

import java.util.List;

/**
 * 可导入的源
 * <p>
 * Author: sinar
 * 2022/9/1 01:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportableProfile {
    private Profile profile;
    private List<Source> sources;
    private List<Tag> tags;
}
