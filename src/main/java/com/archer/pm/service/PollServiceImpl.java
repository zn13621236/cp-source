package com.archer.pm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import com.archer.pm.domain.db.Idols;
import com.archer.pm.domain.db.PinedPolls;
import com.archer.pm.domain.db.Poll;
import com.archer.pm.domain.db.PollComment;
import com.archer.pm.domain.db.PollHistory;
import com.archer.pm.domain.db.PollVoterMetaData;
import com.archer.pm.domain.db.User;
import com.archer.pm.domain.model.Activity;
import com.archer.pm.domain.model.Option;
import com.archer.pm.domain.model.PollEntity;
import com.archer.pm.domain.model.PollProfile;
import com.archer.pm.domain.repo.IdolsRepo;
import com.archer.pm.domain.repo.PinedPollsRepo;
import com.archer.pm.domain.repo.PollCommentRepo;
import com.archer.pm.domain.repo.PollHistoryRepo;
import com.archer.pm.domain.repo.PollRepo;
import com.archer.pm.domain.repo.PollVoterMetaDataRepo;
import com.archer.pm.domain.repo.UserRepo;
import com.archer.pm.service.s3.S3Service;

@Service
public class PollServiceImpl implements PollService {

    @Value ("${app.host.vote}")
    private String        pollHostName;
    @Value ("${s3.bucket.name}")
    private String        bucketName;
    @Value ("${vote.exp}")
    private int           voteExp;
    @Value ("${user.level.increse.power}")
    private int           levelpower;
    private int           range = 1000;
    @Autowired
    PollRepo              pollRepo;
    @Autowired
    UserService           uService;
    @Autowired
    UserRepo              userRepo;
    @Autowired
    PollHistoryRepo       phRepo;
    @Autowired
    S3Service             s3Service;
    @Autowired
    PollCommentRepo       cp;
    @Autowired
    IdolsRepo             iRepo;
    @Autowired
    CommonService         cs;
    @Autowired
    PinedPollsRepo        ppRepo;
    @Autowired
    PollCommentRepo       pcRepo;
    @Autowired
    PollVoterMetaDataRepo pvmRepo;

    @Override
    public Poll getRandomPoll () {
        Random random = new Random ();
        int r = random.nextInt (range);
        PageRequest p = new PageRequest (0, 30);
        int count = 0;
        List <Poll> pList = new ArrayList <Poll> ();
        while (pList.size () == 0) {
            r = random.nextInt (range);
            Page <Poll> p2 = pollRepo.findByRandomGreaterThanAndStatusAndInviteOnly (r, "active", false, p);
            pList.addAll (p2.getContent ());
            System.out.println ("this is count: = " + count);
            count++;
            if (count == 30) {
                break;
            }
        }
        System.out.println ("this is size: = " + pList.size ());
        int i = random.nextInt (pList.size ());
        return pList.get (i);
    }

    // @Override
    // public PollComment likeComment (String guid) {
    // PollComment pc=pcRepo.findByGuidAndStatus (guid,"active");
    // if(pc!=null){
    // pc.setThumbUp (pc.getThumbUp ()+1);
    // pcRepo.save (pc);}
    // return pc;
    // }
    //
    // @Override
    // public PollComment hateComment (String guid) {
    // PollComment pc=pcRepo.findByGuidAndStatus (guid,"active");
    // if(pc!=null){
    // pc.setThumbUp (pc.getThumbUp ()-1);
    // pcRepo.save (pc);}
    // return pc;
    // }
    @Override
    public Poll getRandomPoll (User user) {
        Random r = new Random ();
        PollHistory ph = phRepo.findByUserGuid (user.getGuid ());
        int randomNum = r.nextInt (range);
        System.out.println ("this is random number : == " + randomNum);
        List <Poll> pList = new ArrayList <Poll> ();
        PageRequest pr = new PageRequest (0, 30);
        int count = 0;
        if (ph == null) {
            while (pList.size () == 0) {
                randomNum = r.nextInt (range);
                Page <Poll> p2 = pollRepo.findByRandomGreaterThanAndStatusAndInviteOnlyAndUserGuidNotIn (randomNum, "active", false, user.getGuid (), pr);
                if (p2 != null) {
                    pList.addAll (p2.getContent ());
                }
                count++;
                if (count == 30) {
                    break;
                }
            }
            int i = r.nextInt (pList.size ());
            return pList.get (i);
        } else {
            while (pList.size () == 0) {
                randomNum = r.nextInt (range);
                Page <Poll> p2 = pollRepo.findByRandomGreaterThanAndStatusAndInviteOnlyAndUserGuidNotInAndGuidNotIn (randomNum, "active", false, user.getGuid (), ph.getPollList (), pr);
                if (p2 != null) {
                    pList.addAll (p2.getContent ());
                }
                count++;
                if (count == 30) {
                    break;
                }
            }
            int i = r.nextInt (pList.size ());
            return pList.get (i);
        }
    }

