package minsk.util;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;


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

	
	public static long getCpuTime() {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported()?
				bean.getCurrentThreadCpuTime()/1000000L: 0L;
	}
	
}
