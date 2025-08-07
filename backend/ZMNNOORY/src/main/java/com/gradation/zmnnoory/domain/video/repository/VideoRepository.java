package com.gradation.zmnnoory.domain.video.repository;

import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByIsPublicTrueAndVideoUrlIsNotNull();

    List<Video> findAllByParticipation_Member(Member member);

}