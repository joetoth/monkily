package com.monkily.user.internal.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import com.monkily.user.model.User;
import com.monkily.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@PersistenceContext
	EntityManager entityManager;

	public List<User> getAllUsers() {
		return entityManager.createQuery(
				"SELECT o FROM " + User.class.getName() + " o ")
				.getResultList();
	}

}
