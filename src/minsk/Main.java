package minsk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import minsk.structure.*;
import minsk.util.Util;
import minsk.brtree.*;
import minsk.docindex.*;
import minsk.rtree.*;

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
			BRTree brt = new BRTree(Env.W);
			InvertedFile iv = new InvertedFile();
			LinList list = new LinList();

			System.out.println("Indexing Start");
			for (STObject o: db) {
//				rt.insert(o);
				brt.insert(o);
				iv.add(o);
				list.add(o);
			}
			System.out.println("Indexing End");
			
			System.out.print("M: " + RTree.M + " m: " + RTree.m + " objects: " + db.size() + " nodes: "+rt.nodes+" heights: "+rt.height+ "\n");
			System.out.print("keywords: " + Env.W.size() + "\n");
			
			long cpuTimeElapsed;
			
			int k = 1, l = 40;
			for (int i = 0; i<1; i++){
				// generate random query
				double x = Math.random();
				double y = Math.random();
				Point q = new Point(x,	y);
//				Point q = new Point(0.5042344,	0.2175871);
//				HashSet<String> T = Env.W.rand(l);
				HashSet<String> T = new HashSet<String>(Arrays.asList(new String [] {"Eurolink", "Sittingbourne", "Estate", "Commercial"}));
				System.out.println("q:" + q + "  T:" + T);

				cpuTimeElapsed = Util.getCpuTime();
				for (String t: T) {
					BLEntry e = (BLEntry)list.nextNN(q, t, Env.W);
					System.out.println(e.obj);
				}
				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;
				System.out.println("List\t------------------------------" + cpuTimeElapsed/(double)1000000000);
				
				cpuTimeElapsed = Util.getCpuTime();
				ArrayList<BEntry> result1 = brt.textNNSearch(q, T, Env.W);
				for (BEntry e: result1) {
					System.out.println(((BLEntry)e).obj);
				}
				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;
				System.out.println("bRtree\t------------------------------" + cpuTimeElapsed/(double)1000000000);

				cpuTimeElapsed = Util.getCpuTime();
				Dataset fdb = iv.dataset(T);
				Words w = new Words(T);
				BRTree fbrt = new BRTree(w);
				for (STObject o: fdb) {
					fbrt.insert(o);
				}
				ArrayList<BEntry> result2 = fbrt.textNNSearch(q, T, w);
				for (BEntry e: result2) {
					System.out.println(((BLEntry)e).obj);
				}
				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;
				System.out.println("vir bRtree\t------------------------------" + cpuTimeElapsed/(double)1000000000);



				
				System.out.println("\n");
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
				if (!"".equals(tag.trim()))
					text.add(tag.trim()); 
			}
			STObject obj = new STObject(count, x, y, text);
			db.add(obj);		
			Env.W.add(obj);
			
			count ++; 
		}
		
		System.out.print("DB Load End\n");
		in.close();
		
		return db;

	}
}