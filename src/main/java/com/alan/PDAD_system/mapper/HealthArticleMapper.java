package com.alan.PDAD_system.mapper;

import com.alan.PDAD_system.dto.HealthArticleDTO;
import com.alan.PDAD_system.entity.HealthArticle;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HealthArticleMapper {

    @Insert("""
        INSERT INTO health_article (doctor_id, title, content, tags, created_at,doctor_name)
        VALUES (#{doctorId}, #{title}, #{content}, #{tags}, NOW(),#{doctor_name})
        """)
    void insertHealthArticle(@Param("doctorId") String doctorId,
                             @Param("title") String title,
                             @Param("content") String content,
                             @Param("tags") String tags,
                             @Param("doctor_name") String doctor_name);
    // 查询所有文章
    @Select("SELECT a.* FROM health_article a JOIN doctor d ON a.doctor_id = d.doctorId ORDER BY created_at DESC")
    List<HealthArticleDTO> findAll();

    //查询一篇文章的内容
    @Select("""
    SELECT a.*, d.doctorName
    FROM health_article a
    JOIN doctor d ON a.doctor_id = d.doctorId 
    WHERE article_id = #{articleId}
""")
    HealthArticleDTO findById(@Param("articleId") Integer articleId);

    // 查询指定标签的文章
    @Select("SELECT a.*, d.doctorName FROM health_article a JOIN doctor d ON a.doctor_id = d.doctorId WHERE tags LIKE CONCAT('%', #{tag}, '%') ORDER BY created_at DESC")
    List<HealthArticleDTO> findByTag(@Param("tag") String tag);

    // 根据姓名 查询医生的文章
    @Select("SELECT a.* FROM health_article a JOIN doctor d ON a.doctor_name= d.doctorName WHERE a.doctor_name = #{doctorName} ORDER BY created_at DESC")
    List<HealthArticleDTO> findByDoctorName(@Param("doctorName") String doctorName);

    // 根据id 查询医生的文章
    @Select("SELECT a.article_id,a.doctor_id,a.doctor_name AS doctorName,a.content , a.created_at , a.tags , a.title FROM health_article a  WHERE a.doctor_id = #{doctorId} ORDER BY created_at DESC")
    List<HealthArticleDTO> findByDoctorId(@Param("doctorId") String doctorId);


    // 更新文章
    @Update("UPDATE health_article SET title=#{title}, content=#{content}, tags=#{tags} WHERE article_id=#{articleId} AND doctor_id=#{doctorId}")
    int updateArticle(@Param("articleId") Integer articleId,
                      @Param("doctorId") String doctorId,
                      @Param("title") String title,
                      @Param("content") String content,
                      @Param("tags") String tags);

    // 删除文章
    @Delete("DELETE FROM health_article WHERE article_id=#{articleId} AND doctor_id=#{doctorId}")
    int deleteArticle(@Param("articleId") Integer articleId, @Param("doctorId") String doctorId);

    @Select("""
    SELECT a.*, d.doctorName 
    FROM health_article a 
    JOIN doctor d ON a.doctor_id = d.doctorId
    WHERE (#{keyword} IS NULL OR a.title LIKE CONCAT('%', #{keyword}, '%'))
      AND (#{tag} IS NULL OR a.tags LIKE CONCAT('%', #{tag}, '%'))
    ORDER BY a.created_at DESC
    LIMIT #{offset}, #{size}
""")
    List<HealthArticleDTO> findForMobile(@Param("offset") int offset,
                                         @Param("size") int size,
                                         @Param("keyword") String keyword,
                                         @Param("tag") String tag);
    // 【新增】获取所有医疗健康文章的方法
    @Select("SELECT article_id, doctor_id, title, content, tags, created_at FROM health_article ORDER BY created_at DESC")
    List<HealthArticle> findAllArticles();

    @Select("SELECT * FROM health_article ORDER BY created_at DESC LIMIT 3")
    List<HealthArticleDTO> selectLatestThree();

}
