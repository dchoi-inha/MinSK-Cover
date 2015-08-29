package minsk.algorithm;

import java.util.ArrayList;
import java.util.HashSet;

import minsk.brtree.BRTree;
import minsk.docindex.InvertedFile;
import minsk.structure.STObject;

public class Algorithm {
	public ArrayList<STObject> GKG(HashSet<String> T, BRTree brt, InvertedFile iv){
		String t_inf;
		int min = Integer.MAX_VALUE;
		for (String t: T) {
			if (iv.freq(t) < min) {
				min = iv.freq(t);
				t_inf = t;
			}
		}
		
		
		return null;
	}
}
