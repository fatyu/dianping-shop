<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%response.setStatus(200);%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>操作错误</title>
</head>
<body>
	<h2>操作错误.</h2>
	<h3>${msg}</h3>
	<p><a href="<c:url value="/${backUrl}"/>">返回</a></p>
</body>
</html>