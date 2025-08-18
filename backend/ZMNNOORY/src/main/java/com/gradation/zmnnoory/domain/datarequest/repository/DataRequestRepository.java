package com.gradation.zmnnoory.domain.datarequest.repository;

import com.gradation.zmnnoory.domain.datarequest.entity.DataRequest;
import com.gradation.zmnnoory.domain.datarequest.entity.DataRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DataRequestRepository extends JpaRepository<DataRequest, Long> {

    List<DataRequest> findByContactInfoOrderByCreatedAtDesc(String contactInfo);

    List<DataRequest> findAllByOrderByCreatedAtDesc();

    Page<DataRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<DataRequest> findByStatusOrderByCreatedAtDesc(DataRequestStatus status);

    @Query("SELECT dr FROM DataRequest dr WHERE dr.status = :status ORDER BY dr.createdAt DESC")
    Page<DataRequest> findByStatusOrderByCreatedAtDesc(@Param("status") DataRequestStatus status, Pageable pageable);

    long countByStatus(DataRequestStatus status);
}