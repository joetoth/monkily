package com.monkily.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.monkily.model.Prediction;
import com.monkily.model.UserType;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("/services/userService")
public interface UserService extends RemoteService {

	public static final String SESSION_ATTR_USER_ID = "SESSION_ATTR_USER_ID";

	Boolean isLoggedIn();

	void googleLogin();

	boolean login(String emailAddress, String password);

	String register(String emailAddress, String password, UserType userType);

	List<Prediction> findPredictions(String userId, Boolean viewed,
			int firstResult, int maxResults);

}
