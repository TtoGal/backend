package com.ttogal.domain.category.entity;

import com.ttogal.domain.album.entity.Album;
import com.ttogal.domain.restaurant.entity.Restaurant;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "category")
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long CategoryId;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @Column(unique = true, nullable = false)
    private String name;
}
