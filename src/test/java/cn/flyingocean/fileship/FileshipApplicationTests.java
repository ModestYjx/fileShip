package cn.flyingocean.fileship;

import cn.flyingocean.fileship.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileshipApplicationTests {
	@Autowired
	UserRepository userRepo;

	@Test
	public void contextLoads() {
	}

	@Test
	@Transactional
	@Rollback(value = false)
	public void test(){

	}
}
