package minsk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import minsk.rtree.Entry;
import minsk.rtree.RTree;
import minsk.util.Debug;

/**
 * Main
 * @author Dong-Wan Choi
 * 2015. 8. 24.
 */
public class Main {
	public static RTree construct (String filename) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
		String s;
		String[] array;
		RTree R = new RTree();
		int count = 1;  
		System.out.print("Insertion Start\n");
		while ((s=in.readLine())!=null){
			array = s.split(" ");
			Entry e = new Entry(Double.parseDouble(array[0]),
					Double.parseDouble(array[1]),
					Double.parseDouble(array[0]),
					Double.parseDouble(array[1]));
//			System.out.print("Insert "+count+" node "+e.x.l+" "+e.x.h+" "+e.y.l+" "+e.y.h+"\n"); 
			R.insert(e);
			count ++; 
		}	
		System.out.print("Insertion End\n");
		in.close();

		System.out.print("objects: " + count + " nodes: "+R.nodes+" heights: "+R.height+ "\n");
		return R;
	}

	public static void main(String[] args) {
		try {
			RTree rt = construct("NE.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
