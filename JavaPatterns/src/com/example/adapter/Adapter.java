package com.example.adapter;

/**
 * Adapter wraps an object and provides a different interface to it.
 * 
 * The Client expects an interface that the new Vendor has not implemented. Both
 * Client and Vendor cannot change. The Adapter wraps the Vendor class
 * while implementing the interface the Client expects.
 */
class Adapter {

	private abstract interface Expected {
		public abstract void expectedMethodA();
		public abstract void expectedMethodB();
	}
	
	// Vendor does not implement the Expected interface and cannot change.
	private class Vendor {
		private void methodA() {System.out.println("Calling methodA on Vendor");}
		private void methodB() {System.out.println("Calling methodB on Vendor");}
		private void methodC() {System.out.println("Calling methodC on Vendor");}
	}
	
	// Client is expecting a particular interface and it cannot change to call the Vendor apis.
	private class Client {
		private Expected expected;
		private Client(Expected expected) {
			this.expected = expected;
		}
		private void doWork() {
			expected.expectedMethodA();
			expected.expectedMethodB();
		}
	}
	
	// The Adapter implement the interface the client expects.
	private class TheAdapter implements Expected {
		private Vendor vendor;
		private TheAdapter(Vendor vendor) {
			this.vendor = vendor;
		}
		@Override
		public void expectedMethodA() {
			System.out.println("Calling expectedMethodA on the Adapter");
			vendor.methodA();
			vendor.methodC();
		}
		@Override
		public void expectedMethodB() {
			System.out.println("Calling expectedMethodB on the Adapter");
			vendor.methodB();
		}
	}
	
	public static void main(String[] args) {
		Adapter a = new Adapter();
		// Create the Vendor that will be wrapped.
		Vendor vendor = a.new Vendor();
		// Create the adapter and give it the Vendor to wrap.
		Expected adapter = a.new TheAdapter(vendor);
		// Create the Client and give it the adapter.
		Client client = a.new Client(adapter);
		
		client.doWork();
	}
}
