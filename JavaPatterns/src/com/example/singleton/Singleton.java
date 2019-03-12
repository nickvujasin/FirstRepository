package com.example.singleton;

/**
 * Ensures one and only one object is created.
 * 
 * Singleton "double-check locking" implementation.
 */
class Singleton {

	private static Singleton singleton;
	
	public static Singleton getInstance() {
		if (singleton == null) {
			synchronized(Singleton.class) {	
				if (singleton == null) {
					singleton = new Singleton();
				}
			}
		}
		
		return singleton;
	}
	
	private Singleton() {}
	
	public void doWork() {
		System.out.println("Doing some work.");
	}
	
	public static void main(String[] args) {
		Singleton.getInstance().doWork();
	}
}
