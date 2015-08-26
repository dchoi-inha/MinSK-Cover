package minsk.docindex;

import java.util.HashMap;
import java.util.TreeSet;

import minsk.structure.STObject;

public class InvertedFile {
	HashMap<String, TreeSet<STObject>> map;
	
	public InvertedFile() {
		map = new HashMap<String, TreeSet<STObject>>();
	}
	
	public TreeSet<STObject> get(String t) {
		return map.get(t);
	}
	
	public void add(STObject o) {
		TreeSet<STObject> postings;
		for (String t: o.text) {
			postings = map.get(t);
			if (postings == null) {
				postings = new TreeSet<STObject>(STObject.CompareLoc);
				map.put(t, postings);
			}
			postings.add(o);
		}
	}
	
	public int size() {
		return map.size();
	}
}
