package minsk.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import com.google.common.collect.TreeMultiset;
import com.google.common.math.DoubleMath;

import minsk.Env;
import minsk.Words;
import minsk.brtree.BRTree;
import minsk.cbrtree.CBRTree;
import minsk.cbrtree.CEntry;
import minsk.docindex.InvertedFile;
import minsk.structure.CardTab;
import minsk.structure.Circle;
import minsk.structure.Dataset;
import minsk.structure.Group;
import minsk.structure.Lune;
import minsk.structure.STObject;
import minsk.structure.WordTab;
import minsk.util.Debug;
import minsk.util.PairObject;
import minsk.util.Util;

public class Algorithm {
	private Group GKG(HashSet<String> T, BRTree brt, InvertedFile iv, Words w){
		Group g = null;
		
		double mind = Double.MAX_VALUE;
		
		String t_inf = null;
		int minf = Integer.MAX_VALUE;
		for (String t: T) {
			if (iv.freq(t) < minf) {
				minf = iv.freq(t);
				t_inf = t;
			}
		}
		
		TreeMultiset<STObject> postings = iv.getList(t_inf);
		
		for (STObject o: postings) {
			Group tg = new Group();
			tg.add(o);
			
			HashSet<String> ucSet = new HashSet<String>(T);
			ucSet.removeAll(o.text);
			tg.addAll(brt.textNNSearch(o.loc, ucSet, w));
			double dia = tg.dia();
			
			if (mind > dia) {
				mind = dia;
				g = tg;
			}
		}
		
		return g;
	}
	public Group GKG(HashSet<String> T, InvertedFile iv) {
		Words w = new Words(T);
		Dataset db = iv.getRelevantDB(T, w);
		BRTree brt = new BRTree(w);
		for (STObject o: db) {
			brt.insert(o);
		}
		
		return GKG(T, brt, iv, w);
	}
	
	private class Tuple implements Comparable<Tuple>{
		double angle;
		int flag; // 1 is in, -1 is out
		STObject obj;
		public Tuple(double a, int f, STObject o) {
			angle = a; flag = f; obj = o;
		}
		@Override
		public int compareTo(Tuple o) {
			if (angle > o.angle) return 1;
			else if (angle < o.angle) return -1;
			else {
				if (flag < o.flag) return -1; // out has a higher priority
				else if (flag > o.flag) return 1; // in has a lower priority
				else return 0; // should not happen...
			}
		}
		public String toString() {
			return "<" + angle + ", " + (flag == 1? "In": "Out") + ", " + obj.id + ">";
		}
	}
	public double getInAngle(STObject pole, double diam, STObject o) {
		double l = pole.loc.distance(o.loc)*0.5;
		double r = diam * 0.5;
		double theta = Math.toDegrees(Math.acos(l/r));
		double between = Util.getAngle(pole.loc, o.loc);
		if (between + theta > 360) return between + theta - 360;
		else return between + theta;
	}
	public double getOutAngle(STObject pole, double diam, STObject o) {
		double l = pole.loc.distance(o.loc)*0.5;
		double r = diam * 0.5;
		double theta = Math.toDegrees(Math.acos(l/r));
		double between = Util.getAngle(pole.loc, o.loc);
		if (between - theta < 0) 
			return 360 + between - theta;
		else 
			return between - theta;
	}
	private Group circleScan(STObject obj, double diam, HashSet<String> T, BRTree brt) {
		Group sg = brt.cirRangeSearch(new Circle(obj.loc, diam)); // objects in the sweeping area
		if (!sg.covers(T)) return null;
		
		ArrayList<Tuple> list = new ArrayList<Tuple>();		
		WordTab tab = new WordTab();
		HashSet<STObject> g = new HashSet<STObject>();
		
		double thetaIn, thetaOut;
		for (STObject o: sg) {
			thetaOut = getOutAngle(obj, diam, o);			
			thetaIn = getInAngle(obj, diam, o);
			list.add(new Tuple(thetaOut, -1, o));
			list.add(new Tuple(thetaIn, 1, o));
		}
		Collections.sort(list, Collections.reverseOrder());
		int pos = 0;
		while (!list.isEmpty()) {
			pos = pos % list.size();
			Tuple tuple = list.get(pos);
			if (tuple.flag == 1) { // in
				list.remove(pos); // delete always if it is 'in'-type
				g.add(tuple.obj);
				tab.add(tuple.obj);
				if (tab.containsAll(T)) 
					return new Group(g);
			} else { // out
				if (g.contains(tuple.obj)) { // only if its 'in'-type came before
					list.remove(pos);
					g.remove(tuple.obj);
					tab.remove(tuple.obj);
				} else { // if its 'in'-type never came
					pos++; // continue to the next tuple
				}
			}
		}
		
		return null;
	}
	private Group findAppOSKEC(STObject o, Group gkg, Circle c, double alpha, HashSet<String> T, BRTree brt) {
		double searchUB = c.dia();
		double searchLB, diam;
		Group g = null, tg = null;
		
		tg = circleScan(o, searchUB, T, brt);
		if (tg == null) return null;
		searchLB = gkg.dia() * 0.5;
		
		while (searchUB - searchLB > alpha) {
			diam = (searchUB + searchLB) * 0.5;
			tg = circleScan(o, diam, T, brt);
			if (tg != null) {
				searchUB = diam; 
				g = tg;
			} else searchLB = diam;
		}
		
		return g;
	}
	public Group SKECa(HashSet<String> T, InvertedFile iv) {
		Words w = new Words(T);
		Dataset db = iv.getRelevantDB(T, w);
		BRTree brt = new BRTree(w);
		for (STObject o: db) {
			brt.insert(o);
		}
		
		Group g = GKG(T, brt, iv, w);
		Circle c = Util.findSec(g);
		double alpha = g.dia()*0.5*Env.Ep;
		Group tg;
		for (STObject o : db) {
			if (o.text.containsAll(T)) return new Group(o);
			tg = findAppOSKEC(o, g, c, alpha, T, brt);
			if (tg != null) {
				g = tg;
				c = Util.findSec(g);
			}
		}
		return g;
	}
	
