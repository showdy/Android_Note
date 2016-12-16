## java 1.5+ 高新技术注解

###1.Annotation实例:

* Override
```java		
	@Override
	public void onCreate(Bundle savedInstanceState);
```

* Retrofit Annotation:
```java	
	@GET("/user/{username}")
	User getUser(@Path("username") String username);
```

* ButterKnife Annotation
```java		
	@InjectView(R.id.user) EidtText username;
```

* ActiveAndroid Annotation
```java			
	@Column(name="Name")public String name;
```

* Retrofit为符合RESTful规范的网络请求框架
* ButterKnife为View及事件等依赖注入框架
* Active Android为ORM框架(Object-relative-mapping)
	

### 2.Annotation概念及其作用

* 2.1 概念:
	* An annotation is a form of metadata, that can be added to Java source code. Classes, methods, variables, parameters and packages may be annotated. Annotations have no direct effect on the operation of the code they annotate.
	* 能够添加到 Java 源代码的语法元数据。类、方法、变量、参数、包都可以被注解，可用来将信息元数据与程序元素进行关联。Annotation 中文常译为“注解”。
	
* 2.2作用
	* a.标记,用于告诉编译器一些信息
	* b.编译时动态处理,如动态生成代码
	* c.运行时动态处理,如得到注解信息
-这里说的三个作用事件对应后面自定义Annotation时说的@Rentention三种值分别表示的Annotation


### 3.Annotation分类
* 3.1标准注解:
	*  Override, Deprecated,SuppressWarnnings;
	> 标注Annotation是指java自带的几个Annotation,上面三个分别表示重写函数,不鼓励使用(有更好方式,使用有风险或者已不再维护),忽略某项warnning
	
* 3.2元Annotation:
	* @Retention @Target @Inherited @Documented
	> 元注解是指用来定义Annotation的Annotation,在后面Annotation自定义部分会详细介绍含义

* 3.3自定义Annotation
	>自定义 Annotation 表示自己根据需要定义的 Annotation，定义时需要用到上面的元 Annotation这里是一种分类而已，也可以根据作用域分为源码时、编译时、运行时 Annotation，后面在自定义 Annotation 时会具体介绍


### 4.自定义注解
* 4.1 注解语法:
	* 使用关键字@Interface表示注解类
	* 注解类属性书写格式: 数据类型+属性名+(), 如String name();
	* 注解类属性数据类型: 基本数据类型,String, class, Annotation, enmu,及上数据类型的一维数组.
	* 注解类属性只允许 public & abstract 修饰符，默认为 public，不允许抛异常
	* 注解类属性值可以使用default关键字定义默认值,如 String name() default "showdy";
	* 当注解类只有一个属性value时,使用该注解类时,可以不用书写属性名,如Retrofit中注解的使用.
```java		
			public @Interface MyAnnotation{
				String name() default "showdy";
				String[] images();
				Retention ret(); //注解类型
				RententionPolicy rp(); //枚举+注解类型
				Class clazz(); //class 类型
			}
```

* 4.2调用
```java	
		public class App {
		    @MethodInfo(author = “trinea.cn+android@gmail.com”,date = "2014/02/14",version = 2)
		    public String getAppName() {
		        return "trinea";
		    }
		}
```
	> 这里调用自定义Annotation--Mehtod的示例,MethoidInfo Annotation作用为给方法添加相关信息,包括author,date,verion.
	

* 4.3定义
```java				
		@Documented
		@Retention(RetentionPolicy.RUNTIME)
		@Target(ElementType.METHOD)
		@Inherited
		public @interface MethodInfo {
		
		    String author() default "trinea@gmail.com";
		
		    String date();
		
		    int version() default 1;
		}
```
* 4.4元Annotation

	* @Documented 是否会保存到 Javadoc 文档中
	* @Retention 保留时间，可选值 SOURCE（源码时），CLASS（编译时），RUNTIME（运行时），默认为 CLASS，SOURCE 大都为 Mark Annotation，这类 Annotation 大都用来校验，比如 Override, SuppressWarnings
	* @Target 可以用来修饰哪些程序元素，如 TYPE, METHOD, CONSTRUCTOR, FIELD, PARAMETER 等，未标注则表示可修饰所有
	* @Inherited 是否可以被继承，默认为 false


