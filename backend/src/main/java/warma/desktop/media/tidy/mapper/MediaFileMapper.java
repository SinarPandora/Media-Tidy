package warma.desktop.media.tidy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import warma.desktop.media.tidy.models.data.MediaFile;

import java.util.List;

/**
 * Author: sinar
 * 2022/8/31 21:17
 */
@Mapper
public interface MediaFileMapper extends BaseMapper<MediaFile> {
    /**
     * 批量插入
     *
     * @param items 文件列表
     * @return 影响行数
     */
    int batchInsert(@Param("items") List<MediaFile> items);
}
