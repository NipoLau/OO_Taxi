package oo.taxi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
class mapInfo{
	int[][] map=new int[80][80];
	/**
	*@Requires:String类型的地图路径,System.in
	*@Modifies:System.out,map[][]
	*@Effects:从文件中读入地图信息，储存在map[][]中
	*/
	public void readmap(String path){//读入地图信息
		Scanner scan=null;
		File file=new File(path);
		if(file.exists()==false){
			System.out.println("地图文件不存在,程序退出");
			System.exit(1);
			return;
		}
		try {
			scan = new Scanner(new File(path));
		} catch (FileNotFoundException e) {
			
		}
		for(int i=0;i<80;i++){
			String[] strArray = null;
			try{
				strArray=scan.nextLine().split("");
			}catch(Exception e){
				System.out.println("地图文件信息有误，程序退出");
				System.exit(1);
			}
			for(int j=0;j<80;j++){
				try{
					this.map[i][j]=Integer.parseInt(strArray[j]);
				}catch(Exception e){
					System.out.println("地图文件信息有误，程序退出");
					System.exit(1);
				}
			}
		}
		scan.close();
	}
}

class lightInfo{
	int[][] lightMap = new int[80][80];
	/**
	*@Requires:String类型的地图路径,System.in
	*@Modifies:System.out,map[][]
	*@Effects:从文件中读入地图信息，储存在lightMap[][]中
	*/
	public void readMap(String path){
		Scanner scan = null;
		File file = new File(path);
		if (!file.exists()){
			System.out.println("LightMap not Found !");
			System.exit(1);
			return;
		}
		try{
			scan = new Scanner(file);
		}catch(Exception e){}
		for (int i = 0 ; i < 80 ; i++){
			String[] strArray = null;
			try{
				strArray = scan.nextLine().split("");
			}catch(Exception e){
				System.out.println("Bad LightMap File !");
				System.exit(1);
				return;
			}
			for (int j = 0 ; j < 80 ; j++){
				try{
					int hasLight = Integer.parseInt(strArray[j]);
					lightMap[i][j] = hasLight;
				}catch(Exception e){
					System.out.println("Bad LightMap File !");
					System.exit(1);
					return;
				}
			}
		}
	}
}
public class Main {
	private static final int MAPSIZE = 80;
	private static final int TAXINUM = 100;
	private static final int SPECNUM = 30;
	private static final long SYSTIME = System.currentTimeMillis();
	private static final long SLEEPGAP = 10;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TrackTaxi[] tracktaxi = new TrackTaxi[SPECNUM];
		SafeFile file = new SafeFile("log.txt");
		SafeFile detail = new SafeFile("detail.txt");
		mapInfo mi = new mapInfo();
		lightInfo li = new lightInfo();
		TaxiGUI gui = new TaxiGUI();
		TrafficLight[][] lightGroup = new TrafficLight[MAPSIZE][MAPSIZE];
		TaxiData taxidata = new TaxiData(TAXINUM);
		EventManager manager = new EventManager();
		mi.readmap("map.txt");
		li.readMap("lightMap.txt");
		FlowMap flowmap = new FlowMap(mi.map);
		
		(new Thread(flowmap)).start();
		Shortpath shortpath = new Shortpath(mi.map, flowmap);               //专用类获取最短路径及其上的所有点
		RoadCtr roadctr = new RoadCtr(mi.map, gui, manager);
		gui.LoadMap(mi.map, MAPSIZE);
		for (int i = 0 ; i < MAPSIZE ; i++){
			for (int j = 0 ; j < MAPSIZE ; j++){
				if (li.lightMap[i][j] == 1 && canCreateLight(mi.map, i, j) == false) li.lightMap[i][j] = 0;
				lightGroup[i][j] = new TrafficLight(i, j, li.lightMap[i][j], gui);
				(new Thread(lightGroup[i][j])).start();
			}
		}
		for (int i = 0 ; i < TAXINUM ; i++) {
			if (i >= SPECNUM) new Thread(new Taxi(i, taxidata, gui, mi.map, shortpath, flowmap, manager, detail, lightGroup, 0)).start();
			else {
				tracktaxi[i] = new TrackTaxi(i, taxidata, gui, copyMap(mi.map, MAPSIZE), shortpath, flowmap, manager, detail, lightGroup, 1);
				new Thread(tracktaxi[i]).start();
			}
			try{Thread.sleep(SLEEPGAP);}catch(Exception e){}
		}
		Reqqueue reqQueue = new Reqqueue();
		FileHandler filehandler = new FileHandler(mi, flowmap, taxidata, gui, reqQueue, SYSTIME, li);
		TestCode testcode = new TestCode(tracktaxi);
		inputHandler handler = new inputHandler(reqQueue, SYSTIME, roadctr, filehandler, testcode);
	    (new Thread(handler)).start();
	    TaxiSystem taxisystem = new TaxiSystem(taxidata, reqQueue, TAXINUM, gui, file, SYSTIME);
	    (new Thread(taxisystem)).start();
	}
	
	private static boolean canCreateLight(int[][] map, int i, int j)
	/**
	 * @REQUIRES : \all i, j; 0<=i, j < 80
	 * @MODIFIES : None
	 * @EFFECTS : \result = (branch >= 3) ? true : false;
	 */
	{
		int branch = 0;
		if (i - 1 >=0 && (map[i - 1][j] == 2 || map[i - 1][j] == 3)) branch++;
		if (j - 1 >= 0 && (map[i][j - 1] == 1 || map[i][j - 1] == 3)) branch++;
		switch (map[i][j]){
		case 1 : branch++;break;
		case 2 : branch++;break;
		case 3 : branch+=2;break;
		default : break;
		}
		if (branch >= 3) return true;
		return false;
	}
	
	private static int[][] copyMap(int[][] map, int mapsize)
	/**
	 * @REQUIRES: mapsize == 80 && map.size == 80
	 * @MODIFIES: None
	 * @EFFECTS: \result = a copy of map
	 */
	{
		int[][] cpmap = new int[mapsize][mapsize];
		for (int i = 0 ; i < mapsize ; i++){
			for (int j = 0 ; j < mapsize ; j++){
				cpmap[i][j] = map[i][j];
			}
		}
		return cpmap;
	}
}