package com.smaato.controller;

import java.net.URI;
import java.time.Duration;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.smaato.model.User;
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
	 *                 http://localhost:8080/api/smaato/accept?id=34&endpoint=http://localhost:8080/api/smaato/redirect
	 * 
	 * @return String
	 * @throws InterruptedException
	 */

	@RequestMapping(value = "/api/smaato/accept")
	public CompletableFuture<String> accept(@RequestParam int id, @RequestParam(required = false) String endpoint)
			throws InterruptedException {
		String response = "failed";
		boolean isNewUser = userService.checkIfUserExists(id);
		if (isNewUser) {
			response = "ok";
		}
		if (null != endpoint) {
			String uri = endpoint + "?input=" + getCount();
			log.debug("endpoint values is " + uri);
			log.info(" Thread  : " + Thread.currentThread().getName() + " is calling  " + uri);
			Long count = restTemplete.getForObject(uri, Long.class);
			Thread.sleep(1000L);
			log.info(" Thread  : " + Thread.currentThread().getName() + " request completed for " + uri);
			log.info(" User Count " + count);
		}
		return CompletableFuture.completedFuture(response);
	}

	@GetMapping(value = "/api/smaato/redirect")
	public ResponseEntity<Void> redirect(@RequestParam Long input) {
		log.info("unique count from the userlist :: " + input);
		log.info("HTTP STATUS CODE for redirect url :: "
				+ ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirect_url)).build());
		return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirect_url)).build();
	}

	@PostMapping(value = "/api/smaato/accept", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public String acceptuser(@RequestBody User user) throws InterruptedException {
		String response = "FAILED";
		log.info("POST Request User Details " + user.getId() + "  URL :: " + user.getUrl());
		boolean isNewUser = userService.checkIfUserExists(user.getId());
		if (isNewUser) {
			response = "OK";
		}
		if (null != user.getUrl()) {
			String uri = user.getUrl() + "?input=" + getCount();
			log.debug("POST URL is " + uri);
			log.info(" Thread  : " + Thread.currentThread().getName() + " is calling  " + uri);
			Long count = restTemplete.getForObject(uri, Long.class);
			Thread.sleep(1000L);
			log.info(" Thread  : " + Thread.currentThread().getName() + " request completed for " + uri);
			log.info(" New User Count :: " + count);
		}
		return response;
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
