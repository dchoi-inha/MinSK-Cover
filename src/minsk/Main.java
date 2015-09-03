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
import minsk.algorithm.Algorithm;
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
			Algorithm alg = new Algorithm();
			Env.W = new Words();
			Dataset db = construct("UK.txt");
			
//			RTree rt = new RTree();
//			BRTree brt = new BRTree(Env.W);
			InvertedFile iv = new InvertedFile();
//			LinList list = new LinList();

			System.out.print("Indexing Start");
			for (STObject o: db) {
//				rt.insert(o);
//				brt.insert(o);
				iv.add(o);
//				list.add(o);
			}
			System.out.println("---Indexing End");
			
			System.out.print("M: " + RTree.M + " m: " + RTree.m);
//			System.out.print(" objects: " + db.size() + " nodes: "+rt.nodes+" heights: "+rt.height);
			System.out.println(" keywords: " + Env.W.size()+"\n");
			
			long cpuTimeElapsed;
			
			int k = 1, l = 12;
			for (int i = 1; i<=1; i++){
				HashSet<String> T = Util.rand(l, Env.W, iv, db);
//				HashSet<String> T = new HashSet<String>(Arrays.asList(new String [] {"Car", "Link", "Crescent", "Londonderry"}));
				System.out.println("T:" + T + "\n");

				// list knn search
//				cpuTimeElapsed = Util.getCpuTime();
//				for (String t: T) {
//					BLEntry e = (BLEntry)list.nextNN(q, t, Env.W);
//					System.out.println(e.obj);
//				}
//				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;
//				System.out.println("List\t------------------------------" + cpuTimeElapsed/(double)1000000000);
				
				// big bR-tree knn search
//				cpuTimeElapsed = Util.getCpuTime();
//				ArrayList<STObject> result1 = brt.textNNSearch(q, T, Env.W);
//				for (STObject o: result1) {
//					System.out.println(o);
//				}
//				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;
//				System.out.println("bRtree\t------------------------------" + cpuTimeElapsed/(double)1000000000);

				// virtual bR-tree knn search
//				cpuTimeElapsed = Util.getCpuTime();
//				ArrayList<STObject> result2 = fbrt.textNNSearch(q, T, w);
//				for (STObject o: result2) {
//					System.out.println(o);
//				}
//				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;
//				System.out.println("vir bRtree------------------------------" + cpuTimeElapsed/(double)1000000000);

				// Greedy Keyword Group (GKG) on a bR-tree
//				cpuTimeElapsed = Util.getCpuTime();
//				Group result3 = alg.GKG(T, brt, iv, Env.W);
//				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;
//				System.out.print(result3);
//				System.out.println("GKG bRtree---------------------------------" + cpuTimeElapsed/(double)1000000000);

				// Greedy Keyword Group (GKG) on a virtual bR-tree
//				cpuTimeElapsed = Util.getCpuTime();
//				Group result4 = alg.GKG(T, iv);
//				result4.shrink(T);
//				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;
//				System.out.print(result4);
//				System.out.println("GKG\t----------------------------------------" + cpuTimeElapsed/(double)1000000000+"\n");
//				
//				// SKECa algorithm
//				cpuTimeElapsed = Util.getCpuTime();
//				Group result5 = alg.SKECa(T, iv);
//				result5.shrink(T);
//				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;
//				System.out.print(result5);
//				System.out.println("SKECa\t----------------------------------------" + cpuTimeElapsed/(double)1000000000+"\n");
//
//				// SKECa+ algorithm
//				cpuTimeElapsed = Util.getCpuTime();
//				Group result6 = alg.SKECaplus(T, iv);
//				result6.shrink(T);
//				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;
//				System.out.print(result6);
//				System.out.println("SKECaplus\t--------------------------------" + cpuTimeElapsed/(double)1000000000+"\n");

				// ScaleLune algorithm
				cpuTimeElapsed = Util.getCpuTime();
				Group result7 = alg.ScaleLune(T, iv);
				result7.shrink(T);
				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;
				System.out.print(result7);
				System.out.println("ScaleLune\t---------------------------------" + cpuTimeElapsed/(double)1000000000+"\n");
				
				
//				if (!(result4.covers(T) && result5.covers(T) && result6.covers(T)) && result7.covers(T))
//					System.err.println("result does not cover T");
				
				
				System.out.println("\n");
			}

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Dataset construct(String filename) throws IOException{
		Dataset db = new Dataset();
		BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
 		int count = 1;  
		System.out.print("DB Load Start");
		
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
		
		System.out.print("---DB Load End\n");
		in.close();
		
		return db;

	}
}