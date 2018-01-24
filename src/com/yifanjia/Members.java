package com.yifanjia;

import java.io.*;
import java.util.*;

import javax.sound.*;
import javax.sound.sampled.*;
//�ظ���
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
//������������
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
		//���ǻ���
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
//��¼��,ͬʱҲ���Ա�����ҵ�����
class Record {
	//��¼ÿ���ж��ٵ���
	private static int enimynum = 3;
	//���ж��ٿ��õ�̹��
	private static int mylife = 3;
	//��¼�ܹ������˶��ٵ���
	private static int alldead = 0;
	//���ļ��лظ���¼��
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
	//��¼�˳�ʱ����Ϣ
	public void savegame() {
		try {
			fw = new FileWriter("E:\\record.txt");
			bw = new BufferedWriter(fw);
			//������˵�̹�����������ꡢ����
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
	//���ٵ���
	public static void reduceenimy() {
		enimynum--;
	}
	public static void adddead() {
		alldead++;
	}
}
//��ը��
class Bomb {
	int x;
	int y;
	//ը��������
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
//�ӵ���
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
	//�ӵ���ʱ����?
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
			//�жϸ��ӵ��Ƿ�����
			if(x > 800 || x < 0 || y < 0 || y > 600) {
				this.isalive = false;
				break;
			}	
		}
	}
}
//̹����
class Tank {
	//�����ٶ�
	int speed = 2;
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	boolean islive = true;
	//̹�˵�����
	int type = 0;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	//̹�˵ĺ�����
	int x = 0;
	//̹�˵�������
	int y = 0;
	//̹�˷���  
	//0:��   1:��  2:�� 3:��
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
//�ҵ�̹��
class MyTank extends Tank {
	Vector<Shot> ss = new Vector<>();
	Shot s = null;
	public MyTank(int x, int y) {
		super(x,y);
		this.setType(0);
	}
	//����
	public void shotenimy() {
		switch(this.direct) {
		case 0:
			//����һ���ӵ�
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
	//̹���ƶ�
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
//���˵�̹�ˣ��ѵ��˵�̹�������߳�
class EnimyTank extends Tank implements Runnable{
	int times = 0;
	//����һ�����������Է��ʵ�Mypanel�����еĵ���̹��
	Vector<EnimyTank> ets = new Vector<>();
	
	//����һ���ӵ�����
	Vector<Shot> ss = new Vector<>();
	//��������ӵ�Ӧ����̹�˸ոմ������ӵ�������
	public EnimyTank(int x, int y) {
		super(x,y);
	}
	//�õ�Mypanel�ĵ���̹������
	public void setEts(Vector<EnimyTank> vv) {
		this.ets = vv;
	}
	//�ж��Ƿ������˱�ĵ���̹��
	public boolean isTouchOtherTank() {
		boolean b = false;
		switch(this.direct) {
		case 0:
			//��
			for(int i=0;i<ets.size();i++) {
				EnimyTank et = ets.get(i);
				if(et != this) {
					//������˵ķ������»�����
					if(et.direct == 0 || et.direct == 1) {
						if(this.x >= et.x && this.x <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}
						if(this.x + 20 >= et.x && this.x + 20 <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}

					}
					//������˵ķ������������
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
			//��
			for(int i=0;i<ets.size();i++) {
				EnimyTank et = ets.get(i);
				if(et != this) {
					//������˵ķ������»�����
					if(et.direct == 0 || et.direct == 1) {
						if(this.x >= et.x && this.x <= et.x + 20 && this.y + 30 >= et.y && this.y + 30 <= et.y + 30) {
							return true;
						}
						if(this.x + 20 >= et.x && this.x + 20 <= et.x + 20 && this.y + 30 >= et.y && this.y + 30 <= et.y + 30) {
							return true;
						}

					}
					//������˵ķ������������
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
			//��
			for(int i=0;i<ets.size();i++) {
				EnimyTank et = ets.get(i);
				if(et != this) {
					//������˵ķ������»�����
					if(et.direct == 0 || et.direct == 1) {
						if(this.x >= et.x && this.x <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}
						if(this.x >= et.x && this.x <= et.x + 20 && this.y + 20 >= et.y && this.y + 20 <= et.y + 30) {
							return true;
						}

					}
					//������˵ķ������������
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
			//��
			for(int i=0;i<ets.size();i++) {
				EnimyTank et = ets.get(i);
				if(et != this) {
					//������˵ķ������»�����
					if(et.direct == 0 || et.direct == 1) {
						if(this.x + 30 >= et.x && this.x + 30 <= et.x + 20 && this.y >= et.y && this.y <= et.y + 30) {
							return true;
						}
						if(this.x + 30 >= et.x && this.x + 30 <= et.x + 20 && this.y + 20 >= et.y && this.y + 20 <= et.y + 30) {
							return true;
						}

					}
					//������˵ķ������������
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
				//̹������������
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
				//��
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
				//��
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
				//��
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
							//����һ���ӵ�
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
			//��̹�˲���һ������ķ���
			direct = (int)(Math.random() * 4);
			//�жϵ����Ƿ�����
			if(this.islive == false) {
				//̹���˳��߳�
				break;
			}
		}
	}
}