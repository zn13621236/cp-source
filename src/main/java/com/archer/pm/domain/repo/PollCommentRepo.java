package com.archer.pm.domain.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.archer.pm.domain.db.PollComment;

@Repository
public interface PollCommentRepo  extends PagingAndSortingRepository<PollComment, String>{
    
    Page<PollComment> findByPollGuidAndStatus(String pollGuid,String status,Pageable pageable);
    PollComment findByGuid(String guid);   
    PollComment findByGuidAndStatus(String guid,String status);    
    List<PollComment> findByNickName(final String nickName);
    Page<PollComment> findByNickNameAndStatusAndInviteOnly(final String nickName,final String status,final boolean inviteOnly,final Pageable pageable);
}
