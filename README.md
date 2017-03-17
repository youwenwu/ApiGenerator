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
ApiGenerator提供`````````APISummary,APIField,APIParameter,APIReturn,APIWrapperField`````````几个注解

#### APIField
字段属性
`````````
name 名称，默认为空
type 类型，默认为空
desc 描述，默认为空
required 是否可为空，默认false
refId 详细数据结构参照标识(如果是包装类型用到，参照APIWrapperField的id)， 默认为空
example 示例，默认为空
_class 定义接口返回对象 和refId 2选1
`````````
使用示例
`````````
public class Book {
	//图书名称为基本类型
	@APIField(name = "name", desc = "图书名称", type = "String", example = "Think in java")
	private String name;
	//图书价格为基本类型
	@APIField(name = "price", desc = "图书价格", type = "BigDecimal", example = "30.00")
	private BigDecimal price;
	//图书类别为封装类型
	@APIField(name = "category", desc = "图书类别", type = "Object", _class = Category.class)
	private Category category;
        //图书作者为List类型(Map类型参照@APIWrapperField用法)
	@APIField(name = "category", desc = "图书类别", type = "List", _class = Author.class)
	private List<Author> authors;
}
`````````
#### APIWrapperField
应用于Method或者Field,定义复杂对象数据结构
`````````
id 结构标识,由APIField中的refId参照，默认为空
fields 定义接口返回的数据结构，为APIField数组，默认为空
`````````
使用示例
`````````
//图书属性为Map类型
@APIField(name = "properties", desc = "图书属性", type = "Map", refId = "properties")
@APIWrapperField(id = "properties", fields = {
                        @APIField(name = "name", desc = "图书名称", type = "String", example = "Think in java"),
			@APIField(name = "price", desc = "图书价格", type = "BigDecimal", example = "30.00"),
			@APIField(name = "category", desc = "图书类别", type = "Object", _class = Category.class)})
private Map<String, String> properties;
`````````
#### APISummary
应用于Controller的Method,描述service基本信息
`````````
name 表示名称，默认为空
url 请求地址，默认为空
methodType 请求类型， 默认"GET/POST"
`````````
使用示例
`````````
@RestController
public class BookController {
	
	@GetMapping("/doNothing")
	@APISummary(name = "不做任何事", url = "/doNothing", methodType = "GET")
	public void doNothing()
	{
		System.out.println("do nothing!");
	}
}
`````````
#### APIParameter
应用于Controller的Method,描述service的入参
`````````
parameters 入参列表，为APIField数组，默认为空
`````````
使用示例
`````````
@RestController
public class BookController {
	
	@GetMapping("/insertBook")
	@APISummary(name = "新增图书", url = "/insertBook", methodType = "POST")
	@APIParameter(parameters = {@APIField(name = "name", desc = "图书名称", type = "String", example = "Think in java"), 
			@APIField(name = "price", desc = "图书价格", type = "BigDecimal", example = "30.00")})
	public void insertBook(String name, BigDecimal price)
	{
		System.out.println("新增图书!");
	}
}
`````````
#### APIReturn
应用于Controller的Method,描述service的返回值
`````````
type 返回类型，例如List, Map等等，默认为空
fields 定义接口返回的数据结构，为APIField数组，默认为空
_class 定义接口返回对象，和fields 2选1
`````````
使用示例
`````````
@GetMapping("/selectBookByName")
@APISummary(name = "根据书名查询图书", url = "/selectBookByName", methodType = "GET")
@APIParameter(parameters = {@APIField(name = "name", desc = "根据书名查询图书", type = "String", example = "Think in java")})
@APIReturn(_class = Book.class)
public Book selectBookByName(String name)
{
	return null;
}
	
@GetMapping("/selectBookByName")
@APISummary(name = "根据书名查询图书属性", url = "/selectBookPropertiesByName", methodType = "GET")
@APIParameter(parameters = {@APIField(name = "name", desc = "根据书名查询图书属性", type = "String", example = "Think in java")})
@APIReturn(type = "Map", fields = {@APIField(name = "name", desc = "图书名称", type = "String", example = "Think in java"),
		@APIField(name = "price", desc = "图书价格", type = "BigDecimal", example = "30.00"),
		@APIField(name = "category", desc = "图书类别", type = "Object", _class = Category.class)})
public Map<String, String> selectBookPropertiesByName(String name)
{
	return null;
}
`````````
