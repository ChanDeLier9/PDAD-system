package com.alan.PDAD_system.mapper;
import com.alan.PDAD_system.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnswerOptionMapper {
    // 查询某个问题的所有选项
    @Select("SELECT * FROM answer_option WHERE question_id = #{questionId}")
    List<AnswerOption> findOptionsByQuestionId(Integer questionId);

    // 根据选项ID查找选项
    @Select("SELECT * FROM answer_option WHERE option_id = #{optionId}")
    AnswerOption findById(Integer optionId);
}
