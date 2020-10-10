<%--
  Created by IntelliJ IDEA.
  User: david
  Date: 2020-10-10
  Time: 13:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>员工列表页</title>
</head>
<body>
<table>
    <c:forEach items="${employees}" var="emp">
        <tr>
            <td>id</td>
            <td>lastName</td>
            <td>email</td>
            <td>gender</td>
        </tr>
        <tr>
            <td>${emp.id}</td>
            <td>${emp.lastName}</td>
            <td>${emp.email}</td>
            <td>${emp.gender}</td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
