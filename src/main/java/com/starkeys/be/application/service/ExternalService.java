package com.starkeys.be.application.service;

import com.starkeys.be.application.repository.ExternalRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExternalService {
    private final ExternalRepositoryImpl externalRepositoryImpl;
}
