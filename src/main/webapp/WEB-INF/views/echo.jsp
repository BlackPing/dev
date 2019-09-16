<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<script type="text/javascript" src="/res/js/jquery-3.4.1.min.js"></script>
<script type="text/javascript" src="/res/js/sockjs.min.js"></script>
</head>

<body>
	<h1>Echo Server Main session ${sessionScope.key}</h1>
	<form action="/1" method="post">
		<input type="submit" value="접속">
	</form>
</body>

</html>