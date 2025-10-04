package com.starkeys.be.adapter.out.mongo;

import com.starkeys.be.entity.Paper;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoDBRepository extends MongoRepository<Paper, String> {
}
