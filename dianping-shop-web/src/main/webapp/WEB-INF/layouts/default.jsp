<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="sitemesh"
	uri="http://www.opensymphony.com/sitemesh/decorator"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
<head lang="en">
<meta charset="UTF-8">
<title>商铺信息</title>
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
<script type="text/javascript"
	src="${ctx}/static/js/amazeui.widgets.helper.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/app.js"></script>
<!--<![endif]-->
<sitemesh:head />
</head>
<body>
	<!--[if lte IE 9]>
<p class="browsehappy">你正在使用<strong>过时</strong>的浏览器，暂不支持。 请 <a href="http://browsehappy.com/" target="_blank">升级浏览器</a>
  以获得更好的体验！</p>
<![endif]-->
	<c:if test="${sessionScope.sitemesh eq 'Y'}">
		<%@ include file="/WEB-INF/layouts/header.jsp"%>
		<div class="am-cf admin-main">
			<%@ include file="/WEB-INF/layouts/left.jsp"%>
			<div class="admin-content">
				<sitemesh:body />
			</div>
		</div>
		<%@ include file="/WEB-INF/layouts/footer.jsp"%>
	</c:if>

	<c:if test="${sessionScope.sitemesh != 'Y'}">
		<div class="am-cf admin-main">
			<sitemesh:body />
		</div>
	</c:if>

</body>
</html>