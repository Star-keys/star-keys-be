package com.starkeys.be.application.service;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.starkeys.be.application.repository.ExternalRepository;
import com.starkeys.be.common.request.PageRequest;
import com.starkeys.be.common.response.ApiResponse;
import com.starkeys.be.common.response.PageMeta;
import com.starkeys.be.dto.EsPaper;
import com.starkeys.be.dto.PaperSimple;
import com.starkeys.be.entity.Paper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.bool;

@RequiredArgsConstructor
@Service
public class ExternalService {
    private final ExternalRepository externalRepository;
    private final EsService esService;

    public ApiResponse<List<Paper>> getGraph() {
        var result = externalRepository.findAllPapers();
        return ApiResponse.success(result);
    }

    public ApiResponse<List<PaperSimple>> search(String q, PageRequest pageRequest) {
        Query query = new PaperQuery(q).build();

        SearchRequest.Builder request = new SearchRequest.Builder()
                .index("paper")
                .source(s -> s.fetch(true))
                .query(query)
                .from(pageRequest.offset())
                .size(pageRequest.size())
                .trackTotalHits(th -> th.enabled(true));

        Page<EsPaper> searched = esService.search(request.build(), EsPaper.class);
        PageMeta meta = new PageMeta(pageRequest.page(), searched.getTotalPages(), searched.getTotalElements());

        List<PaperSimple> paperSimples = searched.stream().map(PaperSimple::of).toList();
        return ApiResponse.success(paperSimples, meta);
    }

    public ApiResponse<EsPaper> getDetail(String paperId) {
        Query query = bool(b -> b
                .must(m -> m
                        .term(t -> t
                                .field("_id").value(paperId))
                )
        );

        SearchRequest.Builder request = new SearchRequest.Builder()
                .index("paper")
                .source(s -> s.fetch(true))
                .query(query)
                .size(1)
                .trackTotalHits(th -> th.enabled(false));

        EsPaper esPaper = esService.search(request.build(), EsPaper.class)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("paper not found"));

        return ApiResponse.success(esPaper);
    }

    private record PaperQuery(
            String value
    ) {
        public Query build() {
            Query multiMatch = QueryBuilders.multiMatch(mm -> mm
                    .query(this.value)
                    .fields(
                            "title^4",
                            "abstract^2",
                            "introduction",
                            "method",
                            "result",
                            "discussion",
                            "conclusion"
                    )
                    .type(TextQueryType.BestFields)
                    .fuzziness("AUTO")
                    .minimumShouldMatch("70%")
            );

            Query matchPhrase = QueryBuilders.matchPhrase(mp -> mp
                    .field("title")
                    .query(this.value)
                    .boost(2.0f)
            );

            return bool(b -> b.should(multiMatch, matchPhrase));
        }
    }
}
