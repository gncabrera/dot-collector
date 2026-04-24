package com.nookx.api.scraper.repository;

import com.nookx.api.scraper.domain.ScrapePage;
import com.nookx.api.scraper.domain.enumeration.FetchStatus;
import com.nookx.api.scraper.domain.enumeration.PageType;
import com.nookx.api.scraper.domain.enumeration.ParseStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrapePageRepository extends JpaRepository<ScrapePage, Long> {
    Optional<ScrapePage> findBySourceCodeAndUrl(String sourceCode, String url);

    Optional<ScrapePage> findBySourceCodeAndPageTypeAndNaturalKey(String sourceCode, PageType pageType, String naturalKey);

    /**
     * Returns the next page whose {@code next_check_at} is due, across all enabled sources.
     * Ordered by due date ascending so oldest pending work is picked first.
     */
    @Query(
        """
        select p from ScrapePage p
         where p.sourceCode = :sourceCode
           and (p.fetchStatus = :pending or p.fetchStatus = :notModified or p.fetchStatus = :done
                or p.fetchStatus = :transientError or p.fetchStatus = :notFound)
           and p.nextCheckAt is not null
           and p.nextCheckAt <= :now
         order by p.nextCheckAt asc
        """
    )
    List<ScrapePage> findDueForFetch(
        @Param("sourceCode") String sourceCode,
        @Param("pending") FetchStatus pending,
        @Param("notModified") FetchStatus notModified,
        @Param("done") FetchStatus done,
        @Param("transientError") FetchStatus transientError,
        @Param("notFound") FetchStatus notFound,
        @Param("now") Instant now,
        Pageable pageable
    );

    List<ScrapePage> findByParseStatusOrderByFetchedAtAsc(ParseStatus parseStatus, Pageable pageable);
}
