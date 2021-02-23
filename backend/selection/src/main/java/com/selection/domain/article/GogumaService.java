package com.selection.domain.article;

import com.selection.domain.notification.NotificationService;
import com.selection.domain.user.User;
import com.selection.domain.user.UserService;
import com.selection.dto.goguma.GogumaRequest;
import com.selection.dto.goguma.GogumaResponse;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GogumaService {

    private final ArticleService articleService;
    private final NotificationService notificationService;
    private final UserService userService;

    @Transactional
    public void create(Long articleId, String userId, GogumaRequest gogumaRequest) {
        Article article = articleService.findArticleById(articleId);
        article.addGoguma(gogumaRequest.toEntity(userId, article));
        notificationService.send(userId, article.getUserId(), article);
    }

    @Transactional
    public void modify(Long articleId, Long gogumaId, String userId, GogumaRequest gogumaRequest) {
        Article article = articleService.findArticleById(articleId);
        article.modifyGoguma(gogumaId, userId,gogumaRequest);
    }

    @Transactional
    public GogumaResponse lookUp(Long articleId, Long gogumaId, String userId) {
        Article article = articleService.findArticleById(articleId);
        Goguma goguma = article.lookUpGoguma(gogumaId);
        User user = userService.findByUserId(goguma.getUserId());
        return new GogumaResponse(
            goguma,
            user.getNickname(),
            goguma.getUserId().equals(userId)
        );
    }

    @Transactional
    public void delete(Long articleId, Long gogumaId, String userId) {
        Article article = articleService.findArticleById(articleId);
        article.deleteGoguma(gogumaId, userId);
    }
}
