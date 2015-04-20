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
		A a = (A)AroundProxyFactory.factory(new B()); // ���ɴ������
		a.test0();
		System.out.println("---------------");
		a.test1();
		System.out.println("---------------");
		a.hello("gdw","llr");
	}
	// �ӿ�
	// ���ɴ����������ַ�ʽ��JDK(���ڽӿ�)��CGLIB(������)
	static interface A {
		void test0();
		String test1();
		String hello(String a, String b);
	}
	// ʵ����
	static class B implements A {
		public void test0(){
			System.out.println("�����void����");
		}
		
		public String test1(){
			return "> hello,test1";
		}
		
		public String hello(String a, String b){
			System.out.println("a=" + a + ",b=" + b);
			return "> hello";
		}
		
		@Around("test.*") // ���������ʽ
		public void testAround(AroundPoint point) {
			System.out.println("����ǰ:" + point.getMethod().getName() + "��������");
			try {
				Object returnValue = point.processed();
				// ����ֵ
				System.out.println("����ֵ��" + returnValue);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			System.out.println("���ú�:" + point.getMethod().getName() + "��������");
		}
		
		@Around("hello") // ���������ʽ
		public void testAround2(AroundPoint point) {
			System.out.println("����helloǰ��������");
			try {
				Object returnValue = point.processed();
				// ����ֵ
				System.out.println("����ֵ��" + returnValue);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			System.out.println("����hello�󡣡�����");
		}
	}

	/**
	 * ע�� 
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@Inherited
	static @interface Around {
		String value(); // �����������ʽ
	}

	/**
	 * �е��ࣨ�������������ĵ��ã�
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
		// ���÷���
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
	 * ��������
	 */
	static class AroundProxyFactory {
		public static Object factory(final Object obj) {
			Class<?> clz = obj.getClass();
			// ��ȡ�ӿ��е����з���
			List<Method> methods = new ArrayList<Method>();
			Class<?>[] interfaces = clz.getInterfaces();
			for(Class<?> _clz : interfaces){
				Method[] ms = _clz.getDeclaredMethods();
				for(Method m : ms){
					methods.add(m);
				}
			}
			
			final Map<Around, Method> _aroundMethodMap = new HashMap<Around, Method>(); // �����Ͷ�Ӧ��@Around����
			// ��ȡ���е�@Around����
			for(Method method : clz.getDeclaredMethods()){
				Around around = method.getAnnotation(Around.class);
				if(around != null){
					_aroundMethodMap.put(around, method);
				}
			}
			final Map<Method, Method> _aroundMethods = new HashMap<Method, Method>(); // �����Ͷ�Ӧ��@Around����
			// ��ȡ���е���Ҫ@Around�ķ���
			for(Method method : methods){
				if(method.getAnnotation(Around.class) == null){
					for(Around around : _aroundMethodMap.keySet()){
						if(method.getName().matches(around.value())){
							_aroundMethods.put(method, _aroundMethodMap.get(around));
						}
					}
				}
			}
			// ��������
			Object _obj = Proxy.newProxyInstance(clz.getClassLoader(), clz.getInterfaces(), new InvocationHandler() {
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					if(_aroundMethods.containsKey(method)){ // true��������Ҫ@Around�ķ���
						return _aroundMethods.get(method).invoke(obj, new AroundPoint(obj, method, args));
					}
					return method.invoke(obj, args);
				}
			});
			return _obj;
		}
	}
}
