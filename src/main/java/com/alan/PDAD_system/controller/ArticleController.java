package com.alan.PDAD_system.controller;

import com.alan.PDAD_system.dto.HealthArticleDTO;
import com.alan.PDAD_system.dto.HealthArticleRequestDTO;
import com.alan.PDAD_system.dto.Result;
import com.alan.PDAD_system.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@Controller和@ResponseBody的组合注解，
// 用于将Controller类中的方法返回的对象直接转换为JSON格式的响应体，
// 并设置响应头为application/json。
@RestController
@RequestMapping("/articles")
//@Validated注解用于开启参数校验功能。
// 当在方法参数上使用 @Validated 时，Spring 会自动根据参数对象中定义的校验注解（如 @NotNull、@NotBlank 等）进行验证。
// 如果验证失败，Spring 会抛出 MethodArgumentNotValidException 异常。
@Validated
public class ArticleController {

    private final ArticleService articleService;
    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    // 获取所有文章
    @GetMapping
    public Result<List<HealthArticleDTO>> getAllArticles() {
        return Result.success(articleService.findAll());
    }

    // 获取最新三篇文章
    @GetMapping("/latest")
    public Result<List<HealthArticleDTO>> getLatestArticles() {
        return Result.success(articleService.findLatestThree());
    }

    // 获取单篇文章详情
    @GetMapping("/{articleId}")
    public Result<HealthArticleDTO> getArticleById(@PathVariable Integer articleId) {
        HealthArticleDTO article = articleService.findById(articleId);
        return article != null ? Result.success(article) : Result.error("文章不存在");
    }


    // 根据标签搜索文章
    @GetMapping("/search")
    public Result<List<HealthArticleDTO>> getArticlesByTag(@RequestParam String tag) {
        return Result.success(articleService.findByTag(tag));
    }

    // 获取指定医生的所有文章
    @GetMapping("/doctors/Name/{doctorName}")
    public Result<List<HealthArticleDTO>> getDoctorArticlesByName(@PathVariable String doctorName) {
        return Result.success(articleService.findByDoctorName(doctorName));
    }


    // 获取指定医生的所有文章
    @GetMapping("/doctors/Id/{doctorId}")
    public Result<List<HealthArticleDTO>> getDoctorArticlesById(@PathVariable String doctorId) {
        System.out.println("进来了 getArticlesByDoctor");

        return Result.success(articleService.findByDoctorId(doctorId));
    }


    // 发布文章
    @PostMapping("/doctors/{doctorId}")
    public Result<String> publishArticle(@PathVariable String doctorId,
                                         @RequestBody HealthArticleRequestDTO articleDTO) {
        articleService.publishArticle(doctorId, articleDTO);
        return Result.success("文章发布成功！");
    }

    // 修改文章
    @PutMapping("/doctors/{doctorId}/{articleId}")
    public Result<String> updateArticle(@PathVariable String doctorId,
                                        @PathVariable Integer articleId,
                                        @RequestBody HealthArticleRequestDTO articleDTO) {
        int updated = articleService.updateArticle(articleId, doctorId, articleDTO);
        return updated > 0 ? Result.success("文章更新成功！") : Result.error("更新失败或无权限！");
    }

    // 删除文章
    @DeleteMapping("/doctors/{doctorId}/{articleId}")
    public Result<String> deleteArticle(@PathVariable String doctorId,
                                        @PathVariable Integer articleId) {
        int deleted = articleService.deleteArticle(articleId, doctorId);
        return deleted > 0 ? Result.success("删除成功！") : Result.error("删除失败或无权限！");
    }

    //分页 关键词 标签过滤查询
    @GetMapping("/mobile")
    public Result<List<HealthArticleDTO>> getArticlesForMobile(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tag) {

        List<HealthArticleDTO> result = articleService.findForMobile(page, size, keyword, tag);
        return Result.success(result);
    }

}
