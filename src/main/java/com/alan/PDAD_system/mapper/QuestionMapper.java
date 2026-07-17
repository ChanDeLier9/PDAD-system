package com.alan.PDAD_system.mapper;
import com.alan.PDAD_system.entity.Question;
import org.apache.ibatis.annotations.*;
import org.aspectj.weaver.patterns.TypePatternQuestions;

import java.util.List;

@Mapper
public interface QuestionMapper {


        @Select("SELECT question_id, scale_id, question_text FROM questions WHERE scale_id = #{scaleId}")
        @Results({
                @Result(property = "questionId", column = "question_id"),
                @Result(property = "scaleId", column = "scale_id"),
                @Result(property = "questionText", column = "question_text")
        })
        List<Question> findQuestionsByScaleId(Integer scaleId);




}


