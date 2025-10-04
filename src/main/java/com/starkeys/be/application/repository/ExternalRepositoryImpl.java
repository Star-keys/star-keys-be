package com.starkeys.be.application.repository;

import com.starkeys.be.adapter.out.mongo.MongoDBRepository;
import com.starkeys.be.entity.Paper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ExternalRepositoryImpl implements ExternalRepository {
    private final MongoDBRepository mongoDBRepository;

    @Override
    public void patchPapers() {

    }

    @Override
    public List<Paper> findAllPapers() {
        return mongoDBRepository.findAll();
    }

    @Override
    public void fetchPaper() {

    }
}
