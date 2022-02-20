package com.smaato.service;

import java.util.List;

public interface UserService {

	boolean checkIfUserExists(Integer id);

	Long count();

	List<String> getSmaatoUserList();

}
