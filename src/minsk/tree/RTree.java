package minsk.tree;

import java.util.ArrayList;
import java.util.Collections;

public class RTree {
	public static final int M = 20;
	public static final int m = 10;
	public Node R=null;
	public int nodeCount = 0;
	public int leafCount = 0;
	public int compareCount = 0;
	public int height = 0;
	public int nodes = 0;

	/* Search */
	private void _search(Node T, int xl, int xh, int yl, int yh, ArrayList<Entry> result){
		Entry e;
		if (T.isleaf) leafCount++;
		else nodeCount++;
		for (int a=0; a<T.size(); a++){
			e = T.get(a);
			if (((!(xh<e.x.l || xl>e.x.h))) && (!(yl>e.y.h || yh<e.y.l))){
				compareCount++;
				if (T.isleaf){ // leaf
					result.add(e);
				}
				else _search(e.child, xl, xh, yl, yh, result);
			}
		}
	}
	public ArrayList<Entry> search(int xl, int xh, int yl, int yh){
		ArrayList<Entry> result = new ArrayList<Entry>(); 
		nodeCount = 0;
		leafCount = 0;
		_search(R, xl, xh, yl, yh, result);

		return result;

	}

	/* Insert */
	private Node chooseLeaf(Entry e){
		Node n = R;
		while (!n.isleaf){
			long mincost = Long.MAX_VALUE;
			long s = -1, d;
			Entry se = null;
			if (n.get(0).child.isleaf) { // the childpointers in N point to leaves
				for (int i=0; i < n.size(); i++)
				{
					Entry k = n.get(i);
					long cost = k.overlap(e);
					if (mincost > cost) {
						mincost = cost;
						se = k;
					} else if (mincost == cost) {
						d = k.diffArea(e);
						if (s < 0 || s > d) {
							s = d;
							se = k;
						}
						else if (s == d) {
							if (se.area() > k.area())
								se = k;
						}
					}
				}
			} else {
				for (int i=0; i < n.size(); i++)
				{
					Entry k = n.get(i);
					d = k.diffArea(e);
					if (s < 0 || s > d) {
						s = d;
						se = k;
					}
					else if (s == d) {
						if (se.area() > k.area())
							se = k;
					}
				}
			}
			n = se.child;
		}
		return n;
	}

	private Node splitNode(Node n, Entry e){  // node n's split and inserted entry is e
		Node nn = new Node(n.isleaf);
		nodes++;
		nn.parent = n.parent;
		n.add(e);
		ArrayList<Entry> temp = n.entryList;
		n.initEntries();
		nn.initEntries();

		chooseSplitAxis(temp);
		int k = chooseSplitIndex(temp);
		
		Entry t;
		for (int i=0; i < m-1+k; i++) {
			t = temp.get(i);
			n.add(t);
			if (!n.isleaf) t.child.parent = n;
		}
		for (int i=m-1+k; i < M+1; i++) {
			t = temp.get(i);
			nn.add(temp.get(i));
			if (!n.isleaf) t.child.parent = nn;
		}
				
		return nn;
	}
	private int chooseSplitIndex(ArrayList<Entry> entries) {
		int index = -1;
		long min = Long.MAX_VALUE, cost;
		Node first = new Node(); Node second = new Node();
		for (int i = 0; i < m-1; i++) {
			first.add(entries.get(i));
		}
		for (int i = m-1; i < M+1; i++) {
			second.add(entries.get(i));
		}
		
		for (int k = 0; k < M-2*m+2; k++) {
			first.add(entries.get(m-1+k));
			second.remove(0);
			cost = overlap(first, second);
			if (min > cost) {
				index = k;
				min = cost;
			}
		}
		return index;
	}
	private long overlap(Node n1, Node n2) {
		int xl, xh, yl, yh;
		
		xl = Math.max(n1.x.l, n2.x.l);
		xh = Math.min(n1.x.h, n2.x.h);
		yl = Math.max(n1.y.l, n2.y.l);
		yh = Math.min(n1.y.h, n2.y.h);
		
		if (xl > xh || yl > yh) return 0;
		else return (xh-xl)*(yh-yl);
	}
	private void chooseSplitAxis(ArrayList<Entry> entries) { // sort entries by the best axis
		long xmargin = 0;
		Collections.sort(entries, Entry.CompareX);
		
		Node first = new Node(); Node second = new Node();
		for (int i = 0; i < m-1; i++) {
			first.add(entries.get(i));
		}
		for (int i = m-1; i < M+1; i++) {
			second.add(entries.get(i));
		}
		
		for (int k = 0; k < M-2*m+2; k++) {
			first.add(entries.get(m-1+k));
			second.remove(0);
			xmargin += first.margin() + second.margin();
		}
		
		long ymargin = 0;
		Collections.sort(entries, Entry.CompareY);
		
		first = new Node(); second = new Node();
		for (int i = 0; i < m-1; i++) {
			first.add(entries.get(i));
		}
		for (int i = m-1; i < M+1; i++) {
			second.add(entries.get(i));
		}
		
		for (int k = 0; k < M-2*m+2; k++) {
			first.add(entries.get(m-1+k));
			second.remove(0);
			ymargin += first.margin() + second.margin();
		}
		
		if (xmargin < ymargin)
			Collections.sort(entries, Entry.CompareX);		
	}

