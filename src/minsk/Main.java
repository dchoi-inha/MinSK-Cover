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
			
			double e1 = 0, e2 = 0, e3 = 0;
			double t1 = 0, t2 = 0, t3 = 0;
			
			int m = 10, l = 10;
			for (int i = 1; i<=m; i++){
				HashSet<String> T = Util.rand(l, Env.W, iv, db);
//				HashSet<String> T = new HashSet<String>(Arrays.asList(new String [] {"Car", "Link", "Crescent", "Londonderry"}));
				System.out.println("T:(" + T.size() + ")" + T + "\n");

				// Greedy Keyword Group (GKG) on a virtual bR-tree
				cpuTimeElapsed = Util.getCpuTime();
				Group result1 = alg.GKG(T, iv);
				result1.shrink(T);
				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed; t1 += cpuTimeElapsed;
				System.out.print(result1);
				System.out.println("GKG\t----------------------------------------" + cpuTimeElapsed/(double)1000000000+"\n");
				
				// SKECa algorithm
//				cpuTimeElapsed = Util.getCpuTime();
//				Group result5 = alg.SKECa(T, iv);
//				result5.shrink(T);
//				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;
//				System.out.print(result5);
//				System.out.println("SKECa\t----------------------------------------" + cpuTimeElapsed/(double)1000000000+"\n");

				// SKECa+ algorithm
				cpuTimeElapsed = Util.getCpuTime();
				Group result2 = alg.SKECaplus(T, iv);
				result2.shrink(T);
				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed; t2 += cpuTimeElapsed;
				System.out.print(result2);
				System.out.println("SKECaplus\t--------------------------------" + cpuTimeElapsed/(double)1000000000+"\n");

				// ScaleLuneCartesian algorithm
				cpuTimeElapsed = Util.getCpuTime();
				Group result3 = alg.ScaleLuneCartesian(T, iv);
				result3.shrink(T);
				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed; t3 += cpuTimeElapsed;
				System.out.print(result3);
				System.out.println("ScaleLuneCartesian\t-----------------------------" + cpuTimeElapsed/(double)1000000000+"\n");
				
				
				e1 += result1.cost1(); e2 += result2.cost1(); e3 += result3.cost1();
				
				if (!(result1.covers(T) && result2.covers(T) && result3.covers(T)))
					System.err.println("result does not cover T");
				
				
				System.out.println("\n");
			}
			
			System.out.println(" \t GKG \t SKECaplus \t ScaleLuneCart\t");
			System.out.println("time \t" + t1/m + " \t " + t2/m + " \t " + t3/m + " \t ");
			System.out.println("cost \t" + e1/m + " \t " + e2/m + " \t " + e3/m + " \t ");

			
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