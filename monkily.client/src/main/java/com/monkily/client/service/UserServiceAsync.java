package com.monkily.client.service;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.monkily.model.Prediction;
import com.monkily.model.UserType;

public interface UserServiceAsync {

	public static class RPC {

		public static UserServiceAsync get() {
			UserServiceAsync ourInstance = (UserServiceAsync) GWT
					.create(UserService.class);
			((ServiceDefTarget) ourInstance)
					.setServiceEntryPoint("/rpc/userService");

			return ourInstance;
		}
	}

	public void register(String emailAddress, String password,
			UserType userType, AsyncCallback<String> callback);

	public void login(String emailAddress, String password,
			AsyncCallback callback);

	public void googleLogin(AsyncCallback callback);

	public void isLoggedIn(AsyncCallback<Boolean> callback);

	public void findPredictions(String userId, Boolean viewed, int firstResult,
			int maxResults, AsyncCallback<List<Prediction>> callback);
}
