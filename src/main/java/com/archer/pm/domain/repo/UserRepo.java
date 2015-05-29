package com.archer.pm.domain.repo;

import java.math.BigInteger;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.archer.pm.domain.db.User;

@Repository
public interface UserRepo extends PagingAndSortingRepository<User, BigInteger>{

    List <User> findAll ();
    Page<User> findByStatusAndGuidNotInAndInterestListIn (final String status,final List<String> guidSet,final List<String> interestList,final Pageable pageable );
    Page<User> findByStatusAndGuidNotIn(final String status,final List<String> guidSet,final Pageable pageable );
    User findByUserNameAndStatus (final String userName, final String status);
    
    Page<User> findByStatusAndInterestListIn (final String status,final List<String> interestList,final Pageable pageable );

    User findByNickNameAndStatus (final String nickName, final String status);

    User findByGuidAndStatus (final String guid, final String status);

    Page <User> findAll (final Pageable pageable);
}
