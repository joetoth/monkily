package com.monkily.server;

import java.util.Map;
import java.util.Properties;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.monkily.dataTransfer.service.DataTransferService;
import com.monkily.utils.AfterInitialization;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Properties properties = new Properties();
		try {
			properties.load(Main.class
					.getResourceAsStream("monkily.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log4JConfigurer.initLogging(properties);

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				new String[] { "classpath*:/com/monkily/**/applicationContext.xml" });
context.start();

//context.

		// <import
		// resource="classpath:/com/monkily/content/applicationContext.xml"/>
		// <import
		// resource="classpath:/com/monkily/dataTransfer/applicationContext.xml"/>
		// <import
		// resource="classpath:/com/monkily/interest/applicationContext.xml"/>
		// <import
		// resource="classpath:/com/monkily/prediction/applicationContext.xml"/>
		// <import
		// resource="classpath:/com/monkily/rssexplorer/applicationContext.xml"/>
		// <import
		// resource="classpath:/com/monkily/user/applicationContext.xml"/>

		// Call After Initialized
		Map<String, AfterInitialization> m = context
				.getBeansOfType(AfterInitialization.class);

		for (AfterInitialization ai : m.values()) {
			ai.afterInitialization();
		}

		// ApplicationContext context = ApplicationContext.getInstance();

		DataTransferService dts = (DataTransferService) context.getBeansOfType(
				DataTransferService.class).values().iterator().next();
		dts.transfer();
		//
		// context.getBean(InterestService.class).processLogs();
		//
		// context.getBean(RssService.class).checkAll();

		// context.getBean(PredictionService.class).predictAllInterest();

	}
}
