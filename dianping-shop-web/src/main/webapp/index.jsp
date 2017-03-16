<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head lang="en">
<meta charset="UTF-8">
<title>点评网吧数据</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="format-detection" content="telephone=no">
<meta name="renderer" content="webkit">
<meta http-equiv="Cache-Control" content="no-siteapp" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<link rel="alternate icon" type="image/png"
	href="${ctx}/static/images/favicon.png">
<link rel="stylesheet" href="${ctx}/static/css/amazeui.min.css" />
<link rel="stylesheet" href="${ctx}/static/css/admin.css" />
<link rel="stylesheet" href="${ctx}/static/css/app.css" />
<style>
.header {
	text-align: center;
}

.header h1 {
	font-size: 200%;
	color: #3BB4F2;
	margin-top: 30px;
}

.footer p {
	margin: 0;
	padding: 15px 0;
	text-align: center;
	background: #3BB4F2;
}

.footer a {
	color: #000000
}
</style>
</head>
<body>
	<div class="header">
		<div class="am-g">
			
			<h1>点评网吧数据</h1><img alt="" src="${ctx}/static/images/logo.png">
		</div>
		<hr />
	</div>
	<div class="am-g">
		<div class="am-u-lg-6 am-u-md-8 am-u-sm-centered">
			<h3>登录</h3>
			<hr>
			<br>
			<c:if test="${msg!=null}">
				<div class="am-alert" data-am-alert>
					<button type="button" class="am-close">&times;</button>
					<p>
						<c:out value="${msg}" />
						<br>
					</p>
				</div>
			</c:if>
			<form method="post" action="${ctx}/login" class="am-form">
				<label for="content">用户名:</label> <input type="text" name="name"
					id=""name"" value=""> <br> <label for="password">密码:</label>
				<input type="password" name="password" id="password" value=""> <br>
				<br />
				<div class="am-cf">
					<input type="submit" name="" value="登 录"
						class="am-btn am-btn-primary am-btn-sm am-fl"
						data-am-loading="{loadingText: '登录中...'}"> 
				</div>
			</form>
			<hr>
		</div>
	</div>

	<footer class="footer">
		<p>
			© 2017 <a href="https://www.xiayi.ren" target="_blank">未经许可不得复制、转载或摘编，违者必究！ </a>
		</p>
	</footer>
	<!--[if lt IE 9]>
	<script type="text/javascript" src="${ctx}/static/js/ltie9/jquery.min.js"></script>
	<script type="text/javascript" src="${ctx}/static/js/ltie9/modernizr.js"></script>
	<script type="text/javascript" src="${ctx}/static/js/polyfill/rem.min.js"></script>
	<script type="text/javascript" src="${ctx}/static/js/polyfill/respond.min.js"></script>
	<script type="text/javascript" src="${ctx}/static/js/amazeui.legacy.js"></script>
	<![endif]-->
	<!--[if (gte IE 9)|!(IE)]><!-->
	<script type="text/javascript" src="${ctx}/static/js/jquery.min.js"></script>
	<script type="text/javascript" src="${ctx}/static/js/amazeui.min.js"></script>
	<script type="text/javascript" src="${ctx}/static/js/amazeui.widgets.helper.min.js"></script>
	<script type="text/javascript" src="${ctx}/static/js/app.js"></script>
	<!--<![endif]-->
</body>
</html>