package com.example.templatemethod;

/**
 * Template Method subclasses decide how to implement steps in an algorithm.
 */
class TemplateMethod {

	private abstract class TemplateMethodClass {
		private void doWork() {
			step1();
			step2();
			step3();
			step4();
		}	
		private void step1() {System.out.println("Step 1 in the algorithm");}
		public abstract void step2();
		private void step3() {System.out.println("Step 3 in the algorithm");}
		public abstract void step4();
	}
	
	private class TemplateMethodClassA extends TemplateMethodClass {
		@Override
		public void step2() {System.out.println("Step B in the algorithm");}
		@Override
		public void step4() {System.out.println("Step D in the algorithm");}
	}
	private class TemplateMethodClassI extends TemplateMethodClass {
		@Override
		public void step2() {System.out.println("Step II in the algorithm");}
		@Override
		public void step4() {System.out.println("Step IV in the algorithm");}
	}
	
	public static void main(String[] args) {
		TemplateMethod tm = new TemplateMethod();
		
		TemplateMethodClass tmcA = tm.new TemplateMethodClassA();
		tmcA.doWork();
		
		TemplateMethodClass tmcI = tm.new TemplateMethodClassI();
		tmcI.doWork();
	}
}
