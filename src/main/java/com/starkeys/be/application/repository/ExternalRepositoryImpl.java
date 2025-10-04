package com.starkeys.be.application.repository;

import com.starkeys.be.adapter.out.mongo.MongoDBRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ExternalRepositoryImpl implements ExternalRepository {
    private final MongoDBRepository mongoDBRepository;

    @Override
    public void patchPapers() {

    }

    @Override
    public void fetchGraph() {

    }

    @Override
    public void fetchPaper() {

    }
}
