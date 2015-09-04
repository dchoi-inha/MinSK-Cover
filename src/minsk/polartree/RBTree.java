package minsk.polartree;

import java.util.HashSet;

/**
 * @author Dong-Wan Choi at SFU, CA
 * @class RBTree
 * @date 2015-09-03
 *
 */
public class RBTree {
	protected RBNode root;
	
	public RBTree() {
		root = new RBNode(null, RBNode.BLACK);
	}

	public void insert(RBItem item, HashSet<String> T) {
		root = root.insert(item, T);
	}
	
	public RBItem get(int i) {
		RBNode node = root.getNode(i);
		if (node != null) return node.getItem();
		else return null;		
	}
}
