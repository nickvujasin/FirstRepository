package com.example.builder;

/**
 * Builders allow for complex objects to be built in steps. Complex objects
 * do not scale well when there are a large number of optional parameters. There
 * ends up being large number of different constructors or a large number of 
 * setters that causes the construction of the object to be split across multiple calls.
 * 
 * Builders are typically used with immutable objects, since immutable objects do not have
 * setter methods and must be created with all of their parameters set, although it
 * can be used with mutable objects as well. Builders are also used to build composite
 * structures.
 */
final class NutritionFacts {

	private final int servingSize;
	private final int servings;
	private final int calories;
	private final int fat;
	private final int sodium;
	private final int carbohydrate;
	
	public static class Builder {
		// Required parameters
		private final int servingSize;
		private final int servings;
		
		// Optional parameters - initialized to default values
		private int calories = 0;
		private int fat = 0;
		private int sodium = 0;
		private int carbohydrate = 0;
		
		public Builder(int servingSize, int servings) {
			this.servingSize = servingSize;
			this.servings = servings;
		}
		
		public Builder calories(int val) {
			this.calories = val;
			return this;
		}
		public Builder fat(int val) {
			this.fat = val;
			return this;
		}
		public Builder sodium(int val) {
			this.sodium = val;
			return this;
		}
		public Builder carbohydrate(int val) {
			this.carbohydrate = val;
			return this;
		}
		
		public NutritionFacts build() {
			return new NutritionFacts(this);
		}
	}
	
	private NutritionFacts(Builder builder) {
		this.servingSize = builder.servingSize;
		this.servings = builder.servings;
		this.calories = builder.calories;
		this.fat = builder.fat;
		this.sodium = builder.sodium;
		this.carbohydrate = builder.carbohydrate;
	}
	
	public int getServingSize() { return this.servingSize; }
	public int getServings() { return this.servings; }
	public int getCalories() { return this.calories; }
	public int getFat() { return this.fat; }
	public int getSodium() { return this.sodium; }
	public int getCarbohydrate() { return this.carbohydrate; }
	
	public static void main(String[] args) {
		NutritionFacts cola = new NutritionFacts.Builder(240, 8)
				.calories(100).sodium(35).carbohydrate(27).build();
	}
}
