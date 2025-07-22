package com.gradation.zmnnoory.domain.member.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gradation.zmnnoory.domain.member.exception.InvalidGenderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum Gender {

    MALE("남성"),
    FEMALE("여성"),;

    private final String description;

    @JsonCreator
    public static Gender from(String value) {
        return Arrays.stream(Gender.values())
                .filter(g -> g.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new InvalidGenderException(value));
    }
}
