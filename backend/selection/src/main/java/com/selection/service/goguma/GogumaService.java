package com.selection.service.goguma;

import com.selection.domain.article.Article;
import com.selection.domain.article.Goguma;
import com.selection.dto.goguma.GogumaRequest;
import com.selection.dto.goguma.GogumaResponse;
import com.selection.repository.GogumaRepository;
import com.selection.service.article.ArticleService;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GogumaService {

    private final ArticleService articleService;
    private final GogumaRepository gogumaRepository;

    @Transactional
    public Long create(Long articleId, String author, GogumaRequest gogumaRequest) {
        Article article = articleService.findById(articleId);
        Goguma goguma = gogumaRepository.save(gogumaRequest.toEntity(author, article));
        article.addGoguma(goguma);
        return goguma.getId();
    }

    @Transactional
    public void modify(Long articleId, Long gogumaId, GogumaRequest gogumaRequest) {
        Article article = articleService.findById(articleId);
        article.modifyGoguma(gogumaId,gogumaRequest);
    }

    @Transactional
    public GogumaResponse lookUp(Long articleId, Long gogumaId) {
        Article article = articleService.findById(articleId);
        return new GogumaResponse(article.lookUpGoguma(gogumaId));
    }

    @Transactional
    public void delete(Long articleId, Long gogumaId) {
        Article article = articleService.findById(articleId);
        article.deleteGoguma(gogumaId);
    }
}
