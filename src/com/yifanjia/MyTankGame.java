/**
 * ̹����Ϸ
 * 1������̹��
 * 2��̹�˿���������������
 * 3��̹�˿��Է��ӵ�(������)
 * 4��ʵ�з�̹�˵������ƶ���̹�˱���������
 * 5����ֹ����̹���ص�
 * 6�����Էֹ�
 * 7��������ͣ�ͼ���
 * 8�����Լ�¼��ҵĳɼ�
 * 9��������
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
	//����һ����ʼ���
	MyStartPanel msp = null;
	//��������Ҫ�Ĳ˵�
	JMenuBar jmb = null;
	//��ʼ��Ϸ
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
		//�����˵�ѡ��
		jmb = new JMenuBar();
		jm1 = new JMenu("��Ϸ");
		jmi1 = new JMenuItem("��ʼ����Ϸ");
		jmi1.addActionListener(this);
		jmi1.setActionCommand("new game");
		jmi2 = new JMenuItem("�˳���������Ϸ");
		jmi2.addActionListener(this);
		jmi2.setActionCommand("exit");
		jmi3 = new JMenuItem("������Ϸ");
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
		//���û���ͬ�ĵ��������ͬ�Ĵ���
		if(e.getActionCommand().equals("new game")) {
			//����ս�����
			mp = new MyPanel("newGame"); 
			new Thread(mp).start();
			//��ɾ���ɵ����
			this.remove(msp);
			//�������
			this.addKeyListener(mp);
			
			this.add(mp);
			//��ʾ����壬ˢ��
			this.setVisible(true);
		}
		else if(e.getActionCommand().equals("exit")) {
			//�û�������˳�,��������
			Record rd = new Record();
			rd.setEts(mp.entank);
			rd.savegame();
			System.exit(0);
		}
		else if(e.getActionCommand().equals("regame")) {
			//����ս�����
			
			mp = new MyPanel("continue"); 
			new Thread(mp).start();
			//��ɾ���ɵ����
			this.remove(msp);
			//�������
			this.addKeyListener(mp);
			
			this.add(mp);
			//��ʾ����壬ˢ��
			this.setVisible(true);
		}
	}
}
//����һ����ʾ������
class MyStartPanel extends JPanel implements Runnable{
	int times = 0;
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, 800, 600);
		//��ʾ��Ϣ
		if(times % 2 == 0) {
			g.setColor(Color.yellow);
			Font myfont = new Font("����", Font.BOLD, 60);
			g.setFont(myfont);
			g.drawString("���ǵ�һ��", 230, 300);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			//����1s
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
//�ҵ����
class MyPanel extends JPanel implements KeyListener,Runnable{
	//����һ��MyTank
	MyTank me = null;
	//����һ��ը������
	Vector<Bomb> bombs = new Vector<>();
	//���屬ըͼƬ
	Image im1 = null;
	Image im2 = null;
	Image im3 = null;
	//������˵�̹��
	Vector<EnimyTank> entank = new Vector<EnimyTank>();
	Vector<Node> nodes = new Vector<>();
	int enimysize = 3;
	private Object enimyshot;
	public MyPanel(String flag) {
		me = new MyTank(100,300);
		if(flag.equals("newGame")) {
			//��ʼ������̹��
			for(int i=0;i<enimysize;i++) {
				EnimyTank en = new EnimyTank(i*50,0);
				en.setDirect(1);
				en.setType(1);
				//��Mypanel�ĵ���̹�����������õ���̹��
				en.setEts(entank);
				Thread t = new Thread(en);
				t.start();
				//������̹������ӵ�
				Shot s =new Shot(en.x + 10, en.y + 35, en.direct);
				en.ss.add(s);
				Thread t2 = new Thread(s);
				t2.start();
				entank.add(en);
			}
		}
		else if(flag.equals("continue")){
			//��ʼ������̹��
			nodes = new Record().getNodes();
			for(int i=0;i<nodes.size();i++) {
				Node node = nodes.get(i);
				EnimyTank en = new EnimyTank(node.x, node.y);
				en.setDirect(node.direct);
				en.setType(1);
				//��Mypanel�ĵ���̹�����������õ���̹��
				en.setEts(entank);
				Thread t = new Thread(en);
				t.start();
				//������̹������ӵ�
				Shot s =new Shot(en.x + 10, en.y + 35, en.direct);
				en.ss.add(s);
				Thread t2 = new Thread(s);
				t2.start();
				entank.add(en);
			}
		}
		
		//��ʼ��ͼƬ
		try {
			im1 = ImageIO.read(new File("bomb_1.gif"));
			im2 = ImageIO.read(new File("bomb_2.gif"));
			im3 = ImageIO.read(new File("bomb_3.gif"));
		} catch (Exception e) {
			// TODO: handle exception
		}
		//��������
		AePlayWave apw = new AePlayWave("111.wav");
		apw.start();
		
	}
	//�ж��ҵ��ӵ��Ƿ���е��˵�̹��
	public void hitEnimyTank() {
		for(int i=0;i<me.ss.size();i++) {
			Shot s = me.ss.get(i);
			//�ж��ӵ��Ƿ���Ч
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
	//�жϵ��˵��ӵ��Ƿ�����ҵ�̹��
	public void hitMyTank() {
		for(int i=0;i<entank.size();i++) {
			
			EnimyTank ets = entank.get(i);
			
			for(int j=0;j<ets.ss.size();j++) {
				Shot enimyshot = ets.ss.get(j);
				//�ж��ӵ��Ƿ���Ч
				if(me.islive)
					this.hitTank(enimyshot, me);
			}
		}	
	}
	//дһ�������ж���һ���ӵ��Ƿ����̹��
	boolean hitTank(Shot s, Tank et) {
		boolean bb = false;
		//�жϸ�̹�˵ķ���
		switch(et.direct) {
		//���̹�˵ķ������ϻ�����
		case 0:
		case 1:
			if(s.x > et.x && s.x < et.x + 20 &&s.y > et.y && s.y < et.y + 30) {
				//����
				//�ӵ�������̹������
				s.isalive = false;
				et.islive = false;
				bb = true;
				//����һ��ը��,����Vector
				Bomb b = new Bomb(et.x, et.y);
				bombs.add(b);
			}
			break;
		case 2:
		case 3:
			if(s.x > et.x && s.x < et.x + 30 &&s.y > et.y && s.y < et.y + 20) {
				//����
				//�ӵ�������̹������
				s.isalive = false;
				et.islive = false;
				bb = true;
				//����һ��ը��,����Vector
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
		//������ʾ��Ϣ
		this.drawTank(50, 650, g, 0, 1);
		g.setColor(Color.black);
		g.setFont(new Font("����", Font.BOLD, 25));
		g.drawString(Record.getEnimynum() + "��", 80, 675);
		
		this.drawTank(150, 650, g, 0, 0);
		g.setColor(Color.black);
		g.setFont(new Font("����", Font.BOLD, 25));
		g.drawString(Record.getMylife() + "��", 180, 675);
		//������ҵ��ܳɼ�
		g.setColor(Color.black);
		g.setFont(new Font("����", Font.BOLD, 25));
		g.drawString("����ܳɼ�:", 800, 100);
		this.drawTank(830, 150, g, 0, 1);
		g.setColor(Color.black);
		g.setFont(new Font("����", Font.BOLD, 25));
		g.drawString(Record.getAlldead() + "��", 860, 175);
		//�����Լ���̹��
		if(me.islive) {
			this.drawTank(me.getX(), me.getY(), g, this.me.getDirect(), me.getType());
		}
		//�����ӵ�
		for(int i=0;i<me.ss.size();i++) {
			Shot myshot = me.ss.get(i);
			if(myshot != null && myshot.isalive == true) {
				g.fill3DRect(myshot.x, myshot.y, 2, 2, false);
			}
			if(myshot.isalive == false) {
				me.ss.remove(myshot);
			}
		}
		//������ը
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
		//�������˵�̹��
		for(int i=0;i<entank.size();i++) {
			EnimyTank et = entank.get(i);
			if(et.islive) {
				this.drawTank(et.getX(), et.getY(), g, et.getDirect(), et.getType());
				//�������˵��ӵ�
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
	//����Tank�ĺ���(��չ)
	public void drawTank(int x, int y, Graphics g, int direct, int type) {
		//�ж���ʲô���͵�̹��
		switch(type) {
		case 0:
			g.setColor(Color.CYAN);
			break;
		case 1:
			g.setColor(Color.YELLOW);
		}
		//�жϷ���
		switch(direct) {
		//����
		case 0:
			//1.������ߵľ���
			g.fill3DRect(x, y, 5, 30, false);
			//2.�����ұߵľ���
			g.fill3DRect(x+15,y, 5, 30, false);
			//3.�����м����
			g.fill3DRect(x+5, y+5, 10, 20,false);
			//4.����Բ��
			g.fillOval(x+5, y+10, 8, 8);
			//5.������
			g.drawLine(x+10, y+15, x+10, y-5);
			break;
		//����
		case 1:
			//1.������ߵľ���
			g.fill3DRect(x, y, 5, 30, false);
			//2.�����ұߵľ���
			g.fill3DRect(x+15,y, 5, 30, false);
			//3.�����м����
			g.fill3DRect(x+5, y+5, 10, 20,false);
			//4.����Բ��
			g.fillOval(x+5, y+10, 8, 8);
			//5.������
			g.drawLine(x+10, y+15, x+10, y+35);
			break;
		//����
		case 2:
			//1.������ߵľ���
			g.fill3DRect(x, y, 30, 5, false);
			//2.�����ұߵľ���
			g.fill3DRect(x, y+15, 30, 5, false);
			//3.�����м����
			g.fill3DRect(x+5, y+5, 20, 10,false);
			//4.����Բ��
			g.fillOval(x+10, y+5, 8, 8);
			//5.������
			g.drawLine(x+15, y+10, x-5, y+10);
			break;
		//����
		case 3:
			//1.������ߵľ���
			g.fill3DRect(x, y, 30, 5, false);
			//2.�����ұߵľ���
			g.fill3DRect(x, y+15, 30, 5, false);
			//3.�����м����
			g.fill3DRect(x+5, y+5, 20, 10,false);
			//4.����Բ��
			g.fillOval(x+10, y+5, 8, 8);
			//5.������
			g.drawLine(x+15, y+10, x+35, y+10);
			break;
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		//w:�� ,s:��,a:��,d:��
		if(e.getKeyCode() == KeyEvent.VK_W) {
			//����MyTank�ķ���
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
		//�ж�����Ƿ���J��
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
		//ÿ��100msˢ��һ��
		while(true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//�ж��Ƿ�з�̹�˱��ӵ�����
			this.hitEnimyTank();
			this.hitMyTank();
			this.repaint();
		}
	}
}