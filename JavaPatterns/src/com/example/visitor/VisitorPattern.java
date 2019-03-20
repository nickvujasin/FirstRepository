package com.example.visitor;

import java.util.Arrays;
import java.util.List;

/**
 * The pattern should be used when you have distinct and unrelated operations to perform across a structure of objects. 
 * This avoids adding in code throughout your object structure that is better kept seperate, so it encourages cleaner code. 
 * You may want to run operations against a set of objects with different interfaces.  Visitors are also valuable if you 
 * have to perform a number of unrelated operations across the classes.
 *
 * In summary, if you want to decouple some logical code from the elements that you're using as input, visitor is 
 * probably the best pattern for the job. 
 * 
 */
public class VisitorPattern {
	
	// The elements we want to visit implement this method.
	private abstract interface Visitable {
		public abstract void accept(Visitor v);
	}
	
	// Here we want to visit the node element.
	private class Node implements Visitable {
		private int value;
		private Node leftNode;
		private Node rightNode;
		
		private Node(int value) {
			this.value = value;
		}
		private int getValue() {
			return value;
		}
		private Node getLeftNode() {
			return leftNode;
		}
		private void setLeftNode(Node leftNode) {
			this.leftNode = leftNode;
		}
		private Node getRightNode() {
			return rightNode;
		}
		private void setRightNode(Node rightNode) {
			this.rightNode = rightNode;
		}
		
		@Override
		public void accept(Visitor v) {
			v.visit(this);
		}
	}
	
	// This interface defines a visit operation for each type of concrete element in the object structure.
	// The concrete visitor will store local state, typically as it traverses the set of elements. 
	private abstract interface Visitor {
		public abstract void visit(Visitable v); 
	}

	// Here is the node visitor which will visit the node and retrieve its value.
	private class NodeVisitor implements Visitor {

		private int total = 0;
		
		private int getTotal() {
			return total;
		}
		
		@Override
		public void visit(Visitable v) {
			if (v instanceof Node) {
				total += ((Node) v).getValue();
			}
		}
	}
	
	// Depth first traversal.
	private static void traverseTree(Node node, Visitor visitor) {
		node.accept(visitor);
		if (node.getLeftNode() != null)
			traverseTree(node.getLeftNode(), visitor);
		if (node.getRightNode() != null)
			traverseTree(node.getRightNode(), visitor);
	}
	
	public static void main(String[] args) {
		VisitorPattern v = new VisitorPattern();
		
		NodeVisitor visitor = v.new NodeVisitor();
		
		// Here we are going to visit a list of list of nodes.
		List<Visitable> nodes = Arrays.asList(v.new Node(5), v.new Node(10), v.new Node(15));
		
		for (Visitable visitable : nodes) {
			visitable.accept(visitor);
		}
		
		System.out.println("List traversal: " + visitor.getTotal());
		
		visitor = v.new NodeVisitor();
		
		// Here we are going to build a tree of nodes and traverse them.
		Node rootNode = v.new Node(1);
		rootNode.setLeftNode(v.new Node(2));
		rootNode.setLeftNode(v.new Node(3));
		rootNode.getLeftNode().setLeftNode(v.new Node(4));
		rootNode.getLeftNode().setRightNode(v.new Node(5));
		
		traverseTree(rootNode, visitor);
		
		System.out.println("Tree traversal: " + visitor.getTotal());
	}
}