	public Group SKECaplus(HashSet<String> T, InvertedFile iv) {
		Words w = new Words(T);
		Dataset db = iv.getRelevantDB(T, w);
		BRTree brt = new BRTree(w);
		for (STObject o: db) {
			brt.insert(o);
		}
		
		boolean foundResult;
		Group g, tg; 
		Circle c;
		double searchUB, searchLB, alpha, diam;
		double [] maxInvalidRange = new double[db.size()];
		
		g = GKG(T, brt, iv, w);
		c = Util.findSec(g);
		alpha = g.dia()*0.5*Env.Ep;		
		searchUB = c.dia();
		searchLB = g.dia() * 0.5;
		
		for (int i=0; i < db.size(); i++) {
			STObject o = db.get(i);
			if (o.text.containsAll(T)) return new Group(o);
			maxInvalidRange[i] = 0;
		}
		
		while (searchUB - searchLB > alpha) {
			diam = (searchUB + searchLB) * 0.5;
			foundResult = false;
			
			for (int i=0; i < db.size(); i++) {
				STObject o = db.get(i);
				if (diam < maxInvalidRange[i]) continue;
				
				tg = circleScan(o, diam, T, brt);
				if (tg != null) {
					searchUB = diam;
					g = tg;
					foundResult = true;
					break;
				} else {
					if (diam > maxInvalidRange[i])
						maxInvalidRange[i] = diam;
				}
			}
			if (!foundResult) searchLB = diam;
		}
		return g;
	}
	
