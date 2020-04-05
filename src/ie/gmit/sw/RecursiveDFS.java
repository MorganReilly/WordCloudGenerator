package ie.gmit.sw;

public class RecursiveDFS {

	public void search(Node node){
		if (node.isGoalNode()){
			System.out.println("Reached goal node " + node.getNodeName());
			System.exit(0);
		}else{
			if (!node.isVisited()){
				node.setVisited(true);
				Node[] children = node.children();
				for (int i = 0; i < children.length; i++) {
					search(children[i]);
				}
			}
		}
	}

}
