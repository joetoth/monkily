/**
 * 
 */
package com.monkily.server;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class ApplicationContext {

	private static final String STATELESS_CONTEXT_FILE = "com/monkily/server/applicationContext.xml";
	private static ApplicationContext instance = null;
	protected GenericApplicationContext springContext = null;

	public static synchronized final ApplicationContext getInstance() {
		if (instance == null) {
			instance = new ApplicationContext();
		}
		return instance;
	}

	protected ApplicationContext() {
		initContextFile();
	}

	public <T> T getBean(Class<T> type) {
		return (T) springContext.getBeansOfType(type).values().iterator()
				.next();
	}

	public Object getBean(String beanName) {
		return springContext.getBean(beanName);
	}

	private void initContextFile() {
		springContext = new GenericApplicationContext();
		XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(
				springContext);
		xmlReader.loadBeanDefinitions(new ClassPathResource(
				STATELESS_CONTEXT_FILE));
		springContext.refresh();
	}
}
