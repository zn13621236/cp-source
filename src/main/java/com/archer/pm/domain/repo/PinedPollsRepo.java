package com.archer.pm.domain.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.archer.pm.domain.db.PinedPolls;

@Repository
public interface PinedPollsRepo extends PagingAndSortingRepository<PinedPolls, String>{
    
    PinedPolls findByUserGuid(String userGuid);
    
}
