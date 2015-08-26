/**
 * Dataset.java, 2014. 9. 17.
 */
package minsk.structure;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Dong-Wan Choi
 * @date 2014. 9. 17.
 */
public class Dataset extends ArrayList<STObject>{
	public Dataset()
	{
		super(); 
	}

	public Dataset(Point [] points)
	{
		super();
		super.clear();
		for (Point p: points)
			super.add(new STObject(p.getX(), p.getY()));
	}
	
	public Dataset(STObject [] objs)
	{
		super();
		super.addAll(Arrays.asList(objs));
	}
	private static final long serialVersionUID = 1L;
}
