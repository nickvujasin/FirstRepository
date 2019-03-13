package com.example.facade;

/**
 * Facade simplifies the interface of a set of classes. With the facade
 * pattern you can take a complex subsystem and make it easier to use by
 * implementing a Facade class that provides one, more reasonable interface.
 */
class Facade {

	private class SubSystemClassA {
		private void methodA() {System.out.println("Calling methodA on SubSystemClassA");}
		private void methodB() {System.out.println("Calling methodB on SubSystemClassA");}
		private void methodC() {System.out.println("Calling methodC on SubSystemClassA");}
	}
	private class SubSystemClassB {
		private void methodA() {System.out.println("Calling methodA on SubSystemClassB");}
		private void methodB() {System.out.println("Calling methodB on SubSystemClassB");}
	}
	private class SubSystemClassC {
		private void methodA() {System.out.println("Calling methodA on SubSystemClassC");}
	}
	
	// The Facade exposes a few simple methods and hides the complexity of the subsystem.
	private class TheFacade {
		private SubSystemClassA subSysClassA;
		private SubSystemClassB subSysClassB;
		private SubSystemClassC subSysClassC;
		
		private TheFacade(SubSystemClassA subSysClassA, SubSystemClassB subSysClassB, SubSystemClassC subSysClassC) {
			this.subSysClassA = subSysClassA;
			this.subSysClassB = subSysClassB;
			this.subSysClassC = subSysClassC;
		}
		
		private void doWork() {
			System.out.println("Calling doWork on the Facade");
			subSysClassA.methodA();
			subSysClassB.methodA();
			subSysClassC.methodA();
		}
		private void doWorkAgain() {
			System.out.println("Calling doWorkAgain on the Facade");
			subSysClassA.methodB();
			subSysClassA.methodC();
			subSysClassB.methodB();
		}
	}
	
	public static void main(String[] args) {
		Facade f = new Facade();
		
		TheFacade facade = f.new TheFacade(f.new SubSystemClassA(), f.new SubSystemClassB(), f.new SubSystemClassC());
		
		// Here we are playing the Client that has an instance of the Facade. Instead of 
		// the Client knowing about all the sub system classes and the methods to call 
		// and in what order, the facade hides that complexity. 
		facade.doWork();	
		facade.doWorkAgain();
	}
}
