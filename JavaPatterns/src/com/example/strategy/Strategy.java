package com.example.strategy;

/**
 * The strategy pattern encapsulates interchangeable behaviors and uses delegation
 * to decide which one to use. The strategy pattern is a good example of 3
 * design principals. 
 * <pre>
 * 1. Identify the aspects of your application that vary and separate them from what stays the same.
 * 2. Program to an interface (abstract class or interface), not an implementation.
 * 3. Favor composition over inheritance.
 * </pre>
 * The strategy pattern is implemented using functional interfaces.
 */
class Strategy {

	// Favor composition over inheritance.
	private Function function;
	
	private void setFunction(Function function) {
		this.function = function;
	}
	
	private void process() {
		System.out.println("Start Processing...");
		function.doWork();
		System.out.println("Ending Processing...");
	}
	
	// The Function and its implementations is what varies. Here we are programming to an interface.
	private abstract interface Function {
		public abstract void doWork();
	}
	
	private class FunctionA implements Function {
		@Override
		public void doWork() {
			System.out.println("Doing work in Function A");
		}
	}
	
	private class FunctionB implements Function {
		@Override
		public void doWork() {
			System.out.println("Doing work in Function B");
		}
	}
	
	public static void main(String[] args) {
		Strategy strategy = new Strategy();
		
		strategy.setFunction(strategy.new FunctionA());
		strategy.process();
		
		strategy.setFunction(strategy.new FunctionB());
		strategy.process();
	}
}
