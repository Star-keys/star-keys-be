package com.starkeys.be.application.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.starkeys.be.util.EsUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EsService {

    private final ElasticsearchClient esClient;

    public <T> Page<T> search(SearchRequest request, Class<T> clazz) {
        SearchResponse<T> response = searchInternal(request, clazz);
        HitsMetadata<T> hits = response.hits();

        List<T> list = hits.hits()
                .stream()
                .map(Hit::source)
                .toList();

        return new PageImpl<>(list, EsUtils.getPageable(request), EsUtils.getTotal(response));
    }

    private <T> SearchResponse<T> searchInternal(SearchRequest request, Class<T> clazz) {
        try {
            return esClient.search(request, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException("Elasticsearch exception: " + e.getMessage());
        }
    }
}
