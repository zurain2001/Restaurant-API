package com.example.diningAPI.controller;

import com.example.diningAPI.model.Restaurant;
import com.example.diningAPI.model.Review;
import com.example.diningAPI.model.Admin;
import com.example.diningAPI.model.User;
import com.example.diningAPI.model.ReviewStatus;
import com.example.diningAPI.repository.RestaurantRepository;
import com.example.diningAPI.repository.ReviewRepository;
import com.example.diningAPI.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.EntityManagerFactory;

import java.text.DecimalFormat;
import java.util.Optional;
import java.util.List;

@RequestMapping("/admin")
@RestController
public class AdminController {
    @PersistenceUnit
    private EntityManagerFactory emf;

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;

    private final DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public AdminController(final UserRepository userRepository, final RestaurantRepository restaurantRepository, final ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/reviews")
    public List<Review> getReviewByStatus(@RequestParam String review_status) {
        ReviewStatus reviewStatus = ReviewStatus.PENDING;
        try {
            reviewStatus = ReviewStatus.valueOf(review_status.toUpperCase());

        } catch (IllegalArgumentException xe) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return reviewRepository.findReviewsByStatus(reviewStatus);

    }

    @PutMapping("/reviews/{reviewId}")
    public void performReviewAction(Long reviewId, @RequestParam Admin adminReviewAction){
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if(optionalReview.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Review review = optionalReview.get();

        Optional<Restaurant> optionalRestaurant = restaurantRepository.findById(review.getRestaurantId());
        if(optionalRestaurant.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if(adminReviewAction.getAccept()){
            review.setStatus(ReviewStatus.ACCEPTED);
        }else{
            review.setStatus(ReviewStatus.REJECTED);
        }

        reviewRepository.save(review);
        updateRestaurantReviewScores(optionalRestaurant.get());

    }

    private void updateRestaurantReviewScores(Restaurant restaurant){
        List<Review> review = reviewRepository.findReviewsByRestaurantIdAndStatus(restaurant.getId(), ReviewStatus.ACCEPTED);
        if(review.size() == 0){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        int peanutSum = 0;
        int peanutCount = 0;
        int dairySum = 0;
        int dairyCount = 0;
        int eggSum = 0;
        int eggCount = 0;

        for(Review r : review){
            if(!ObjectUtils.isEmpty(r.getPeanutScore())){
                peanutSum += r.getPeanutScore();
                peanutCount++;
            }
            if(!ObjectUtils.isEmpty(r.getDairyScore())){
                dairySum += r.getDairyScore();
                dairyCount++;
            }
            if(ObjectUtils.isEmpty(r.getEggScore())){
                eggSum += r.getDairyScore();
                eggCount++;
            }
            int totalCount = peanutCount + dairyCount + eggCount;
            int totalSum = peanutSum + dairySum + eggSum;

            float overallScore = (float) totalSum/totalCount;

            restaurant.setOverallScore(decimalFormat.format(overallScore));

            if(peanutCount > 0){
                float peanutScore = (float) peanutSum/peanutCount;
                restaurant.setPeanutScore(decimalFormat.format(peanutScore));
            }
            if(dairyCount > 0){
                float dairyScore = (float) dairySum/dairyCount;
                restaurant.setDairyScore(decimalFormat.format(dairyScore));
            }
            if(eggCount > 0){
                float eggScore = (float) eggSum/eggCount;
                restaurant.setEggScore(decimalFormat.format(eggScore));
            }

            restaurantRepository.save(restaurant);

        }

    }

    public UserRepository getUserRepository() {
        return userRepository;
    }
}