package com.alan.PDAD_system.mapper;

import com.alan.PDAD_system.entity.Scale;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ScaleMapper {
    // 查询评估表的基本信息
    @Select("SELECT * FROM scales WHERE scale_id = #{scaleId}")
    Scale findById(Integer scaleId);
}
