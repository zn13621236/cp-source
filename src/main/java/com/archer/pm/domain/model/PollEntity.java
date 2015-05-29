package com.archer.pm.domain.model;

import java.io.Serializable;
import com.archer.pm.domain.constant.Category;
import com.archer.pm.domain.db.Poll;

public class PollEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String            guid;
    private String            question;
    private String            test="";
    private String            option1;
    private int               option1Counter;
    private String            option2;
    private int               option2Counter;
    private String            option3;
    private int               option3Counter;
    private String            option4;
    private int               option4Counter;
    private String            option5;
    private int               option5Counter;
    private String            option6;
    private int               option6Counter;
    // targeted vote group
    private String            gender           = "all";
    private String            ageRange         = "all";
    private Category          category;
    private boolean           inviteOnly       = false;

    public PollEntity () {
        super ();
        // TODO Auto-generated constructor stub
    }

    public PollEntity (Poll poll) {
        super ();
        this.guid = poll.getGuid ();
        this.question = poll.getQuestion ();
        for (int i = 0; i < poll.getOptions ().size (); i++) {
            if (i == 0) {
                this.option1 = poll.getOptions ().get (0).getDescription ();
                this.option1Counter = (int) poll.getOptions ().get (0).getCounter ();
            }
            if (i == 1) {
                this.option2 = poll.getOptions ().get (1).getDescription ();
                this.option2Counter = (int) poll.getOptions ().get (1).getCounter ();
            }
            if (i == 2) {
                this.option3 = poll.getOptions ().get (2).getDescription ();
                this.option3Counter = (int) poll.getOptions ().get (2).getCounter ();
            }
            if (i == 3) {
                this.option4 = poll.getOptions ().get (3).getDescription ();
                this.option4Counter = (int) poll.getOptions ().get (3).getCounter ();
            }
            if (i == 4) {
                this.option5 = poll.getOptions ().get (4).getDescription ();
                this.option5Counter = (int) poll.getOptions ().get (4).getCounter ();
            }
            if (i == 5) {
                this.option6 = poll.getOptions ().get (5).getDescription ();
                this.option6Counter = (int) poll.getOptions ().get (5).getCounter ();
            }
        }
    }

    public String getTest () {
        return test;
    }

    public void setTest (String test) {
        this.test = test;
    }

    public Category getCategory () {
        return category;
    }

    public void setCategory (Category category) {
        this.category = category;
    }

    public long getOption1Counter () {
        return option1Counter;
    }

    public int getOption2Counter () {
        return option2Counter;
    }

    public void setOption2Counter (int option2Counter) {
        this.option2Counter = option2Counter;
    }

    public int getOption3Counter () {
        return option3Counter;
    }

    public void setOption3Counter (int option3Counter) {
        this.option3Counter = option3Counter;
    }

    public int getOption4Counter () {
        return option4Counter;
    }

    public void setOption4Counter (int option4Counter) {
        this.option4Counter = option4Counter;
    }

    public int getOption5Counter () {
        return option5Counter;
    }

    public void setOption5Counter (int option5Counter) {
        this.option5Counter = option5Counter;
    }

    public int getOption6Counter () {
        return option6Counter;
    }

    public void setOption6Counter (int option6Counter) {
        this.option6Counter = option6Counter;
    }

    public void setOption1Counter (int option1Counter) {
        this.option1Counter = option1Counter;
    }

    public String getAgeRange () {
        return ageRange;
    }

    public void setAgeRange (String ageRange) {
        this.ageRange = ageRange;
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

    public String getOption1 () {
        return option1;
    }

    public void setOption1 (String option1) {
        this.option1 = option1;
    }

    public String getOption2 () {
        return option2;
    }

    public void setOption2 (String option2) {
        this.option2 = option2;
    }

    public String getOption3 () {
        return option3;
    }

    public void setOption3 (String option3) {
        this.option3 = option3;
    }

    public String getOption4 () {
        return option4;
    }

    public void setOption4 (String option4) {
        this.option4 = option4;
    }

    public String getOption5 () {
        return option5;
    }

    public void setOption5 (String option5) {
        this.option5 = option5;
    }

    public String getOption6 () {
        return option6;
    }

    public void setOption6 (String option6) {
        this.option6 = option6;
    }

    public String getGender () {
        return gender;
    }

    public void setGender (String gender) {
        this.gender = gender;
    }

    public boolean isInviteOnly () {
        return inviteOnly;
    }

    public void setInviteOnly (boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    @Override
    public String toString () {
        return "PollEntity [guid=" + guid + ", question=" + question + ", test=" + test + ", option1=" + option1 + ", option1Counter=" + option1Counter + ", option2=" + option2 + ", option2Counter="
                + option2Counter + ", option3=" + option3 + ", option3Counter=" + option3Counter + ", option4=" + option4 + ", option4Counter=" + option4Counter + ", option5=" + option5
                + ", option5Counter=" + option5Counter + ", option6=" + option6 + ", option6Counter=" + option6Counter + ", gender=" + gender + ", ageRange=" + ageRange + ", category=" + category
                + ", inviteOnly=" + inviteOnly + "]";
    }
}
