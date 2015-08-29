package minsk.algorithm;

import java.util.HashSet;

import com.google.common.collect.TreeMultiset;

import minsk.Env;
import minsk.Words;
import minsk.brtree.BRTree;
import minsk.docindex.InvertedFile;
import minsk.structure.STObject;
import minsk.util.Util;

public class Algorithm {
	public HashSet<STObject> GKG(HashSet<String> T, BRTree brt, InvertedFile iv, Words w){
		HashSet<STObject> g = null;
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
			HashSet<STObject> tg = new HashSet<STObject>();
			tg.add(o);
			
			HashSet<String> ucSet = new HashSet<String>(T);
			ucSet.removeAll(o.text);
			tg.addAll(brt.textNNSearch(o.loc, ucSet, w));
			double dia = Util.diameter(tg);
			
			if (mind > dia) {
				mind = dia;
				g = tg;
			}
		}
		
		return g;
	}
}
