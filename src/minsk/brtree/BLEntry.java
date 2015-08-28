package minsk.brtree;

import minsk.structure.STObject;

/**
 * @author Dong-Wan Choi at SFU, CA
 * @class LEntry, this is the leaf entry class
 * @date 2015-08-26
 *
 */
public class BLEntry extends BEntry {

	public STObject obj;
	
	public BLEntry(double xl, double xh, double yl, double yh, STObject o) {
		super(xl, xh, yl, yh);
		obj = o;
	}

}
