package minsk.algorithm;

import java.util.HashSet;
import com.google.common.collect.TreeMultiset;
import minsk.Words;
import minsk.brtree.BRTree;
import minsk.docindex.InvertedFile;
import minsk.structure.Circle;
import minsk.structure.Dataset;
import minsk.structure.Group;
import minsk.structure.STObject;
import minsk.util.Util;

public class Algorithm {
	public Group GKG(HashSet<String> T, BRTree brt, InvertedFile iv, Words w){
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
		Dataset db = iv.dataset(T);
		Words w = new Words(T);
		BRTree brt = new BRTree(w);
		for (STObject o: db) {
			brt.insert(o);
		}
		
		return GKG(T, brt, iv, w);
	}
	
	private Circle findOSKEC(STObject o, Circle c) {
		// TODO Auto-generated method stub
		return null;
	}
	public Group SKEC(HashSet<String> T, InvertedFile iv) {
		Dataset db = iv.dataset(T);
		Words w = new Words(T);
		BRTree brt = new BRTree(w);
		for (STObject o: db) {
			brt.insert(o);
		}
		
		Group g = GKG(T, brt, iv, w);
		Circle c = Util.findSec(g);
		
		for (STObject o : db) {
			if (o.text.containsAll(T)) return new Group(o);
			
			c = findOSKEC(o, c);
		}
		
		System.out.println(c);
		
		return g;
	}
}
