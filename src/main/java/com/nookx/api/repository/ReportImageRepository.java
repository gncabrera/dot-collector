package com.nookx.api.repository;

import com.nookx.api.domain.ReportImage;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface ReportImageRepository extends JpaRepository<ReportImage, Long> {
    List<ReportImage> findByReport_Id(Long reportId);

    List<ReportImage> findByReport_IdIn(Collection<Long> reportIds);
}
