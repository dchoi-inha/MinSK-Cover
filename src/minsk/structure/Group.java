package minsk.structure;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

public class Group implements Iterable<STObject>{

	private ArrayList<STObject> g;
	private double d = -1;
	
	public Group() {
		g = new ArrayList<STObject>();
	}
	
	public Group(Collection<? extends STObject> c) {
		g = new ArrayList<STObject>(c);
	}
	
	public int size() {
		return g.size();
	}

	public double dia() {
		if (d >= 0) return d;
		else {
			double max = Double.MIN_VALUE;
			double dist;
			for (int i=0; i < g.size(); i++) {
				for (int j=i+1; j < g.size(); j++) {
					dist = g.get(i).loc.distance(g.get(j).loc);
					if ( max < dist ) max = dist;
				}
			}
			d = max;
			return d;
		}
	}
	
	public double cost1() {
		return size()*dia();
	}
	public double cost2() {
		long n = (long) size();
		long comb = n*(n-1)/2;
		return comb * dia();
	}
	
	public void add(STObject o) {
		g.add(o);
	}
	
	public void addAll(Collection<? extends STObject> c) {
		g.addAll(c);
	}

	@Override
	public Iterator<STObject> iterator() {
		return g.iterator();
	}
	
	public String toString() {
		String s="";
		
		s += "d: " + dia() + " n: " + size() + "\n";
		s += "cost1: " + cost1() + " cost2: " + cost2() + "\n";
		s += g.toString(); s+="\n";
//		for (STObject o: g) s += o.toString() + "\n";
		
		return s;
	}
	
}
