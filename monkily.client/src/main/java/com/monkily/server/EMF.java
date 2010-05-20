package com.monkily.server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public final class EMF {
	private static final EntityManagerFactory emfInstance = Persistence
			.createEntityManagerFactory("transactions-optional");

	private EMF() {
	}

	public static EntityManagerFactory get() {
		return emfInstance;
	}

	public static void run(Transaction transaction) {
		EntityManager manager = get().createEntityManager();
		manager.getTransaction().begin();
		transaction.execute(manager);
		manager.getTransaction().commit();
		manager.close();
	}

}