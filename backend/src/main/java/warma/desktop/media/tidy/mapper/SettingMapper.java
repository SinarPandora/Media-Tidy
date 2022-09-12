package warma.desktop.media.tidy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.val;
import org.apache.ibatis.annotations.Mapper;
import warma.desktop.media.tidy.models.data.Setting;
import warma.desktop.media.tidy.system.exception.ApiException;

/**
 * Author: sinar
 * 2022/8/31 21:17
 */
@Mapper
public interface SettingMapper extends BaseMapper<Setting> {
}
