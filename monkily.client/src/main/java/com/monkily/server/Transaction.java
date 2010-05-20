package com.monkily.server;

import javax.persistence.EntityManager;

public interface Transaction {
	public void execute(EntityManager manager);
}
