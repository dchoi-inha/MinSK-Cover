package minsk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import minsk.structure.*;
import minsk.util.Debug;
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
			Random r = new Random();

			int cnt = 50, l;
			for (l = 2; l <= 20; l = l+2) {
			
			
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
			System.out.println(" keywords: " + Env.W.size() + " |T|: " + l+"\n");
			
			long cpuTimeElapsed;
			
			String [] a = new String[]{"GKG", "SKECa+", "ScaleLune(w/o PT)", "ScaleLune(PT)", "GreedyMinSK"};
			double [] c1 = new double[a.length]; 
			double [] c1max = new double[a.length];
			double [] c2 = new double[a.length];
			double [] c2max = new double[a.length];
			double [] t = new double[a.length];
			Group [] result = new Group[a.length];
			
			double [] n = new double[a.length];
			double [] dia = new double[a.length];
			
			
			for (int i = 0; i < cnt; i++){
				System.out.print(i+" ");
				if (i % 20 == 0 && i > 0) System.out.println();

				HashSet<String> T = Util.rand(l, Env.W, iv, db, r);
//				HashSet<String> T = new HashSet<String>(Arrays.asList(new String [] {"Car", "Link", "Crescent", "Londonderry"}));
				Debug._PrintL("T:(" + T.size() + ")" + T + "\n");

				// Greedy Keyword Group (GKG) on a virtual bR-tree
				cpuTimeElapsed = Util.getCpuTime();
				result[0] = alg.GKG(T, iv);
				result[0].shrink(T);
				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed; t[0] += cpuTimeElapsed/(double)1000000000;
				Debug._Print(result[0]);
				Debug._PrintL("GKG\t----------------------------------------" + cpuTimeElapsed/(double)1000000000+"\n");
				
				// SKECa algorithm
//				cpuTimeElapsed = Util.getCpuTime();
//				Group result5 = alg.SKECa(T, iv);
//				result5.shrink(T);
//				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;
//				Debug._Print(result5);
//				Debug._PrintL("SKECa\t----------------------------------------" + cpuTimeElapsed/(double)1000000000+"\n");

				// SKECa+ algorithm
				cpuTimeElapsed = Util.getCpuTime();
				result[1] = alg.SKECaplus(T, iv);
				result[1].shrink(T);
				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed; t[1] += cpuTimeElapsed/(double)1000000000;
				Debug._Print(result[1]);
				Debug._PrintL("SKECaplus\t--------------------------------" + cpuTimeElapsed/(double)1000000000+"\n");

				// ScaleLuneCartesian algorithm
				cpuTimeElapsed = Util.getCpuTime();
				result[2] = alg.ScaleLuneCartesian(T, iv);
				result[2].shrink(T);
				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed; t[2] += cpuTimeElapsed/(double)1000000000;
				Debug._Print(result[2]);
				Debug._PrintL("ScaleLuneCartesian\t-----------------------------" + cpuTimeElapsed/(double)1000000000+"\n");
				
				// ScaleLunePolar algorithm
				cpuTimeElapsed = Util.getCpuTime();
				result[3] = alg.ScaleLunePolar(T, iv);
				result[3].shrink(T);
				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed; t[3] += cpuTimeElapsed/(double)1000000000;
				Debug._Print(result[3]);
				Debug._PrintL("ScaleLunePolar\t-----------------------------" + cpuTimeElapsed/(double)1000000000+"\n");
				
				// GreedyMinSK algorithm
				cpuTimeElapsed = Util.getCpuTime();
				result[4] = alg.GreedyMinSK(T, iv);
				result[4].shrink(T);
				cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed; t[4] += cpuTimeElapsed/(double)1000000000;
				Debug._Print(result[4]);
				Debug._PrintL("GreedyMinSK\t-----------------------------" + cpuTimeElapsed/(double)1000000000+"\n");
				
				
				for (int j = 0; j < result.length; j++) {
					c1[j] += result[j].cost1();
					c2[j] += result[j].cost2();
					c1max[j] = Math.max(c1max[j], result[j].cost1());
					c2max[j] = Math.max(c2max[j], result[j].cost2());
					n[j] += result[j].size();
					dia[j] += result[j].dia();
					
					if (!result[j].covers(T)) {
						System.err.println("result does not cover T");
						System.err.println(result[j]);
						System.exit(0);
					}
				}

				Debug._Print("\n");
			}
			System.out.println();
			System.out.format("%-10s%-15s%-15s%-15s%-15s%-15s\n", "", a[0], a[1], a[2], a[3], a[4]);
			System.out.format("%-10s%-15f%-15f%-15f%-15f%-15f\n", "time avg.", t[0]/cnt, t[1]/cnt, t[2]/cnt, t[3]/cnt, t[4]/cnt);
			System.out.format("%-10s%-15f%-15f%-15f%-15f%-15f\n", "cost1 avg.", c1[0]/cnt, c1[1]/cnt, c1[2]/cnt, c1[3]/cnt, c1[4]/cnt);
			System.out.format("%-10s%-15f%-15f%-15f%-15f%-15f\n", "N avg.", n[0]/cnt, n[1]/cnt, n[2]/cnt, n[3]/cnt, n[4]/cnt);
			System.out.format("%-10s%-15f%-15f%-15f%-15f%-15f\n", "Dia. avg.", dia[0]/cnt, dia[1]/cnt, dia[2]/cnt, dia[3]/cnt, dia[4]/cnt);
			System.out.format("%-10s%-15f%-15f%-15f%-15f%-15f\n", "cost1 max", c1max[0], c1max[1], c1max[2], c1max[3], c1max[4]);
			System.out.format("%-10s%-15f%-15f%-15f%-15f%-15f\n", "cost2 avg.", c2[0]/cnt, c2[1]/cnt, c2[2]/cnt, c2[3]/cnt, c2[4]/cnt);
			System.out.format("%-10s%-15f%-15f%-15f%-15f%-15f\n", "cost2 max", c2max[0], c2max[1], c2max[2], c2max[3], c2max[4]);

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