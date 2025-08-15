package com.gradation.zmnnoory.domain.datarequest.entity;

public enum DataRequestStatus {
    PENDING,        // 대기중
    REVIEWING,      // 검토중
    APPROVED,       // 승인됨
    IN_PROGRESS,    // 진행중
    COMPLETED,      // 완료
    REJECTED        // 거절됨
}