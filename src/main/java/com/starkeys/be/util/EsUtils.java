package com.starkeys.be.util;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EsUtils {

    public static Pageable getPageable(SearchRequest request) {
        Integer size = request.size();
        if (size == null || size == 0) {
            // No size info. Return default pageable.
            return Pageable.ofSize(10);
        }

        Integer from = request.from();
        if (from == null) {
            // No from info. Assume it's first page.
            return Pageable.ofSize(size);
        }

        return Pageable.ofSize(size)
                .withPage(from / size);
    }

    public static long getTotal(SearchResponse<?> response) {
        HitsMetadata<?> hits = response.hits();
        return Optional.ofNullable(hits.total())
                .map(TotalHits::value)
                .orElse((long) hits.hits().size());
    }
}
