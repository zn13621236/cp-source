package com.archer.pm.domain.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.archer.pm.domain.db.Idols;

@Repository
public interface IdolsRepo extends PagingAndSortingRepository<Idols, String>{
    
    Idols findByUserGuid(String userGuid);
    
}
