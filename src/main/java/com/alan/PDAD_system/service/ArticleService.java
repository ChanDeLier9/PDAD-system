package com.alan.PDAD_system.service;


import com.alan.PDAD_system.dto.HealthArticleDTO;
import com.alan.PDAD_system.dto.HealthArticleRequestDTO;

import java.util.List;

public interface ArticleService {
    List<HealthArticleDTO> findAll();

    HealthArticleDTO findById(Integer articleId);

    List<HealthArticleDTO> findByTag(String tag);
    List<HealthArticleDTO> findByDoctorName(String doctorName);
    List<HealthArticleDTO> findByDoctorId(String doctorId);
    void publishArticle(String doctorId, HealthArticleRequestDTO articleDTO);
    int updateArticle(Integer articleId, String doctorId, HealthArticleRequestDTO dto);
    int deleteArticle(Integer articleId, String doctorId);
    List<HealthArticleDTO> findForMobile(int page, int size, String keyword, String tag);
    List<HealthArticleDTO> findLatestThree();
}
