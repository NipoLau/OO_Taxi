package oo.taxi;

import java.util.Vector;

public class Shortpath 
/**
 * @OVERVIEW :
 * return shortest path between two points
 */
{
	
	private int[][] map;
	FlowMap mapFlow;        //权值
	private static final int INFINITY = Integer.MAX_VALUE;
	private static final int MAPSIZE = 80;
	
	public Shortpath(int[][] _map, FlowMap _mapFlow){
		map = _map;
		mapFlow = _mapFlow;
	}
	
	public boolean repOK(){
		return true;
	}
	
	/**
	 * @REQUIRES: (\all int x1, y1, x2, y2; 0<=x1,y1,x2,y2<80);
	 *@MODIFIES: None;
	 *@EFFECTS: 
	 *(\exist one shortest path) ==> ArrayList<int[][]> contains points of shortest path;
	 *(\exist more than one shortest path) ==> ArrayList<int[][]> contains shortest path of lowest flow;
	 */
	public synchronized int getShortestPath(int x1, int y1, int x2, int y2, int[] path)
	{
		int[][] D = new int[MAPSIZE*MAPSIZE][MAPSIZE*MAPSIZE];
		int[][] graph = new int[MAPSIZE*MAPSIZE][MAPSIZE*MAPSIZE]; graph = getGraph(graph, this.map);
		int[] offset = new int[] { 0, 1, -1, 80, -80 };
		Vector<node> queue = new Vector<node>();
		boolean[] view = new boolean[6405];
		for (int i = 0; i < 6400; i++) {
			for (int j = 0; j < 6400; j++) {
				if (i == j) {
					D[i][j] = 0;
				} else {
					D[i][j] = graph[i][j];// 赋初值
				}
			}
		}
		int x = x2 * 80 + y2;// 开始进行单点bfs
		for (int i = 0; i < 6400; i++)
			view[i] = false;
		queue.add(new node(x, 0));
		while (queue.size() > 0) {
			node n = queue.get(0);
			view[n.NO] = true;
			for (int i = 1; i <= 4; i++) {
				int next = n.NO + offset[i];
				if (next >= 0 && next < 6400 && view[next] == false && graph[n.NO][next] == 1) {
					path[next]=n.NO;
					view[next] = true;
					queue.add(new node(next, n.depth + 1));// 加入遍历队列
					D[x][next] = n.depth + 1;
					D[next][x] = n.depth + 1;
				}
			}
			queue.remove(0);// 退出队列
		}
		// 检测孤立点
		int count = 0;
		for (int i = 0; i < 6400; i++) {
			if (D[i][x] == gv.MAXNUM) {
				count++;
			}
		}
		if (count > 0) {
			System.out.println("地图并不是连通的,程序退出");
			System.exit(1);
		}
		return D[x2 * MAPSIZE + y2][x1 * MAPSIZE + y1];
	}
	/**
	 * @REQUIRES: _map是连通图, graph.size >= 6400;
	 * @MODIFIES: int[][] graph;
	 * @EFFECTS: 返回由_map构造的邻接矩阵;
	 */
	private int[][] getGraph(int[][] graph, int[][] _map)
	{
		for (int i = 0; i < 6400; i++) {
			for (int j = 0; j < 6400; j++) {
				if (i == j)
					graph[i][j] = 0;
				else
					graph[i][j] = INFINITY;
			}
		}
		for (int i = 0; i < 80; i++) {
			for (int j = 0; j < 80; j++) {
				if (map[i][j] == 1 || map[i][j] == 3) {
					graph[i * 80 + j][i * 80 + j + 1] = 1;
					graph[i * 80 + j + 1][i * 80 + j] = 1;
				}
				if (map[i][j] == 2 || map[i][j] == 3) {
					graph[i * 80 + j][(i + 1) * 80 + j] = 1;
					graph[(i + 1) * 80 + j][i * 80 + j] = 1;
				}
			}
		}
		return graph;
	}
}
