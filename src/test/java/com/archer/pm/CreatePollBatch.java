package com.archer.pm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.archer.pm.domain.constant.Category;
import com.archer.pm.domain.db.User;
import com.archer.pm.domain.model.PollEntity;
import com.archer.pm.service.PollService;
import com.archer.pm.service.UserService;

@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations = { "/test-applicationContext.xml" })
public class CreatePollBatch {

	  @Autowired
	    UserService uService;
	    @Autowired
	    PollService pService;

	    @Test
	    public void createUserBatch () {
	        for (int h = 0; h < 30; h++) {
	            // create user
	            Random r = new Random ();
	            int i = r.nextInt ();
	            String email="abcde" + i + System.currentTimeMillis ();
	            int start= email.length()-8<0?0:email.length()-8;
	            String random = email.substring(start,email.length()-1) + "@walapoll.com";
	            User user = new User ();
	            user.setAcceptTerm (true);
	            user.setNickName (random);
	            user.setUserName (random);
	            user.setPassWord (random);
	            user = uService.create (user);
	            // create poll..
	            for (int j = 0; j < 10; j++) {
	                random = "this is random question, it supposed to be something fun here. what do you think?  "  + r.nextInt ();
	                PollEntity pe = new PollEntity ();
	                pe.setInviteOnly (false);
	                pe.setQuestion (random);
	                pe.setOption1 ("abc     " + r.nextInt ());
	                pe.setOption2 ("ab     c" + r.nextInt ());
	                List <Category> list = new ArrayList <Category> ();
	                list.add (Category.CARS);
	                list.add (Category.ENTERTAINMENT);
	                list.add (Category.SPORTS);
	                list.add (Category.MOVIE);
	                list.add (Category.MUSIC);
	                list.add (Category.PRODUCT);
	                list.add(Category.OTHERS);
	                pe.setCategory (list.get(r.nextInt(list.size())));
	                pService.createPoll (pe, user);
	            }
	        }
	    }
	
	
}
