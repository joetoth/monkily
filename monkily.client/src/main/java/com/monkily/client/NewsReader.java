package com.monkily.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.monkily.client.service.UserServiceAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class NewsReader implements EntryPoint {

	public void onModuleLoad() {
		String token = History.getToken();
		if (token != null && token.equals("google")) {
			GWT.log("Google login!", null);
			googleLogin();
		}
	}

	private void googleLogin() {
		UserServiceAsync.RPC.get().googleLogin(new AsyncCallback() {

			@Override
			public void onSuccess(Object result) {
				showLoginSuccess();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}

	private void showLoginSuccess() {
		RootPanel.get().add(new HTML("<a href=\"/monkily-1.0.xpi\">Download Monkily</a>"));
	}

}
