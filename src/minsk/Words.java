package minsk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import minsk.structure.STObject;
import minsk.util.Bitmap;
import minsk.util.Util;

/**
 * Words
 * @author Dong-Wan Choi
 * @date 2015. 8. 27.
 * 
 * This class can be a query object in our MinSK problem.
 */
public class Words {

	public HashMap<String, Integer> indices; // a word to an integer representing a position of a bitmap
	public ArrayList<String> words;
	
	public Words() {
		indices = new HashMap<String, Integer>();
		words = new ArrayList<String>();
	}
	
	public Words(HashSet<String> T) {
		indices = new HashMap<String, Integer>();
		words = new ArrayList<String>();
		for (String t: T) {
			if (!indices.containsKey(t)) {
				words.add(t);
				indices.put(t, words.size()-1);
			}
		}
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
	
	public int getIdx(String t) {
		return indices.get(t);
	}
	
	public HashSet<String> rand(int l) {
		HashSet<String> T = new HashSet<String>();
		Random r = new Random();
		for (int i = 0; i < l; i++) {
			String s = words.get(r.nextInt(words.size()));
			if (T.contains(s)) {
				i--;
			} else T.add(s);
		}
		return T;
	}
}
