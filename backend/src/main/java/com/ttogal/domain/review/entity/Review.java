package com.ttogal.domain.review.entity;

import com.ttogal.common.entity.BaseEntity;
import com.ttogal.domain.restaurant.entity.Restaurant;
import com.ttogal.domain.review.entity.constant.ReviewKeyword;
import com.ttogal.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "review")
@Getter
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewKeyword keywords;

}

