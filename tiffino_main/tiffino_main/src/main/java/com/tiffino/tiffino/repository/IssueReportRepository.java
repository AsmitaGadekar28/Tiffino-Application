package com.tiffino.tiffino.repository;

import com.tiffino.tiffino.entity.IssueReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueReportRepository extends JpaRepository<IssueReport, Long> {
    List<IssueReport> findAllByOrderByIdDesc();

    List<IssueReport> findByCloudKitchenId(String cloudKitchenId);

}
