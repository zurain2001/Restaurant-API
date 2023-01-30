package com.example.diningAPI.controller;

import com.example.diningAPI.model.Restaurant;
import com.example.diningAPI.model.Review;
import com.example.diningAPI.model.ReviewStatus;
import com.example.diningAPI.model.User;
import com.example.diningAPI.repository.RestaurantRepository;
import com.example.diningAPI.repository.ReviewRepository;
import com.example.diningAPI.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import java.util.Optional;

@RequestMapping("/reviews")
@RestController
public class ReviewController{
    @PersistenceUnit
    private EntityManagerFactory emf;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;


    public ReviewController(final ReviewRepository reviewRepository, final UserRepository userRepository, final RestaurantRepository restaurantRepository){
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void createNewReview(@RequestBody Review review){
        validateUserReview(review);
        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(review.getRestaurantId());
        if(optionalRestaurant.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        review.setStatus(ReviewStatus.PENDING);
        reviewRepository.save(review);

    }

    private void validateUserReview(Review review){
        if(ObjectUtils.isEmpty(review.getSubmittedBy())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if(ObjectUtils.isEmpty(review.getRestaurantId())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if(ObjectUtils.isEmpty(review.getDairyScore()) && ObjectUtils.isEmpty(review.getPeanutScore()) && ObjectUtils.isEmpty(review.getEggScore())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        Optional<User> optionalUser = userRepository.findUserByDisplayName(review.getSubmittedBy());
        if(optionalUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
