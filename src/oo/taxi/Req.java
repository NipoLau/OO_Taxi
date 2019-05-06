package oo.taxi;

public class Req 
/**
 * @OVERVIEW :
 * Req class stores all data of Req and keep it thread-safe
 */
{
	private int[][] src;
	private int[][] dst;
	private long time;
	
	public Req(int x1, int y1, int x2, int y2, long _time){
		dst = new int[1][2];
		src = new int[1][2];
		src[0][0] = x1; 
		src[0][1] = y1;
		dst[0][0] = x2; 
		dst[0][1] = y2;
		time = _time;
	}
	
	public boolean repOK(){
		if (time < 0 || (src[0][0] == dst[0][0] && src[0][1] == dst[0][1])) return false;
		return true;
	}
	
	/**
	 * @EFFECTS : \result = src;
	 */
	public synchronized int[][] getSrcLoc(){
		return src;
	}
	
	/**
	 * @EFFECTS : \result = dst;
	 */
	public synchronized int[][] getDstLoc(){
		return dst;
	}
	
	/**
	 * @EFFECTS : \result = time;
	 */
	public synchronized long getTime(){
		return time;
	}
	
	/**
	 * @EFFECTS : \result = String;
	 */
	public synchronized String toString(){
		return "[CR,(" + src[0][0] + "," + src[0][1] + ")" + "," + "(" + dst[0][0] + "," + dst[0][1] + ")" + "]";
	}
}
