<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.google.appengine.api.users.UserService"%>
<%@page import="com.google.appengine.api.users.UserServiceFactory"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Download Monkily!</title>
</head>
<body>

To download Monkily...


<%
	UserService userService = UserServiceFactory.getUserService();

	String thisURL = request.getRequestURI();
	if (request.getUserPrincipal() != null) {
		response.getWriter().println(
				"<p>Hello, " + request.getUserPrincipal().getName()
						+ "!  You can <a href=\""
						+ userService.createLogoutURL(thisURL)
						+ "\">sign out</a>.</p>");
	} else {
		response
				.getWriter()
				.println(
						"<p>Please <a href=\""
								+ userService
										.createLoginURL("/NewsReader.html#google")
								+ "\">sign in</a>.</p>");
	}
%>

</body>
</html>