/**
 * @author Dong-Wan Choi
 *
 */

import index.*;
import index.rtree.*;
import gnu.trove.*;

public class Main {


	public static void main(String[] args) {

		int rowCount = 1000;
		int columnCount = 1000;
		int count = rowCount * columnCount;
		long start, end;
		
		SpatialIndex rt = new RTree();
		rt.init(null);

		final Rectangle[] rects = new Rectangle[count];
		int id = 0;
		for (int row = 0; row < rowCount; row++)
			for (int column = 0; column < rowCount; column++) {
				rects[id++] = new Rectangle(row, column, row, column); // 
			}
		
		for (id = 0; id < count; id++) {
			rt.add(rects[id], id);
		}
		
		final Point p = new Point(36.3f, 84.3f);
		
		rt.nearestN(p, new TIntProcedure() {
			public boolean execute(int i) {
				 System.out.println("Rectangle " + i + " " + rects[i] + ", distance=" + rects[i].distance(p));
				 return true;
			}}, 3, Float.MAX_VALUE);

	}

}
