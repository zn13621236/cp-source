package com.archer.pm.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.archer.pm.domain.db.User;

public interface UserService {

    int getUserVoteNumber (User user);

    int getTotalPollNumberForUser (User user);

    long reducePollPoints (User user, int reduceValue);

    boolean isUserQualifiedForImagePoll (User user);

    boolean isUserQualifiedForCreateTextPoll (User user);

    User create (User user);
    
    User create (String email);

    User create (User user, int pointToDeduct);

    boolean watchUser (String fanGuid, String idolGuid);

    boolean removeWatcher (String fanGuid, String idolGuid);

    boolean isIdol (String fanGuid, String idolGuid);

    List <User> getMyIdols (String userGuid);

    Page <User> getFansRankingList (int size);

    User increaseExp (User u, int voteExp2);

    List <User> getRecommendUser (User user, Pageable pg);

    List <User> getUserWithSameInterest (User user);

    User updateLastLogIn (User user);

    void prepareUser (HttpServletRequest httpServletRequest, User u);

    boolean checkUserName (String userName);

    boolean checkNickName (String nickName);


}
