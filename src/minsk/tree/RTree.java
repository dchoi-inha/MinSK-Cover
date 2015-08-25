package minsk.tree;

import java.util.ArrayList;

public class RTree {
	public static final int M = 20;
	public static final int m = 10;
	public Node R=null;
	public int nodeCount = 0;
	public int leafCount = 0;
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
						if (se.diffArea(e) > k.diffArea(e))
							se = k;
						else if (se.diffArea(e) == k.diffArea(e)) {
							if (se.area() > k.area())
								se = k;
						}
					}
				}
			} else {
				long s = -1, d;
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
	private Entry[] pickSeeds(Node n){ // return the most wasteful pair of entries
		Entry me1=null;
		Entry me2=null;
		long d = -1;
		for (int i=0; i<n.size(); i++) {
			for (int j=i+1; j<n.size(); j++){
				Entry e1 = n.get(i);
				Entry e2 = n.get(j);
				Node l = new Node();
				l.add(e1);
				l.add(e2);
				long k = l.area()-e1.area()-e2.area();
				if (me1==null || d<k) {
					me1 = e1; me2 = e2;
					d = k;
				}
			}
		}
		Entry[] result = new Entry[2];
		result[0] = me1;
		result[1] = me2;
		return result;
	}
	private Entry pickNext(ArrayList<Entry> remain, Node l1, Node l2){
		long d =-1;
		Entry k = null;
		for (int i = 0; i<remain.size(); i++){
			Entry e = remain.get(i);
			long d1 = l1.diffArea(e);
			long d2 = l2.diffArea(e);
			if (d<Math.abs(d1-d2)){
				d = Math.abs(d1-d2);
				k = e;
			}
		}
		return k;
	}
	private Node splitNode(Node n, Entry e){  // node n's split and inserted entry is e
		Node nn = new Node(n.isleaf);
		nodes++;
		nn.parent = n.parent;
		n.add(e);
		Entry[] seeds = pickSeeds(n);
		ArrayList<Entry> temp = n.entryList;
		n.initEntries();
		nn.initEntries();
		long s1,s2,d1,d2;
		n.add(seeds[0]);
		if (!n.isleaf) seeds[0].child.parent = n;
		nn.add(seeds[1]);
		if (!n.isleaf) seeds[1].child.parent = nn;
		temp.remove(seeds[0]);
		temp.remove(seeds[1]);	
		while (!temp.isEmpty()){
			if (n.size() >= m && (nn.size()+temp.size() == m)){
				Entry t = null;
				while(!temp.isEmpty()){
					t = (Entry)temp.remove(0);
					nn.add(t);
					if (!n.isleaf)	t.child.parent = nn;
				}
				break;
			} 
			if (nn.size() >= m && (n.size()+temp.size() == m)){
				Entry t = null;
				while(temp.size()!=0) {
					t = (Entry) temp.remove(0);
					n.add(t);
					if (!n.isleaf) t.child.parent = n;
				}
				break;
			}
			Entry next = pickNext(temp, n, nn);
			temp.remove(next);
			s1 = n.area();
			s2 = nn.area();
			d1 = n.diffArea(next);
			d2 = nn.diffArea(next);
			int l1 = n.size();
			if (d1>d2) nn.add(next);
			else if (d1<d2) n.add(next);
			else {
				if (s1>s2) nn.add(next);
				else if (s1<s2) n.add(next);
				else {
					if (n.size()<nn.size()) n.add(next);
					else nn.add(next);
				}
			}
			if (!n.isleaf) {
				if (n.size()== l1) next.child.parent = nn;
				else next.child.parent = n;
			}
		}
		return nn;
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
		adjustTree(l,ll);
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
