/**
 * Dataset.java, 2014. 9. 17.
 */
package minsk.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import minsk.brtree.BEntry;

/**
 * @author Dong-Wan Choi
 * @date 2014. 9. 17.
 */
public class Dataset extends ArrayList<STObject>{
	public Dataset()
	{
		super(); 
	}
	
	public Dataset(Collection<? extends STObject> c) {
		super(c);
	}

	
	public Dataset(STObject [] objs)
	{
		super();
		super.addAll(Arrays.asList(objs));
	}
	
	
	private static final long serialVersionUID = 1L;
}
