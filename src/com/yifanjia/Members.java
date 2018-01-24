package com.yifanjia;

import java.io.*;
import java.util.*;

import javax.sound.*;
import javax.sound.sampled.*;
//回复点
class Node {
	int x;
	int y;
	int direct;
	public Node(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.direct = z;
	}
}
//播放声音的类
class AePlayWave extends Thread {

	private String filename;
	public AePlayWave(String wavfile) {
		filename = wavfile;

	}

	public void run() {

		File soundFile = new File(filename);

		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		AudioFormat format = audioInputStream.getFormat();
		SourceDataLine auline = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

		try {
			auline = (SourceDataLine) AudioSystem.getLine(info);
			auline.open(format);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		auline.start();
		int nBytesRead = 0;
		//这是缓冲
		byte[] abData = new byte[512];

		try {
			while (nBytesRead != -1) {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
				if (nBytesRead >= 0)
					auline.write(abData, 0, nBytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			auline.drain();
			auline.close();
		}

	}
}
//记录类,同时也可以保存玩家的设置
class Record {
	//记录每关有多少敌人
	private static int enimynum = 3;
	//我有多少可用的坦克
	private static int mylife = 3;
	//记录总共消灭了多少敌人
	private static int alldead = 0;
	//从文件中回复记录点
	static Vector<Node> nodes = new Vector<>();
	private static FileWriter fw = null;
	private static BufferedWriter bw = null;
	private static FileReader fr = null;
	private static BufferedReader br = null;
	private Vector<EnimyTank> ets = new Vector<>();
	public Vector<Node> getNodes() {
		try {
			fr = new FileReader("E:\\record.txt");
			br = new BufferedReader(fr);
			String n = "";
			while((n = br.readLine()) != null) {
				String[] xyz = n.split(" ");
				Node node = new Node(Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2]));
				nodes.add(node);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			// TODO: handle finally clause
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return nodes;
	}
	public void setEts(Vector<EnimyTank> ets) {
		this.ets = ets;
	}
	//记录退出时的信息
	public void savegame() {
		try {
			fw = new FileWriter("E:\\record.txt");
			bw = new BufferedWriter(fw);
			//保存敌人的坦克数量、坐标、方向
			for(int i=0;i<ets.size();i++) {
				EnimyTank et = ets.get(i);
				if(et.islive) {
					String record = "" + et.x + " " + et.y + " " + et.direct;
					bw.write(record + "\r\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		} finally {
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static int getAlldead() {
		return alldead;
	}
	public static void setAlldead(int alldead) {
		Record.alldead = alldead;
	}
	public static int getEnimynum() {
		return enimynum;
	}
	public static void setEnimynum(int enimynum) {
		Record.enimynum = enimynum;
	}
	public static int getMylife() {
		return mylife;
	}
	public static void setMylife(int mylife) {
		Record.mylife = mylife;
	}
	//减少敌人
	public static void reduceenimy() {
		enimynum--;
	}
	public static void adddead() {
		alldead++;
	}
}
//爆炸类
class Bomb {
	int x;
	int y;
	//炸弹的生命
	int life = 12;
	boolean islive = true;
	public Bomb(int x, int y) {
		this.x = x;
		this.y  =y;
	}
	public void lifedown() {
		if(life > 0) 
			life--;
		else
			this.islive = false;
	}
}
//子弹类
class Shot implements Runnable {
	int x;
	int y;
	int direct;
	int speed = 3;
	boolean isalive = true;
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public Shot(int x, int y, int direct) {
		this.x = x;
		this.y = y;
		this.direct = direct;
	}
	//子弹何时死亡?
	public void run() {
		while(true) {
			try {
				Thread.sleep(50);
			} catch (Exception e) {
				// TODO: handle exception
			}
			switch(direct) {
			case 0:
				y -= speed;break;
			case 1:
				y += speed;break;
			case 2:
				x -= speed;break;
			case 3:
				x += speed;break;	
			}
			//判断该子弹是否死亡
			if(x > 800 || x < 0 || y < 0 || y > 600) {
				this.isalive = false;
				break;
			}	
		}
	}
}
//坦克类
class Tank {
	//设置速度
	int speed = 2;
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	boolean islive = true;
	//坦克的种类
	int type = 0;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	//坦克的横坐标
	int x = 0;
	//坦克的纵坐标
	int y = 0;
	//坦克方向  
	//0:上   1:下  2:左 3:右
	int direct = 0;
	public int getDirect() {
		return direct;
	}
	public void setDirect(int direct) {
		this.direct = direct;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public Tank(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
//我的坦克
class MyTank extends Tank {
	Vector<Shot> ss = new Vector<>();
	Shot s = null;
	public MyTank(int x, int y) {
		super(x,y);
		this.setType(0);
	}
	//开火
	public void shotenimy() {
		switch(this.direct) {
		case 0:
			//创建一颗子弹
			s = new Shot(x+10, y-5, 0);
			ss.add(s);
			break;
		case 1:
			s = new Shot(x+10, y+35, 1);
			ss.add(s);
			break;
		case 2:
			s = new Shot(x-5, y+10, 2);
			ss.add(s);
			break;
		case 3:
			s = new Shot(x+35, y+10, 3);
			ss.add(s);
			break;
		}
		Thread t = new Thread(s);
		t.start();
	}
	//坦克移动
	public void moveUp() {
		if(this.y > 0)
			this.y-=this.speed;
	}
	public void moveDown() {
		if(this.y < 600)
			this.y+=this.speed;
	}
	public void moveLeft() {
		if(this.x > 0)
			this.x-=this.speed;
	}
	public void moveRight() {
		if(this.x < 800)
			this.x+=this.speed;
	}
}
//敌人的坦克，把敌人的坦克做成线程
class EnimyTank extends Tank implements Runnable{
	int times = 0;
	//定义一个向量，可以访问到Mypanel上所有的敌人坦克
	Vector<EnimyTank> ets = new Vector<>();
	
	//定义一个子弹向量
	Vector<Shot> ss = new Vector<>();
	//敌人添加子弹应该在坦克刚刚创建和子弹死亡后
	public EnimyTank(int x, int y) {
		super(x,y);
	}
	//得到Mypanel的敌人坦克向量
	public void setEts(Vector<EnimyTank> vv) {
		this.ets = vv;
	}
	//判断是否碰到了别的敌人坦克
	public boolean isTouchOtherTank() {
		boolean b = false;
		switch(this.direct) {
		case 0:
			//上
			for(int i=0;i<ets.size();i++) {
				EnimyTank et = ets.get(i);
				if(et != this) {
					//如果敌人的方向向下或向上
					if(et.direct == 0 || et.direct == 1) {
						if(this.x >= et.x && this.x <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}
						if(this.x + 20 >= et.x && this.x + 20 <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}

					}
					//如果敌人的方向向左或向右
					if(et.direct == 2 || et.direct == 3) {
						if(this.x >= et.x && this.x <= et.x + 30 && this.y >= et.y && this.y <= et.y + 20) {
							return true;
						}
						if(this.x + 20 >= et.x && this.x + 20 <= et.x + 30 && this.y >= et.y && this.y <= et.y + 20) {
							return true;
						}
					}
				}	
			}
			break;
		case 1:
			//下
			for(int i=0;i<ets.size();i++) {
				EnimyTank et = ets.get(i);
				if(et != this) {
					//如果敌人的方向向下或向上
					if(et.direct == 0 || et.direct == 1) {
						if(this.x >= et.x && this.x <= et.x + 20 && this.y + 30 >= et.y && this.y + 30 <= et.y + 30) {
							return true;
						}
						if(this.x + 20 >= et.x && this.x + 20 <= et.x + 20 && this.y + 30 >= et.y && this.y + 30 <= et.y + 30) {
							return true;
						}

					}
					//如果敌人的方向向左或向右
					if(et.direct == 2 || et.direct == 3) {
						if(this.x >= et.x && this.x <= et.x + 30 && this.y + 30 >= et.y && this.y + 30 <= et.y + 20) {
							return true;
						}
						if(this.x + 20 >= et.x && this.x + 20 <= et.x + 30 && this.y + 30 >= et.y && this.y + 30 <= et.y + 20) {
							return true;
						}
					}
				}	
			}
			break;
		case 2:
			//左
			for(int i=0;i<ets.size();i++) {
				EnimyTank et = ets.get(i);
				if(et != this) {
					//如果敌人的方向向下或向上
					if(et.direct == 0 || et.direct == 1) {
						if(this.x >= et.x && this.x <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}
						if(this.x >= et.x && this.x <= et.x + 20 && this.y + 20 >= et.y && this.y + 20 <= et.y + 30) {
							return true;
						}

					}
					//如果敌人的方向向左或向右
					if(et.direct == 2 || et.direct == 3) {
						if(this.x >= et.x && this.x <= et.x + 30 && this.y >= et.y && this.y <= et.y + 20) {
							return true;
						}
						if(this.x >= et.x && this.x <= et.x + 30 && this.y + 20 >= et.y && this.y + 20 <= et.y + 20) {
							return true;
						}
					}
				}	
			}
			break;
		case 3:
			//右
			for(int i=0;i<ets.size();i++) {
				EnimyTank et = ets.get(i);
				if(et != this) {
					//如果敌人的方向向下或向上
					if(et.direct == 0 || et.direct == 1) {
						if(this.x + 30 >= et.x && this.x + 30 <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}
						if(this.x + 30 >= et.x && this.x + 30 <= et.x + 20 && this.y + 20 >= et.y && this.y + 20 <= et.y + 30) {
							return true;
						}

					}
					//如果敌人的方向向左或向右
					if(et.direct == 2 || et.direct == 3) {
						if(this.x + 30 >= et.x && this.x + 30 <= et.x + 30 && this.y >= et.y && this.y <= et.y + 20) {
							return true;
						}
						if(this.x + 30 >= et.x && this.x + 30 <= et.x + 30 && this.y + 20 >= et.y && this.y + 20 <= et.y + 20) {
							return true;
						}
					}
				}	
			}
			break;
		}
		return false;
	}
	@Override
	public void run() {
		while(true) {
			switch(this.direct) {
			case 0:
				//坦克正在向上走
				for(int i=0;i<30;i++) {
					if(y > 0 && !this.isTouchOtherTank())
						y -= speed;
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				break;
			case 1:
				//下
				for(int i=0;i<30;i++) {
					if(y < 600 - 30 && !this.isTouchOtherTank())
						y += speed;
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				break;
			case 2:
				//左
				for(int i=0;i<30;i++) {
					if(x > 0 && !this.isTouchOtherTank())
						x -= speed;
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				break;
			case 3:
				//右
				for(int i=0;i<30;i++) {
					if(x < 800 - 30 && !this.isTouchOtherTank())
						x += speed;
					try {
						Thread.sleep(50);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
				break;
			}
			times++;
			if(times % 2 == 0) {
				if(islive) {
					if(ss.size() < 5) {
						Shot s =null;
						switch(direct) {
						case 0:
							//创建一颗子弹
							s = new Shot(x+10, y-5, 0);
							ss.add(s);
							break;
						case 1:
							s = new Shot(x+10, y+35, 1);
							ss.add(s);
							break;
						case 2:
							s = new Shot(x-5, y+10, 2);
							ss.add(s);
							break;
						case 3:
							s = new Shot(x+35, y+10, 3);
							ss.add(s);
							break;
						}
						Thread t = new Thread(s);
						t.start();
					}
				}	
			}
			//让坦克产生一个随机的方向
			direct = (int)(Math.random() * 4);
			//判断敌人是否死亡
			if(this.islive == false) {
				//坦克退出线程
				break;
			}
		}
	}
}