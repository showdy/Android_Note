# Lambda表达式

### 学习lambda表达式前先看个例子

* 输入1000到10000之间水仙花数?
	```java		
      IntStream.range(1000,10000).filter(v->
			v / 1000 * v / 1000 * v / 1000 + v / 100 * v / 100 * v / 100 + v / 10 * v / 10 * v / 10 == v 
    		).forEach(System.out::println);
   	```
	>上面的表达式中多处用到lamnda表达式以及Stream API,那么什么是lambda表达式?

### 什么是lambda表达式?

* 1. Lambda(其实就是希腊字母λ大写字符为Λ)就是没有名称的代码块,有形式参数列表和实现体
* 2. 可以作为方法的参数或者赋值给变量
* 3. 自身没有类型, 编译器会根据环境推断出其类型

	```java	
		(int x,int y)-> {return x+y;};
		(int x,int y)->{int max= x>y ? x: y; return max;};
```
### lambda表达式的语法:

* (<参数列表>)->(<实现体>)
* 与方法有所不同:
	* 不能有名称;
	* 不能有返回值类型,其类型由编译器根据环境推断出其类型;
	* 没有throws,由编译器根据环境推断
	* 不能声明参数类型,即参数不能为泛型
	* 可以将其赋值给一个合适的函数式接口变量.
* 省略参数类型:编译器会根据使用的环境推断出其参数类型.
	* 如 (int x,int y)-> x+y;可以省略为(x,y)->x+y;但是不能写成(int x,y)-> x+y;
* 当只有一个参数时,不仅能省略参数类型,连小括号也能省略
	* 如 (int x)->  x; 可以省略为x->x;但是不允许写为 int x-> x;
* 没有参数时,小括号不能省略
	* 如 ()->System.out.println("hello");
* 带修饰符的参数类型不能省略
	* 如( final int x,int y)-> x+y; 不能写 (x,y)->x+y;或者(final x,y)-> x+y;
* lamda表达式的实现体:
	* 可以是代码块或者表达式
  ```java	
	public class Lambda2 {
	    public static void main(String[] args) {
	        // lambda表达式可以复制给一个函数式接口
	
	        Addable sum = (int a, int b) -> {
	            return a + b;
	        };
	        Addable q = (a, b) -> a + b;
	
	        Outputable out = () -> {
	            System.out.println();
	        };
	
	        Outputable out3 = () -> 
	            System.out.println();
	        ;
	        
	        //方法引用
	        Outputable out2 = System.out::println;
	        
	        //有修饰符修饰时,参数类型不能省略
	        Finable fin = (final int x, int y) -> x / y;
	        
	    }
	
	    private static interface Addable {
	        int add(int a, int b);
	    }
	
	    private static interface Outputable {
	        void output();
	    }
	
	    private static interface Finable {
	        int divide(final int x, int y);
	    }
	}
```

### Lambda表达式的目标类型
* Lambda表达式实际是一个函数式接口类型,但是并不知道到底是哪个函数式接口类型,只有在使用时,只有在使用时,编译器会根据环境推断出被期望的类型,即为目标类型.意味着同样的lambda表达式在不同的环境中可以拥有不同的类型.
```java	
		 public static void main(String[] args) {

	        Addable a = (x, y) -> System.out.println();
	
	        Outputable o = (x, y) -> System.out.println();
	    }

	    private static interface Addable {
	        void add(int a, int b);
	    }
	
	    private static interface Outputable {
	        void output(String x, String y);
	    }

* 赋值表达式中目标类型的推断:
	* T t = <Lambda表达式>;
	* 规则:
		* T 必须是函数式接口
		* lambda表达式的参数数量和类型与T中的抽象方法声明一直
		* lambda表达式的实现体返回值类型与T中方法返回值类型一致
		* lambda表达式的实现体中抛出的任何受检异常都要与T中的抽象方法声明的异常一致,若抽象方法中没有异常声明会出现编译错误
		
```java
	public static void main(String[] args) {    
        //lambda表达式中不能抛出受检异常,但是对应的函数式接口中可以抛出
        //lambda表达式中参数类型,以及实现体的返回值类型与声明的函数式接口中抽象方法保持一致.
        Addable x = (a, b) -> a / b;
    }
    private static interface Addable {
        int add(int a, int b) throws IOException;
    }
