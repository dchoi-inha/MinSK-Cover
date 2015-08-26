package minsk.tree;

import java.util.Comparator;

import minsk.Env;


public class Entry {
	public Pair x;
	public Pair y;
	public Node child;
	
	public Entry(){
		x = new Pair();
		y = new Pair();
	}
	
	public Entry(int xl, int xh, int yl, int yh){
		x = new Pair();
		y = new Pair();
		x.l = xl;
		x.h = xh;
		y.l = yl;
		y.h = yh;
	}
	
	public long area(){    
		return (x.h-x.l)*(y.h-y.l);
	}
	
	public long diffArea(Entry e){
		return child.diffArea(e);
	}
	public long overlap(Entry e){
		int xl, xh, yl, yh;
		
		xl = Math.max(x.l, e.x.l);
		xh = Math.min(x.h, e.x.h);
		yl = Math.max(y.l, e.y.l);
		yh = Math.min(y.h, e.y.h);
		
		if (xl > xh || yl > yh) return 0;
		else return (xh-xl)*(yh-yl);
		
	}
	public void adjust(){  
		int xl=Env.MaxCoord,xh=0,yl=Env.MaxCoord,yh=0;
		for (int i = 0; i<child.size(); i++){
			Entry e = (Entry) child.get(i);
			if (e.x.l<xl) xl = e.x.l;
			if (e.x.h>xh) xh = e.x.h;
			if (e.y.l<yl) yl = e.y.l;
			if (e.y.h>yh) yh = e.y.h;
		}
		x.l = xl;
		x.h = xh;
		y.l = yl;
		y.h = yh;

		child.x.l = xl;
		child.x.h = xh;
		child.y.l = yl;
		child.y.h = yh;
	}
	
	public String toString()
	{
		return "("+ this.x.l + "," + this.x.h + ")" + "("+ this.y.l + "," + this.y.h + ")";
	}
	
	public static Comparator<Entry> CompareX = new Comparator<Entry>() {
		public int compare(Entry e1, Entry e2) {
			if (e1.x.l == e2.x.l) return e1.x.h - e2.x.h;
			else return e1.x.l - e2.x.l;
		}
	};
	public static Comparator<Entry> CompareY = new Comparator<Entry>() {
		public int compare(Entry e1, Entry e2) {
			if (e1.y.l == e2.y.l) return e1.y.h - e2.y.h;
			else return e1.y.l - e2.y.l;
		}
	};
}
