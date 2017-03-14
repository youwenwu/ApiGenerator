<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
<title>API列表</title>

<!-- Bootstrap -->
<link
	href="http://apps.bdimg.com/libs/bootstrap/3.3.4/css/bootstrap.css"
	rel="stylesheet">

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
<!--[if lt IE 9]>
      <script src="https://cdn.bootcss.com/html5shiv/3.7.3/html5shiv.min.js"></script>
      <script src="https://cdn.bootcss.com/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body class="container">
	<p></p>
	<div class="panel panel-primary">
		<!-- Default panel contents -->
		<div class="panel-heading">API列表</div>

		<!-- Table -->
		<table class="table">
			<thead>
				<tr class="active">
					<td>服务名称</td>
					<td>服务地址</td>
					<td>版本号</td>
					<td>服务类型</td>
				</tr>
			</thead>
			<tbody>
				<#list list as being>
				<tr>
					<td>${being.name!''}</td>
					<td><a
						href="${contextPath!''}/api/apiDetail?className=${being.className!''}&methodName=${being.methodName!''}">${being.url!''}</a></td>
					<td>1.0</td>
					<td>${being.methodType!''}</td>
				</tr>
				</#list>
			</tbody>
		</table>
	</div>
</body>
</html>