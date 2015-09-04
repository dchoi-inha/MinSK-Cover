package minsk.polartree;

import java.util.HashSet;

/**
 * Object RBTree : RedBlack Tree Class
 * @author  Dong-Wan Choi
 * from my undergraduate homework
 */
public class RBNode 
{
	public static final int RED = 0;
	public static final int BLACK = 1;
	
	private RBItem item;
	private RBNode leftChild;
	private RBNode rightChild;
	private RBNode parent;
	private int color;
	private static StringBuffer status = new StringBuffer("");
	private static int cnt = 0;
	private static int height = 0;
	
	public RBNode( RBNode parent, int color )
	{
		this.item = null;
		this.leftChild = null;
		this.rightChild = null;
		this.parent = parent;
		this.color = color;
	}

	/**
	 * @param item, to be inserted
	 * @return the root of the red-black tree
	 */
	public RBNode insert( RBItem e, HashSet<String> T)
	{
		if (item != null) item.update(e, T);
		status.setLength(0);
		RBNode K = searchPosition( e.getKey() ); // find the position to be inserted in the tree
		
		K.setItem( e );
		K.leftChild = new RBNode( K, BLACK ); // create child external node
		K.rightChild = new RBNode( K, BLACK ); // create child external node
		
		K = fixRBTree( K );
		if ( K.parent == null )
			return K; // new root
		else
			return this;
	}
	
	/**
	 * @param K, the node retrieved
	 * @return the node where the fixing process is completed
	 */
	public RBNode fixRBTree( RBNode K )
	{
		try {
			if ( K.parent == null ) // if it is root
			{
				K.setColor( BLACK );			
				return K;
			}
			else if ( K.parent.color == BLACK )
			{
				K.setColor( RED );
				return K;
			}
			else 
			{
				K.setColor( RED );
				if ( K.parent.getSibling().color == BLACK )
					return K.reStructure();
				else if ( K.parent.getSibling().color == RED )
					return fixRBTree( K.reColor() );
				else
					return K;
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			System.out.println( " key value when exception  : " + K.getItem().getKey());
			System.out.println( " status  : " + status);
			return null;
		}
	}
	
	/**
	 * reassign colors to 3 relevant nodes (current node, parent node, parent of parent node)
	 * @return the highest level node among 3 nodes
	 */
	public RBNode reColor()
	{
		this.parent.setColor( BLACK );
		this.parent.getSibling().setColor( BLACK );
		this.parent.parent.setColor( RED );
		
		//System.out.println( " --> reColor() return position : " + this.parent.parent.toString() );
		status.setLength( status.length()-2 );
		return this.parent.parent;
	}
	
	/**
	 * restructure 3 relevant nodes (current node, parent node, parent of parent node)
	 * @return the highest level node among 3 nodes after restructuring
	 */
	public RBNode reStructure()
	{
		RBNode L, M, R;
		
		String curState = status.substring(status.length()-2, status.length());
		
		if ( curState.equals( "RR" ))
		{
			L = this.parent.parent;
			M = this.parent;
			R = this;
		}
		else if ( curState.equals( "RL" ))
		{
			L = this.parent.parent;
			M = this;
			R = this.parent;
		}
		else if ( curState.equals( "LL" ))
		{
			L = this;
			M = this.parent;
			R = this.parent.parent;
		}
		else if ( curState.equals( "LR" ))
		{
			L = this.parent;
			M = this;
			R = this.parent.parent;
		}
		else
			L = M = R = null;
		
		if ( !curState.equals( "LL" ))
		{
			L.rightChild = M.leftChild;
			M.leftChild.parent = L;
		}
		if ( !curState.equals( "RR" ))
		{
			R.leftChild = M.rightChild;
			M.rightChild.parent = R;
		}
		
		M.leftChild = L;
		M.rightChild = R;
		
		if ( curState.equals( "RL" ) || curState.equals( "RR" ) )
			M.parent = L.parent;
		else
			M.parent = R.parent;
			
		if ( M.parent != null )
		{
			String prevState = status.substring(status.length()-3, status.length()-2);
			if ( prevState.equals("R"))
				M.parent.rightChild = M;
			else
				M.parent.leftChild = M;
		}
		L.parent = M;
		R.parent = M;
		
		L.setColor( RED );
		M.setColor( BLACK );
		R.setColor( RED );
		
		return M;		
	}
	
	/**
	 * find the position (external node) for a new item being inserted
	 * @param key, key value to be searched for
	 * @return
	 */
	public RBNode searchPosition( double key )
	{
		
		if ( this.item == null ) // return if this is external node
			return this;
		
		if ( this.rightChild != null && key >= this.item.getKey() )
		{
			status.append( "R" );
			return this.rightChild.searchPosition( key );
		}
		else if ( this.leftChild != null && key < this.item.getKey() )
		{
			status.append( "L" );
			return this.leftChild.searchPosition( key );
		}
		else
			return this;		
	}
	
	/**
	 * print out all the items having the same key
	 * @param key, key value to be searched for
	 */
	public void printEqualItem( double key )
	{
		if ( this.item == null ) 
			return; // return if this is external node
		
		if ( key == this.item.getKey() )
			System.out.println( this.item.toString() + " (" + Integer.toString(height) + ") " );
		
		if ( this.rightChild != null )
		{
			height++; 
			rightChild.printEqualItem(key);
			height--; 
		}
		if ( this.leftChild != null )
		{
			height++;
			leftChild.printEqualItem(key);
			height--;
		}
	}
	
	/**
	 * @return the sibling node if exists. Otherwise, return null
	 */
	public RBNode getSibling()
	{
		if ( this.parent == null )
			return null;
		if ( this.parent.leftChild == this )
			return this.parent.rightChild;
		else if ( this.parent.rightChild == this )
			return this.parent.leftChild;
		return null;		
	}
	
	/**
	 * @param seq, rank value
	 * @return the node whose rank equals seq
	 */
	public RBNode getNode( int seq )
	{
		RBNode tmp = null;
		
		if ( this.item != null )
		{
			tmp = this.leftChild.getNode( seq );
			if ( tmp != null )
				return tmp;
			if ( cnt == seq )
			{
				cnt = 0;
				return this;
			}
			cnt++;
			tmp = this.rightChild.getNode( seq );
			if ( tmp != null )
				return tmp;
		}
		return null;
	}
	
	public String toString()
	{
		String [] colorStr = new String[]{ "Red", "Black" };
		
		if ( item == null )
			return "External Node" + "(" + colorStr[this.color] + ")";
		else 
		{
			String nodeInfo = item.toString() + "(" + colorStr[this.color] + ")";
			
			return nodeInfo;
		}
	}
	public int getColor() 
	{
		return color;
	}
	public void setColor(int color) 
	{
		this.color = color;
	}
	public RBItem getItem() 
	{
		return item;
	}
	public void setItem(RBItem item) 
	{
		this.item = item;
	}
	public RBNode getLeftChild() 
	{
		return leftChild;
	}
	public void setLeftChild(RBNode leftChild) 
	{
		this.leftChild = leftChild;
	}
	public RBNode getParent() 
	{
		return parent;
	}
	public void setParent(RBNode parent) 
	{
		this.parent = parent;
	}
	public RBNode getRightChild() 
	{
		return rightChild;
	}
	public void setRightChild(RBNode rightChild) 
	{
		this.rightChild = rightChild;
	}
}
