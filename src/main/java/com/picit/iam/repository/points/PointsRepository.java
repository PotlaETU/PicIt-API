package com.picit.iam.repository.points;

import com.picit.iam.entity.points.Points;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PointsRepository extends MongoRepository<Points, String> {
    Optional<Points> findByUserId(String userId);
}
