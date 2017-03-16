# 概览
当使用spring mvc来构建restful风格的web service时，需要同时为服务提供文档描述，当服务功能有调整时还需要更新对应的文档，这是程序员不太愿意干的事情，有了ApiGenerator,这一切将变得简单

ApiGenerator是一个spring mvc的插件，致力于将开发人员从繁琐的文档中解放出来，它提供了一系列注解用来描述restful service的功能，出参，入参等等，它的使用非常简单

### 列表页示例
![列表页](https://github.com/youwenwu/ApiGenerator/blob/master/list.png)

### 详情页示例
![详情页](https://github.com/youwenwu/ApiGenerator/blob/master/detail.png)
# maven依赖
首先导入maven依赖
`````````
<dependency>
	<groupId>com.github.youwenwu</groupId>
	<artifactId>api-generator</artifactId>
	<version>0.0.1</version>
</dependency>
`````````
# spring配置自动扫描
ApiGenerator提供了一个Controller来处理所有请求，APIController，配置spring自动扫描com.github.youwenwu.controller
`````````
<context:component-scan base-package="com.github.youwenwu.api.controller" />
`````````

项目部署完成，示例项目名为demo，启动tomcat,尝试访问localhost:8080/demo/api，你将看到api列表页
# 注解
ApiGenerator提供`````````APISummary,APIField,APIParameter,APIReturn,APIReturnDetail`````````几个注解

#### APIField
应用于APIParameter,APIReturn,描述出参入参的具体数据结构
`````````
name 名称，默认为空
type 类型，默认为空
desc 描述，默认为空
required 是否可为空，默认false
refId 详细数据结构参照标识(如果是包装类型用到，参照APIReturnDetail的id)， 默认为空
example 示例，默认为空
`````````

#### APISummary
应用于Controller的Method,描述service基本信息
`````````
name 表示名称，默认为空
url 请求地址，默认为空
methodType 请求类型， 默认"GET/POST"
`````````
#### APIParameter
应用于Controller的Method,描述service的入参
`````````
parameters 入参列表，为APIField数组，默认为空
`````````
#### APIReturn
应用于Controller的Method,描述service的返回值
`````````
type 返回类型，例如List, Map等等，默认为空
fields 定义接口返回的数据结构，为APIField数组，默认为空
`````````
#### APIReturnDetail
应用于Controller的Method,定义返回的复杂对象数据结构
`````````
id 结构标识,由APIField中的refId参照，默认为空
fields 定义接口返回的数据结构，为APIField数组，默认为空
`````````
# 示例
该示例入参为3个参数token，memberCode，companyName，出参为复杂对象RestResult，RestResult定义如下
`````````
private int code;
private String msg;
private Object data;
`````````
其中data为复杂对象，定义参照APIReturnDetail，注意refId的使用
`````````
@RequestMapping("/selectCustomerInfo")
@APISummary(name = "客户基础信息查询接口", url = "/selectCustomerInfo")
@APIParameter(parameters = {
		@APIField(name = "token", desc = "请求令牌", required = true, type = "String"),
		@APIField(name = "memberCode", desc = "会员编码"), 
		@APIField(name = "companyName", desc = "会员店名称"),
		@APIField(name = "artificialPersonMobile", desc = "会员手机号")})
@APIReturn(type = "Map", fields = {
		@APIField(name = "code", desc = "状态码"), 
		@APIField(name = "msg", desc = "返回消息"), 
		@APIField(name = "data", desc = "业务数据", refId = "dataDetail", type = "Map")})
@APIReturnDetail(id = "dataDetail", fields = {
		@APIField(name = "memberID", desc = "会员ID", type = "String"), 
		@APIField(name = "memberCode", desc = "会员Code(会员账号)", type = "String"), 
		@APIField(name = "companyName", desc = "会员店名称", type = "String"),
		@APIField(name = "artificialPersonMobile", desc = "法人手机号", type = "String"),
		@APIField(name = "artificialPersonName", desc = "法人姓名", type = "String"),
		@APIField(name = "curBelongSellerId", desc = "当前归属商家", type = "Long"),
		@APIField(name = "curBelongManagerId", desc = "当前商家客户经理ID", type = "Long"),
		@APIField(name = "curBelongSellerName", desc = "平台公司名称", type = "String")})
public RestResult selectCustomerInfo(
		@RequestParam(value = "memberCode", required = false) String memberCode,
		@RequestParam(value = "companyName", required = false) String companyName,
		@RequestParam(value = "artificialPersonMobile", required = false) String artificialPersonMobile) {
	RestResult result = new RestResult();
	try {
		......	

	} catch (Exception e) {
		......
	}
	return result;
}
`````````
