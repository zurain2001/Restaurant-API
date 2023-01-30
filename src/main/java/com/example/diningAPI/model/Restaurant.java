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
public class Restaurant{

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String city;
    private String country;
    private String overallScore;
    private String peanutScore;
    private String dairyScore;
    private String eggScore;

    private String zipCode;


}