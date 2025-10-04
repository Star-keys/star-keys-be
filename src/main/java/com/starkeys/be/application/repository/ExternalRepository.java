package com.starkeys.be.application.repository;

import com.starkeys.be.entity.Paper;

import java.util.List;

public interface ExternalRepository {

    void patchPapers();

    List<Paper> findAllPapers();

    void fetchPaper();
}
