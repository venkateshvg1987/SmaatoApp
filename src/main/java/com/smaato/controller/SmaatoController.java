package com.smaato.controller;

import java.net.URI;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.smaato.service.UserService;

/**
 * @author Venkatesh
 * 
 *         http://localhost:8090/api/smaato/accept?id=20&endpoint=http://localhost:8080/api/smaato/redirect
 *         http://localhost:8080/api/smaato/accept?id=20&endpoint=http://localhost:8080/api/smaato/redirect
 *
 */
@RestController
class SmaatoController {
	private static final Logger log = Logger.getLogger(SmaatoController.class.getName());

	private static final String SMAATO_USER = "smaatousers";

	private static final String redirect_url = "http://localhost:8080/api/smaato/count";

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
	 *                 http://localhost:8080//api/smaato/accept?id=18&endpoint=http://localhost:8080/api/smaato/redirect
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
			String uri = endpoint + "?input=" + getCount();
			log.info("endpoint values is " + uri);
			log.info("Triggering " + uri);
			Long count = restTemplete.getForObject(uri, Long.class);
			log.info("Request completed for " + endpoint);
			log.info("user count" + count);
		}
		return response;
	}

	@GetMapping(value = "/api/smaato/redirect")
	public ResponseEntity<Void> redirect(@RequestParam Long input) {
		log.info("unique count from the userlist :: " + input);
		log.info("HTTP STATUS CODE for redirect url :: "
				+ ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirect_url)).build());
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirect_url)).build();
	}

	/**
	 * @return count and log the count for every one minute
	 */

	@Scheduled(fixedRate = 60000)
	@RequestMapping(value = "/api/smaato/count")
	public Long getCount() {
		log.info("unique count from the userlist :: " + userService.count() + " Current time is :: "
				+ Calendar.getInstance().getTime());
		return userService.count();

	}

	@RequestMapping(value = "/api/smaato/getusers")
	public List<String> getUsers() {
		return userService.getSmaatoUserList();

	}

}
