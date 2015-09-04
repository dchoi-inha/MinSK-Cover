package minsk.polartree;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeSet;

import minsk.structure.STObject;
import minsk.util.Util;

/**
 * @author Dong-Wan Choi at SFU, CA
 * @class PolarTree
 * @date 2015-09-03
 *
 */

public class RBTreeTester {

	public static void main(String [] args) {
		double cpuTimeElapsed;

		int len = 1000000;
		RBItem [] items = new RBItem[len];
		RBTree pt = new RBTree();
		TreeSet<RBItem> treeSet = new TreeSet<RBItem>(RBItem.ComparePolar);
		HashSet<RBItem> hashSet = new HashSet<RBItem>();
		Random rand = new Random();
		HashSet<String> T = new HashSet<String>(Arrays.asList(new String [] {"Car", "Link", "Crescent", "Londonderry"}));
		STObject obj = new STObject(1, 0.2, 0.4, T);

		
		for (int i=0; i < len; i++) {
			items[i] = new RBItem(rand.nextInt(360)+rand.nextDouble(), obj);
		}
		
		cpuTimeElapsed = Util.getCpuTime();
		for (int i=0; i < len; i++) {
			pt.insert(items[i], T);
		}
		cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;		
		System.out.println("time for rb-tree: " + cpuTimeElapsed/(double)1000000000 + "secs");
		
		cpuTimeElapsed = Util.getCpuTime();
		for (int i=0; i < len; i++) {
			treeSet.add(items[i]);
		}
		cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;		
		System.out.println("time for TreeSet: " + cpuTimeElapsed/(double)1000000000 + "secs");
		
		cpuTimeElapsed = Util.getCpuTime();
		for (int i=0; i < len; i++) {
			hashSet.add(items[i]);
		}
		cpuTimeElapsed = Util.getCpuTime() - cpuTimeElapsed;		
		System.out.println("time for HashSet: " + cpuTimeElapsed/(double)1000000000 + "secs");

		
//		for (int i=0; i < len; i++) {
//			System.out.println(i + ": " + pt.get(i));
//		}
		
	}
}
