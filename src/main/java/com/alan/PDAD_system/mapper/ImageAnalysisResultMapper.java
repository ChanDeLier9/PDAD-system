package com.alan.PDAD_system.mapper;

import com.alan.PDAD_system.entity.ImageAnalysisResult;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ImageAnalysisResultMapper {

    /**
     * 插入一条影像分析记录
     */
    @Insert("INSERT INTO image_results (" +
            "patientId, image, update_at, analysisResult" +
            ") VALUES (" +
            "#{patientId}, #{imageData}, #{updateTime}, #{analysisResult}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "imageId", keyColumn = "image_id")
    void insertImageAnalysisResult(ImageAnalysisResult entity);

    /**
     * 按患者查询所有影像分析记录（按时间倒序）
     */
    @Select("SELECT " +
            "image_id, " +
            "patientId, " +
            "image, " +
            "update_at, " +
            "analysisResult " +
            "FROM image_results " +
            "WHERE patientId = #{patientId} " +
            "ORDER BY update_at DESC")
    @Results(id = "ImageAnalysisResultMap", value = {
            @Result(column = "image_id", property = "imageId", id = true),
            @Result(column = "patientId", property = "patientId"),
            @Result(column = "image", property = "imageData"),
            @Result(column = "update_at", property = "updateTime"),
            @Result(column = "analysisResult", property = "analysisResult")
    })
    List<ImageAnalysisResult> findByPatientId(@Param("patientId") String patientId);

    /**
     * 查询某患者最新一条影像分析记录
     */
    @Select("SELECT " +
            "image_id, " +
            "patientId, " +
            "image, " +
            "update_at, " +
            "analysisResult " +
            "FROM image_results " +
            "WHERE patientId = #{patientId} " +
            "ORDER BY update_at DESC " +
            "LIMIT 1")
    @ResultMap("ImageAnalysisResultMap")
    ImageAnalysisResult findLatestByPatientId(@Param("patientId") String patientId);
}
