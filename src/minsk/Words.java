package minsk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import minsk.structure.STObject;
import minsk.util.Bitmap;
import minsk.util.Util;

/**
 * Words
 * @author Dong-Wan Choi
 * 2015. 8. 27.
 */
public class Words {

	public HashMap<String, Integer> indices; // a word to an integer representing a position of a bitmap
	public ArrayList<String> words;
	
	public Words() {
		indices = new HashMap<String, Integer>();
		words = new ArrayList<String>();
	}
	
	public void add(STObject obj) {
		for (String t: obj.text) {
			if (!indices.containsKey(t)) {
				words.add(t);
				indices.put(t, words.size()-1);
			}
		}
	}
	
	public Bitmap getBitmap(HashSet<String> text) {
		return Util.getBitmap(text, words.size(), indices);
	}

	public int size() {
		return words.size();
	}
}
