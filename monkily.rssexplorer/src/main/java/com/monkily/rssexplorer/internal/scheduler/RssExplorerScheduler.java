package com.monkily.rssexplorer.internal.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.osgi.context.event.OsgiBundleApplicationContextEvent;
import org.springframework.osgi.context.event.OsgiBundleApplicationContextListener;
import org.springframework.stereotype.Service;

import com.monkily.rssexplorer.service.RssService;
import com.monkily.utils.StringUtils;

@Service
public class RssExplorerScheduler implements InitializingBean {

	private final static Log log = LogFactory
			.getLog(RssExplorerScheduler.class);

	@Autowired
	RssService rssService;

	ScheduledExecutorService executorService;

	public RssExplorerScheduler() {
		executorService = Executors.newScheduledThreadPool(1);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		start();
	}

	public void start() {
		Runnable command = new Runnable() {

			@Override
			public void run() {
				log.debug("Running Rss Scheduler");
				System.out.println("Runnings scheduler!!!!");
				try {
					rssService.checkAll();
				} catch (Exception e) {
					log.error("Error Checking Rss", e);
				}
			}
		};

		executorService
				.scheduleWithFixedDelay(command, 0, 15, TimeUnit.MINUTES);
	}

}
