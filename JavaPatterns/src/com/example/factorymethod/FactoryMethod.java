package com.example.factorymethod;

/**
 * Subclasses decide which concrete classes to create. The superclass only wants to work
 * with the object that the subclass creates. It doesn't want to hard code which product 
 * to create. It simply wants to use the product.
 */
class FactoryMethod {

	private abstract interface Product {
		public abstract void doWork();
		public abstract void doMoreWork();
	}
	
	private class ConcreteProductA implements Product {
		@Override
		public void doWork() {
			System.out.println("Product A is doing work");			
		}
		@Override
		public void doMoreWork() {
			System.out.println("Product A is doing more work");	
		}
	}
	
	private class ConcreteProductB implements Product {
		@Override
		public void doWork() {
			System.out.println("Product B is doing work");			
		}
		@Override
		public void doMoreWork() {
			System.out.println("Product B is doing more work");	
		}
	}
	
	// The superclass simply wants to work with a product and calls its methods.
	private abstract class Creator {
		public void workWithProduct() {
			// If you use new, you'll be holding a reference to a concrete class. Use
			// a factory to get around that. Product is an abstraction, not a concrete
			// class.
			Product product = createProduct();
			product.doWork();
			product.doMoreWork();
		}
		
		// The factory method.
		public abstract Product createProduct();
	}
	
	// The concrete creators are responsible for creating the concrete product to be used by the superclass.
	// It is the only class that has the knowledge of how to create the product.
	private class ConcreteCreatorA extends Creator {
		@Override
		public Product createProduct() {
			return new ConcreteProductA();
		}
	}
	private class ConcreteCreatorB extends Creator {
		@Override
		public Product createProduct() {
			return new ConcreteProductB();
		}
	}
	
	public static void main(String[] args) {
		FactoryMethod fm = new FactoryMethod();
		
		Creator creator = fm.new ConcreteCreatorA();
		creator.workWithProduct();
		
		creator = fm.new ConcreteCreatorB();
		creator.workWithProduct();
	}
}
