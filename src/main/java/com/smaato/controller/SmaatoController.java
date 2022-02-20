package com.smaato.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.smaato.service.UserService;

/**
 * @author Venkatesh
 * 
 * http://localhost:8090/api/smaato/accept?id=20&endpoint=http://localhost:8080/api/smaato/count
 * http://localhost:8080/api/smaato/accept?id=20&endpoint=http://localhost:8080/api/smaato/count
 *
 */
@RestController
class SmaatoController {
	private static final Logger log = Logger.getLogger(SmaatoController.class.getName());

	private static final String SMAATO_USER = "smaatousers";

	@Autowired
	RestTemplate restTemplete;

	@Autowired
	private UserService userService;

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	/**
	 * @param id
	 * @param endpoint
	 * 
	 * http://localhost:8080//api/smaato/accept?id=18&endpoint=http://localhost:8080/api/smaato/count
	 *                 
	 * @return String
	 */
	@RequestMapping(value = "/api/smaato/accept")
	public String accept(@RequestParam int id, @RequestParam(required = false) String endpoint) {
		String response = "failed";
		boolean isNewUser = userService.checkIfUserExists(id);
		if (isNewUser) {
			response = "ok";
		}
		if (null != endpoint) {
			log.info("endpoint values is " + endpoint);
			log.info("Triggering " + endpoint);
			Long count = restTemplete.getForObject(endpoint, Long.class);
			log.info("Request completed for " + endpoint);
			log.info("user count" + count);
		}
		return response;
	}

	/**
	 * @return count
	 */
	@RequestMapping(value = "/api/smaato/count")
	public Long getCount() {
		return userService.count();

	}

	@RequestMapping(value = "/api/smaato/getusers")
	public List<String> getUsers() {
		return userService.getSmaatoUserList();

	}

}