### 5 Annotation解析

* 5.1 运行时 Annotation 解析

	* (1) 运行时 Annotation 指 @Retention 为 RUNTIME 的 Annotation，可手动调用下面常用 API 解析
	```java	
			method.getAnnotation(AnnotationName.class);
			method.getAnnotations();
			method.isAnnotationPresent(AnnotationName.class);
	```
	
	> 其他 @Target 如 Field，Class 方法类似getAnnotation(AnnotationName.class) 表示得到该 Target 某个 Annotation 的信息，因为一个 Target 可以被多个 Annotation 修饰getAnnotations() 则表示得到该 Target 所有 AnnotationisAnnotationPresent(AnnotationName.class) 表示该 Target 是否被某个 Annotation 修饰
	
	* (2) 解析示例如下：
		```java	
			public static void main(String[] args) {
			    try {
			        Class cls = Class.forName("cn.trinea.java.test.annotation.App");
			        for (Method method : cls.getMethods()) {
			            MethodInfo methodInfo = method.getAnnotation(MethodInfo.class);
			            if (methodInfo != null) {
			                System.out.println("method name:" + method.getName());
			                System.out.println("method author:" + methodInfo.author());
			                System.out.println("method version:" + methodInfo.version());
			                System.out.println("method date:" + methodInfo.date());
			            }
			        }
			    } catch (ClassNotFoundException e) {
			        e.printStackTrace();
			    }
			}
		```
		
	>以之前自定义的 MethodInfo 为例，利用 Target（这里是 Method）getAnnotation 函数得到 Annotation 信息，然后就可以调用 Annotation 的方法得到响应属性值


* 5.2 编译时Annotation解析
	* (1) 编译时 Annotation 指 @Retention 为 CLASS 的 Annotation，甴编译器自动解析。需要做的
		* a. 自定义类继承自 AbstractProcessor
		* b. 重写其中的 process 函数
		> 实际是编译器在编译时自动查找所有继承自 AbstractProcessor 的类，然后调用他们的 process 方法去处理
	* (2) 假设 MethodInfo 的 @Retention 为 CLASS，解析示例如下：
	```java			
			@SupportedAnnotationTypes({ "cn.trinea.java.test.annotation.MethodInfo" })
			public class MethodInfoProcessor extends AbstractProcessor {
			
			    @Override
			    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
			        HashMap<String, String> map = new HashMap<String, String>();
			        for (TypeElement te : annotations) {
			            for (Element element : env.getElementsAnnotatedWith(te)) {
			                MethodInfo methodInfo = element.getAnnotation(MethodInfo.class);
			                map.put(element.getEnclosingElement().toString(), methodInfo.author());
			            }
			        }
			        return false;
			    }
			}
	```
		* SupportedAnnotationTypes 表示这个 Processor 要处理的 Annotation 名字。
		* process 函数中参数 annotations 表示待处理的 Annotations，参数 env 表示当前或是之前的运行环境
		* process 函数返回值表示这组 annotations 是否被这个 Processor 接受，如果接受后续子的 rocessor 不会再对这个 Annotations 进行处理

