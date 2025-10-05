package com.starkeys.be.adapter.api;

import com.starkeys.be.application.service.ExternalService;
import com.starkeys.be.common.request.PageRequest;
import com.starkeys.be.common.response.ApiResponse;
import com.starkeys.be.dto.EsPaper;
import com.starkeys.be.entity.Paper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/external")
@RestController
public class ExternalController {
    private final ExternalService externalService;

    //  일반 검색 - 필터
    @GetMapping("/papers")
    public ApiResponse getPapers(@RequestParam(required = false, value = "q") String q, PageRequest pageRequest) {
        return externalService.search(q, pageRequest);
    }

    //  그래프
    @GetMapping("/graph")
    public ApiResponse<List<Paper>> getGraph() {
        return externalService.getGraph();
    }

    //  상세페이지
    @GetMapping("/paper")
    public ApiResponse getPaper() {
        return new ApiResponse<>(true, "success", null, null);
    }
}
