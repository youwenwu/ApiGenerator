<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
<title>API详情</title>

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
		<div class="panel-heading">服务概览</div>

		<!-- 服务概览 -->
		<table class="table">
			<thead>
				<tr>
					<td>服务名称</td>
					<td>服务地址</td>
					<td>版本号</td>
					<td>服务类型</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>${detail.name!''}</td>
					<td>${detail.url!''}</td>
					<td>1.0</td>
					<td>同步</td>
				</tr>
			</tbody>
		</table>
	</div>

	<div class="panel panel-primary">
		<!-- Default panel contents -->
		<div class="panel-heading">服务入参</div>

		<!-- 服务入参 -->
		<table class="table">
			<thead>
				<tr>
					<td>参数名称</td>
					<td>参数类型</td>
					<td>参数说明</td>
					<td>是否必填</td>
				</tr>
			</thead>
			<tbody>
				<#list detail.parameters as being>
				<tr>
					<td>${being.value!''}</td>
					<td>${being.type!''}</td>
					<td>${being.name!''}</td>
					<td>${being.required?string("true","flase")}</td>
				</tr>
				</#list>
			</tbody>
		</table>
	</div>

	<div class="panel panel-primary">
		<!-- Default panel contents -->
		<div class="panel-heading">服务出参</div>

		<table class="table">
			<thead>
				<tr>
					<td>参数名称</td>
					<td>参数类型</td>
					<td>参数说明</td>
					<td>是否必填</td>
				</tr>
			</thead>
			<tbody>
				<#list detail.returnType?keys as key> <#if
				detail.returnType[key].detail??>
				<tr>
					<td>${key ! ''}</td>
					<td><a data-toggle="collapse" href="#collapseExample" aria-expanded="false" aria-controls="collapseExample">${detail.returnType[key].type ! ''}</a></td>
					<td></td>
					<td></td>
				</tr>
				<tr class="collapse" id="collapseExample">
					<td colspan="4">test</td>
				</tr>
				</tbody>
				<#else>
				<tr>
					<td>${key ! ''}</td>
					<td>${detail.returnType[key].type ! ''}</td>
					<td></td>
					<td></td>
				</tr>
				</#if> 
				</#list>
			</tbody>
		</table>
	</div>


	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script type="text/javascript"
		src="http://apps.bdimg.com/libs/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	
	<script type="text/javascript">
		function popup()
		{
			alert (1);
		}
	</script>
</body>
</html>