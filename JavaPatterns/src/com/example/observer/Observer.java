package com.example.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows objects to be notified when state changes. The observer pattern is good example
 * of another design principle.
 * 
 * Strive for loosely coupled designs between objects that interact.
 */
class Observer {

	// Here we are passing the Observable object to the observers so the observers can pull the data
	// instead of the observable pushing the data through the update method. This makes modifications
	// easier in the future. If there are more values we add more methods on the observable. The 
	// observers that are interested in the new values can change to retrieve the data, the rest 
	// stay the same.
	private abstract interface TheObserver {
		public abstract void update(TheObservable theObservable);
	}
	
	private class TheObserverA implements TheObserver {
		@Override
		public void update(TheObservable theObservable) {
			if (theObservable instanceof TheObservableObject) {
				System.out.println("Observer A has been notified with value: " + ((TheObservableObject) theObservable).getValue());
			}
		}		
	}
	
	private class TheObserverB implements TheObserver {
		@Override
		public void update(TheObservable theObservable) {
			if (theObservable instanceof TheObservableObject) {
				System.out.println("Observer B has been notified with value: " + ((TheObservableObject) theObservable).getValue());
			}
		}
	}
	
	private abstract interface TheObservable {
		public abstract void addObserver(TheObserver theObserver);
		public abstract void removeObserver(TheObserver theObserver);
		public abstract void notifyObservers();
	}
	
	// The observable implementation doesn't know anything about which observers
	// are listening. This is loose coupling.
	private class TheObservableObject implements TheObservable {

		private List<TheObserver> theObservers = new ArrayList<>();
		private String value; 
		
		@Override
		public void addObserver(TheObserver theObserver) {
			theObservers.add(theObserver);
		}

		@Override
		public void removeObserver(TheObserver theObserver) {
			theObservers.remove(theObserver);
		}

		@Override
		public void notifyObservers() {
			theObservers.forEach(observer -> observer.update(this));
		}
		
		public void setValue(String value) {
			this.value = value;
			notifyObservers();
		}
		
		public String getValue() {
			return value;
		}
	}
	
	public static void main(String[] args) {
		Observer observer = new Observer();
		
		// Some part of the application wiring the observers and observables together.
		TheObserver theObserverA = observer.new TheObserverA();
		TheObserver theObserverB = observer.new TheObserverB();
		
		TheObservableObject theObservedA = observer.new TheObservableObject();
		theObservedA.addObserver(theObserverA);
		theObservedA.addObserver(theObserverB);
		
		// Some part of the application changing the value.
		theObservedA.setValue("IT CHANGED");
		
		// Some part of the application wiring the observers and observables together.
		theObservedA.removeObserver(theObserverB);
		
		// Some part of the application changing the value.
		theObservedA.setValue("IT CHANGED AGAIN");
	}
}
