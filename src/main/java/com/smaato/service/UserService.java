package com.smaato.service;

import java.time.Duration;
import java.util.List;

public interface UserService {

	boolean checkIfUserExists(Integer id);

	Long count();
	
	Long clear(Duration duration);

	List<String> getSmaatoUserList();

}
