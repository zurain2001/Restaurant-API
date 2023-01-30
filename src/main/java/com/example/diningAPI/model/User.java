package com.example.diningAPI.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
public class User{
    @Id
    @GeneratedValue
    private Long id;

    private String displayName;
    private String city;
    private String state;
    private String zipcode;
    private Boolean seePeanuts;
    private Boolean seeDairy;
    private Boolean seeEggs;

}