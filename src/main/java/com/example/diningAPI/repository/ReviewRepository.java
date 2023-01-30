package com.example.diningAPI.repository;

import com.example.diningAPI.model.Review;
import com.example.diningAPI.model.ReviewStatus;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReviewRepository extends CrudRepository<Review, Long>{
    List<Review> findReviewsByStatus(ReviewStatus status);
    List<Review> findReviewsByRestaurantIdAndStatus(Long restaurantId, ReviewStatus status);

}