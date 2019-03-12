package com.example.immutable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * To create an immutable object the following needs to occur:
 * <pre>
 * 1. Use a constructor to set all the properties. You can also use the Builder pattern for
 *    complex objects that have many optional parameters.
 * 2. Mark all the instance variables private and final.
 * 3. Don't define any setter methods.
 * 4. Don't allow referenced mutable objects to be modified or accessed directly.
 * 5. Prevent methods from being overridden. Set each method to final or simply set the class to final.
 * </pre>
 * Immutable objects are thread safe.
 */
public final class Immutable {

	private final String stringValue;
	private final int intValue;
	private final List<String> listValue;
	
	public Immutable(String stringValue, int intValue, List<String> listValue) {
		this.stringValue = stringValue;
		this.intValue = intValue;
		// Create copies of mutable objects (ArrayList) so that the calling code cannot manipulate it.
		this.listValue = new ArrayList<String>(listValue);
	}
	
	public String getStringValue() { return stringValue; }
	public int getIntValue() { return intValue; }
	
	// Return unmodifiable (read only) collections so the calling code cannot manipulate it.
	public List<String> getListValue() { return Collections.unmodifiableList(listValue); }
	
	// You could also provide code to iterate over the collection. Since the collection
	// contains Strings there isn't a worry of the calling code changing them since Strings
	// are immutable. 
	public int getListValueCount() {
		return listValue.size();
	}
	public String getListValue(int index) {
		return listValue.get(index);
	}
	
	public static void main(String[] args) {
		List<String> originalList = Arrays.asList("Hey", "Everybody");
		
		Immutable immutable = new Immutable("Nick", 5, originalList);
		
		// Change the original list.
		originalList.set(0, "Bye");
		System.out.println(originalList);

		// The list on the immutable object should not have changed.
		List<String> immutableList = immutable.getListValue();
		System.out.println(immutableList);
		
		// Should throw an exception since you cannot change an immutable list.
		immutableList.set(1, "Everyone!");
	}

}
