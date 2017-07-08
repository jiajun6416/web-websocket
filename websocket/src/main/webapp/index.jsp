<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	response.setHeader("Cache-Control","no-cache"); 
	response.setDateHeader ("Expires", 0); 
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
<title>WebSocket 聊天室 1</title>
<!-- 引入CSS文件 -->
<link rel="stylesheet" type="text/css" href="<%=path%>/ext4/resources/css/ext-all.css">
<link rel="stylesheet" type="text/css" href="<%=path%>/ext4/shared/example.css" />
<link rel="stylesheet" type="text/css" href="<%=path%>/css/websocket.css" />
<style type="text/css">
	.login {
		margin-left: 40%;
		margin-top: 10%;
		width: 300px;
		height: 400px;
		position: absolute;
		z-index: 999;
	}
	
	.time {
		margin-left: 10%;
		margin-top: 5%;
		width: 200px;
		height: 200px;
		position: absolute;
		z-index: 999;
	}
</style>
<script type="text/javascript" src="<%=basePath%>/ext4/ext-all-debug.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/jquery-2.1.0.min.js"></script>
<script type="text/javascript" src="<%=basePath%>/js/websocket.js"></script>
<script type="text/javascript">
	var user;
	$(function() {
		if(${sessionScope.username != null}) {
			user = '${sessionScope.username}';
			$(".login").hide();
			//显示聊天窗口
			showChart();
		} else {
			$("input").focus();
			$("input").bind("keyup", function(event) {
				if(event.keyCode == "13") {
					var name = $(this).val();
					if(name.trim().length == 0) {
						alert("姓名不能为空!");
					} else {
						if(check(name)) {
							user = name;
							$(".login").hide();
							//显示聊天窗口
							showChart();
						}
					}
				}
			});
		}
	});
	function check(username) {
		var flag = false;
	    $.ajax({
	        type: "POST",
	        url: "<%=path%>/NameConfirm", 
			async: false,
	        data: {'username':username},
	        dataType: "json", //返回类型
	        success: function(data){
	        	if(data.result == "success") {
		        	flag = true;
	        	} else {
	        		alert("名字已经存在");
	        	}
	        },
		 	error:function() {
				alert("服务器异常");
		 	}
	    });
		return flag;
	}
	

	
	
</script>
</head>
<div class="time" ><span id="timeMessage"></span></div>
<div style="margin-left: 40%; margin-top: 10%; height: 300px; width: 300px;" class="login">
	用户名: <input type="text" name="user"> 
</div>
<body>
</body>
</html>

