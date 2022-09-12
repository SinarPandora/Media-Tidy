package warma.desktop.media.tidy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import warma.desktop.media.tidy.models.data.Tag;

import java.util.List;

/**
 * Author: sinar
 * 2022/8/31 21:17
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
    /**
     * 批量插入
     *
     * @param items 标签列表
     * @return 影响行数
     */
    int batchInsert(@Param("items") List<Tag> items);

    /**
     * 在末尾插入
     *
     * @param tag 标签对象
     * @return 影响行数
     */
    int insertAtLast(Tag tag);

    /**
     * 根据列表中 ID 的顺序更新 menu_order
     *
     * @param ids 有序 ID 列表
     * @return 影响行数
     */
    int updateOrder(@Param("items") List<Long> ids);

    /**
     * 批量更新
     *
     * @param tags 标签列表
     * @return 更新结果
     */
    int batchUpdate(@Param("items") List<Tag> tags);
}
