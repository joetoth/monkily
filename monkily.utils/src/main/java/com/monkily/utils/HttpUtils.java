package com.monkily.utils;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

public abstract class HttpUtils {

	public static void post(String url, String json) {
		try {
			// Send data
			URL _url = new URL(url);
			URLConnection conn = _url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn
					.getOutputStream());
			wr.write(json);
			wr.flush();
			wr.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static InputStream get(String url) {

		try {
			// Send data
			HttpClient client = new HttpClient();
			
			GetMethod method = new GetMethod(url);
			method.setFollowRedirects(true);
			client.executeMethod(method);

			return method.getResponseBodyAsStream();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
