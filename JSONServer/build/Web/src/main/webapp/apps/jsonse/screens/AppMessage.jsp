
<!DOCTYPE html>
<html>
<head>
<title>Error</title>
</head>
<body>
	<%
		String message = (String)request.getAttribute("Message");
	%>
	<h4><%=message%></h4>
</body>
</html>