	private void adjustTree(Node l, Node ll){
		Node n = l;
		Node nn = ll;
		while(!n.equals(R)){
			Node p = n.parent;
			Entry en = p.find(n);
			en.adjust();
			if (nn!=null){
				Entry enn = new Entry();
				enn.child = nn;
				enn.adjust();
				if (p.size()<M) {
					p.add(enn);
					nn = null;
				}
				else nn = splitNode(p, enn);
			}
			n = p;
		}
		if (nn!=null){      // root is split!
			Node r = new Node(false);
			nodes++;
			R = r;
			n.parent = R;
			nn.parent = R;
			Entry e1 = new Entry();
			Entry e2 = new Entry();
			e1.child = n;
			e2.child = nn;
			e1.adjust();
			e2.adjust();
			r.add(e1);
			r.add(e2);	
			height++;
		}
	}
	public void insert(Entry e){
		if (R==null){
			Node n = new Node(true);
			nodes++;
			R = n;
			n.add(e);
			height++;
			return;
		}
		Node l = chooseLeaf(e);
		Node ll = null;
		if (l.size() < M) l.add(e);
		else ll = splitNode(l, e);
		adjustTree(l, ll);
	}

	/* Delete */
	private Node chooseNode(Entry e){   /// e is not leaf, 
		Node n = R;    // at least has root
		int th = 1;
		int h = 1;
		while (!n.isleaf){
			n = n.get(0).child;
			th++;
		}
		n = e.child;
		while (!n.isleaf){
			n = n.get(0).child;
			h++;
		}
		h = th-h;
		n = R;
		while (h!=0){
			long s = -1;
			Node sn=null;
			for (int a=0; a<n.size(); a++){
				Entry k = n.get(a);
				if (e.x.h<=k.x.h&&e.x.l>=k.x.l&&e.y.h<=k.y.h&&e.y.l>=k.y.l){
					if ((s==-1)||s>k.area()) {
						s = k.area();
						sn = k.child;
					}
				}
			}
			n = sn;
			h--;
		}
		return n;
	}
	private void hinsert(Entry e){ // e has a appropriate subtree.   Insert �ܰ迡 ��� entry ����
		Node l = chooseNode(e);
		Node ll = null;
		if (l.size()<M) l.add(e);
		else ll = splitNode(l, e);
		adjustTree(l,ll);
	}
	private void _findLeaf(Node t, int xl, int xh, int yl, int yh, Entry k){  // assume there exists 1 entry
		for (int a = 0; a<t.size(); a++){
			Entry b = t.get(a);
			if (t.isleaf){
				if (xl==b.x.l&&xh==b.x.h&&yl==b.y.l&&yh==b.y.h)
					k.child = t;
			}
			else if (xl>=b.x.l&&xh<=b.x.h&&yl>=b.y.l&&yh<=b.y.h) 
				_findLeaf(b.child, xl, xh, yl, yh, k);
		}
	}
	private Node findLeaf(int xl, int xh, int yl, int yh){
		Entry result = new Entry();
		result.child = null;
		_findLeaf(R, xl, xh, yl, yh ,result);
		return result.child;
	}
	private void condenseTree(Node n){
		Node p = null;
		Entry e = null;
		ArrayList<Node> q = new ArrayList<Node>();
		while(n!=R){
			p = n.parent;
			e = p.find(n);
			if (n.size()<m){
				p.remove(e);
				q.add(n);
			}else e.adjust();
			n = p;
		}
		while (!q.isEmpty()){
			n = (Node) q.remove(0);
			for (int a=0; a<n.size(); a++){
				if (n.isleaf) insert(n.get(a));
				else hinsert(n.get(a));
			}
		}
	}
	public void delete(int xl, int xh, int yl, int yh){
		Node l = findLeaf(xl, xh, yl, yh);
		if (l==null) return;
		for (int a=0; a<l.size(); a++){
			Entry b = l.get(a);
			if (b.x.l==xl&&b.x.h==xh&&b.y.l==yl&&b.y.h==yh)
				l.remove(a);
		}
		condenseTree(l);
		if (R.size()==1) R = R.get(0).child;
	}
}
