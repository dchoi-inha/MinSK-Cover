package minsk.brtree;

import java.util.ArrayList;
import java.util.PriorityQueue;

import minsk.rtree.Entry;
import minsk.rtree.RTree;
import minsk.structure.Point;

/**
 * @author Dong-Wan Choi at SFU, CA
 * @class BRTREE
 * @date 2015-08-26
 *
 */
public class BRTree extends RTree {

	@Override
	public Entry nextNN(Point q, PriorityQueue<Entry> pq) {
		// TODO Auto-generated method stub
		return super.nextNN(q, pq);
	}

	@Override
	public ArrayList<Entry> kNNSearch(Point q, int k) {
		// TODO Auto-generated method stub
		return super.kNNSearch(q, k);
	}

	@Override
	public void insert(Entry e) {
		// TODO Auto-generated method stub
		super.insert(e);
	}

}
