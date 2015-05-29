package com.archer.pm.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.archer.pm.domain.db.Poll;
import com.archer.pm.domain.db.PollComment;
import com.archer.pm.domain.db.User;
import com.archer.pm.domain.model.Activity;
import com.archer.pm.domain.model.PollEntity;

public interface PollService {

    Poll getRandomPoll ();

    Poll getRandomPoll (User user);

    Poll getRandomPoll (String category);

    Poll createPoll (PollEntity poll, User user);

    Poll getRandomPoll (String category, User user);

    void deletePoll (String pollGuid, String userGuid);

    Page <Poll> getRandomPollList ();

    Page <Poll> getRandomPollList (User user);

    Page <Poll> getWatchedPoll (Pageable pageable, String userGuid);

    List <Poll> listVotedPolls (String userGuid);

    // Page<Poll> listVotedPolls(String userGuid, Pageable pageable);
    PollComment removeComment (String guid);

//    List <PollComment> getPollComments (String pollGuid);
    List <PollComment> getPollComments (String pollGuid,final Pageable pageable);

//    boolean vote (Poll pollToSave, String optionId, User u);

    PollComment addComment (String pollGuid, User user, PollComment comment);

    boolean pinPoll (String pollGuid, String userGuid);

    boolean unPinPoll (String pollGuid, String userGuid);

    boolean isPined (String pollGuid, String userGuid);

    List <Poll> getPinedPoll (String userGuid);

    boolean isQuailifiedForVote (User user, Poll poll);

    // List<Poll> getRecommendPollList(User user);
    Page <Poll> getRecommendPollList (User user, Pageable pageable);

    List <Activity> getIdolsActivity (User user, int page, int size);

    Page <Poll> getHotPoll (String userGuid, Pageable pageable);

    Page <Poll> getHotPoll (Pageable pageable);

    List <Activity> getUserActivity (User user, int page, int size);

    Page <Poll> getHotPollByCategory (String userGuid, String category, Pageable pageable);

    Page <Poll> getHotPollByCategory (String category, Pageable pageable);

    boolean vote (Poll pollToSave, String optionId, User u, HttpServletRequest httpServletRequest);


    boolean checkVoterIp (Poll p, HttpServletRequest httpServletRequest);

//    PollComment likeComment (String guid);
//
//    PollComment hateComment (String guid);
}