```

* 方法重载: 当方法重载造成问题时:
	
	* 明确lambda表达式的参数类型,
	* 使用类型的强制转换
	* 不要直接传lambda表达式,而是先将其赋值给接口类型的变量,再将该变量传入方法.
	
```
```java	
	interface Outputable {
	
	    static void output(Lambda4.Addable add) {
	    }
	    
	
	    static void output(Lambda4.Addable2 add) {
	    }
	    
	}
	
	
	public class Lambda4 {
	    public static void main(String[] args) {
	        Addable add = (x, y) -> x + y;

        	Outputable.output(add);
        	Outputable.output((Addable2)(x, y) -> x + y);
	}	
        
```

* lambda表达式使用场景:
	* 赋值
	* 方法调用
	* 返回值
	* 转换
	
### 函数式接口

* 什么是函数式接口
	* 只有一个抽象方法的接口
	* 重新定义Object类中的方法不影响
	* 使用可选的注解@FunctionalInterface可标识
	* 可将lambda表达式赋值给适合的函数式接口类型变量

```java
	 @FunctionalInterface
    interface Addable2 {
        int add(int a, int b) throws IOException;

        String toString();

        static void randomAdd() {

        }

        default int divide() {
            return 0;
        }

    }
```

* 泛型接口
	* lambda表达式可以赋值给泛型函数式接口类型的变量
	* 但是不能赋值给带有泛型方法的非泛型函数式接口类型的变量,需要使用方法引用或者匿名内部类
	
```java
 	@FunctionalInterface
    interface Dividable<T> {
        T divide(T t);

        static <V> void divde1( V v) {
        }

    }

	@FunctionalInterface
    interface Dividable2 {

        <V> void divde1(V v);

        String toString();

    }
```

* 交集类型

	* 标记接口,表示其无任何内部成员都不存在;
	* 一般使用&将多个类型连接起来就成了交集类型
	* 一般是将标记接口和函数式接口进行连接
	
```java
 	public static void main(String[] args) {
        
        Addable n = (Multable & Addable) (a, b) -> a + b;
    }

    interface Addable {
        int add(int a, int b) throws IOException;

    }
    interface Multable {

    }
```

### Java8中java.util.function包中的函数式接口
* 方法引用:方法引用是使用已经存在的方法创建Lambda表达式
* 方法引用语法:
	* <限定>::<方法名>
	* ToIntFuntion<String> function= String::length;
* 方法引用的类型:
	 * TypeName::staticMethod----引用类,接口,枚举中的静态方法
     * objectRef::instanceMethod---特定对象的实例方法
     * ClassName::instanceMethod---类的任意对象的实例方法
     * TypeName.super::instanceMthod---特定对象的超类的实例方法
     * ArrayTypeName::new---数组的构造方法
     
```java
	interface Convert<T> {
        int convert(T t);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void main(String[] args) {

        Convert<String> convert = Integer::parseInt; //不会立即执行, 类型1

        System.out.println(convert.convert("1234")); //此处字符串必须是数字字符串


        Supplier<Integer> s1 = "Java"::length; //类型2
        System.out.println(s1.get());

        BiPredicate<String, String> b = TextUtils::equals;
        boolean test = b.test("java", "JAVA");

        Consumer<String> s2 = System.out::print;

        Function<String, Integer> f1 = String::length; //类型3
        System.out.println(f1.apply("Java"));


        Supplier<ArrayList> c = ArrayList<String>::new;
        ArrayList list = c.get();

        Function<Integer, ArrayList> c1 = ArrayList<String>::new;
        ArrayList apply = c1.apply(10);

        Function<Integer, int[]> fun2 = int[]::new;
        int[] nums = fun2.apply(5);


    }
```
### Lambda表达式词法作用域及变量捕获

* 词法作用域:lambda表达式没有自己的作用域,他存在于外围作用域中,也称词法作用域.
```java
		public static void main(String[] args) {

        int num = 10;

        // Consumer<Integer> consumer = num -> System.out.print(num); 
        //此处num的作用域为词法作用域,取决于main(),而main()中已经声明num变量,此处lambda表达式中不能声明num;
        Consumer<Integer> consumer = n -> System.out.print(n + num);
        //num=5;
        //num在lambda表达式中使用,故为final类型, 不能继续给num赋值,只能初始化一次;
        consumer.accept(20);
        Consumer<int[]> c = n -> {
            int count = 0;
            for (int i : n) {
                if (i % 2 == 0) {
                    count++;
                    continue;
                }
                if (count == 3) {
                    break;
                }
            }
        };

        c.accept(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

        for (int i = 0; i < 10; i++) {
            Consumer<Integer> c2 = n -> {
                System.out.println(n);
                if (n == 2) {
                    // break; 
					// 只能在lambda表达式中循环中使用,不能在lambda表达式中控制外部循环;
                }
            };
        }
    }
```
