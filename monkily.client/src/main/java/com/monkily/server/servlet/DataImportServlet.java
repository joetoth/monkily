package com.monkily.server.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monkily.model.ContentSummary;
import com.monkily.model.Prediction;
import com.monkily.model.User;
import com.monkily.server.EMF;
import com.monkily.server.Transaction;
import com.monkily.utils.DatabaseUtils;
import com.monkily.utils.JsonUtils;

public class DataImportServlet extends HttpServlet {

	Gson gson = JsonUtils.createDefaultGson();

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Map<String, String> parameters = extractParameters(req.getPathInfo());

		if (!parameters.get("password").equals("89getuf54")) {
			throw new RuntimeException("Authentication Error");
		}

		String className = parameters.get("class");
		String criteria = parameters.get("criteria");

		Class clazz = null;
		try {
			clazz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		if (criteria != null) {
			if (criteria.equals("ALL")) {
				criteria = null;
			}
			String json = exportData(clazz, criteria);
			resp.getWriter().write(json);
		} else {
			importData(clazz, req.getInputStream());
		}

	}

	private Map<String, String> extractParameters(String path) {
		Map<String, String> parameters = new HashMap<String, String>();
		String[] str = path.split("/");
		for (int i = 1; i < str.length; i = i + 2) {
			parameters.put(str[i], str[i + 1]);
		}

		return parameters;
	}

	private void importData(Class clazz, InputStream stream) {

		Type type = null;

		if (clazz.equals(User.class)) {
			type = new TypeToken<Collection<User>>() {
			}.getType();
		} else if (clazz.equals(Prediction.class)) {
			type = new TypeToken<Collection<User>>() {
			}.getType();
		} else if (clazz.equals(ContentSummary.class)) {
			type = new TypeToken<Collection<User>>() {
			}.getType();
		}

		InputStreamReader reader = new InputStreamReader(stream);
		List contents = (List) gson.fromJson(reader, type);

		for (final Object object : contents) {
			String tmp;

			try {
				tmp = (String) PropertyUtils.getProperty(object, "id");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

			final String id = tmp;

			EMF.run(new Transaction() {

				@Override
				public void execute(EntityManager manager) {
					DatabaseUtils.forcePersist(manager, id, object);
				}

			});
		}
	}

	private String exportData(Class clazz, String criteria) {
		EntityManager entityManager = EMF.get().createEntityManager();
		Query query = entityManager.createQuery("SELECT o FROM "
				+ clazz.getName() + " o " + (criteria == null ? "" : criteria));
		return gson.toJson(query.getResultList());
	}

}
