package com.monkily.utils;

import javax.persistence.EntityManager;

public abstract class DatabaseUtils {

	public static void forcePersist(EntityManager entityManager, Object id,
			Object object) {
		Object tmp = entityManager.find(object.getClass(), id);
		if (tmp == null) {
			entityManager.persist(object);
		} else {
			entityManager.merge(object);
		}
	}

}
