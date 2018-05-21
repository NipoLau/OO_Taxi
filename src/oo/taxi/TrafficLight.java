package oo.taxi;

import java.awt.Point;

public class TrafficLight implements Runnable
/**
 * @OVERVIEW : 
 * manage traffic light, if a point has traffic light, will auto change light color every 1000ms
 */
{
	private int posX;
	private int posY;
	private LIGHT updown;
	private LIGHT leftright;
	private int hasLight;
	private static final int SWITCHGAP = 1000;
	enum LIGHT {GREEN, RED};
	private TaxiGUI gui;
	
	public TrafficLight(int _pox, int _poy, int _hasLight, TaxiGUI _gui){
		posX = _pox;
		posY = _poy;
		gui = _gui;
		hasLight = _hasLight;
		if (hasLight == 1){
			switch ((int)(Math.random()*2)){
			case 0 : updown = LIGHT.GREEN; leftright = LIGHT.RED; gui.SetLightStatus(new Point(posX, posY), 2); break;
			case 1 : updown = LIGHT.RED; leftright = LIGHT.GREEN; gui.SetLightStatus(new Point(posX, posY), 1); break;
			}
		}
		else gui.SetLightStatus(new Point(posX, posY), 0);
	}
	
	public boolean repOK(){
		if (posX < 0 || posX >= 80 || posY < 0 || posY >= 80) return false;
		return true;
	}
	
	public void run()
	/**
	 * @REQUIRES : None
	 * @MODIFIES : None
	 * @EFFECTS : switch traffic light status every SWITCHGAP
	 */
	{
		if (hasLight == 1){
			while (true){
				try{Thread.sleep(SWITCHGAP);}catch(Exception e){}
				if (updown == LIGHT.GREEN){
					updown = LIGHT.RED;
					leftright = LIGHT.GREEN;
					 gui.SetLightStatus(new Point(posX, posY), 1);
				}else{
					updown = LIGHT.GREEN;
					leftright = LIGHT.RED;
					gui.SetLightStatus(new Point(posX, posY), 2);
				}
			}
		}
	}
	
	public synchronized boolean hasLight()
	/**
	 * @EFFECTS : \result = (hasLight == 0) ? false : true;
	 */
	{
		switch(hasLight){
		case 0 : return false;
		case 1 : return true;
		default : return false;
		}
	}
	
	public LIGHT getudLight()
	/**
	 * @EFFECTS : \result = updown
	 */
	{
		return updown;
	}
	
	public LIGHT getlrLight()
	/**
	 * @EFFECTS : \result = leftright
	 */
	{
		return leftright;
	}
	
	public synchronized int[][] getPos()
	/**
	 * @EFFECTS : get traffic light's position coordinate
	 */
	{
		int[][] pos = new int[1][2];
		pos[0][0] = posX;
		pos[0][1] = posY;
		return pos;
	}
}