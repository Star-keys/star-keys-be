package com.starkeys.be.application.service;

import com.starkeys.be.application.repository.ExternalRepository;
import com.starkeys.be.application.repository.ExternalRepositoryImpl;
import com.starkeys.be.common.response.ApiResponse;
import com.starkeys.be.entity.Paper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ExternalService {
    private final ExternalRepository externalRepository;

    public ApiResponse<List<Paper>> getGraph() {
        var result = externalRepository.findAllPapers();
        return ApiResponse.success(result);
    }
}
