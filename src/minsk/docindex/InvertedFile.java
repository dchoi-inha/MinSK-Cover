package minsk.docindex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.TreeMultiset;

import minsk.structure.Dataset;
import minsk.structure.STObject;

public class InvertedFile {
	HashMap<String, TreeMultiset<STObject>> map;
	
	public InvertedFile() {
		map = new HashMap<String, TreeMultiset<STObject>>();
	}
	
	public TreeMultiset<STObject> getList(String t) {
		return map.get(t);
	}
	
	public void add(STObject o) {
		TreeMultiset<STObject> postings;
		for (String t: o.text) {
			postings = map.get(t);
			if (postings == null) {
				postings = TreeMultiset.create(STObject.CompareLoc);
				map.put(t, postings);
			}
			postings.add(o);
		}
	}
		
	public Set<String> keywords() {
		return map.keySet();
	}
	
	public int size() {
		return map.size();
	}

	public Dataset dataset(HashSet<String> T) {
		HashMap<Integer, STObject> tmp = new HashMap<Integer, STObject>();
		STObject a;
		
		for (String t: T) {
			TreeMultiset<STObject> list = getList(t);
			for (STObject o: list) {
				if (tmp.containsKey(o.id)) {
					a = tmp.get(o.id);
				} else {
					a = new STObject(o.id, o.loc.x, o.loc.y);
					tmp.put(a.id, a);
				}
				a.text.add(t);
			}
		}
		return new Dataset(tmp.values());

	}
}
