package com.gradation.zmnnoory.domain.watch.repository;

import com.gradation.zmnnoory.domain.watch.entity.Watch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WatchRepository extends JpaRepository<Watch, Long> {

    Optional<Watch> findByMemberIdAndVideoId(Long memberId, Long videoId);

    boolean existsByMemberIdAndVideoId(Long memberId, Long videoId);

    long countByVideoId(Long videoId);
}
