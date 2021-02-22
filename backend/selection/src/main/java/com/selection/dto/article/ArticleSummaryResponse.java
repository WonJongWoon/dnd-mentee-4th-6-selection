package com.selection.dto.article;


import com.selection.dto.goguma.HotGogumaType;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ArticleSummaryResponse {

    @ApiModelProperty(notes = "번호", required = true, example = "1")
    private Long id;
    @ApiModelProperty(notes = "제목", required = true, example = "제목")
    private String title;
    @ApiModelProperty(notes = "본문", required = true, example = "본문")
    private String content;
    @ApiModelProperty(notes = "작성자", required = true, example = "xxx@google.com")
    private String author;
    @ApiModelProperty(notes = "고구마수", required = true, example = "100")
    private Long numOfGogumas;
    @ApiModelProperty(notes = "핫 고구마 타입", required = true, example = "DRAFTGUMA")
    private HotGogumaType hotGogumaType;
    @ApiModelProperty(notes = "작성일자", required = true)
    private LocalDateTime createdAt;

    public ArticleSummaryResponse(ArticleSummaryProjection articleSummaryProjection) {
        this.id = articleSummaryProjection.getId();
        this.title = articleSummaryProjection.getTitle();
        this.content = articleSummaryProjection.getContent();
        this.author = articleSummaryProjection.getAuthor();
        this.numOfGogumas = articleSummaryProjection.getNumOfGogumas();
        this.hotGogumaType = calculateHotGogumaType(this.numOfGogumas);
        this.createdAt = articleSummaryProjection.getCreatedAt().toLocalDateTime();
    }

    private HotGogumaType calculateHotGogumaType(Long numOfGogumas) {
        if (numOfGogumas >= 50) {
            return HotGogumaType.HONORGUMA;
        } else if (numOfGogumas >= 10) {
            return HotGogumaType.FIREGOUMA;
        } else {
            return HotGogumaType.DRAFTGUMA;
        }
    }
}