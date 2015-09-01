package minsk.util;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import minsk.Env;
import minsk.Words;
import minsk.docindex.InvertedFile;
import minsk.structure.Circle;
import minsk.structure.Dataset;
import minsk.structure.Group;
import minsk.structure.Point;
import minsk.structure.STObject;


/**
 * Util.java, 2011. 12. 1.
 */

/**
 * @author Dongwan Choi
 * @date 2011. 12. 1.
 */
public class Util {
	
	public static byte[] intToByteArray(int value) {
		ByteBuffer byteBuf = ByteBuffer.allocate(Integer.SIZE/Byte.SIZE);
		byteBuf.order(ByteOrder.LITTLE_ENDIAN);
		return byteBuf.putInt(value).array();
	}
	
	public static String getTodayString() {
		Calendar cal = Calendar.getInstance();
		String today = String.format("%02d%02d%02d%02d%02d%02d", 
									cal.get(Calendar.YEAR), 
									cal.get(Calendar.MONTH),
									cal.get(Calendar.DATE),
									cal.get(Calendar.HOUR),
									cal.get(Calendar.MINUTE),
									cal.get(Calendar.SECOND));
		
		return today;
	}
	
	public static Bitmap getBitmap(HashSet<String> text, int len, HashMap<String, Integer> indices) {
		Bitmap bmp = new Bitmap(len);
		for (String t: text) {
			bmp.set(indices.get(t));
		}
		
		return bmp;
	}
	
	public static ArrayList<String> getText(Bitmap bmp, ArrayList<String> words) {
		ArrayList<String> text = new ArrayList<String>();
		
		for(int i=0; i < bmp.size(); i++)
		{
			if (bmp.get(i))
				text.add(words.get(i));
		}
		
		return text;
	}
	
	public static double diameter(Collection<? extends STObject> c) {
		double max = Double.MIN_VALUE;
		for (STObject o1: c) {
			for (STObject o2: c) {
				double dist = o1.loc.distance(o2.loc);
				if (max < dist)	max = dist;
			}
		}
		return max;
	}
	
	public static Circle findSec(Group g) 
	{
		int n = g.size();					// Number of points 
	    Point[] p = new Point[n];	// points input by the user
	    Point[] b = new Point[3];				// Points on the boundary of the circle

	    int i = 0;
	    for ( STObject o: g ) {
	    	p[i++] = o.loc;
	    }
	    
	    return findSec(n, p, 0, b);
	}	
	// Compute the Smallest Enclosing Circle of the n points in p, 
	// such that the m points in B lie on the boundary of the circle.
	private static Circle findSec(int n, Point[] p, int m, Point[] b)
	{
		Circle sec = new Circle();
		
		// Compute the Smallest Enclosing Circle defined by B
		if(m == 1)
		{
			sec = new Circle(b[0]);
		}
		else if(m == 2)
		{
			sec = new Circle(b[0], b[1]);
		}
		else if(m == 3)
		{
			return new Circle( b[0], b[1], b[2]);
		}
	
		// Check if all the points in p are enclosed
		for(int i=0; i<n; i++)
		{
			if(sec.contain(p[i]) == 1)
			{
				// Compute B <--- B union P[i].
				b[m] = new Point(p[i]);	
				// Recurse
				sec = findSec(i, p, m+1, b);
			}
		}
		
		return sec;
	}
	
	
	public static long getCpuTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported()?
				bean.getCurrentThreadCpuTime(): 0L;
	}
	
	public static HashSet<String> rand(int l, Words w, InvertedFile iv, Dataset db) {
		HashSet<String> T = new HashSet<String>();
		Random r = new Random();
		for (int i = 0; i < l; i++) {
			String s = w.words.get(r.nextInt(w.words.size()));
			if (T.contains(s) || (iv.freq(s) < Env.PF*(double)db.size())) {
				i--;
			} else T.add(s);
		}
		return T;
	}
	
}
