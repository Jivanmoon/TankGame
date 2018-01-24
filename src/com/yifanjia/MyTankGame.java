/**
 * 坦克游戏
 * 1、画出坦克
 * 2、坦克可以上下左右行走
 * 3、坦克可以发子弹(最多五颗)
 * 4、实敌方坦克的自主移动和坦克被击中死亡
 * 5、防止敌人坦克重叠
 * 6、可以分关
 * 7、可以暂停和继续
 * 8、可以记录玩家的成绩
 * 9、有音乐
 */
package com.yifanjia;
import java.awt.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
public class MyTankGame extends JFrame implements ActionListener {
	MyPanel mp = null;
	//定义一个开始面板
	MyStartPanel msp = null;
	//做出我需要的菜单
	JMenuBar jmb = null;
	//开始游戏
	JMenu jm1 = null;
	JMenuItem jmi1 = null;
	JMenuItem jmi2 = null;
	JMenuItem jmi3 = null;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyTankGame mt = new MyTankGame();
	}
	public MyTankGame() {
		msp = new MyStartPanel();
		new Thread(msp).start();
		//创建菜单选项
		jmb = new JMenuBar();
		jm1 = new JMenu("游戏");
		jmi1 = new JMenuItem("开始新游戏");
		jmi1.addActionListener(this);
		jmi1.setActionCommand("new game");
		jmi2 = new JMenuItem("退出并保存游戏");
		jmi2.addActionListener(this);
		jmi2.setActionCommand("exit");
		jmi3 = new JMenuItem("继续游戏");
		jmi3.addActionListener(this);
		jmi3.setActionCommand("regame");
		jm1.add(jmi1);
		jm1.add(jmi2);
		jm1.add(jmi3);
		jmb.add(jm1);
		this.setJMenuBar(jmb);
		
		
		this.add(msp);
		this.setSize(1000,800);
		this.setLocation(100,100);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		//对用户不同的点击做出不同的处理
		if(e.getActionCommand().equals("new game")) {
			//创建战场面板
			mp = new MyPanel("newGame"); 
			new Thread(mp).start();
			//先删除旧的面板
			this.remove(msp);
			//加入监听
			this.addKeyListener(mp);
			
			this.add(mp);
			//显示新面板，刷新
			this.setVisible(true);
		}
		else if(e.getActionCommand().equals("exit")) {
			//用户点击了退出,保存数据
			Record rd = new Record();
			rd.setEts(mp.entank);
			rd.savegame();
			System.exit(0);
		}
		else if(e.getActionCommand().equals("regame")) {
			//创建战场面板
			
			mp = new MyPanel("continue"); 
			new Thread(mp).start();
			//先删除旧的面板
			this.remove(msp);
			//加入监听
			this.addKeyListener(mp);
			
			this.add(mp);
			//显示新面板，刷新
			this.setVisible(true);
		}
	}
}
//就是一个提示的作用
class MyStartPanel extends JPanel implements Runnable{
	int times = 0;
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, 800, 600);
		//提示信息
		if(times % 2 == 0) {
			g.setColor(Color.yellow);
			Font myfont = new Font("楷体", Font.BOLD, 60);
			g.setFont(myfont);
			g.drawString("这是第一关", 230, 300);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			//休眠1s
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			times++;
			this.repaint();
		}
	}
}
//我的面板
class MyPanel extends JPanel implements KeyListener,Runnable{
	//定义一个MyTank
	MyTank me = null;
	//定义一个炸弹向量
	Vector<Bomb> bombs = new Vector<>();
	//定义爆炸图片
	Image im1 = null;
	Image im2 = null;
	Image im3 = null;
	//定义敌人的坦克
	Vector<EnimyTank> entank = new Vector<EnimyTank>();
	Vector<Node> nodes = new Vector<>();
	int enimysize = 3;
	private Object enimyshot;
	public MyPanel(String flag) {
		me = new MyTank(100,300);
		if(flag.equals("newGame")) {
			//初始化敌人坦克
			for(int i=0;i<enimysize;i++) {
				EnimyTank en = new EnimyTank(i*50,0);
				en.setDirect(1);
				en.setType(1);
				//将Mypanel的敌人坦克向量交给该敌人坦克
				en.setEts(entank);
				Thread t = new Thread(en);
				t.start();
				//给敌人坦克添加子弹
				Shot s =new Shot(en.x + 10, en.y + 35, en.direct);
				en.ss.add(s);
				Thread t2 = new Thread(s);
				t2.start();
				entank.add(en);
			}
		}
		else if(flag.equals("continue")){
			//初始化敌人坦克
			nodes = new Record().getNodes();
			for(int i=0;i<nodes.size();i++) {
				Node node = nodes.get(i);
				EnimyTank en = new EnimyTank(node.x, node.y);
				en.setDirect(node.direct);
				en.setType(1);
				//将Mypanel的敌人坦克向量交给该敌人坦克
				en.setEts(entank);
				Thread t = new Thread(en);
				t.start();
				//给敌人坦克添加子弹
				Shot s =new Shot(en.x + 10, en.y + 35, en.direct);
				en.ss.add(s);
				Thread t2 = new Thread(s);
				t2.start();
				entank.add(en);
			}
		}
		
		//初始化图片
		try {
			im1 = ImageIO.read(new File("bomb_1.gif"));
			im2 = ImageIO.read(new File("bomb_2.gif"));
			im3 = ImageIO.read(new File("bomb_3.gif"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		//播放声音
		AePlayWave apw = new AePlayWave("111.wav");
		apw.start();
		
	}
	//判断我的子弹是否击中敌人的坦克
	public void hitEnimyTank() {
		for(int i=0;i<me.ss.size();i++) {
			Shot s = me.ss.get(i);
			//判断子弹是否有效
			if(s.isalive) {
				for(int j=0;j<entank.size();j++) {
					EnimyTank et = entank.get(j);
					if(et.islive) {
						if(this.hitTank(s, et)) {
							Record.reduceenimy();
							Record.adddead();
						}		
					}
				}
			}
		}	
	}
	//判断敌人的子弹是否击中我的坦克
	public void hitMyTank() {
		for(int i=0;i<entank.size();i++) {
			
			EnimyTank ets = entank.get(i);
			
			for(int j=0;j<ets.ss.size();j++) {
				Shot enimyshot = ets.ss.get(j);
				//判断子弹是否有效
				if(me.islive)
					this.hitTank(enimyshot, me);
			}
		}	
	}
	//写一个函数判断下一个子弹是否击中坦克
	boolean hitTank(Shot s, Tank et) {
		boolean bb = false;
		//判断该坦克的方向
		switch(et.direct) {
		//如果坦克的方向是上或者下
		case 0:
		case 1:
			if(s.x > et.x && s.x < et.x + 20 &&s.y > et.y && s.y < et.y + 30) {
				//击中
				//子弹死亡，坦克死亡
				s.isalive = false;
				et.islive = false;
				bb = true;
				//创建一颗炸弹,放入Vector
				Bomb b = new Bomb(et.x, et.y);
				bombs.add(b);
			}
			break;
		case 2:
		case 3:
			if(s.x > et.x && s.x < et.x + 30 &&s.y > et.y && s.y < et.y + 20) {
				//击中
				//子弹死亡，坦克死亡
				s.isalive = false;
				et.islive = false;
				bb = true;
				//创建一颗炸弹,放入Vector
				Bomb b = new Bomb(et.x, et.y);
				bombs.add(b);
			}
			break;
		}
		return bb;
	}
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, 800, 600);
		//画出提示信息
		this.drawTank(50, 650, g, 0, 1);
		g.setColor(Color.black);
		g.setFont(new Font("楷体", Font.BOLD, 25));
		g.drawString(Record.getEnimynum() + "辆", 80, 675);
		
		this.drawTank(150, 650, g, 0, 0);
		g.setColor(Color.black);
		g.setFont(new Font("楷体", Font.BOLD, 25));
		g.drawString(Record.getMylife() + "辆", 180, 675);
		//画出玩家的总成绩
		g.setColor(Color.black);
		g.setFont(new Font("楷体", Font.BOLD, 25));
		g.drawString("你的总成绩:", 800, 100);
		this.drawTank(830, 150, g, 0, 1);
		g.setColor(Color.black);
		g.setFont(new Font("楷体", Font.BOLD, 25));
		g.drawString(Record.getAlldead() + "辆", 860, 175);
		//画出自己的坦克
		if(me.islive) {
			this.drawTank(me.getX(), me.getY(), g, this.me.getDirect(), me.getType());
		}
		//画出子弹
		for(int i=0;i<me.ss.size();i++) {
			Shot myshot = me.ss.get(i);
			if(myshot != null && myshot.isalive == true) {
				g.fill3DRect(myshot.x, myshot.y, 2, 2, false);
			}
			if(myshot.isalive == false) {
				me.ss.remove(myshot);
			}
		}
		//画出爆炸
		for(int i=0;i<bombs.size();i++) {
			Bomb b = bombs.get(i);
			if(b.life > 8) {
				g.drawImage(im1, b.x, b.y, 60, 60, this);
			}
			else if(b.life > 4) {
				g.drawImage(im2, b.x, b.y, 60, 60, this);
			}
			else {
				g.drawImage(im3, b.x, b.y, 60, 60, this);
			}
			b.lifedown();
			if(b.life == 0) {
				bombs.remove(b);
			}
		}
		//画出敌人的坦克
		for(int i=0;i<entank.size();i++) {
			EnimyTank et = entank.get(i);
			if(et.islive) {
				this.drawTank(et.getX(), et.getY(), g, et.getDirect(), et.getType());
				//画出敌人的子弹
				for(int j=0;j<et.ss.size();j++) {
					Shot ets = et.ss.get(j);
					if(ets.isalive) {
						g.fill3DRect(ets.x, ets.y, 2, 2, false);
					}
					else {
						et.ss.remove(ets);
					}
				}
			}	
		}
	}
	//画出Tank的函数(扩展)
	public void drawTank(int x, int y, Graphics g, int direct, int type) {
		//判断是什么类型的坦克
		switch(type) {
		case 0:
			g.setColor(Color.CYAN);
			break;
		case 1:
			g.setColor(Color.YELLOW);
		}
		//判断方向
		switch(direct) {
		//向上
		case 0:
			//1.画出左边的矩形
			g.fill3DRect(x, y, 5, 30, false);
			//2.画出右边的矩形
			g.fill3DRect(x+15,y, 5, 30, false);
			//3.画出中间矩形
			g.fill3DRect(x+5, y+5, 10, 20,false);
			//4.画出圆形
			g.fillOval(x+5, y+10, 8, 8);
			//5.画出线
			g.drawLine(x+10, y+15, x+10, y-5);
			break;
		//向下
		case 1:
			//1.画出左边的矩形
			g.fill3DRect(x, y, 5, 30, false);
			//2.画出右边的矩形
			g.fill3DRect(x+15,y, 5, 30, false);
			//3.画出中间矩形
			g.fill3DRect(x+5, y+5, 10, 20,false);
			//4.画出圆形
			g.fillOval(x+5, y+10, 8, 8);
			//5.画出线
			g.drawLine(x+10, y+15, x+10, y+35);
			break;
		//向左
		case 2:
			//1.画出左边的矩形
			g.fill3DRect(x, y, 30, 5, false);
			//2.画出右边的矩形
			g.fill3DRect(x, y+15, 30, 5, false);
			//3.画出中间矩形
			g.fill3DRect(x+5, y+5, 20, 10,false);
			//4.画出圆形
			g.fillOval(x+10, y+5, 8, 8);
			//5.画出线
			g.drawLine(x+15, y+10, x-5, y+10);
			break;
		//向右
		case 3:
			//1.画出左边的矩形
			g.fill3DRect(x, y, 30, 5, false);
			//2.画出右边的矩形
			g.fill3DRect(x, y+15, 30, 5, false);
			//3.画出中间矩形
			g.fill3DRect(x+5, y+5, 20, 10,false);
			//4.画出圆形
			g.fillOval(x+10, y+5, 8, 8);
			//5.画出线
			g.drawLine(x+15, y+10, x+35, y+10);
			break;
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		//w:上 ,s:下,a:左,d:右
		if(e.getKeyCode() == KeyEvent.VK_W) {
			//设置MyTank的方向
			this.me.setDirect(0);
			this.me.moveUp();
		}
		else if(e.getKeyCode() == KeyEvent.VK_S) {
			this.me.setDirect(1);
			this.me.moveDown();
		}
		else if(e.getKeyCode() == KeyEvent.VK_A) {
			this.me.setDirect(2);
			this.me.moveLeft();
		}
		else if(e.getKeyCode() == KeyEvent.VK_D) {
			this.me.setDirect(3);
			this.me.moveRight();
		}
		//判断玩家是否按下J键
		if(e.getKeyCode() == KeyEvent.VK_J) {
			if(me.ss.size() <= 4)
				me.shotenimy();
		}
		this.repaint();		
	}
	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	@Override
	public void run() {
		//每隔100ms刷新一次
		while(true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//判断是否敌方坦克被子弹击中
			this.hitEnimyTank();
			this.hitMyTank();
			this.repaint();
		}
	}
}