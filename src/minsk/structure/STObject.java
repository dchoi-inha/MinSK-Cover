
package minsk.structure;

import java.util.Comparator;
import java.util.HashSet;

import minsk.rtree.Entry;


/**
 * @author Dong-Wan Choi
 * @date 2014. 9. 17.
 */
public class STObject {

	public Point loc; // original location
	public HashSet<String> text;
	
	
	public STObject(double x, double y)
	{
		loc = new Point(x, y);
		text = new HashSet<String>();
	}
	
	public STObject(double x, double y, HashSet<String> argStr)
	{
		loc = new Point(x, y);
		text = new HashSet<String>();
		text.addAll(argStr);
	}
	
	public STObject(STObject obj, double x, double y)
	{
		loc = new Point(obj.loc.x, obj.loc.y);
		text = new HashSet<String>();
		text.addAll(obj.text);
	}
	
	
	public STObject(STObject obj) 
	{
		this.loc = new Point(obj.loc.x, obj.loc.y);
		this.text = new HashSet<String>();
		this.text.addAll(obj.text);
	}
	


	public String toString()
	{
//		return loc.toString() ;

		return loc.toString() + " " + text.toString();
	}
	
	public static Comparator<STObject> CompareLoc = new Comparator<STObject>() {
		public int compare(STObject o1, STObject o2) {
			if (o1.loc.x == o2.loc.x) return (o1.loc.y - o2.loc.y >= 0? 1 : -1);
			else return (o1.loc.x - o2.loc.x>=0? 1: -1);
		}
	};
}
