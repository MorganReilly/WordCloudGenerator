package ie.gmit.sw;

import java.util.*;

public class Node {
	private String nodeName;
	private List<Node> children = new ArrayList<Node>();
	private boolean visited = false;
	private boolean goalNode;
	private Colour colour = Colour.White;

	public Node(String name) {
		this.nodeName = name;
	}

	public Node[] children() {
		return (Node[]) children.toArray(new Node[children.size()]);
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

	public void addChildNode(Node child) {
		children.add(child);
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

	public Colour getColour() {
		return colour;
	}

	public void colour(Colour colour) {
		this.colour = colour;
	}

	public boolean isGoalNode() {
		return goalNode;
	}

	public void setGoalNode(boolean goalNode) {
		this.goalNode = goalNode;
	}

	public String toString() {
		return this.nodeName;
	}
}