	/**
	 * @param S, the collection of sets (i.e., objects)
	 * @param T, the set of keywords to be covered
	 * @return the subset of S that covers T
	 */
	private Group fastSetCover(Group S, HashSet<String> T) {
		CardTab tb = new CardTab(); // partition
		HashSet<String> C = new HashSet<String>(); // covered keywords so far
		Group g = new Group();
		
		// partition the sets into sub-collections
		for (STObject o: S) tb.add(o);
		
		// start covering elements
		double k, pk;
		int diff;
		for (int l = tb.max; l >= 1; l--) {
			if (!tb.containsKey(l)) continue;
			
			k = (int) Math.floor(DoubleMath.log2(l)/DoubleMath.log2(Env.P));
			pk = Math.pow(Env.P, k); // p^k
			HashSet<STObject> cardSet = tb.get(l);
			for (STObject o: cardSet) {
				diff = o.diffCnt(C); // |o.\tau - C|
				if (diff >= pk) {
					g.add(o);
					C.addAll(o.text);
				} else {
//					cardSet.remove(o);
					if (diff > 0) tb.add(o, diff);
				}
				
				if (C.containsAll(T)) return g;
			}
		}
		return null;
	}
	private Group GKG4ScaleLune(HashSet<String> T, CBRTree crt, InvertedFile iv, Words w){
		Group g = null;
		
		double mind = Double.MAX_VALUE;
		
		String t_inf = null;
		int minf = Integer.MAX_VALUE;
		for (String t: T) {
			if (iv.freq(t) < minf) {
				minf = iv.freq(t);
				t_inf = t;
			}
		}
		
		TreeMultiset<STObject> postings = iv.getList(t_inf);
		
		for (STObject o: postings) {
			Group tg = new Group();
			tg.add(o);
			
			HashSet<String> ucSet = new HashSet<String>(T);
			ucSet.removeAll(o.text);
			tg.addAll(crt.textNNSearch(o.loc, ucSet, w));
			double dia = tg.dia();
			
			if (mind > dia) {
				mind = dia;
				g = tg;
			}
		}
		
		return g;
	}
	public Group ScaleLuneCartesian(HashSet<String> T, InvertedFile iv) {
		Dataset db = iv.filter(T);
		Words w = new Words(T);
		CBRTree crt = new CBRTree(w);
		CardTab htb = new CardTab();
		for (STObject o: db) {
			crt.insert(o);
			htb.add(o);
		}
		
		Group gr, gk, gl, g;
		int kLB;
		double rLB, fmin;
		
		// Lines 5 - 6
		gr = GKG4ScaleLune(T, crt, iv, w); 
		rLB = gr.dia()*0.5;
//		rLB = gr.dia();
		gk = fastSetCover(db, T); 
		kLB = (int) Math.ceil((double)gk.size()/(Math.log(T.size())+1));
//		kLB = gk.size();
		if (gr.cost1() > gk.cost1()) {
			g = gk; fmin = gk.cost1();
		} else {
			g = gr; fmin = gr.cost1();
		}
		
		Debug._PrintL("rLB: " + rLB + " kLB: " + kLB + " costLB: " + rLB*kLB);
		
		// from Line 7 in Algorithm 2
		for (int l = htb.max; l >= 1; l--) {
			for (STObject o: htb.get(l)) {
//				System.out.println(o); 
				o.checked = true;
				PriorityQueue<CEntry> pq = crt.initPQ(o);
				STObject nn = crt.nextNN(o, pq);
				int i = 0;
				while(nn != null && o.loc.distance(nn.loc) < fmin/(double)kLB) {
					if (Math.ceil(T.size()/l)*rLB >= fmin)
						return g;
					
					Lune lune = new Lune(o.loc, nn.loc);
					gl = crt.luneRangeSearch(lune);
					if (gl.covers(T)) {
						gl = fastSetCover(gl, T);
						if (gl.size()*lune.width() < g.cost1()) {
							fmin = gl.size()*lune.width();
//						if (gl.cost1() < fmin) {
//							fmin = gl.cost1();
							g = gl;
						}
					}
					nn = crt.nextNN(o, pq);
					i++;
				}
//				System.out.println("# NNs: " + i + "\n");
			}
		}
		
		return g;
	}
	
	public Group ScaleLunePolar(HashSet<String> T, InvertedFile iv) {
		Dataset db = iv.filter(T);
		Words w = new Words(T);
		CBRTree crt = new CBRTree(w);
		CardTab htb = new CardTab();
		for (STObject o: db) {
			crt.insert(o);
			htb.add(o);
		}
		
		Group gr, gk, gl, g;
		int kLB;
		double rLB, fmin;
		
		// Lines 5 - 6
		gr = GKG4ScaleLune(T, crt, iv, w); 
//		rLB = gr.dia()*0.5;
		rLB = gr.dia();
		gk = fastSetCover(db, T); 
//		kLB = (int) Math.ceil((double)gk.size()/(Math.log(T.size())+1));
		kLB = gk.size();
		if (gr.cost1() > gk.cost1()) {
			g = gk; fmin = gk.cost1();
		} else {
			g = gr; fmin = gr.cost1();
		}
		
		Debug._PrintL("rLB: " + rLB + " kLB: " + kLB + " costLB: " + rLB*kLB);
		
		// from Line 7 in Algorithm 2
		for (int l = htb.max; l >= 1; l--) {
			for (STObject o: htb.get(l)) {
//				System.out.println(o); 
				o.checked = true;
				PriorityQueue<CEntry> pq = crt.initPQ(o);
				STObject nn = crt.nextNN(o, pq);
				int i = 0;
				while(nn != null && o.loc.distance(nn.loc) < fmin/(double)kLB) {
					if (Math.ceil(T.size()/l)*rLB >= fmin)
						return g;
					
					Lune lune = new Lune(o.loc, nn.loc);
					gl = crt.luneRangeSearch(lune);
					if (gl.covers(T)) {
						gl = fastSetCover(gl, T);
						if (gl.size()*lune.width() < g.cost1()) {
							fmin = gl.size()*lune.width();
//						if (gl.cost1() < fmin) {
//							fmin = gl.cost1();
							g = gl;
						}
					}
					nn = crt.nextNN(o, pq);
					i++;
				}
//				System.out.println("# NNs: " + i + "\n");
			}
		}
		
		return g;
	}
}
