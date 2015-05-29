package com.archer.pm;

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
import com.archer.pm.domain.repo.PollRepo;
import com.archer.pm.service.PollService;

@RunWith (SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations = { "/test-applicationContext.xml" })
public class PollControllerTest {
    private static Logger log = Logger.getLogger (PollControllerTest.class.getName ());
    @Autowired
    PollService   pollService;
    @Autowired
    PollRepo      pollRepo;
	  @Test
	  public void testPoll(){
	      int sizeNo =1;
	        final int firstResult = 2;
	        PageRequest pq = new PageRequest (firstResult / sizeNo, sizeNo, new Sort (Direction.DESC, "voteNum"));
	       
	        Page<Poll> pp= pollService.getHotPoll (pq);
	        log.debug ("this is the size: "+pp.getContent().size());
	  }
}
