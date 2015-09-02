package minsk;




/**
 * @author Dong-Wan Choi
 * @date 2014. 9. 17.
 */
public class Env {

	public static final String HomeDir = System.getProperty("user.home") + "/study/exp/minsk/";
	
	public static double MaxCoord = 1.0;
	
	public static final int B = 1024; // bytes

	public static Words W = new Words();

	public static double ep = 0.01;
	
	public static final double PF = 0.01;
}
