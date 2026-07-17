package com.alan.PDAD_system.service.impl;

import com.alan.PDAD_system.dto.HealthArticleDTO;
import com.alan.PDAD_system.dto.HealthArticleRequestDTO;
import com.alan.PDAD_system.mapper.HealthArticleMapper;
import com.alan.PDAD_system.service.ArticleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final HealthArticleMapper healthArticleMapper;

    public ArticleServiceImpl(HealthArticleMapper healthArticleMapper) {
        this.healthArticleMapper = healthArticleMapper;
    }

    @Override
    public List<HealthArticleDTO> findAll() {
        return healthArticleMapper.findAll();
    }

    @Override
    public HealthArticleDTO findById(Integer articleId) {
        return healthArticleMapper.findById(articleId);
    }

    @Override
    public List<HealthArticleDTO> findByTag(String tag) {
        return healthArticleMapper.findByTag(tag);
    }

    @Override
    public List<HealthArticleDTO> findByDoctorName(String doctorName) {
        return healthArticleMapper.findByDoctorName(doctorName);
    }
    @Override
    public List<HealthArticleDTO> findByDoctorId(String doctorId) {
        return healthArticleMapper.findByDoctorId(doctorId);
    }
    @Override
    public void publishArticle(String doctorId, HealthArticleRequestDTO dto) {
        healthArticleMapper.insertHealthArticle(doctorId, dto.getTitle(), dto.getContent(), dto.getTags(),dto.getDoctor_name());
    }

    @Override
    public int updateArticle(Integer articleId, String doctorId, HealthArticleRequestDTO dto) {
        return healthArticleMapper.updateArticle(articleId, doctorId, dto.getTitle(), dto.getContent(), dto.getTags());
    }

    @Override
    public int deleteArticle(Integer articleId, String doctorId) {
        return healthArticleMapper.deleteArticle(articleId, doctorId);
    }

    @Override
    public List<HealthArticleDTO> findForMobile(int page, int size, String keyword, String tag) {
        int offset = (page - 1) * size;
        return healthArticleMapper.findForMobile(offset, size, keyword, tag);
    }

    @Override
    public List<HealthArticleDTO> findLatestThree() {
        return healthArticleMapper.selectLatestThree();
    }

}
