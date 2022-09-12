package warma.desktop.media.tidy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import warma.desktop.media.tidy.models.data.Source;

import java.util.List;

/**
 * Author: sinar
 * 2022/8/31 21:17
 */
@Mapper
public interface SourceMapper extends BaseMapper<Source> {
    /**
     * 批量插入
     *
     * @param items 源文件夹列表
     * @return 影响行数
     */
    int batchInsert(@Param("items") List<Source> items);
}
