package com.archer.pm;

import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.archer.pm.domain.db.Poll;
import com.archer.pm.domain.db.User;
import com.archer.pm.domain.repo.PollRepo;
import com.archer.pm.domain.repo.UserRepo;
import com.archer.pm.service.PollService;
import com.archer.pm.service.UserService;

@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations = { "/test-applicationContext.xml" })
public class UserControllerTest {
    private static Logger log = Logger.getLogger (UserControllerTest.class.getName ());
    @Autowired
    UserService           userService;
    @Autowired
    UserRepo uRepo;
	  @Test
	  public void testGetRecommendUser(){
	      Integer size=null;
	      Integer page=null;
	      int sizeNo = size == null ? 30 : size.intValue ();
          final int firstResult = page == null ? 0 : page;
          PageRequest pq = new PageRequest (firstResult, 30);
         User user=uRepo.findByNickNameAndStatus ("koan2", "active");
          List <User> uList = userService.getRecommendUser (user, pq);
         log.info (uList.size ());
     
	  }
}
