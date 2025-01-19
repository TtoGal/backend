package com.ttogal.domain.albumItem.entity;

import com.ttogal.domain.album.entity.Album;
import com.ttogal.domain.restaurant.entity.Restaurant;
import com.ttogal.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "albumitem")
@Getter
public class AlbumItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "album_item_id")
    private Long albumItemId;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "is_public", columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean isPublic = true;
}
