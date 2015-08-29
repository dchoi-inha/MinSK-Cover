package minsk.util;


/**
 * @author Dongwan Choi
 * @date 2011. 12. 2.
 */
public class Bitmap {
	private byte[] bits;
	private int len;
	
	public Bitmap(int len) {
		int index = (int) Math.ceil((double)len/(double)Byte.SIZE);
		bits = new byte[index];
		this.len = len;
	}
	
	public Bitmap(byte[] bits) {
		this.bits = bits;
		this.len = bits.length * Byte.SIZE;
	}
	
	public Bitmap(Bitmap b) {
		len = b.len;
		int index = (int) Math.ceil((double)len/(double)Byte.SIZE);
		bits =  new byte[index];
		for ( int i = 0; i < index; i++ )
			bits[i] = (byte) b.bits[i];
	}
	
	public void set(int pos) {
		if (pos < len) {
			int index = pos/Byte.SIZE;
			int offset = pos%Byte.SIZE;
			
			byte tmp = bits[index];
			bits[index] = (byte) (tmp | ((byte)0x01 << offset));
		}
		else {
			Debug._Error(this, "invalid bit position");
		}
	}
	
	public void unset(int pos) {
		if (pos < len) {
			int index = pos/Byte.SIZE;
			int offset = pos%Byte.SIZE;
			
			byte tmp = bits[index];
			bits[index] = (byte) (tmp & ~((byte)0x01 << offset));
		}
		else {
			Debug._Error(this, "invalid bit position");
		}
	}
	
	public void setAll() {
		
		for ( int i = 0; i < len/Byte.SIZE; i++ )
			bits[i] = (byte)0xff;
		if ( len%Byte.SIZE > 0 ) {
			for (int pos = (len/Byte.SIZE)*Byte.SIZE; pos < len; pos++)
				set(pos);
		}
	}
	
	public boolean intersect(Bitmap bmp) {
		Bitmap tmp = new Bitmap(this);
		tmp.and(bmp);
		int index = (int) Math.ceil((double)len/(double)Byte.SIZE);

		for (int i=0; i < index; i++) {
			if (bits[i] != (byte)0x00)
				return true;
		}
		return false;
	}
	
	public void and(Bitmap bmp) {
		if (len != bmp.len) {
			Debug._Error(this, "AND cannot be done for bitmaps of different lengths");
			System.exit(0);
		}
		int index = (int) Math.ceil((double)len/(double)Byte.SIZE);

		for ( int i = 0; i < index; i++ )
			bits[i] = (byte) (bits[i] & bmp.bits[i]);
	}

	public void or(Bitmap bmp) {
		if (len != bmp.len) {
			Debug._Error(this, "OR cannot be done for bitmaps of different lengths");
			System.exit(0);
		}
		int index = (int) Math.ceil((double)len/(double)Byte.SIZE);

		for ( int i = 0; i < index; i++ )
			bits[i] = (byte) (bits[i] | bmp.bits[i]);
	}
	
	public byte[] toByteArray() {
		return bits;
	}

	public boolean get(int pos) {
		if (pos < len) {
			int index = pos/Byte.SIZE;
			int offset = pos%Byte.SIZE;
			
			byte tmp = bits[index];
			return ( (tmp & ((byte)0x01 << offset)) != 0 );			
		}
		else {
			Debug._Error(this, "invalid bit position");
			return false;
		}
	}
	
	public int size() {
		return len;
	}
	
	public String toString() {
		String s="";
		for (int i=len-1; i >= 0; i--) {
			if (get(i)) s += "1";
			else s+= "0";
		}
		return s;
	}
}

