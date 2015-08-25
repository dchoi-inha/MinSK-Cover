package rtree;

import java.util.ArrayList;

public class Node {
	public int flag;   // leaf = 1
	ArrayList<Entry> entryList;
	Node parent;
	Pair x;
	Pair y;
	
	public Node(int a){
		flag = a;
		parent = null;
		entryList = new ArrayList<Entry>();
		x = new Pair();
		y = new Pair();
		x.l = 1000000000;
		x.h = 0;
		y.l = 1000000000;
		y.h = 0;
	}
	public Node(){
		flag = 0;
		parent = null;
		entryList = new ArrayList<Entry>();
		
		x = new Pair();
		y = new Pair();
		x.l = 1000000000;
		x.h = 0;
		y.l = 1000000000;
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
		x.l = Math.min(x.l, e.x.l);
		x.h = Math.max(x.h, e.x.h);
		y.l = Math.min(y.l, e.y.l);
		y.h = Math.max(y.h, e.y.h);
		
	}
	public void remove(Entry e){
		entryList.remove(e);
	}
	public Entry remove(int a){
		return entryList.remove(a);
	}
	public long diff(Entry e) {   // return difference if e is inserted
		long s = area();
		entryList.add(e);
		long r = area()-s;
		entryList.remove(e);
		return r;
	}	
	public Entry find(Node n){
		for (int a=0; a<size(); a++)
			if (get(a).child.equals(n)) return get(a);
		return null;
	}	
	public long area(){        // return area
//		int xl=0,xh=0,yl=0,yh=0;
//		int flag = 1;
//		for (int a = 0; a<entryList.size(); a++){
//			Entry b = (Entry) entryList.get(a);
//			if (flag==1 || b.x.l<xl) xl = b.x.l;
//			if (flag==1 || b.x.h>xh) xh = b.x.h;
//			if (flag==1 || b.y.l<yl) yl = b.y.l;
//			if (flag==1 || b.y.h>yh) yh = b.y.h;
//			if (flag==1) flag =0;
//		}
//		return (xh-xl)*(yh-yl); 
		return (x.h-x.l)*(y.h-y.l); 

	}
	
	public String toString() {
		return "("+ this.x.l + "," + this.x.h + ")" + "("+ this.y.l + "," + this.y.h + ")";
	}
}
