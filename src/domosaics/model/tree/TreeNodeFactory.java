package domosaics.model.tree;

public class TreeNodeFactory {

	private int id = -1;
	
	public TreeNodeI createNode() {
		return new TreeNode(++id);
	}
}
