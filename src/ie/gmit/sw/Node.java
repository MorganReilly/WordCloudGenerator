package ie.gmit.sw;

import java.util.*;

public class Node {
	private String nodeName;
	private Node parent;
	private Map<Node, Integer> children = new HashMap<Node, Integer>();
	private boolean visited = false;
	private boolean goalNode;
	private int approximateDistanceFromGoal = 0;

	public Node(String name) {
		this.nodeName = name;
	}

	public Node(String name, int goalDistance) {
		this.nodeName = name;
		this.approximateDistanceFromGoal = goalDistance;
	}

	public Node[] children() {
		return (Node[]) children.keySet().toArray(new Node[children.size()]);
	}

	public int getDistance(Node node) {
		if (children.get(node) == null)
			System.out.println(this.nodeName + ": " + node.getNodeName());
		return children.get(node);
	}

	public boolean isLeaf() {
		if (children.size() > 0) {
			return false;
		} else {
			return true;
		}
	}

	public int getChildNodeCount() {
		return children.size();
	}

	public void addChildNode(Node child, int distance) {
		children.put(child, distance);
	}

	public void removeChild(Node child) {
		children.remove(child);
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public boolean isGoalNode() {
		return goalNode;
	}

	public void setGoalNode(boolean goalNode) {
		this.goalNode = goalNode;
	}

	public int getApproximateDistanceFromGoal() {
		return approximateDistanceFromGoal;
	}

	public void setApproximateDistanceFromGoal(int approximateDistanceFromGoal) {
		this.approximateDistanceFromGoal = approximateDistanceFromGoal;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public String toString() {
		return this.nodeName;
	}
}
