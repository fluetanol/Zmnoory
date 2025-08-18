package com.gradation.zmnnoory.domain.participation.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ParticipationStatus {

    NOT_PARTICIPATED("미참여"),
    COMPLETED("참여 완료");

    private final String description;
}
