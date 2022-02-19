package com.smaato.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

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
 */
@RestController
class SmaatoController {
	private static final Logger LOG = Logger.getLogger(SmaatoController.class.getName());

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
	 * @return
	 */
	@RequestMapping(value = "/api/smaato/accept")
	public String accept(@RequestParam int id, @RequestParam Optional<String> endpoint) {
		String response = "failed";
		boolean isNewUser = userService.checkIfUserExists(id);
		if (isNewUser) {
			response = "ok";
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

	@RequestMapping(value = "/exception")
	public String exception() {
		String response = "";
		try {
			throw new Exception("Opps Exception raised....");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e);

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String stackTrace = sw.toString();
			LOG.error("Exception - " + stackTrace);
			response = stackTrace;
		}

		return response;
	}

}