###6. 几个 Android 开源库 Annotation 原理简析
* 6.1 Annotation — Retrofit

	* (1) 调用
	```java	
			@GET("/users/{username}")
			User getUser(@Path("username") String username);
	```
	* (2) 定义
	```java	
			@Documented
			@Target(METHOD)
			@Retention(RUNTIME)
			@RestMethod("GET")
			public @interface GET {
			  String value();
			}
	```
	
		> 从定义可看出 Retrofit 的 Get Annotation 是运行时 Annotation，并且只能用于修饰 Method
	* (3) 原理
	```java	
			private void parseMethodAnnotations() {
			    for (Annotation methodAnnotation : method.getAnnotations()) {
			    Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
			    RestMethod methodInfo = null;
			
			    for (Annotation innerAnnotation : annotationType.getAnnotations()) {
			        if (RestMethod.class == innerAnnotation.annotationType()) {
			            methodInfo = (RestMethod) innerAnnotation;
			            break;
			        }
			    }
			    ……
			    }
			} 
	```
			
	> RestMethodInfo.java 的 parseMethodAnnotations 方法如上，会检查每个方法的每个 Annotation， 看是否被 RestMethod 这个 Annotation 修饰的 Annotation 修饰，这个有点绕，就是是否被 GET、DELETE、POST、PUT、HEAD、PATCH 这些 Annotation 修饰，然后得到 Annotation 信息，在对接口进行动态代理时会掉用到这些 Annotation 信息从而完成调用。
	
* 6.2 Annotation — Butter Knife

	* (1) 调用
	```java			
			@InjectView(R.id.user) 
			EditText username;
	```
	* (2) 定义
	```java	
			@Retention(CLASS) 
			@Target(FIELD)
			public @interface InjectView {
			  int value();
			}
	```
		> 可看出 Butter Knife 的 InjectView Annotation 是编译时 Annotation，并且只能用于修饰属性
	* (3) 原理
	```java	
			@Override 
			public boolean process(Set<? extends TypeElement> elements, RoundEnvironment env) {
			    Map<TypeElement, ViewInjector> targetClassMap = findAndParseTargets(env);
			
			    for (Map.Entry<TypeElement, ViewInjector> entry : targetClassMap.entrySet()) {
			        TypeElement typeElement = entry.getKey();
			        ViewInjector viewInjector = entry.getValue();
			
			        try {
			            JavaFileObject jfo = filer.createSourceFile(viewInjector.getFqcn(), typeElement);
			            Writer writer = jfo.openWriter();
			            writer.write(viewInjector.brewJava());
			            writer.flush();
			            writer.close();
			        } catch (IOException e) {
			            error(typeElement, "Unable to write injector for type %s: %s", typeElement, e.getMessage());
			        }
			    }
			
			    return true;
			}
	```
			
		> ButterKnifeProcessor.java 的 process 方法如上，编译时，在此方法中过滤 InjectView 这个 Annotation 到 targetClassMap 后，会根据 targetClassMap 中元素生成不同的 class 文件到最终的 APK 中，然后在运行时调用 ButterKnife.inject(x) 函数时会到之前编译时生成的类中去找。 
* 6.3 Annotation — ActiveAndroid

	* (1) 调用
		```java			
			@Column(name = “Name") 
			public String name;
		```
	* (2) 定义
		```java
			@Target(ElementType.FIELD)
			@Retention(RetentionPolicy.RUNTIME)
			public @interface Column {
			  ……
			}
		```
		> 可看出 ActiveAndroid 的 Column Annotation 是运行时 Annotation，并且只能用于修饰属性。
	* (3) 原java理
		```java			
			Field idField = getIdField(type);
			mColumnNames.put(idField, mIdName);
			
			List<Field> fields = new LinkedList<Field>(ReflectionUtils.getDeclaredColumnFields(type));
			Collections.reverse(fields);
			
			for (Field field : fields) {
			    if (field.isAnnotationPresent(Column.class)) {
			        final Column columnAnnotation = field.getAnnotation(Column.class);
			        String columnName = columnAnnotation.name();
			        if (TextUtils.isEmpty(columnName)) {
			            columnName = field.getName();
			        }
			
			        mColumnNames.put(field, columnName);
			    }
			}
		```
		> TableInfo.java 的构造函数如上，运行时，得到所有行信息并存储起来用来构件表信息。
