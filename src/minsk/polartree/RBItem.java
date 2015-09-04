package minsk.polartree;

import java.util.Comparator;
import java.util.HashSet;

import minsk.structure.STObject;


/**
 * @author Dong-Wan Choi at SFU, CA
 * @class PItem
 * @date 2015-09-03
 *
 */
public class RBItem {

	double ang; // angle
	double minAng, maxAng; // angle range
	double dist;
	HashSet<String> keywords;	
	STObject obj;
	
	public RBItem() {
		minAng = Double.MAX_VALUE;
		maxAng = Double.MIN_VALUE;
	}
	
	public RBItem(double theta, STObject o) {
		ang = theta;
		minAng = Double.MAX_VALUE;
		maxAng = Double.MIN_VALUE;
		keywords = new HashSet<String>();
		obj = o;
	}
	
	public double getKey() {
		return ang;
	}
	
	public static Comparator<RBItem> ComparePolar = new Comparator<RBItem>() {
		public int compare(RBItem item1, RBItem item2) {
			if (item1.ang > item2.ang) return 1;
			else if (item1.ang < item2.ang) return -1;
			else return 0;
		}
	};
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("("+ang+")");
		sb.append("["+minAng+", "+maxAng+"]");
		sb.append(keywords);
		return sb.toString();
	}
	
	public boolean update(RBItem item, HashSet<String> T) {
		minAng = Math.min(minAng, item.ang);
		maxAng = Math.max(maxAng, item.ang);
		keywords.addAll(item.obj.text);
		
		return (keywords.containsAll(T));
	}

}
