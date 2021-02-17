package com.selection.domain.article;

import com.google.common.collect.Sets;
import com.selection.domain.BaseEntity;
import com.selection.dto.question.ChoiceRequest;
import com.selection.dto.question.ChoiceResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Embeddable
public class Choices {

    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = CascadeType.ALL)
    private final List<Choice> choices = new ArrayList<>();

    protected int size() {
        return choices.size();
    }

    protected Optional<Choice> get(Long choiceId) {
        return choices.stream()
            .filter(choice -> choice.getId().equals(choiceId))
            .findFirst();
    }

    protected void add(Choice choice) {
        this.choices.add(choice);
    }

    protected void addAll(List<Choice> choices) {
        this.choices.addAll(choices);
    }

    protected void delete(Long choiceId) {
        choices.removeIf(choice -> choice.getId().equals(choiceId));
    }

    private void modifyContent(Long choiceId, String content) {
        Choice findChoice = get(choiceId)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("해당 선택지(%s)는 존재하지 않습니다.", choiceId)));

        findChoice.modifyContent(content);
    }

    protected void modify(Article article, List<ChoiceRequest> choiceRequests) {
        Set<Long> choicesId = choices.stream()
            .map(BaseEntity::getId)
            .collect(Collectors.toSet());

        Set<Long> choiceRequestsId = choiceRequests.stream()
            .map(ChoiceRequest::getId)
            .collect(Collectors.toSet());

        // 삭제
        Sets.difference(choicesId, choiceRequestsId).forEach(this::delete);

        // 수정
        choiceRequests.stream()
            .filter(choiceRequest -> choicesId.contains(choiceRequest.getId()))
            .forEach(choiceRequest -> modifyContent(choiceRequest.getId(),
                choiceRequest.getContent()));

        // 추가
        choiceRequests.stream()
            .filter(choiceRequest -> !choicesId.contains(choiceRequest.getId()))
            .map(choiceRequest -> choiceRequest.toEntity(article))
            .forEach(this::add);
    }

    protected List<ChoiceResponse> toResponse() {
        return choices.stream()
            .map(ChoiceResponse::new)
            .collect(Collectors.toList());
    }
}
