package oo.taxi;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileHandler 
/**
 * @OVERVIEW :
 * this class works when tester input Load command
 */
{
	private mapInfo mi;
	private lightInfo li;
	private FlowMap flowmap;
	private TaxiData taxidata;
	private TaxiGUI gui;
	private Reqqueue reqQueue;
	private long SYSTIME;
	public FileHandler(mapInfo _mi, FlowMap _flowmap, TaxiData _taxidata, TaxiGUI _gui, Reqqueue _reqQueue, long _SYSTIME, lightInfo _li){
		mi = _mi;
		flowmap = _flowmap;
		taxidata = _taxidata;
		gui = _gui;
		reqQueue = _reqQueue;
		SYSTIME = _SYSTIME;
		li = _li;
	}
	
	public boolean repOK(){
		if (mi.map == null || li.lightMap == null || flowmap == null || taxidata == null || gui == null || reqQueue.size() < 0) return false;
		return true;
	}
	/**
	 * @REQUIRES : filepath is the format of standard file path && \exist file;
	 * @MODIFIES : taxidata, flowmap, gui, reqQueue, mi;
	 * @EFFECTS : 
	 * (\exist line == "#map") ==> updata map;
	 * (\exist line == "#flow") ==> updata flowmap;
	 * (\exist line == "#taxi") ==> updata taxidata;
	 * (\exist line == "#request") ==> updata reqQueue;
	 */
	public void readFile(String filepath){
		//System.out.println(filepath);
		File file = new File(filepath);
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = "";
			while ((line = reader.readLine()) != null){
				switch (line){
				default : continue;
				case "#map" : {
					line = reader.readLine();
					if (line.equals("#end_map")) break;
					loadMap(line);
					break;
				}
				case "#flow" : {
					while (true){
						line = reader.readLine();
						if (line.equals("#end_flow")) break;
						line = line.replaceAll("[\\(\\)]", "").replaceAll(" ", ",");
						String[] ele = line.split(",");
						setFlow(Integer.parseInt(ele[0]),Integer.parseInt(ele[1]),Integer.parseInt(ele[2]),Integer.parseInt(ele[3]),Integer.parseInt(ele[4]));
					}
					break;
				}
				case "#light" : {
					while (true){
						line = reader.readLine();
						if (line.equals("#end_light")) break;
						loadLight(line);
					}
					break;
				}
				case "#taxi" : {
					while (true){
						line = reader.readLine();
						if (line.equals("#end_taxi")) break;
						line = line.substring(3, line.length()); line = line.replaceAll("[\\(\\)]", "").replaceAll(",", " ");
						String[] ele = line.split(" ");
						setTaxiStatus(Integer.parseInt(ele[0]), Integer.parseInt(ele[1]), Integer.parseInt(ele[2]), Integer.parseInt(ele[3]), Integer.parseInt(ele[4]));
					}
					break;
				}
				case "#request" : {
					while (true){
						line = reader.readLine();
						if (line.equals("#end_request")) break;
						int[][] nums = new int[2][2];
						line = line.replaceAll("[^\\d,]", ""); line = line.substring(1, line.length());
						String[] remains = line.split(",");
						for (int i = 0 ; i < remains.length ; i++){
							switch(i){
							case 0 : nums[0][0] = Integer.parseInt(remains[i]); break;
							case 1 : nums[0][1] = Integer.parseInt(remains[i]); break;
							case 2 : nums[1][0] = Integer.parseInt(remains[i]); break;
							case 3 : nums[1][1] = Integer.parseInt(remains[i]); break;
							}
						}
						setTaxiReq(nums, (System.currentTimeMillis() - SYSTIME)/100);
					}
					break;
				}
				}
			}
			reader.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	/**
	 * @EFFECTS : 
	 * (\exist file) ==> updata mi.map;
	 */
	private void loadMap(String path){
		try{
			mi.readmap(path);
		}catch(Exception e){
			System.out.println("Map Not Found ! ");
		}
	}
	
	private void loadLight(String path)
	/**
	 * @EFFECTS : read lightMap, in fact, this function is useless since thread has already been created
	 */
	{
		try{
			li.readMap(path);
		}catch (Exception e){
			System.out.println("Map Not Found ! ");
		}
	}
	
	/**
	 * @EFFECTS : 
	 * updata flowmap;
	 */
	private void setFlow(int x1, int y1, int x2, int y2, int value){
		flowmap.setRoadFlow(x1, y1, x2, y2, value);
	}
	
	/**
	 * @EFFECTS : 
	 * updata taxidata;
	 */
	private void setTaxiStatus(int taxiid, int status, int credit, int x, int y){
		//System.out.println(taxiid + "," + status + "," + credit + "," + x + "," + y);
		//0 stopping
		//2 waiting
		//1 serving
		switch (status){
		case 1 : {
			int[][] taxiloc = new int[1][2]; taxiloc[0][0] = x; taxiloc[0][1] = y;
			taxidata.setTaxiStat(taxiid, "SERVING");
			taxidata.addTaxiCredit(taxiid, credit);
			taxidata.setTaxiLoc(taxiid, taxiloc);
			taxidata.setforceStatusOn(taxiid);
			gui.SetTaxiStatus(taxiid, new Point(x,y), 1);
			break;
		}
		case 2 : {
			int[][] taxiloc = new int[1][2]; taxiloc[0][0] = x; taxiloc[0][1] = y;
			taxidata.setTaxiStat(taxiid, "WAITING"); 
			taxidata.addTaxiCredit(taxiid, credit);
			taxidata.setTaxiLoc(taxiid, taxiloc);
			taxidata.setforceStatusOn(taxiid);
			gui.SetTaxiStatus(taxiid, new Point(x,y), 2);
			break;
		}
		case 3 : {
			int[][] taxiloc = new int[1][2]; taxiloc[0][0] = x; taxiloc[0][1] = y;
			taxidata.setTaxiStat(taxiid, "STOPPING"); 
			taxidata.addTaxiCredit(taxiid, credit);
			taxidata.setTaxiLoc(taxiid, taxiloc);
			taxidata.setforceStatusOn(taxiid);
			gui.SetTaxiStatus(taxiid, new Point(x,y), 0);
			break;
		}
		}
	}
	
	/**
	 * @EFFECTS : 
	 * updata reqQueue;
	 */
	private void setTaxiReq(int[][] nums, long reqTime){
		reqQueue.enQueue(new Req(nums[0][0], nums[0][1], nums[1][0], nums[1][1], reqTime));
	}
}
