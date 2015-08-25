package minsk.tree;

import java.util.ArrayList;

import minsk.Env;

public class Node {
	public boolean isleaf;   // leaf = true
	ArrayList<Entry> entryList;
	Node parent;
	Pair x;
	Pair y;
	
	public Node(boolean flag){
		isleaf = flag;
		parent = null;
		entryList = new ArrayList<Entry>();
		x = new Pair();
		y = new Pair();
		x.l = Env.MaxCoord;
		x.h = 0;
		y.l = Env.MaxCoord;
		y.h = 0;
	}
	public Node(){
		isleaf = false;
		parent = null;
		entryList = new ArrayList<Entry>();
		
		x = new Pair();
		y = new Pair();
		x.l = Env.MaxCoord;
		x.h = 0;
		y.l = Env.MaxCoord;
		y.h = 0;
	}
	public int size(){
		return entryList.size();
	}
	public Entry get(int a){
		return entryList.get(a);
	}
	public void add(Entry e){
		entryList.add(e);
		updateMBR(e);
		
	}
	private void updateMBR(Entry e) {
		x.l = Math.min(x.l, e.x.l);
		x.h = Math.max(x.h, e.x.h);
		y.l = Math.min(y.l, e.y.l);
		y.h = Math.max(y.h, e.y.h);
	}
	public void remove(Entry e){
		entryList.remove(e);
		updateMBR(e);
	}
	public Entry remove(int a){
		Entry e = entryList.remove(a);
		updateMBR(e);
		return e;
	}
	public long diffArea(Entry e) { 
		long s = area();
		int xl, xh, yl, yh;
		xl = Math.min(x.l, e.x.l);
		xh = Math.max(x.h, e.x.h);
		yl = Math.min(y.l, e.y.l);
		yh = Math.max(y.h, e.y.h);
		long r = (xh-xl)*(yh-yl);
		return r-s;
	}	
	public Entry find(Node n){
		for (int a=0; a<size(); a++)
			if (get(a).child.equals(n)) return get(a);
		return null;
	}	
	public long area(){        // return area
		return (x.h-x.l)*(y.h-y.l); 

	}
	
	public String toString() {
		return "("+ this.x.l + "," + this.x.h + ")" + "("+ this.y.l + "," + this.y.h + ")";
	}
}
