package com.monkily.server.service.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.Cookie;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.monkily.client.service.UserService;
import com.monkily.model.Prediction;
import com.monkily.model.User;
import com.monkily.model.UserType;
import com.monkily.server.EMF;
import com.monkily.utils.GUID;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class UserServiceImpl extends RemoteServiceServlet implements
		UserService {

	@Override
	public String register(String emailAddress, String password,
			UserType userType) {
		System.out.println("registering: " + emailAddress);

		User user = new User();
		user.setEmailAddress(emailAddress);
		user.setId(GUID.generate());
		user.setPassword(password);
		user.setUserType(userType);
		user.setUpdatedDate(new Date());
		user.setCreatedDate(new Date());

		EntityManager em = EMF.get().createEntityManager();
		em.getTransaction().begin();
		em.persist(user);

		List<User> users = EMF.get().createEntityManager().createQuery(
				"SELECT o FROM " + User.class.getName() + " o").getResultList();

		for (User u : users) {
			System.out.println(u.getEmailAddress());
		}

		em.flush();
		em.getTransaction().commit();

		em.close();

		return user.getId();
	}

	@Override
	public void googleLogin() {
		String name = getThreadLocalRequest().getUserPrincipal().getName();

		User user = findByEmailAddress(name);

		if (user == null) {
			register(name, null, UserType.Google);
		}

		login(name, null);
	}

	private void setCookie(String userId) {
		Cookie cookie = new Cookie("userId", userId);
		cookie.setMaxAge(60 * 60 * 24 * 365 * 10);
		getThreadLocalResponse().addCookie(cookie);
	}

	private User findByEmailAddress(String emailAddress) {
		EntityManager em = EMF.get().createEntityManager();

		Query query = em.createQuery("SELECT o FROM " + User.class.getName()
				+ " o WHERE o.emailAddress=:emailAddress");
		query.setParameter("emailAddress", emailAddress);
		User user = null;

		List<User> users = query.getResultList();
		if (!users.isEmpty()) {
			user = users.get(0);
		}

		em.close();

		return user;
	}

	@Override
	public boolean login(String emailAddress, String password) {

		boolean valid = false;

		User user = findByEmailAddress(emailAddress);
		
		if (user != null && user.getUserType().equals(UserType.Google)) {
			valid = true;
		} else if (user.getPassword().equals(password)) {
			valid = true;
		}

		if (valid) {
			getThreadLocalRequest().getSession().setAttribute(
					SESSION_ATTR_USER_ID, user.getId());
			setCookie(user.getId());
		}

		return valid;
	}

	@Override
	public Boolean isLoggedIn() {

		String userId = (String) getThreadLocalRequest().getSession()
				.getAttribute(SESSION_ATTR_USER_ID);

		return userId != null;
	}

	@Override
	public List<Prediction> findPredictions(String userId, Boolean viewed,
			int firstResult, int maxResults) {

		EntityManager entityManager = EMF.get().createEntityManager();

		StringBuilder sb = new StringBuilder("SELECT o FROM "
				+ Prediction.class.getName() + " o WHERE o.userId=:userId");

		if (viewed != null) {
			sb.append(" AND o.viewed=:viewed");
		}

		Query query = entityManager.createQuery(sb.toString());
		query.setParameter("userId", userId);

		if (viewed != null) {
			query.setParameter("viewed", viewed);
		}

		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);

		List<Prediction> predictions = query.getResultList();

		entityManager.close();

		return predictions;
	}
}
