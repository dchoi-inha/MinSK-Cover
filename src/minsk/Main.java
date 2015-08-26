package minsk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import minsk.structure.STObject;

import minsk.docindex.InvertedFile;
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
			RTree rt = new RTree();
			InvertedFile docidx = new InvertedFile();
			construct("4SQNY.txt", rt, docidx);
			
			for (String t: docidx.keywords()) {
				System.out.print(t+"\n");
				for (STObject o: docidx.get(t)) {
					System.out.println(o);
				}
				System.out.println();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void construct (String filename, RTree R, InvertedFile L) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
		String s;
		String[] array;
		int count = 1;  
		System.out.print("Insertion Start\n");
		
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
			LEntry e = new LEntry(x,x,y,y, obj);
			R.insert(e);
			L.add(obj);
			count ++; 
		}
		
		System.out.print("Insertion End\n");
		in.close();
		
		System.out.print("objects: " + count + " nodes: "+R.nodes+" heights: "+R.height+ "\n");
		System.out.print("keywords: " + L.size() + "\n");

	}
}
