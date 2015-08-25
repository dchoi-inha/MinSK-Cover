package minsk.tree;

import java.util.ArrayList;


public class Entry {
	public Pair x;
	public Pair y;
	public Node child;
	public int ptr;
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
		int xl=0,xh=0,yl=0,yh=0;
		int flag = 1;
		ArrayList<Entry> k = child.entryList;
		for (int a = 0; a<k.size(); a++){
			Entry b = (Entry) k.get(a);
			if (flag==1 || b.x.l<xl) xl = b.x.l;
			if (flag==1 || b.x.h>xh) xh = b.x.h;
			if (flag==1 || b.y.l<yl) yl = b.y.l;
			if (flag==1 || b.y.h>yh) yh = b.y.h;
			if (flag==1) flag =0;
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
