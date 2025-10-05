package com.starkeys.be.adapter.api;

import com.starkeys.be.application.service.ExternalService;
import com.starkeys.be.common.request.PageRequest;
import com.starkeys.be.common.response.ApiResponse;
import com.starkeys.be.entity.Paper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/paper/{paperId}")
    public ApiResponse getPaper(@PathVariable String paperId) {
        return externalService.getDetail(paperId);
    }
}
