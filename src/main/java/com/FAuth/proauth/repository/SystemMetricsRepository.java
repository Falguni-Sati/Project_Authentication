package com.FAuth.proauth.repository;

import com.FAuth.proauth.entity.SystemMetrics;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SystemMetricsRepository extends MongoRepository<SystemMetrics, ObjectId> {
}
