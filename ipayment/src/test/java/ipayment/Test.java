package ipayment;

public class Test {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		A a = B.class.newInstance();
		a.a = 0;
		System.out.println(a instanceof B);
	}
	
	
	public static class A{
		public int a;
	}
	
	public static class B extends A {
		public int b;
	}
}
