package com.archer.pm.domain.repo;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import com.archer.pm.domain.db.Poll;

@Repository
public interface PollRepo extends PagingAndSortingRepository<Poll, String>{

    List <Poll> findAll ();

    Page <Poll> findByRandomGreaterThanAndStatusAndInviteOnlyAndGuidNotIn (int random, final String status, final boolean inviteOnly, final List <String> guidList, final Pageable pageable);

    Page <Poll> findByRandomGreaterThanAndStatusAndInviteOnly (int random, final String status, final boolean inviteOnly, final Pageable pageable);

    Page <Poll> findByStatusAndInviteOnlyAndUserGuidNotInAndGuidNotInAndCategoryIn (final String status, final boolean inviteOnly,final String userGuid ,final List <String> guidList,List<String> categoryList,final Pageable pageable);
    
    Poll findByGuid (String guid);
    
    Poll  findByGuidAndStatus (final String guid, final String status);

    List <Poll> findByGuidIn (List <String> guidSet);
    Page <Poll> findByGuidIn (List <String> guidSet, final Pageable pageable);

    Page <Poll> findByStatusAndInviteOnlyAndModDateGreaterThanAndGuidNotInAndUserGuidNotIn (final String status, final boolean inviteOnly, final long modDate,final List <String> guidList,final String userGuid ,final Pageable pageable);

    Page <Poll> findByStatusAndInviteOnlyAndModDateGreaterThan (final String status, final boolean inviteOnly, final long modDate,final Pageable pageable);
 
    Page <Poll> findByStatusAndInviteOnlyAndModDateGreaterThanAndCategoryAndGuidNotInAndUserGuidNotIn (final String status, final boolean inviteOnly, final long modDate,final String category,final List <String> guidList,final String userGuid ,final Pageable pageable);

    Page <Poll> findByStatusAndInviteOnlyAndModDateGreaterThanAndCategory (final String status, final boolean inviteOnly, final long modDate,final String category,final Pageable pageable);

    
    List <Poll> findByUserGuid (final String userGuid);

    Page <Poll> findByUserGuid (final String userGuid, final Pageable pageable);

    List <Poll> findByUserGuidAndStatus (final String userGuid, final String status);
    
    @Query(value = "{'userGuid': ?0}, 'status': ?1}", count = true)
    Long countByUserGuidAndStatus(final String userGuid, final String status);

    Page <Poll> findByUserGuidAndStatus (final String userGuid, final String status, final Pageable pageable);

    Page <Poll> findByUserGuidAndStatusAndInviteOnly (final String userGuid, final String status,final boolean inviteOnly ,final Pageable pageable);

    
    Poll findOneByRandomGreaterThanAndStatusAndInviteOnlyAndUserGuidNotInAndGuidNotIn (int random, final String status, final boolean inviteOnly, final String userGuid, final List <String> guidList);

    Poll findOneByRandomGreaterThanAndStatusAndInviteOnlyAndUserGuidNotIn (int random, final String status, final boolean inviteOnly, final String userGuid);

    Poll findOneByRandomGreaterThanAndStatusAndCategoryAndInviteOnly (int random, final String status, String category, final boolean inviteOnly);

    Poll findOneByRandomGreaterThanAndStatusAndCategoryAndInviteOnlyAndUserGuidNotInAndGuidNotIn (int random, final String status, final String category, final boolean inviteOnly,
            final String userGuid, final List <String> guidList);

    Poll findOneByRandomGreaterThanAndStatusAndCategoryAndInviteOnlyAndUserGuidNotIn (int random, final String status, final String category, final boolean inviteOnly, final String userGuid);

    @Query ("{ 'random' : {$gt:?0},'status':?1,'category':?2, 'inviteOnly':?3,'userGuid':{$ne:?4},'profile.gender':?5,'profile.minAge':{$lte:?6},'profile.maxAge':{$gte:?7} }")
    Poll findOneRandom (int random, final String status, final String category, final boolean inviteOnly, final String userGuid, final String gender, final int start, final int end);

    @Query ("{ 'random' : {$gt:?0},'status':?1,'category':?2, 'inviteOnly':?3,'userGuid':{$ne:?4},'profile.gender':?5}")
    Poll findOneRandom (int random, final String status, final String category, final boolean inviteOnly, final String userGuid, final String gender);

    @Query ("{ 'random' : {$gt:?0},'status':?1,'category':?2, 'inviteOnly':?3,'userGuid':{$ne:?4},'profile.minAge':{$lte:?5},'profile.maxAge':{$gte:?6} }")
    Poll findOneRandom (int random, final String status, final String category, final boolean inviteOnly, final String userGuid, final int start, final int end);

    @Query ("{ 'random' : {$gt:?0},'status':?1, 'inviteOnly':?2,'userGuid':{$ne:?3},'profile.gender':?4,'profile.minAge':{$lte:?5},'profile.maxAge':{$gte:?6} }")
    Poll findOneRandom (int random, final String status, final boolean inviteOnly, final String userGuid, final String gender, final int start, final int end);

    @Query ("{ 'random' : {$gt:?0},'status':?1, 'inviteOnly':?2,'userGuid':{$ne:?3},'profile.gender':?4}")
    Poll findOneRandom (int random, final String status, final boolean inviteOnly, final String userGuid, final String gender);

    @Query ("{ 'random' : {$gt:?0},'status':?1, 'inviteOnly':?2,'userGuid':{$ne:?3},'profile.minAge':{$lte:?4},'profile.maxAge':{$gte:?5} }")
    Poll findOneRandom (int random, final String status, final boolean inviteOnly, final String userGuid, final int start, final int end);

    Page <Poll> findByRandomGreaterThanAndStatusAndInviteOnlyAndUserGuidNotIn (final int randomNum,final String status,final boolean inviteOnly,final String userGuid,final Pageable pr);

   
    Page <Poll> findByRandomGreaterThanAndStatusAndInviteOnlyAndUserGuidNotInAndGuidNotIn (final int randomNum,final String status,final boolean inviteOnly,final String userGuid,final List <String> pollList,final Pageable pr);

    Page <Poll> findByStatusAndInviteOnlyAndModDateGreaterThanAndUserGuidNotIn (String status, boolean inviteOnly, long modDate, String userGuid, Pageable pageable);

    Page <Poll> findByStatusAndInviteOnlyAndModDateGreaterThanAndCategoryAndUserGuidNotIn (String status, boolean inviteOnly, long modDate, String userGuid, String category, Pageable pageable);
}
