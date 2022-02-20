package com.smaato.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
	private static final Logger log = Logger.getLogger(UserServiceImpl.class.getName());

	private static final String SMAATO_USER = "smaatousers";

	@Autowired
	private RedisTemplate<String, String> template;

	/**
	 * Get the number of online users in a certain period of time
	 * 
	 * @param duration
	 * @return
	 */
	public Long count(Duration duration) {
		LocalDateTime now = LocalDateTime.now();
		return template.opsForZSet().count(SMAATO_USER,
				now.minus(duration).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
				now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
	}

	public Long count() {
		return template.opsForZSet().zCard(SMAATO_USER);
	}

	/**
	 *   returns the list of users with in the score limit.The minimum value is given as negative infinity as we do not know the minimum value in the score
	 * 
	 */
	public List<String> getSmaatoUserList() {
		Set<String> redisKeys = template.opsForZSet().rangeByScore(SMAATO_USER, Double.NEGATIVE_INFINITY,
				new Double(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
//		Set<String> redisKeys = template.opsForZSet().reverseRangeByScore(SMAATO_USER, Double.NEGATIVE_INFINITY,new Double(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
		List<String> keysList = new ArrayList<>();
		Iterator<String> it = redisKeys.iterator();
		while (it.hasNext()) {
			String data = it.next();
			log.info("User " + data);
			keysList.add(data);
		}
		return keysList;
	}

	/**
	 * Clear user data that has not been online for a certain period of time
	 * 
	 * @param duration
	 * @return
	 */
	public Long clear(Duration duration) {
		return template.opsForZSet().removeRangeByScore(SMAATO_USER, 0,
				LocalDateTime.now().minus(duration).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

	}

	/**
	 * Accept new user
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public boolean checkIfUserExists(Integer userid) {
		boolean isUserExists = template.opsForZSet().add(SMAATO_USER, userid.toString(), Instant.now().toEpochMilli());
		log.info(userid + " is new user :: " + isUserExists);
		return isUserExists;
	}
}
