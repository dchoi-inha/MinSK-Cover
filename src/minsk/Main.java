package minsk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import minsk.structure.Dataset;
import minsk.structure.Point;
import minsk.structure.STObject;
import minsk.util.Bitmap;
import minsk.util.Util;

import minsk.brtree.BRTree;
import minsk.docindex.InvertedFile;
import minsk.rtree.Entry;
import minsk.rtree.LEntry;
import minsk.rtree.RTree;

/**
 * Main
 * @author Dong-Wan Choi
 * 2015. 8. 24.
 */
public class Main {

	public static void main(String[] args) {
		try {
			Env.W = new Words();
			Dataset db = construct("UK.txt");
			
			RTree rt = new RTree();
			BRTree brt = new BRTree();
			InvertedFile docidx = new InvertedFile();

			for (STObject o: db) {
				rt.insert(o);
				brt.insert(o);
				docidx.add(o);
			}
			

			
			Bitmap ab = new Bitmap(Env.W.size());
			ab.setAll();
			System.out.println(ab);
//			for (String t: Util.getText(ab, Env.W.words)) {
//				System.out.print(t);
//			}
			System.out.println();
			System.out.println(brt.R.bmp);
//			for (String t: Util.getText(brt.R.bmp, Env.W.words)) {
//				System.out.print(t);
//			}
			System.out.println("\n");
			
			System.out.print("M: " + RTree.M + " m: " + RTree.m + " objects: " + db.size() + " nodes: "+rt.nodes+" heights: "+rt.height+ "\n");
			System.out.print("keywords: " + Env.W.size() + "\n");
			
			int k = 4;
			for (int i = 0; i<1; i++){
				double x = Math.random();
				double y = Math.random();

				Point q = new Point(x,	y);
				System.out.println("q:" + q);


				ArrayList<Entry> list1 = rt.kNNSearch(q, k);
				
				for (Entry e: list1) {
					System.out.println(((LEntry) e).obj);
				}

			}

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Dataset construct (String filename) throws IOException{
		Dataset db = new Dataset();
		BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
 		int count = 1;  
		System.out.print("DB Load Start\n");
		
		final HashSet<String> symbols = new HashSet<String>();
		String[] spChars = {
				"`", "-", "=", ";", "'", "/", "~", "!", "@", 
				"#", "$", "%", "^", "&", "|", ":", "<", ">", 
				"\\", "*", "+", "{", "}", "?", ".",	",", "the",
				"The", "of", "(", ")", "]", "[", "\"" 
		};
		symbols.addAll(Arrays.asList(spChars));
		
		for (String line = in.readLine(); line != null; line = in.readLine())
		{
			String [] tokens = line.split("\t");
			double x = Double.parseDouble(tokens[0]);
			double y = Double.parseDouble(tokens[1]);
			HashSet<String> text = new HashSet<String>();
			String [] keywords = tokens[2].split(" ");
			for (String tag: keywords)
			{
				if (symbols.contains(tag) || tag.length() == 0) // remove extra symbols
					continue;

				String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
				tag = tag.replaceAll(match, "");
				text.add(tag.trim()); 
			}
			STObject obj = new STObject(x, y, text);
			db.add(obj);		
			Env.W.add(obj);
			
			count ++; 
		}
		
		System.out.print("DB Load End\n");
		in.close();
		
		return db;

	}
}
