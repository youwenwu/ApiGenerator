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
		<table class="table table-bordered table-condensed">
			<thead>
				<tr class="active">
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
					<td>${detail.methodType!''}</td>
				</tr>
			</tbody>
		</table>
	</div>

	<div class="panel panel-primary">
		<!-- Default panel contents -->
		<div class="panel-heading">服务入参</div>

		<!-- 服务入参 -->
		<table class="table table-bordered table-condensed">
			<thead>
				<tr class="active">
					<td>参数名称</td>
					<td>参数类型</td>
					<td>参数说明</td>
					<td>是否必填</td>
					<td>示例</td>
				</tr>
			</thead>
			<tbody>
				<#list detail.parameters as being>
				<tr>
					<td>${being.name!''}</td>
					<td>${being.type!''}</td>
					<td>${being.desc!''}</td>
					<td>${being.required?string("是","否")}</td>
					<td>${being.example!''}</td>
				</tr>
				</#list>
			</tbody>
		</table>
	</div>

	<div class="panel panel-primary">
		<!-- Default panel contents -->
		<div class="panel-heading">服务出参</div>

		<table class="table table-bordered table-condensed">
			<thead>
				<tr class="active">
					<td>参数名称</td>
					<td>参数类型</td>
					<td>参数说明</td>
					<td>示例</td>
				</tr>
			</thead>
			<tbody id = "output">
			</tbody>
		</table>
	</div>



	<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
	<script src="https://cdn.bootcss.com/jquery/1.12.4/jquery.min.js"></script>
	<!-- Include all compiled plugins (below), or include individual files as needed -->
	<script type="text/javascript"
		src="http://apps.bdimg.com/libs/bootstrap/3.3.4/js/bootstrap.min.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			var returnFieldList = JSON.parse('${detail.returnFieldList}');
			var returnDetailMap = JSON.parse('${detail.returnDetailMap}');
			$('#output').html(initOutput(returnFieldList, returnDetailMap));
		});
		
		function initOutput(returnFieldList, returnDetailMap)
		{
			var html = '';
			returnFieldList.forEach(function( val, index ) {
	
				html += '<tr><td>' + val.name + '</td><td>' + val.type +  '</td><td>' + val.desc + '</td><td>' + val.example + '</td></tr>';
				if (val.refId != '')
				{
					html += '<tr><td colspan="4"><table class="table table-bordered table-condensed"><thead><tr class="success"><td>(' + val.name + ')参数名称</td><td>参数类型</td><td>参数说明</td><td>示例</td></tr></thead><tbody>';
					$.each(returnDetailMap, function(key, value) {
						if (key == val.refId)
						{
							html += initOutput(value, returnDetailMap);
						}
					});
					html += '</tbody></table></td></tr>';
				}
				console.log(html);
			});
			return html;
		}
		
	</script>
</body>
</html>