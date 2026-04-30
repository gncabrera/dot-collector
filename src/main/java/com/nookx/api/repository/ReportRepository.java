package com.nookx.api.repository;

import com.nookx.api.domain.Report;
import com.nookx.api.domain.enumeration.ReportCategory;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByCategoryIn(Collection<ReportCategory> categories, Pageable pageable);

    List<Report> findByCategoryIn(Collection<ReportCategory> categories);
}
