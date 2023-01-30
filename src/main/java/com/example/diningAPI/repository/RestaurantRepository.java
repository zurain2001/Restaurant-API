package com.example.diningAPI.repository;

import com.example.diningAPI.model.Restaurant;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends CrudRepository<Restaurant, Long>{
    Optional<Restaurant> findRestaurantsByNameAndZipCode(String name, String zipCode);
    List<Restaurant> findRestaurantsByZipCodeAndPeanutScoreNotNullOrderByPeanutScore(String zipCode);
    List<Restaurant> findRestaurantsByZipCodeAndDairyScoreNotNullOrderByDairyScore(String zipCode);
    List<Restaurant> findRestaurantsByZipCodeAndEggScoreNotNullOrderByEggScore(String zipCode);
    List<Restaurant> findRestaurantsByCityAndCountryOrderByOverallScore(String city, String country);
}
