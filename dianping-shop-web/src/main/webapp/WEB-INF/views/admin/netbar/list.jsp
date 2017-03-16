<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="sitemesh"
	uri="http://www.opensymphony.com/sitemesh/decorator"%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head lang="en">
<meta charset="UTF-8">
<script type="text/javascript">
$.extend({
    StandardPost:function(url,args){
        var body = $(document.body),
            form = $("<form method='post'></form>"),
            input;
        form.attr({"action":url});
        $.each(args,function(key,value){
            input = $("<input type='hidden'>");
            input.attr({"name":key});
            input.val(value);
            form.append(input);
        });

        form.appendTo(document.body);
        form.submit();
        document.body.removeChild(form[0]);
    }
});
</script>
<script type="text/javascript" src="${ctx}/static/plugin/My97DatePicker/WdatePicker.js"></script>
</head>
<!-- content start -->
<div class="admin-content">
	<div class="am-cf am-padding">
		<div class="am-fl am-cf">
			<strong class="am-text-primary am-text-lg">网吧管理</strong> 
		</div>
	</div>
	<div class="am-g">
		<div class="am-u-sm-12">
			<table class="am-table am-table-striped am-table-hover table-main">
				<thead>
					<tr>
						<th class="table-id">ID</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="netbar" items="${vo.list}">
						<tr>
							<td>${netbar.id}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</div>
<div class="am-modal am-modal-confirm" tabindex="-1"
	id="operate-confirm">
	<div class="am-modal-dialog">
		<div class="am-modal-hd">温馨提示</div>
		<div class="am-modal-bd">确定要执行操作吗？</div>
		<div class="am-modal-footer">
			<span class="am-modal-btn" data-am-modal-cancel>取消</span> <span
				class="am-modal-btn" data-am-modal-confirm>确定</span>
		</div>
	</div>
</div>
