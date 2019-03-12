package com.example.decorator;

/**
 * Wraps an object to provide new behavior. Decorators provide a flexible alternative
 * to subclassing for extending functionality.  We are acquiring new behavior not by 
 * inheriting it from a super class, but by composing objects together. Relying on inheritance
 * determines our behavior statically at compile time either by the superclass or by what is
 * overridden. With composition we can mix and match decorators anyway we like at runtime.
 * 
 * Decorators add their own behavior either before and/or after delegating to the object it decorates
 * to do the rest of the job.
 */
class BeverageCondimentDecorator {
	
	private abstract class Beverage {
		String description = "Unknown Beverage";
		
		public String getDescription() {
			return this.description;
		}
		
		public abstract double cost();
	}
	
	// We need the decorator to be interchangeable with a beverage. Decorators
	// have the same super type as the object they decorate, we can then 
	// pass around the decorated object in place of the original wrapped
	// object.
	private abstract class CondimentDecorator extends Beverage { 
		public abstract String getDescription();
	}
	
	// Base Class that we'll be decorating to provided new behavior.
	private class Espresso extends Beverage {
		private Espresso() {
			this.description = "Espresso";
		}

		@Override
		public double cost() {
			return 1.99;
		}
	}
	// Base Class that we'll be decorating to provide new behavior.
	private class HouseBlend extends Beverage {
		private HouseBlend() {
			this.description = "House Blend Coffee";
		}

		@Override
		public double cost() {
			return .89;
		}
	}
	// Base Class that we'll be decorating to provide new behavior.
	private class Tea extends Beverage {
		private Tea() {
			this.description = "Tea";
		}

		@Override
		public double cost() {
			return 1.19;
		}
	}
	// Decorators
	private class Milk extends CondimentDecorator {

		private Beverage beverage;
		
		private Milk(Beverage beverage) {
			this.beverage = beverage;
		}
		@Override
		public String getDescription() {
			return beverage.getDescription() + ", Milk";
		}

		@Override
		public double cost() {
			return beverage.cost() + .10;
		}
	}
	private class Mocha extends CondimentDecorator {

		private Beverage beverage;
		
		private Mocha(Beverage beverage) {
			this.beverage = beverage;
		}
		@Override
		public String getDescription() {
			return beverage.getDescription() + ", Mocha";
		}

		@Override
		public double cost() {
			return beverage.cost() + .20;
		}
	}
	private class Soy extends CondimentDecorator {

		private Beverage beverage;
		
		private Soy(Beverage beverage) {
			this.beverage = beverage;
		}
		@Override
		public String getDescription() {
			return beverage.getDescription() + ", Soy";
		}

		@Override
		public double cost() {
			return beverage.cost() + .15;
		}
	}
	private class Whip extends CondimentDecorator {

		private Beverage beverage;
		
		private Whip(Beverage beverage) {
			this.beverage = beverage;
		}
		@Override
		public String getDescription() {
			return beverage.getDescription() + ", Whip";
		}

		@Override
		public double cost() {
			return beverage.cost() + .10;
		}
	}
	
	public static void main(String[] args) {
		BeverageCondimentDecorator dec = new BeverageCondimentDecorator();
		
		// Base class.
		Beverage beverage = dec.new Espresso();
		// Now decorate it.
		beverage = dec.new Milk(beverage);
		beverage = dec.new Whip(beverage);
		
		System.out.println("Order: " + beverage.getDescription() + " is: " + beverage.cost());
		
		// Base class.
		beverage = dec.new HouseBlend();
		// Now decorate it.
		beverage = dec.new Mocha(beverage);
		beverage = dec.new Mocha(beverage);
		beverage = dec.new Soy(beverage);
		
		System.out.println("Order: " + beverage.getDescription() + " is: " + beverage.cost());
		
		// Base class.
		beverage = dec.new Tea();
		// Now decorate it.
		beverage = dec.new Milk(beverage);
		
		System.out.println("Order: " + beverage.getDescription() + " is: " + beverage.cost());
		
	}
}
