package com.archer.pm.domain.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Persistent;
import com.archer.pm.domain.constant.Category;
import com.archer.pm.domain.model.Option;
import com.archer.pm.domain.model.PollEntity;
import com.archer.pm.domain.model.PollProfile;

@Persistent
public class Poll implements Serializable, Comparable <Poll> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    private String            guid;
    private String            question;
    private String            userGuid;
    private String            owner;                    // user nickName..
    private String            status;
    private long              createDate;
    private long              voteNum;
    private int               commentNum;
    private List <Option>     options;
    private Category          category;
    private PollProfile       profile;
    private String            choice;
    private boolean           imagePoll;
    private boolean           inviteOnly       = false;
    private String            privateLink;
    private long              modDate;
    private int               random;
    private String            userImage;

    public Poll populateOptions (Poll pollToSave, PollEntity poll) {
        List <Option> options = null;
        if (pollToSave.getOptions () != null) {
            options = pollToSave.getOptions ();
        } else {
            options = new ArrayList <Option> ();
        }
        if (poll.getOption1 () != null && poll.getOption1 ().length () >= 1) {
            Option o1 = new Option (poll.getOption1 ());
            options.add (o1);
        }
        if (poll.getOption2 () != null && poll.getOption2 ().length () >= 1) {
            Option o1 = new Option (poll.getOption2 ());
            options.add (o1);
        }
        if (poll.getOption3 () != null && poll.getOption3 ().length () >= 1) {
            Option o1 = new Option (poll.getOption3 ());
            options.add (o1);
        }
        if (poll.getOption4 () != null && poll.getOption4 ().length () >= 1) {
            Option o1 = new Option (poll.getOption4 ());
            options.add (o1);
        }
        if (poll.getOption5 () != null && poll.getOption5 ().length () >= 1) {
            Option o1 = new Option (poll.getOption5 ());
            options.add (o1);
        }
        pollToSave.setOptions (options);
        return pollToSave;
    }

    public String getUserImage () {
        return userImage;
    }

    public void setUserImage (String userImage) {
        this.userImage = userImage;
    }

    public int getRandom () {
        return random;
    }

    public void setRandom (int random) {
        this.random = random;
    }

    public String getOwner () {
        return owner;
    }

    public void setOwner (String nickName) {
        this.owner = nickName;
    }

    public String getPrivateLink () {
        return privateLink;
    }

    public void setPrivateLink (String privateLink) {
        this.privateLink = privateLink;
    }

    public Boolean getImagePoll () {
        return imagePoll;
    }

    public void setImagePoll (Boolean imagePoll) {
        this.imagePoll = imagePoll;
    }

    public boolean isInviteOnly () {
        return inviteOnly;
    }

    public void setInviteOnly (boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    public String getGuid () {
        return guid;
    }

    public void setGuid (String guid) {
        this.guid = guid;
    }

    public String getQuestion () {
        return question;
    }

    public void setQuestion (String question) {
        this.question = question;
    }

    public String getUserGuid () {
        return userGuid;
    }

    public void setUserGuid (String userGuid) {
        this.userGuid = userGuid;
    }

    public String getStatus () {
        return status;
    }

    public void setStatus (String status) {
        this.status = status;
    }

    public long getCreateDate () {
        return createDate;
    }

    public void setCreateDate (long createDate) {
        this.createDate = createDate;
    }

    public long getVoteNum () {
        return voteNum;
    }

    public void setVoteNum (long voteNum) {
        this.voteNum = voteNum;
    }

    public List <Option> getOptions () {
        return options;
    }

    public void setOptions (List <Option> options) {
        this.options = options;
    }

    public Category getCategory () {
        return category;
    }

    public void setCategory (Category category) {
        this.category = category;
    }

    public PollProfile getProfile () {
        return profile;
    }

    public void setProfile (PollProfile profile) {
        this.profile = profile;
    }

    public String getChoice () {
        return choice;
    }

    public void setChoice (String choice) {
        this.choice = choice;
    }

    public void setImagePoll (boolean imagePoll) {
        this.imagePoll = imagePoll;
    }

    public long getModDate () {
        return modDate;
    }

    public void setModDate (long modDate) {
        this.modDate = modDate;
    }

    public int getCommentNum () {
        return commentNum;
    }

    public void setCommentNum (int commentNum) {
        this.commentNum = commentNum;
    }

    @Override
    public String toString () {
        return "Poll [guid=" + guid + ", question=" + question + ", userGuid=" + userGuid + ", owner=" + owner + ", status=" + status + ", createDate=" + createDate + ", voteNum=" + voteNum
                + ", commentNum=" + commentNum + ", options=" + options + ", category=" + category + ", profile=" + profile + ", choice=" + choice + ", imagePoll=" + imagePoll + ", inviteOnly="
                + inviteOnly + ", privateLink=" + privateLink + ", modDate=" + modDate + ", random=" + random + ", userImage=" + userImage + "]";
    }

    @Override
    public int compareTo (Poll o) {
        // desc order
        return (int) (this.voteNum - o.voteNum);
    }
}
