package com.archer.pm.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import com.archer.pm.cypher.CipherService;
import com.archer.pm.domain.db.Idols;
import com.archer.pm.domain.db.Poll;
import com.archer.pm.domain.db.PollHistory;
import com.archer.pm.domain.db.User;
import com.archer.pm.domain.repo.IdolsRepo;
import com.archer.pm.domain.repo.PollHistoryRepo;
import com.archer.pm.domain.repo.PollRepo;
import com.archer.pm.domain.repo.UserRepo;
import com.archer.pm.service.email.EmailService;

@Service
public class UserServiceImpl implements UserService {

    private static Logger log = Logger.getLogger (UserServiceImpl.class.getName ());
    @Value ("${user.level.base}")
    private int           levelBase;
    @Value ("${user.level.increse.power}")
    private int           levelpower;
    @Value ("${fan.exp}")
    private int           fanExp;
    @Value ("${user.default.image}")
    private String        defaultUserImage;
    @Value ("${poll.points.text}")
    private int           textPollPoints;
    @Value ("${poll.points.image}")
    private int           imagePollPoints;
    @Value ("${user.initial.points}")
    private int           initialPoints;
    @Autowired
    UserRepo              userRepo;
    @Autowired
    PollHistoryRepo       phRepo;
    @Autowired
    PollRepo              pollRepo;
    @Autowired
    IdolsRepo             idolRepo;
    @Autowired
    private CipherService cs;
    @Autowired
    private EmailService  es;
    @Autowired
    IdolsRepo             iRepo;

    @Override
    public List <User> getUserWithSameInterest (User user) {
        Idols idol = idolRepo.findByUserGuid (user.getGuid ());
        List <User> uList = new ArrayList <User> ();
        List <User> newList = null;
        List <String> guidList = new ArrayList <String> ();
        if (idol != null) {
            Set <String> guidSet = idol.getIdolList ();
            guidList.addAll (guidSet);
            guidList.add (user.getGuid ());
            List <String> getInterestList = user.getInterestList () == null ? new ArrayList <String> () : user.getInterestList ();
            newList = userRepo.findByStatusAndGuidNotInAndInterestListIn ("active", guidList, getInterestList, new PageRequest (0, 30)).getContent ();
            uList.addAll (newList);
        }
        if (uList == null || uList.size () == 0) {
            newList = userRepo.findByStatusAndGuidNotIn ("active", guidList, new PageRequest (0, 30)).getContent ();
            uList.addAll (newList);
        } else if (uList.size () < 3) {
            newList = userRepo.findByStatusAndGuidNotIn ("active", guidList, new PageRequest (0, 4 - uList.size ())).getContent ();
            uList.addAll (newList);
        }
        log.info ("this is recommend user list size: = " + uList.size ());
        return uList;
    }

