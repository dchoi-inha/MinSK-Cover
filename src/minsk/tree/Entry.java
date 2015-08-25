package minsk.tree;

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
}
