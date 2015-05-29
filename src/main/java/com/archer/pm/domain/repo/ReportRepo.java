package com.archer.pm.domain.repo;
import java.math.BigInteger;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.archer.pm.domain.db.Report;
@Repository
public interface ReportRepo  extends PagingAndSortingRepository<Report, BigInteger>{

    List<Report> findAll();
}