    @Override
    public boolean isUserQualifiedForCreateTextPoll (User user) {
        if (user.getPoints () > textPollPoints) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isUserQualifiedForImagePoll (User user) {
        if (user.getPoints () > imagePollPoints) {
            return true;
        }
        return false;
    }

    @Override
    public long reducePollPoints (User user, int reduceValue) {
        long value = user.getPoints () - reduceValue;
        user.setPoints (value);
        userRepo.save (user);
        return value;
    }

    @Override
    public int getUserVoteNumber (User user) {
        PollHistory ph = phRepo.findByUserGuid (user.getGuid ());
        if (ph == null)
            return 0;
        else
            return ph.getPollList ().size ();
    }

    @Override
    public int getTotalPollNumberForUser (User user) {
        List <Poll> pList = pollRepo.findByUserGuid (user.getGuid ());
        if (pList == null)
            return 0;
        else
            return pList.size ();
    }

    @Override
    public User create (User user) {
        return create (user, 0);
    }

    @Override
    public User create (User user, int pointToDeduct) {
        long currentTime = System.currentTimeMillis ();
        user.setCreateDate (currentTime);
        user.setGuid (UUID.randomUUID ().toString ().replace ("-", ""));
        user.setPoints (initialPoints - pointToDeduct);
        user.setStatus ("active");
        log.info ("this is user password:  =" + user.getPassWord ());
        user.setPassWord (cs.encrypt (user.getPassWord ()));
        user.setExp (0);
        user.setExpLimit (levelBase);
        user.setLevel (0);
        user.setLastLogInTime (currentTime);
        user.setLogInTime (currentTime);
        if (user.getUserImage () == null) {
            user.setUserImage (defaultUserImage);
        }
        userRepo.save (user);
        // send welcome email...
        // es.sendEmail ("1", new String[] { user.getUserName () });
        return user;
    }
    //used by fb create user
    @Override
    public User create (String email) {
        User user = new User();
        user.setUserName (email);
        long currentTime = System.currentTimeMillis ();
        user.setCreateDate (currentTime);
        user.setGuid (UUID.randomUUID ().toString ().replace ("-", ""));
        user.setPoints (initialPoints);
        user.setStatus ("active");
        user.setPassWord (cs.encrypt ("1234"));
        user.setExp (0);
        user.setExpLimit (levelBase);
        user.setLevel (0);
        user.setLastLogInTime (currentTime);
        user.setLogInTime (currentTime);
        if (user.getUserImage () == null) {
            user.setUserImage (defaultUserImage);
        }
        userRepo.save (user);
        // send welcome email...
        // es.sendEmail ("1", new String[] { user.getUserName () });
        return user;
    }

    @Override
    public User updateLastLogIn (User user) {
        long currentTime = System.currentTimeMillis ();
        user.setLastLogInTime (user.getLogInTime ());
        user.setLogInTime (currentTime);
        return userRepo.save (user);
    }

    @Override
    public boolean watchUser (String fanGuid, String idolGuid) {
        if (fanGuid.equalsIgnoreCase (idolGuid)) {
            return false;
        }
        // find my idol list, add idol..
        Idols idol = idolRepo.findByUserGuid (fanGuid);
        if (idol != null) {
            idol.getIdolList ().add (idolGuid);
            idol.setModDate (System.currentTimeMillis ());
        } else {
            idol = new Idols (fanGuid);
        }
        idolRepo.save (idol);
        // increase fan number in User collection
        User u = userRepo.findByGuidAndStatus (idolGuid, "active");
        u.setFanNum (u.getFanNum () + 1);
        // add exp to idol..
        u = increaseExp (u, fanExp);
        userRepo.save (u);
        return true;
    }

    @Override
    public User increaseExp (User u, int voteExp2) {
        // see if user qualified for level up
        int newExp = (int) (u.getExp () + voteExp2);
        if (newExp >= u.getExpLimit ()) {
            u.setExp (newExp - u.getExpLimit ());
            u.setExpLimit (u.getExpLimit () + levelpower * (u.getLevel () == 0 ? 1 : u.getLevel ()));
            u.setLevel (u.getLevel () + 1);
            return u;
        } else {
            u.setExp (newExp);
            return u;
        }
    }

    @Override
    public boolean removeWatcher (String fanGuid, String idolGuid) {
        Idols idol = idolRepo.findByUserGuid (fanGuid);
        if (idol == null) {
            return false;
        } else {
            idol.getIdolList ().remove (idolGuid);
            idolRepo.save (idol);
            // decrease fan number in User collection
            User u = userRepo.findByGuidAndStatus (idolGuid, "active");
            if (u.getFanNum () > 0) {
                u.setFanNum (u.getFanNum () - 1);
            }
            userRepo.save (u);
            return true;
        }
    }

    @Override
    public boolean isIdol (String fanGuid, String idolGuid) {
        Idols idol = idolRepo.findByUserGuid (fanGuid);
        if (idol != null) {
            return idol.getIdolList ().contains (idolGuid);
        } else {
            return false;
        }
    }

    @Override
    public List <User> getMyIdols (String userGuid) {
        Idols idol = idolRepo.findByUserGuid (userGuid);
        if (idol != null) {
            List <User> uList = new ArrayList <User> ();
            for (String guid: idol.getIdolList ()) {
                User user = userRepo.findByGuidAndStatus (guid, "active");
                if (user != null) {
                    uList.add (user);
                }
            }
            return uList;
        } else
            return null;
    }

    @Override
    public Page <User> getFansRankingList (int size) {
        PageRequest request = new PageRequest (0, size, Direction.DESC, "fanNum");
        Page <User> uPage = userRepo.findAll (request);
        return uPage;
    }

    @Override
    public List <User> getRecommendUser (User user, Pageable pg) {
        List <User> uList = new ArrayList <User> ();
        Set <String> idolGuidSet = getRecommendUserByIdolList (user, new HashSet <String> ());
        if (idolGuidSet != null && idolGuidSet.size () > 0) {
            for (String guid: idolGuidSet) {
                User u = userRepo.findByGuidAndStatus (guid, "active");
                uList.add (u);
            }
        }
        if (uList.size () < 30) {
            uList.addAll (getUserWithSameInterest (user));
        }
        return uList;
    }

    @Override
    public void prepareUser (HttpServletRequest httpServletRequest, User u) {
        httpServletRequest.getSession ().setAttribute ("userId", u);
        updateLastLogIn (u);
        List <User> uList = getRecommendUser (u, new PageRequest (0, 4));
        if (uList.size () > 4) {
            uList = uList.subList (0, 4);
        }
        httpServletRequest.getSession ().setAttribute ("recommend_users", uList);
    }
    @Override
    public boolean checkUserName (String userName) {
        User user = userRepo.findByUserNameAndStatus (userName, "active");
        if (user != null) {
            return true;
        }
        return false;
    }
    @Override
    public boolean checkNickName (String nickName) {
        User user = userRepo.findByNickNameAndStatus (nickName, "active");
        if (user != null) {
            return true;
        }
        return false;
    }

    // private List <User> getUserWithSameInterest (User user, Pageable pg) {
    // return userRepo.findByStatusAndInterestListIn ("active", user.getInterestList (), pg).getContent ();
    // }
    private Set <String> getRecommendUserByIdolList (User user, Set <String> guidSet) {
        Idols idols = iRepo.findByUserGuid (user.getGuid ());
        if (idols == null) {
            return null;
        }
        Set <String> idolList = idols.getIdolList ();
        for (String guid: idolList) {
            User u = userRepo.findByGuidAndStatus (guid, "active");
            if (u != null) {
                idols = iRepo.findByUserGuid (u.getGuid ());
                if (idols != null) {
                    guidSet.addAll (idols.getIdolList ());
                    guidSet.removeAll (idolList);
                }
            }
        }
        if (guidSet.size () < 30) {
            for (String guid: idolList) {
                User u = userRepo.findByGuidAndStatus (guid, "active");
                if (u != null) {
                    getRecommendUserByIdolList (u, guidSet);
                }
            }
        }
        return guidSet;
    }
}
