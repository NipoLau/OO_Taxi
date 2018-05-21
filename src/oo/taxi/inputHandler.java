package oo.taxi;

import java.util.Scanner;

public class inputHandler implements Runnable
/**
 * @OVERVIEW : 
 * inputHandler deal with all input from console, first it will use expHandler to test if input has correct format
 */
{
	
	private expHandler handler;
	private Reqqueue reqQueue;
	private long sysStartTime;
	private RoadCtr roadctr;
	private FileHandler filehandler;
	private TestCode testcode;
	
	public inputHandler(Reqqueue _reqQueue, long _sysStartTime, RoadCtr _roadctr, FileHandler _filehandler, TestCode _testcode){
		handler = new expHandler();
		reqQueue = _reqQueue;
		sysStartTime = _sysStartTime;
		roadctr = _roadctr;
		filehandler = _filehandler;
		testcode = _testcode;
	}
	
	public boolean repOK(){
		if (reqQueue.size() < 0) return false;
		return true;
	}
	/**
	 * @MODIFIES : reqQueue;
	 * @EFFECTS : 
	 * (if input is Req, then reqQueue.enQueue(Req));
	 * (if input is Load File, then Load File);
	 * (if input is changing road status, then change road status);
	 */
	public void run(){
			Scanner scan = new Scanner(System.in);
			System.out.println("请输入乘客请求, 格式为 : [CR,(x1,y1),(x2,y2)]");
			long preReqTime = -1;
			int[][] prenums = new int[1][2];
			while (true){
				String thisline = scan.nextLine();
				if (thisline.equals("END")) break;
				if (thisline.equals("TestStart")) {
					testcode.reachdata();
					continue;
				}
				int[][] nums = handler.checkInput(thisline);
				long reqTime = (System.currentTimeMillis() - sysStartTime)/100;
				if (nums == null){
					if (thisline.length() > 4 && thisline.substring(0, 4).equals("Load")){
						thisline = thisline.substring(5, thisline.length());
						filehandler.readFile(thisline);
					}
					else{
						System.out.println("无效请求，已忽略: " + thisline);
						continue;
					}
				}
				else {
					if (nums.length == 2){
						if (preReqTime == -1) {
							reqQueue.enQueue(new Req(nums[0][0], nums[0][1], nums[1][0], nums[1][1], reqTime));
							prenums = nums;
							preReqTime = reqTime;
						}
						else{
							if (preReqTime == reqTime && (prenums[0][0] == nums[0][0] && prenums[0][1] == nums[0][1] && prenums[1][0] == nums[1][0] && prenums[1][1] == nums[1][1])) System.out.println("相同请求,已忽略: " + thisline);
							else{
								reqQueue.enQueue(new Req(nums[0][0], nums[0][1], nums[1][0], nums[1][1], reqTime));
								prenums = nums;
								preReqTime = reqTime;
							}
						}
					}
					else if (nums.length == 3){
						roadctr.setRoadStatus(nums[0][0], nums[0][1], nums[1][0], nums[1][1], nums[2][0]);
						switch(nums[2][0]){
						case 0 : {
							System.out.println("连接 " + "(" + nums[0][0] + "," + nums[0][1] + ")" + " 和 " + "(" + nums[1][0] + "," + nums[1][1] + ")" + " 的道路已关闭");break;
						}
						case 1 : {
							System.out.println("连接 " + "(" + nums[0][0] + "," + nums[0][1] + ")" + " 和 " + "(" + nums[1][0] + "," + nums[1][1] + ")" + " 的道路已打开");break;
						}
						}
					}
				}
			}
			scan.close();
	}
}
