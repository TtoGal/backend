package com.ttogal.domain.bookmark.repository;

import com.ttogal.domain.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookMarkRepository extends JpaRepository<Bookmark, Long> {

}
