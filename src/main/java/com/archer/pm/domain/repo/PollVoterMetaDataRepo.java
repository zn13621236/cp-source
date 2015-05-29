package com.archer.pm.domain.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.archer.pm.domain.db.PollVoterMetaData;

@Repository
public interface PollVoterMetaDataRepo extends PagingAndSortingRepository<PollVoterMetaData, String>{
    
    PollVoterMetaData findByPollGuid(String pollGuid);
    
}
