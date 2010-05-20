package com.monkily.utils;

import java.text.DateFormat;

import com.dumbster.smtp.SimpleSmtpServer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class JsonUtils {

	SimpleSmtpServer a;
	
	public static Gson createDefaultGson() {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		builder.setDateFormat(DateFormat.MILLISECOND_FIELD);
		return builder.create();
	}

}
