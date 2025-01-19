package com.ttogal.domain.review.entity.constant;

public enum ReviewKeyword {
    AMAZING("😢 인생 맛집이에요!!! 무조건 재방문 예정"),
    GOOD("😆 꽤 맛있었어요! 재방문 할 것 같아요"),
    NOT_BAD("😉 낫배드~ 괜찮았어요"),
    SO_SO("😥 그냥 그랬어요.. 재방문은 딱히.."),
    BAD("😡 최악!!");

    private final String description;

    ReviewKeyword(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