    @Override
    public Page <Poll> getHotPoll (String userGuid, final Pageable pageable) {
        PollHistory ph = phRepo.findByUserGuid (userGuid);
        Page <Poll> polls = null;
        if (ph != null) {
            polls = pollRepo.findByStatusAndInviteOnlyAndModDateGreaterThanAndGuidNotInAndUserGuidNotIn ("active", false, System.currentTimeMillis () - 1000 * 60 * 60 * 24 * 10, ph.getPollList (),
                    userGuid, pageable);
        } else {
            polls = pollRepo.findByStatusAndInviteOnlyAndModDateGreaterThanAndUserGuidNotIn ("active", false, System.currentTimeMillis () - 1000 * 60 * 60 * 24 * 10, userGuid, pageable);
        }
        return polls;
    }

    @Override
    public Page <Poll> getHotPoll (final Pageable pageable) {
        Page <Poll> polls = pollRepo.findByStatusAndInviteOnlyAndModDateGreaterThan ("active", false, System.currentTimeMillis () - 1000 * 60 * 60 * 24 * 10, pageable);
        return polls;
    }

    @Override
    public Page <Poll> getHotPollByCategory (String userGuid, String category, final Pageable pageable) {
        PollHistory ph = phRepo.findByUserGuid (userGuid);
        Page <Poll> polls = null;
        if (ph != null) {
            polls = pollRepo.findByStatusAndInviteOnlyAndModDateGreaterThanAndCategoryAndGuidNotInAndUserGuidNotIn ("active", false, System.currentTimeMillis () - 1000 * 60 * 60 * 24 * 10, category,
                    ph.getPollList (), userGuid, pageable);
        } else {
            polls = pollRepo.findByStatusAndInviteOnlyAndModDateGreaterThanAndCategoryAndUserGuidNotIn ("active", false, System.currentTimeMillis () - 1000 * 60 * 60 * 24 * 10, userGuid, category,
                    pageable);
        }
        return polls;
    }

    @Override
    public Page <Poll> getHotPollByCategory (String category, final Pageable pageable) {
        Page <Poll> polls = pollRepo.findByStatusAndInviteOnlyAndModDateGreaterThanAndCategory ("active", false, System.currentTimeMillis () - 1000 * 60 * 60 * 24 * 10, category, pageable);
        return polls;
    }

    @Override
    public Page <Poll> getWatchedPoll (final Pageable pageable, String userGuid) {
        Idols idol = iRepo.findByUserGuid (userGuid);
        if (idol == null) {
            return null;
        }
        List <Poll> pList = new ArrayList <Poll> ();
        for (String guid: idol.getIdolList ()) {
            Page <Poll> pPage = pollRepo.findByUserGuid (guid, pageable);
            pList.addAll (pPage.getContent ());
        }
        pList = sortListByModDate (pList);
        Page <Poll> newPolls = new PageImpl <Poll> (pList, pageable, pList.size ());
        return newPolls;
    }

