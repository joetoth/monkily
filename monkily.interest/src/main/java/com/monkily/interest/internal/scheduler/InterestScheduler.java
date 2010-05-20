package com.monkily.interest.internal.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.monkily.interest.service.InterestService;

@Service
public class InterestScheduler implements InitializingBean {

	private final static Log log = LogFactory.getLog(InterestScheduler.class);

	@Autowired
	InterestService interestService;

	ScheduledExecutorService executorService;

	@Override
	public void afterPropertiesSet() throws Exception {
		start();
	}

	public InterestScheduler() {
		executorService = Executors.newScheduledThreadPool(1);
	}

	public void start() {
		Runnable command = new Runnable() {

			@Override
			public void run() {
				try {
					interestService.processLogs();
				} catch (Exception e) {
					log.error("Error processing logs", e);
					e.printStackTrace();
				}
			}
		};

		executorService.scheduleWithFixedDelay(command, 0, 5, TimeUnit.SECONDS);
	}

}
