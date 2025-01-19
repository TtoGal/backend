package com.ttogal.domain.albumItem.repository;

import com.ttogal.domain.albumItem.entity.AlbumItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumItemRepository extends JpaRepository<AlbumItem, Long> {

}
