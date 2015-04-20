package ipayment;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

	public static void main(String[] args) {
		A a = (A)AroundProxyFactory.factory(new B()); // 生成代理对象
		a.test0();
		System.out.println("---------------");
		a.test1();
		System.out.println("---------------");
		a.hello("gdw","llr");
	}
	// 接口
	// 生成代理类有两种方式：JDK(基于接口)和CGLIB(基于类)
	static interface A {
		void test0();
		String test1();
		String hello(String a, String b);
	}
	// 实现类
	static class B implements A {
		public void test0(){
			System.out.println("输出：void方法");
		}
		
		public String test1(){
			return "> hello,test1";
		}
		
		public String hello(String a, String b){
			System.out.println("a=" + a + ",b=" + b);
			return "> hello";
		}
		
		@Around("test.*") // 方法正则格式
		public void testAround(AroundPoint point) {
			System.out.println("调用前:" + point.getMethod().getName() + "。。。。");
			try {
				Object returnValue = point.processed();
				// 返回值
				System.out.println("返回值：" + returnValue);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			System.out.println("调用后:" + point.getMethod().getName() + "。。。。");
		}
		
		@Around("hello") // 方法正则格式
		public void testAround2(AroundPoint point) {
			System.out.println("调用hello前。。。。");
			try {
				Object returnValue = point.processed();
				// 返回值
				System.out.println("返回值：" + returnValue);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			System.out.println("调用hello后。。。。");
		}
	}

	/**
	 * 注解 
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@Inherited
	static @interface Around {
		String value(); // 方法名正则格式
	}

	/**
	 * 切点类（包括整个方法的调用）
	 */
	static class AroundPoint {
		private Method method;
		private Object obj;
		private Object[] args;

		public AroundPoint(Object obj, Method method, Object[] args) {
			this.obj = obj;
			this.method = method;
			this.args = args;
		}
		// 调用方法
		public Object processed() throws Throwable {
			return method.invoke(obj, args);
		}

		public Object getObj() {
			return obj;
		}

		public void setObj(Object obj) {
			this.obj = obj;
		}

		public Method getMethod() {
			return method;
		}

		public void setMethod(Method method) {
			this.method = method;
		}

		public Object[] getArgs() {
			return args;
		}

		public void setArgs(Object[] args) {
			this.args = args;
		}
	}

	/**
	 * 代理工厂类
	 */
	static class AroundProxyFactory {
		public static Object factory(final Object obj) {
			Class<?> clz = obj.getClass();
			// 获取接口中的所有方法
			List<Method> methods = new ArrayList<Method>();
			Class<?>[] interfaces = clz.getInterfaces();
			for(Class<?> _clz : interfaces){
				Method[] ms = _clz.getDeclaredMethods();
				for(Method m : ms){
					methods.add(m);
				}
			}
			
			final Map<Around, Method> _aroundMethodMap = new HashMap<Around, Method>(); // 方法和对应的@Around方法
			// 获取所有的@Around方法
			for(Method method : clz.getDeclaredMethods()){
				Around around = method.getAnnotation(Around.class);
				if(around != null){
					_aroundMethodMap.put(around, method);
				}
			}
			final Map<Method, Method> _aroundMethods = new HashMap<Method, Method>(); // 方法和对应的@Around方法
			// 获取所有的需要@Around的方法
			for(Method method : methods){
				if(method.getAnnotation(Around.class) == null){
					for(Around around : _aroundMethodMap.keySet()){
						if(method.getName().matches(around.value())){
							_aroundMethods.put(method, _aroundMethodMap.get(around));
						}
					}
				}
			}
			// 创建代理
			Object _obj = Proxy.newProxyInstance(clz.getClassLoader(), clz.getInterfaces(), new InvocationHandler() {
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					if(_aroundMethods.containsKey(method)){ // true，则是需要@Around的方法
						return _aroundMethods.get(method).invoke(obj, new AroundPoint(obj, method, args));
					}
					return method.invoke(obj, args);
				}
			});
			return _obj;
		}
	}
}