    @Override
    public boolean isQuailifiedForVote (User user, Poll poll) {
        PollHistory ph = phRepo.findByUserGuid (user.getGuid ());
        if (ph == null) {
            return true;
        } else if (ph.getPollList ().contains (poll.getGuid ())) {
            return false;
        } else if (user.getGuid ().equalsIgnoreCase (poll.getUserGuid ())) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public List <Activity> getUserActivity (User user, int page, int size) {
        // sort create date...
        PageRequest pr = new PageRequest (page, size, new Sort (Direction.DESC, "createDate"));
        Page <Poll> pList = pollRepo.findByUserGuidAndStatusAndInviteOnly (user.getGuid (), "active",false ,pr);
        List <Object> obList = new ArrayList <Object> ();
        obList.addAll (pList.getContent ());
        // sort add date...
        pr = new PageRequest (page, size, new Sort (Direction.DESC, "addDate"));
        Page <PollComment> commentPage = cp.findByNickNameAndStatusAndInviteOnly (user.getNickName (), "active",false, pr);
        List <PollComment> pComment = new ArrayList <> ();
        pComment.addAll (commentPage.getContent ());
        // remove redundant poll
        for (int i = 0; i < pComment.size (); i++) {
            for (int j = i + 1; j < pComment.size (); j++) {
                if (pComment.get (i).getPollGuid ().equalsIgnoreCase (pComment.get (j).getPollGuid ())) {
                    // redundant found...
                    pComment.remove (j);
                }
            }
        }
        obList.addAll (pComment);
        // sort Map by date...
        return sortObjListByModDate (obList);
    }

    @Override
    public List <Activity> getIdolsActivity (User user, int page, int size) {
        Idols idol = iRepo.findByUserGuid (user.getGuid ());
        if (idol == null)
            return null;
        Set <String> guidSet = idol.getIdolList ();
        size = size >= guidSet.size () ? 3 : 1;
        List <Object> obList = new ArrayList <Object> ();
        for (String guid: guidSet) {
            User u = userRepo.findByGuidAndStatus (guid, "active");
            // sort create date...
            PageRequest pr = new PageRequest (page, size, new Sort (Direction.DESC, "createDate"));
            Page <Poll> pList = pollRepo.findByUserGuidAndStatusAndInviteOnly (u.getGuid (), "active",false ,pr);
            obList.addAll (pList.getContent ());
            // sort add date...
            pr = new PageRequest (page, size, new Sort (Direction.DESC, "addDate"));
            Page <PollComment> comments = cp.findByNickNameAndStatusAndInviteOnly  (u.getNickName (), "status", false,pr);
            List <PollComment> pComment = new ArrayList <> ();
            pComment.addAll (comments.getContent ());
            // remove redundant poll
            for (int i = 0; i < pComment.size (); i++) {
                for (int j = i + 1; j < pComment.size (); j++) {
                    if (pComment.get (i).getPollGuid ().equalsIgnoreCase (pComment.get (j).getPollGuid ())) {
                        // redundant found...
                        pComment.remove (j);
                    }
                }
            }
            obList.addAll (pComment);
        }
        return sortObjListByModDate (obList);
    }

    @Override
    public Page <Poll> getRandomPollList () {
        Random random = new Random ();
        int r = random.nextInt (range);
        PageRequest p = new PageRequest (0, 10);
        Page <Poll> p2 = null;
        int retryCount = 0;
        while (p2 == null) {
            p2 = pollRepo.findByRandomGreaterThanAndStatusAndInviteOnly (r, "active", false, p);
            retryCount++;
            if (retryCount == 30) {
                break;
            }
        }
        return p2;
    }

    @Override
    public Page <Poll> getRandomPollList (User user) {
        Random r = new Random ();
        PollHistory ph = phRepo.findByUserGuid (user.getGuid ());
        int randomNum = r.nextInt (range);
        System.out.println ("this is random number : == " + randomNum);
        Page <Poll> p2 = null;
        PageRequest pr = new PageRequest (0, 10);
        int count = 0;
        if (ph == null) {
            while (p2 == null) {
                randomNum = r.nextInt (range);
                p2 = pollRepo.findByRandomGreaterThanAndStatusAndInviteOnlyAndUserGuidNotIn (randomNum, "active", false, user.getGuid (), pr);
                count++;
                if (count == 30) {
                    break;
                }
            }
            return p2;
        } else {
            while (p2 == null) {
                randomNum = r.nextInt (range);
                p2 = pollRepo.findByRandomGreaterThanAndStatusAndInviteOnlyAndUserGuidNotInAndGuidNotIn (randomNum, "active", false, user.getGuid (), ph.getPollList (), pr);
                count++;
                if (count == 30) {
                    break;
                }
            }
            return p2;
        }
    }

    @Override
    public boolean vote (Poll pollToSave, String optionId, User u, HttpServletRequest httpServletRequest) {
        // if user not null, reward him poll points and exp..
        if (u != null) {
            // store poll history
            PollHistory ph = phRepo.findByUserGuid (u.getGuid ());
            if (ph == null) {
                ph = new PollHistory ();
                ph.setUserGuid (u.getGuid ());
            }
            if (pollToSave.getUserGuid ().equalsIgnoreCase (u.getGuid ()) || ph.getPollList ().contains (pollToSave.getGuid ())) {
                return false;
            }
            if (!ph.getPollList ().contains (pollToSave.getGuid ())) {
                ph.getPollList ().add (pollToSave.getGuid ());
                phRepo.save (ph);
            }
            u.setPoints (u.getPoints () + 1);
            u = uService.increaseExp (u, voteExp);
            userRepo.save (u);
        }
        saveVoterMetadata (pollToSave, u, httpServletRequest);
        // do vote thing...
        for (int i = 0; i < pollToSave.getOptions ().size (); i++) {
            if (optionId.equalsIgnoreCase (pollToSave.getOptions ().get (i).getOptionId ())) {
                pollToSave.getOptions ().get (i).setCounter (pollToSave.getOptions ().get (i).getCounter () + 1);
            }
        }
        // calculate vote number..
        long totalVoteNum = 0;
        for (Option o: pollToSave.getOptions ()) {
            totalVoteNum += o.getCounter ();
        }
        pollToSave.setVoteNum (totalVoteNum);
        pollToSave.setModDate (System.currentTimeMillis ());
        pollRepo.save (pollToSave);
        return true;
    }

    /**
     * @param pollToSave
     * @param u
     * @param httpServletRequest
     */
    private void saveVoterMetadata (Poll pollToSave, User u, HttpServletRequest httpServletRequest) {
        // save poll voter metadata..
        PollVoterMetaData pvm = null;
        pvm = pvmRepo.findByPollGuid (pollToSave.getGuid ());
        if (pvm == null) {
            pvm = new PollVoterMetaData ();
            pvm.setPollGuid (pollToSave.getGuid ());
            pvm.setVoterGuid (new HashSet <String> ());
            pvm.setVoterIp (new HashSet <String> ());
        }
        if (u != null) {
            pvm.getVoterGuid ().add (u.getGuid ());
        }
        String ipAddress= getUserIp (httpServletRequest);
        if(ipAddress!=null){pvm.getVoterIp ().add (ipAddress);}
        pvmRepo.save (pvm);
    }

    /**
     * @param httpServletRequest
     * @param ipAddress
     * @return
     */
    private String getUserIp (HttpServletRequest httpServletRequest) {
        String ipAddress=null;
        try {
             ipAddress = httpServletRequest.getHeader ("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = httpServletRequest.getRemoteAddr ();
            }
         
        } catch (Exception ex) {
            // ok if no ip catched
        }
        return ipAddress;
    }

    @Override
    public Poll createPoll (PollEntity poll, User user) {
        // create the poll...
        Poll pollToSave = new Poll ();
        pollToSave.setQuestion (poll.getQuestion ());
        pollToSave = pollToSave.populateOptions (pollToSave, poll);
        long currentTime = System.currentTimeMillis ();
        pollToSave.setCreateDate (currentTime);
        String guid = UUID.randomUUID ().toString ().replace ("-", "");
        pollToSave.setGuid (guid);
        pollToSave.setStatus ("active");
        if(user!=null){
        pollToSave.setUserGuid (user.getGuid ());
        pollToSave.setOwner (user.getNickName ());
        pollToSave.setUserImage (user.getUserImage ());}
        Random r = new Random ();
        pollToSave.setRandom (r.nextInt (range));
        // get min age and max age..
        int min = 0;
        int max = 300;
        if (!poll.getAgeRange ().equalsIgnoreCase ("all")) {
            String yearRange = poll.getAgeRange ();
            List <String> yList = Arrays.asList (yearRange.split ("-"));
            min = Integer.valueOf (yList.get (0));
            max = Integer.valueOf (yList.get (1));
        }
        pollToSave.setProfile (new PollProfile (poll.getGender (), min, max));
        pollToSave.setCategory (poll.getCategory ());
        pollToSave.setModDate (currentTime);
        if (poll.isInviteOnly ()) {
            pollToSave.setInviteOnly (poll.isInviteOnly ());
            pollToSave.setPrivateLink (pollHostName + "/" + guid + "?invite");
        }
        pollRepo.save (pollToSave);
        return pollToSave;
    }

    @Override
    public Poll getRandomPoll (String category) {
        Random r = new Random ();
        int randomNum = r.nextInt (range);
        System.out.println ("this is random number : == " + randomNum);
        return pollRepo.findOneByRandomGreaterThanAndStatusAndCategoryAndInviteOnly (randomNum, "active", category, false);
    }

    @Override
    public Poll getRandomPoll (String category, User user) {
        Random r = new Random ();
        int randomNum = r.nextInt (range);
        System.out.println ("this is random number : == " + randomNum);
        PollHistory ph = phRepo.findByUserGuid (user.getGuid ());
        if (ph == null) {
            return pollRepo.findOneByRandomGreaterThanAndStatusAndCategoryAndInviteOnlyAndUserGuidNotIn (randomNum, "active", category, false, user.getGuid ());
        } else {
            return pollRepo.findOneByRandomGreaterThanAndStatusAndCategoryAndInviteOnlyAndUserGuidNotInAndGuidNotIn (randomNum, "active", category, false, user.getGuid (), ph.getPollList ());
        }
    }

    @Override
    public void deletePoll (String pollGuid, String userGuid) {
        // delete poll in mongo..
        Poll poll = pollRepo.findOne (pollGuid);
        poll.setStatus ("deleted");
        poll.setModDate (System.currentTimeMillis ());
        pollRepo.save (poll);
        // delete image in S3 is applicable
        if (poll.getImagePoll ()) {
            if (poll.getQuestion ().contains (bucketName)) {
                s3Service.deleteFromS3 (poll.getQuestion ().substring (poll.getQuestion ().lastIndexOf ("/") + 1));
            } else {
                for (Option o: poll.getOptions ()) {
                    if (o.getDescription ().contains (bucketName)) {
                        s3Service.deleteFromS3 (o.getDescription ().substring (o.getDescription ().lastIndexOf ("/") + 1));
                    }
                }
            }
        }
    }

    @Override
    public List <Poll> listVotedPolls (String userGuid) {
        PollHistory ph = phRepo.findByUserGuid (userGuid);
        if (ph == null || ph.getPollList ().size () == 0) {
            return null;
        } else {
            List <Poll> pList = new ArrayList <Poll> ();
            for (int i=ph.getPollList ().size ()-1;i>=0;i--) {
                Poll p = pollRepo.findOne (ph.getPollList ().get (i));
                if (p != null && p.getStatus ().equalsIgnoreCase ("active")) {
                    pList.add (p);
                }
            }
            return pList;
        }
    }

    @Override
    public PollComment addComment (String pollGuid, User user, PollComment comment) {
        // update poll mod time
        Poll p = pollRepo.findByGuid (pollGuid);
        p.setModDate (System.currentTimeMillis ());
        p.setCommentNum (p.getCommentNum () + 1);
        pollRepo.save (p);
        long currentTime = System.currentTimeMillis ();
        comment.setAddDate (currentTime);
        comment.setStatus ("active");
        comment.setPollGuid (pollGuid);
        comment.setGuid (UUID.randomUUID ().toString ().replace ("-", ""));
        comment.setNickName (user.getNickName ());
        comment.setAddDateGMT (cs.convertTime (currentTime, "MM/dd/yyyy hh:mm:ss"));
        comment.setUserGuid (user.getGuid ());
        comment.setUserImage (user.getUserImage ());
        comment.setInviteOnly (p.isInviteOnly ());
        return cp.save (comment);
    }

    @Override
    public PollComment removeComment (String guid) {
        PollComment c = cp.findByGuid (guid);
        c.setStatus ("deleted");
        cp.save (c);
        Poll p = pollRepo.findByGuid (c.getPollGuid ());
        if (p.getCommentNum () >= 1) {
            p.setCommentNum (p.getCommentNum () - 1);
        }
        pollRepo.save (p);
        return c;
    }

    // @Override
    // public List <PollComment> getPollComments (String pollGuid) {
    // PageRequest request = new PageRequest (0, 100, Direction.DESC, "addDate");
    // Page <PollComment> cPage = cp.findByPollGuidAndStatus (pollGuid, "active", request);
    // return cPage.getContent ();
    // }
    @Override
    public List <PollComment> getPollComments (String pollGuid, Pageable pg) {
        Page <PollComment> cPage = cp.findByPollGuidAndStatus (pollGuid, "active", pg);
        return cPage.getContent ();
    }

    @Override
    public boolean pinPoll (String pollGuid, String userGuid) {
        PinedPolls pp = ppRepo.findByUserGuid (userGuid);
        if (pp == null) {
            pp = new PinedPolls (userGuid);
        }
        pp.getPollSet ().add (pollGuid);
        ppRepo.save (pp);
        return true;
    }

    @Override
    public List <Poll> getPinedPoll (String userGuid) {
        PinedPolls pp = ppRepo.findByUserGuid (userGuid);
        if (pp == null)
            return null;
        else {
            List <Poll> pList = new ArrayList <Poll> ();
            for (String p: pp.getPollSet ()) {
                Poll poll = pollRepo.findByGuid (p);
                if (poll != null) {
                    pList.add (poll);
                }
            }
            return pList;
        }
    }

    @Override
    public boolean unPinPoll (String pollGuid, String userGuid) {
        PinedPolls pp = ppRepo.findByUserGuid (userGuid);
        if (pp == null)
            return false;
        else {
            pp.getPollSet ().remove (pollGuid);
            ppRepo.save (pp);
            return true;
        }
    }

    @Override
    public boolean isPined (String pollGuid, String userGuid) {
        PinedPolls pp = ppRepo.findByUserGuid (userGuid);
        if (pp == null)
            return false;
        else {
            return pp.getPollSet ().contains (pollGuid);
        }
    }

    // @Override
    // public List<Poll> getRecommendPollList(User user) {
    // PollHistory ph = phRepo.findByUserGuid(user.getGuid());
    // Set<String> pSet = getRecommendPoll(ph, user, new HashSet<String>());
    // List<String> pList = new ArrayList<>();
    // pList.addAll(pSet);
    // return pollRepo.findByGuidIn(pList);
    // }
    @Override
    public Page <Poll> getRecommendPollList (User user, Pageable pageable) {
        PollHistory ph = phRepo.findByUserGuid (user.getGuid ());
        Set <String> pSet = getRecommendPoll (ph, user, new HashSet <String> ());
        if (pSet == null) {
            pSet = getInterestingPoll (user, ph, pageable);
        }
        List <String> pList = new ArrayList <> ();
        pList.addAll (pSet);
        return pollRepo.findByGuidIn (pList, pageable);
    }
    
    @Override
    public boolean checkVoterIp (Poll p,HttpServletRequest httpServletRequest) {
        PollVoterMetaData pvm= pvmRepo.findByPollGuid (p.getGuid ());
       if (pvm!=null&&pvm.getVoterIp ().contains (getUserIp (httpServletRequest))){
           return false;
       }else return true;
      
    }

    private Set <String> getInterestingPoll (User user, PollHistory ph, Pageable pageable) {
        Page <Poll> pp = pollRepo.findByStatusAndInviteOnlyAndUserGuidNotInAndGuidNotInAndCategoryIn ("active", false, user.getGuid (), ph.getPollList () == null ? null : ph.getPollList (),
                user.getInterestList (), pageable);
        Set <String> guidSet = new HashSet <String> ();
        for (Poll p: pp.getContent ()) {
            guidSet.add (p.getGuid ());
        }
        return guidSet;
    }

    // private Poll getPoll (User user, int randomNum) {
    // Poll p = null;
    // PollHistory ph = phRepo.findByUserGuid (user.getGuid ());
    // // no preferred list...one of four
    // if (user.getGender ().equalsIgnoreCase ("not specified") && user.getBirthYear ().equalsIgnoreCase ("not specified")) {
    // // non specified...
    // if (ph == null) {
    // p = pollRepo.findOneByRandomGreaterThanAndStatusAndInviteOnlyAndUserGuidNotIn (randomNum, "active", false, user.getGuid ());
    // } else {
    // p = pollRepo.findOneByRandomGreaterThanAndStatusAndInviteOnlyAndUserGuidNotInAndGuidNotIn (randomNum, "active", false, user.getGuid (),
    // ph.getPollList ());
    // }
    // } else if (user.getGender ().equalsIgnoreCase ("not specified") && (!user.getBirthYear ().equalsIgnoreCase ("not specified"))) {
    // int age = getUserAge (user);
    // // only specify birth year
    // p = pollRepo.findOneRandom (randomNum, "active", false, user.getGuid (), age, age);
    // } else if ((!user.getGender ().equalsIgnoreCase ("not specified")) && user.getBirthYear ().equalsIgnoreCase ("not specified")) {
    // // only specify gender
    // p = pollRepo.findOneRandom (randomNum, "active", false, user.getGuid (), user.getGender ());
    // } else {
    // // specified both...
    // int age = getUserAge (user);
    // p = pollRepo.findOneRandom (randomNum, "active", false, user.getGuid (), user.getGender (), age, age);
    // }
    // return p;
    // }
    // private int getUserAge (User user) {
    // String year = user.getBirthYear ();
    // int currentYear = Calendar.getInstance ().get (Calendar.YEAR);
    // return currentYear - Integer.valueOf (year);
    // }
    // private Poll getPoll (User user, int randomNum, String category) {
    // Poll p = null;
    // PollHistory ph = phRepo.findByUserGuid (user.getGuid ());
    // // has preferred list.. choose one of four
    // if (user.getGender ().equalsIgnoreCase ("not specified") && user.getBirthYear ().equalsIgnoreCase ("not specified")) {
    // // non specified...
    // if (ph == null) {
    // p = pollRepo.findOneByRandomGreaterThanAndStatusAndCategoryAndInviteOnlyAndUserGuidNotIn (randomNum, "active", category, false, user.getGuid
    // ());
    // } else {
    // p = pollRepo.findOneByRandomGreaterThanAndStatusAndCategoryAndInviteOnlyAndUserGuidNotInAndGuidNotIn (randomNum, "active", category, false,
    // user.getGuid (), ph.getPollList ());
    // }
    // } else if (user.getGender ().equalsIgnoreCase ("not specified") && (!user.getBirthYear ().equalsIgnoreCase ("not specified"))) {
    // // only specify birth year
    // int age = getUserAge (user);
    // p = pollRepo.findOneRandom (randomNum, "active", category, false, user.getGuid (), age, age);
    // } else if ((!user.getGender ().equalsIgnoreCase ("not specified")) && user.getBirthYear ().equalsIgnoreCase ("not specified")) {
    // // only specify gender
    // p = pollRepo.findOneRandom (randomNum, "active", category, false, user.getGuid (), user.getGender ());
    // } else {
    // // specified both...
    // int age = getUserAge (user);
    // p = pollRepo.findOneRandom (randomNum, "active", category, false, user.getGuid (), user.getGender (), age, age);
    // }
    // return p;
    // }
    private Set <String> getRecommendPoll (PollHistory ph, User user, Set <String> guidSet) {
        Idols idols = iRepo.findByUserGuid (user.getGuid ());
        if (idols == null)
            return guidSet;
        Set <String> idolList = idols.getIdolList ();
        for (String guid: idolList) {
            User u = userRepo.findByGuidAndStatus (guid, "active");
            if (u != null) {
                guidSet.addAll (getUserInterestedPoll (u));
                if (ph != null) {
                    guidSet.removeAll (ph.getPollList ());
                }
            }
        }
        if (guidSet.size () < 100) {
            for (String guid: idolList) {
                User u = userRepo.findByGuidAndStatus (guid, "active");
                if (u != null) {
                    getRecommendPoll (ph, u, guidSet);
                }
            }
        }
        return guidSet;
    }

    public Set <String> getUserInterestedPoll (User user) {
        List <Poll> pList = pollRepo.findByUserGuidAndStatus (user.getGuid (), "active");
        Set <String> guidSet = new HashSet <String> ();
        for (Poll p: pList) {
            guidSet.add (p.getGuid ());
        }
        List <PollComment> pComent = cp.findByNickName (user.getNickName ());
        for (PollComment pc: pComent) {
            guidSet.add (pc.getPollGuid ());
        }
        return guidSet;
    }

    private List <Activity> sortObjListByModDate (List <Object> pList) {
        int size = pList.size ();
        List <Activity> acList = new ArrayList <Activity> ();
        while (acList.size () != size) {
            long max = 0;
            if (pList.get (0) instanceof Poll) {
                max = ((Poll) pList.get (0)).getCreateDate ();
            } else if (pList.get (0) instanceof PollComment) {
                max = ((PollComment) pList.get (0)).getAddDate ();
            }
            int point = 0;
            for (int i = 0; i < pList.size (); i++) {
                if (pList.get (i) instanceof Poll) {
                    if (((Poll) pList.get (i)).getCreateDate () > max) {
                        max = ((Poll) pList.get (i)).getCreateDate ();
                        point = i;
                    }
                } else if (pList.get (i) instanceof PollComment) {
                    if (((PollComment) pList.get (i)).getAddDate () > max) {
                        max = ((PollComment) pList.get (i)).getAddDate ();
                        point = i;
                    }
                }
            }
            if (pList.get (point) instanceof Poll) {
                Poll p = (Poll) pList.get (point);
                String startTime = cs.convertTime (p.getCreateDate (), "MM/dd/yyyy hh:mm:ss");
                long diff = cs.calculateTimeDifference (startTime);
                Activity ac=new Activity ("poll", p.getGuid (), String.valueOf (diff), p.getOwner (), p.getCategory ().toString (),p.getUserImage ());
                ac.setPoll (p);
                acList.add (ac);
            } else if (pList.get (point) instanceof PollComment) {
                PollComment pc = (PollComment) pList.get (point);
                String startTime = cs.convertTime (pc.getAddDate (), "MM/dd/yyyy hh:mm:ss");
                long diff = cs.calculateTimeDifference (startTime);
                Activity ac= new Activity ("comment", pc.getPollGuid (), String.valueOf (diff), pc.getNickName (),null,pc.getUserImage ());
                ac.setPoll (pollRepo.findByGuidAndStatus (pc.getPollGuid (), "active"));
                acList.add (ac);
            }
            pList.remove (point);
        }
        return acList;
    }

    private List <Poll> sortListByModDate (List <Poll> pList) {
        int size = pList.size ();
        List <Poll> newList = new ArrayList <> ();
        while (newList.size () != size) {
            long max = pList.get (0).getCreateDate ();
            int point = 0;
            for (int i = 0; i < pList.size (); i++) {
                if (pList.get (i).getCreateDate () > max) {
                    max = pList.get (i).getCreateDate ();
                    point = i;
                }
            }
            newList.add (pList.get (point));
            pList.remove (point);
        }
        return newList;
    }
}
