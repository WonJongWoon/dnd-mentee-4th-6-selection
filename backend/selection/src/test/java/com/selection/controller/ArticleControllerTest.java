package com.selection.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.selection.domain.article.Article;
import com.selection.domain.question.Question;
import com.selection.domain.question.Questions;
import com.selection.domain.tag.Tag;
import com.selection.dto.article.ArticleLatestResponse;
import com.selection.dto.article.ArticleRequest;
import com.selection.dto.question.QuestionRequest;
import com.selection.dto.tag.TagRequest;
import com.selection.repository.ArticleRepository;
import com.selection.service.article.ArticleService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@TestMethodOrder(OrderAnnotation.class)
@AutoConfigureMockMvc
@SpringBootTest
@DisplayName("게시글 API 테스트")
class ArticleControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mockMvc;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleService articleService;

    @BeforeEach
    @DisplayName("테스트 목업 준비")
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
            .addFilters(new CharacterEncodingFilter("UTF-8", true))  // UTF-8 인코딩 필터 추가
            .alwaysDo(print())
            .build();
    }

    public ArticleRequest createArticleSaveRequest() {
        final String title = "제목 1";
        final String content = "내용 1";
        final String backgroundColor = "#FFFFFF";
        final String questionContent1 = "선택지 1";
        final String questionContent2 = "선택지 2";
        final String tagContent1 = "태그 1";
        final String tagContent2 = "태그 2";

        List<QuestionRequest> questions = new ArrayList<>();
        questions.add(new QuestionRequest(questionContent1));
        questions.add(new QuestionRequest(questionContent2));

        List<TagRequest> tags = new ArrayList<>();
        tags.add(new TagRequest(tagContent1));
        tags.add(new TagRequest(tagContent2));

        return ArticleRequest.builder()
            .title(title)
            .content(content)
            .backgroundColor(backgroundColor)
            .questions(questions)
            .tags(tags)
            .build();
    }

    @Test
    @Order(1)
    @DisplayName("게시글 작성 API 테스트")
    public void createArticle() throws Exception {
        // given
        ArticleRequest createArticle = createArticleSaveRequest();

        // when
        mockMvc.perform(post("/articles")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createArticle)))
            .andExpect(status().isOk());

        // then
        Article article = articleRepository.findAll().get(0);
        assertThat(article.getTitle()).isEqualTo(createArticle.getTitle());
        assertThat(article.getContent()).isEqualTo(createArticle.getContent());
        assertThat(article.getBackgroundColor()).isEqualTo(createArticle.getBackgroundColor());
    }

    @Test
    @Order(2)
    @DisplayName("게시글 수정 API 테스트")
    public void modifyArticle() throws Exception {
        // given
        final String title = "제목 2";
        final String content = "내용 2";
        final String backgroundColor = "#FFEFFF";
        final String questionContent1 = "선택지 1";
        final String questionContent2 = "선택지 2";
        final String tagContent1 = "태그 1";
        final String tagContent2 = "태그 2";

        ArticleRequest modifyArticle = ArticleRequest.builder()
            .title(title)
            .content(content)
            .backgroundColor(backgroundColor)
            .questions(new ArrayList<>())
            .tags(new ArrayList<>())
            .build();

        Long id = articleRepository.findAll().get(0).getId();

        // when
        mockMvc.perform(
            put("/articles/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modifyArticle))
        )
            .andExpect(status().isOk());

        // then
        Article loadArticle = articleRepository.findAll().get(0);
        assertThat(loadArticle.getTitle()).isEqualTo(title);
        assertThat(loadArticle.getContent()).isEqualTo(content);
        assertThat(loadArticle.getBackgroundColor()).isEqualTo(backgroundColor);
    }

    @Test
    @Order(3)
    @DisplayName("게시글 조회 API 테스트")
    public void lookUpArticle() throws Exception {
        // given
        Article article = articleRepository.findAll().get(0);
        Long id = article.getId();

        // when
        mockMvc.perform(
            get("/articles/" + id)
        )
            .andExpect(status().isOk());

        // then
        assertThat(article.getTitle()).isEqualTo(article.getTitle());
        assertThat(article.getContent()).isEqualTo(article.getContent());
        assertThat(article.getBackgroundColor()).isEqualTo(article.getBackgroundColor());
    }

    @Test
    @Order(4)
    @DisplayName("게시글 삭제 API 테스트")
    public void deleteArticle() throws Exception {
        // given
        Long id = articleRepository.findAll().get(0).getId();

        // when
        mockMvc.perform(delete("/articles/" + id)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        // then
        List<Article> articles = articleRepository.findAll();
        assertThat(articles.size()).isEqualTo(0);
    }

    @Test
    @Order(5)
    @DisplayName("최신글 API 테스트")
    public void lookUpLatestArticle() throws Exception {
        // given
        ArticleRequest article1 = createArticleSaveRequest();
        ArticleRequest article2 = createArticleSaveRequest();
        articleRepository.save(article1.toEntity());
        articleRepository.save(article2.toEntity());

        List<ArticleLatestResponse> articleLatestResponses = articleService.lookUpLatest(10L);

        // when
        MvcResult result = mockMvc.perform(
            get("/articles/latest")
        )
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        // then
        assertThat(result.getResponse().getContentAsString()).isEqualTo(objectMapper.writeValueAsString(articleLatestResponses));
    }
}