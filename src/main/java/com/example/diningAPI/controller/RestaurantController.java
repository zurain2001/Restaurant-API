package com.example.diningAPI.controller;

import com.example.diningAPI.model.Restaurant;
import com.example.diningAPI.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.util.Optional;
import java.util.Collections;
import java.util.regex.Pattern;

@RequestMapping("/restaurants")
@RestController
public class RestaurantController{
    @PersistenceUnit
    private EntityManagerFactory emf;
    private final RestaurantRepository restaurantRepository;
    private final Pattern zipCodePattern = Pattern.compile("\\d{5}");

    public RestaurantController(final RestaurantRepository restaurantRepository){
        this.restaurantRepository = restaurantRepository;

    }

    @PostMapping
    public Restaurant createNewRestaurant(@RequestBody Restaurant restaurant){
        validateNewRestaurant(restaurant);
        Restaurant newRestaurant = this.restaurantRepository.save(restaurant);
        return newRestaurant;
    }

    @GetMapping("/{id}")
    public Restaurant getRestaurant(@PathVariable Long id){
        Optional<Restaurant> restaurant = this.restaurantRepository.findById(id);
        if(restaurant.isPresent()){
            return restaurant.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public Iterable<Restaurant> getAllRestaurants(){
        Iterable<Restaurant> getAllRestaurants = this.restaurantRepository.findAll();
        return getAllRestaurants;
    }

    @GetMapping("/search")
    public Iterable<Restaurant> searchRestaurants(@RequestParam String zipcode, @RequestParam String allergy){
        Iterable<Restaurant> restaurants = Collections.EMPTY_LIST;

        if(allergy.equalsIgnoreCase("peanut")){
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndPeanutScoreNotNullOrderByPeanutScore(zipcode);
        }
        else if(allergy.equalsIgnoreCase("dairy")){
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndDairyScoreNotNullOrderByDairyScore(zipcode);
        }
        else if(allergy.equalsIgnoreCase("egg")){
            restaurants = restaurantRepository.findRestaurantsByZipCodeAndEggScoreNotNullOrderByEggScore(zipcode);
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return restaurants;
    }

    private void validateNewRestaurant(Restaurant restaurant){
        if(ObjectUtils.isEmpty(restaurant.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        validateZipCode(restaurant.getZipCode());

        Optional<Restaurant> existingRestaurant = restaurantRepository.findRestaurantsByNameAndZipCode(restaurant.getName(), restaurant.getZipCode());
        if(existingRestaurant.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    private void validateZipCode(String zipcode){
        if(!zipCodePattern.matcher(zipcode).matches()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

}