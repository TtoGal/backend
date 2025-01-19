package com.ttogal.domain.file.entity;

import com.ttogal.common.entity.BaseEntity;
import com.ttogal.domain.bookmark.entity.Bookmark;
import com.ttogal.domain.restaurant.entity.Restaurant;
import com.ttogal.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "file")
@Getter
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "bookmark_id", nullable = false)
    private Bookmark bookmark;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String storeFilename;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

}

