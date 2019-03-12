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
class RuleConditionDecorator {

	private abstract class Rule {
		public abstract boolean evaluate();
	}
	
	private abstract class ConditionDecorator extends Rule {}
	
	// Base class that is being decorated always returns true.
	private class RuleA extends Rule {
		@Override
		public boolean evaluate() {
			return true;
		}
	}
	
	// Decorators
	private class SuccessfulConditionA extends ConditionDecorator {

		private Rule rule;
		
		private SuccessfulConditionA(Rule rule) {
			this.rule = rule;
		}
		
		@Override
		public boolean evaluate() {
			if (rule.evaluate()) {
				return true; // If the rule we are decorating returns true, perform our fake condition evaluation and return true.
			} else {
				return false;
			}
		}
	}
	private class SuccessfulConditionB extends ConditionDecorator {

		private Rule rule;
		
		private SuccessfulConditionB(Rule rule) {
			this.rule = rule;
		}
		
		@Override
		public boolean evaluate() {
			if (rule.evaluate()) {
				return true; // If the rule we are decorating returns true, perform our fake condition evaluation and return true.
			} else {
				return false;
			}
		}
	}
	private class FailedConditionA extends ConditionDecorator {

		private Rule rule;
		
		private FailedConditionA(Rule rule) {
			this.rule = rule;
		}
		
		@Override
		public boolean evaluate() {
			if (rule.evaluate()) {
				return false; // If the rule we are decorating returns true, perform our fake condition evaluation and return false.
			} else {
				return false;
			}
		}
	}
	
	public static void main(String[] args) {
		RuleConditionDecorator rcd = new RuleConditionDecorator();
		
		// Create the base rule.
		Rule rule = rcd.new RuleA();
		
		// Decorate it with conditions.
		rule = rcd.new SuccessfulConditionA(rule);
		rule = rcd.new SuccessfulConditionB(rule);
		
		System.out.println("Evaluating rule and its conditions to: " + rule.evaluate());
		
		// Create the base rule.
		rule = rcd.new RuleA();
		
		// Decorate it with conditions.
		rule = rcd.new SuccessfulConditionA(rule);
		rule = rcd.new SuccessfulConditionB(rule);
		rule = rcd.new FailedConditionA(rule);
		
		System.out.println("Evaluating rule and its conditions to: " + rule.evaluate());
		
	}
}
