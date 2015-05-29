package com.archer.pm.domain.repo;
import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.archer.pm.domain.db.PollHistory;

@Repository
public interface PollHistoryRepo  extends PagingAndSortingRepository<PollHistory, String>{

    List<PollHistory> findAll();
    PollHistory findByUserGuid(final String userGuid);
}
