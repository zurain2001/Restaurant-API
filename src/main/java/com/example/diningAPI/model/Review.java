package com.example.diningAPI.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class Review{

    @Id
    @GeneratedValue
    private Long id;
    private String submittedBy;
    private Long restaurantId;
    private Integer peanutScore;
    private Integer dairyScore;
    private Integer eggScore;
    private String review;

    private ReviewStatus status;
}