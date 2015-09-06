package minsk.structure;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Group
 * @author Dong-Wan Choi
 * @date 2015. 8. 28.
 */
public class Group implements Iterable<STObject>{

	protected ArrayList<STObject> g;
	private WordTab tab;
	private double d = -1;
	
	public Group() {
		g = new ArrayList<STObject>();
	}
	
	public Group(STObject o) {
		g = new ArrayList<STObject>();
		g.add(o);
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
	
	public void updateDia() {
		double max = Double.MIN_VALUE;
		double dist;
		for (int i=0; i < g.size(); i++) {
			for (int j=i+1; j < g.size(); j++) {
				dist = g.get(i).loc.distance(g.get(j).loc);
				if ( max < dist ) max = dist;
			}
		}
		d = max;
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

	public boolean covers(HashSet<String> T) {
		if (tab == null) {
			tab = new WordTab();
			for (STObject o: g) tab.add(o);
		}
		return tab.containsAll(T);
	}
	
	public void shrink(HashSet<String> T) {
		// perform greedy set cover
		HashSet<String> U = new HashSet<String>(T);
		ArrayList<STObject> tg = new ArrayList<STObject>();
		
		while (!U.isEmpty()) {
			int cnt, maxC = Integer.MIN_VALUE;
			STObject next=null;
			for (STObject o: g) {
				cnt = o.interCnt(U);
				if (cnt > maxC) {
					maxC = cnt;
					next = o;
				}
			}
			if (next != null) {
				U.removeAll(next.text);
				tg.add(next);
			}
		}
		g = tg;
		tab = null;
	}
	
	@Override
	public Iterator<STObject> iterator() {
		return g.iterator();
	}
	
	public void sort() {
		Collections.sort(g, STObject.CompareLoc);
	}
	
	public String toString() {
		StringBuffer s = new StringBuffer();
		
		s.append("dia: " + dia() + " n: " + size() + "\n");
		s.append("cost1: " + cost1() + " cost2: " + cost2() + "\n");
		s.append(g.toString() + "\n");
//		for (STObject o: g) s += o.toString() + "\n";
		
		return s.toString();
	}
	
}
