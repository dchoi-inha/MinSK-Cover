package minsk.brtree;


import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;

import minsk.structure.Point;

public class BrtreeSimulator {
	private static String filename = "UK.txt";
	public static long getCpuTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported()?
				bean.getCurrentThreadCpuTime(): 0L;
	}
	public static BRTree construct () throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
		String s;
		String[] array;
		BRTree R = new BRTree();
		int count = 1;  
		System.out.print("Insertion Start\n");
		while ((s=in.readLine())!=null){
			array = s.split("\t");
			BEntry e = new BEntry(Double.parseDouble(array[0]),Double.parseDouble(array[0]),Double.parseDouble(array[1]),Double.parseDouble(array[1]));
			//			System.out.print("Insert "+count+" node "+e.x.l+" "+e.x.h+" "+e.y.l+" "+e.y.h+"\n"); 
			R.insert(e);
			count ++; 
		}	
		System.out.print("Insertion End\n");
		in.close();

		System.out.print("Nodes : "+R.nodes+" heights: "+R.height+"\n");
		return R;
	}
	public static ArrayList<BEntry> construct_array() throws NumberFormatException, IOException{
		BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
		String s;
		String[] array;
		ArrayList<BEntry> list  = new ArrayList<BEntry>();
		while ((s=in.readLine())!=null){
			array = s.split("\t");
			BEntry e = new BEntry(Double.parseDouble(array[0]),Double.parseDouble(array[0]),Double.parseDouble(array[1]),Double.parseDouble(array[1]));
			list.add(e);
		}	
		in.close();		 
		return list;
	}

	public static void main(String[] args) throws IOException {
		BRTree r = construct();
		LinList linList = new LinList(construct_array());
		double xl, xh, yl, yh;
		double size = 0.01;
		long measurement = 1000;
		int k = 3;
		long s1 = 0, s2 = 0, s3 = 0,s4 = 0, s5 = 0;
		long cpuTimeElapsed;
		/* test begins */
		System.out.println("\nTest is started with (" + size + " X " + size + ", " + measurement + " times)"); 
		for (int i = 0; i<measurement; i++){
			xl = Math.random();
			yl = Math.random();
			xh = xl+size;
			yh = yl+size;

			Point q = new Point(xl, yl);

			cpuTimeElapsed = System.nanoTime();
			ArrayList<BEntry> list1 = linList.kNNSearch(q, k);
			linList.l.addAll(list1);
			cpuTimeElapsed = System.nanoTime()-cpuTimeElapsed;


			s1 += cpuTimeElapsed;

			cpuTimeElapsed = System.nanoTime();
			ArrayList<BEntry> list2 = r.kNNSearch(q, k);
			cpuTimeElapsed = System.nanoTime()-cpuTimeElapsed;

			for (int j=0; j < k; j++) {
				BEntry result1 = list1.get(j);
				BEntry result2 = list2.get(j);
				if (!(result1.x.l == result2.x.l && result1.x.h == result2.x.h &&
						result1.y.l == result2.y.l && result2.y.h == result2.y.h)) {
					System.err.println("Error!!!!");
					System.out.println("q:" + q);
					System.out.println("linlist:" + result1 + " dist=" + result1.dist);
					System.out.println("rtree:" + result2+ " dist=" + result2.dist);
				}
			}
			s3 += r.nodeCount; 	s4 += r.leafCount;

			s2 += cpuTimeElapsed;


			//			cpuTimeElapsed = System.nanoTime();
			//			ArrayList<Entry> result1 = linList.rangeSearch(xl, xh, yl, yh);
			//			cpuTimeElapsed = System.nanoTime()-cpuTimeElapsed;
			//
			//			
			//			s1 += cpuTimeElapsed;
			//
			//			cpuTimeElapsed = System.nanoTime();
			//			ArrayList<Entry> result2 = r.rangeSearch(xl, xh, yl, yh);
			//			cpuTimeElapsed = System.nanoTime()-cpuTimeElapsed;
			//			
			//			if (result1.size() != result2.size()) {
			//				System.out.println("Error!!!!");
			//				for (Entry e: result1) {
			//					System.out.println("Search x: "+e.x.l+" "+e.x.h+ ", y: "+e.y.l+" "+e.y.h);
			//				}				
			//				System.out.print("total: "+(result1.size())+"\n"); 
			//				for (Entry e: result2) {
			//					System.out.println("Search x: "+e.x.l+" "+e.x.h+ ", y: "+e.y.l+" "+e.y.h);
			//				}				
			//				System.out.print("total: "+(result2.size())+"\n"); 
			//			}
			//			s3 += r.nodeCount; 	s4 += r.leafCount; s5 += result1.size();
			//
			//			s2 += cpuTimeElapsed;
		}
		System.out.print("Normal search avg. "+s1/measurement+" ns Index search avg. "+s2/measurement+" ns\n");
		System.out.print("List node access " + linList.size()/BRTree.M + "\n");
		System.out.print("Nonleaf access avg. "+s3/measurement+" Leaf access avg. "+s4/measurement+"\n");
		System.out.print("Num of result avg. "+(double)s5/(double)measurement+ "\n");

	}

}

class LinList {
	ArrayList<BEntry> l;

	public LinList (ArrayList<BEntry> l) {
		this.l = l;
	}

	public int size() {
		return l.size();
	}

	public ArrayList<BEntry> rangeSearch(double xl, double xh, double yl, double yh) {
		ArrayList<BEntry> result = new ArrayList<BEntry>(); 
		for (BEntry e : l){
			if (((!(xh<e.x.l || xl>e.x.h))) && (!(yl>e.y.h || yh<e.y.l))){ 
				result.add(e);
			}
		}
		return result;
	}

	public BEntry nextNN(Point q) {
		BEntry nn = null;
		double dist, mindist = Double.MAX_VALUE;
		for (BEntry e: l) {
			dist = e.distTo(q);
			if (mindist > dist) {
				nn = e;
				mindist = dist;
			}
		}
		nn.dist = mindist;
		return nn;
	}
	
	public ArrayList<BEntry> kNNSearch(Point q, int k) {
		ArrayList<BEntry> knns = new ArrayList<BEntry>();
		for (int i=0; i < k; i++){
			BEntry next = nextNN(q);
			knns.add(next);
			l.remove(next);
		}
		return knns;
	}
}
