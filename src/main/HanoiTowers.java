package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


enum Location {L,R,C};

@SuppressWarnings("serial")
public class HanoiTowers extends JPanel{

	static  int FWIDTH=700,FHEIGHT=400;
	public static ArrayList<Tower> towers;

	final Thread displayThread;


	public static Tower getTower(Location l) {
		for (Tower tower : towers) {
			if (tower.location==l) {
				return tower;
			}
		}
		return null;

	}
	public void start() {
		HanoiTowers.towers=new ArrayList<Tower>();
		Tower tower=new Tower(10,Location.L);
		for (int i = 10; i >0; i--) {
			tower.createBlock(new Block(i));
		}
		towers.add(tower);
		Tower.activeTower=tower;
		towers.add(new Tower(220,Location.C));
		towers.add(new Tower(440,Location.R));

	}

	public HanoiTowers() {

		displayThread= new Thread() {
			@Override
			public void run() {
				while (true) {
					repaint();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {

					}
				}
			}
		};
		start();
		displayThread.start();
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		for (Tower tower : towers) {
			tower.draw(g);
			g.setColor(Color.BLACK);
			for (Block block : tower.blocks) {
				block.draw(g);
			}
		}

	}
	public static void  main(String[] args) {

		Runnable runnable=new Runnable() {

			public void run() {
				JFrame windowFrame=new JFrame("Hanoi");
				windowFrame.setAlwaysOnTop(true);
				windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				windowFrame.setSize(new Dimension(HanoiTowers.FWIDTH,HanoiTowers.FHEIGHT));
				windowFrame.setLocationRelativeTo(null);
				final HanoiTowers hanoiTowers=new HanoiTowers();
				windowFrame.addKeyListener(new KeyListener() {

					public void keyTyped(KeyEvent arg0) {
						// TODO Auto-generated method stub

					}

					public void keyReleased(KeyEvent arg0) {
						// TODO Auto-generated method stub

					}

					public void keyPressed(KeyEvent arg0) {

						switch (arg0.getKeyCode()) {
							case KeyEvent.VK_LEFT :
								try {
									switch (Tower.activeTower.getLastBlock().location) {
										case L:
										break;
										case R:
										Tower.activeTower.getLastBlock().moveTo(HanoiTowers.getTower(Location.C));
										break;
										case C:
										Tower.activeTower.getLastBlock().moveTo(HanoiTowers.getTower(Location.L));
										break;
									}
								} catch (NullPointerException e) {

								}
								break;

							case KeyEvent.VK_RIGHT :
								try {
									switch (Tower.activeTower.getLastBlock().location) {
										case L:
											Tower.activeTower.getLastBlock().moveTo(HanoiTowers.getTower(Location.C));
											break;
										case R:
											break;
										case C:
											Tower.activeTower.getLastBlock().moveTo(HanoiTowers.getTower(Location.R));
											break;
									}
								} catch (NullPointerException e) {
									// TODO: handle exception
								}
								break;
							case KeyEvent.VK_A:
								Tower.activeTower=HanoiTowers.towers.get(0);
								//activeTower(Location.L);
								break;
							case KeyEvent.VK_S:
								Tower.activeTower=HanoiTowers.towers.get(1);
								//activeTower(Location.C);
								break;
							case KeyEvent.VK_D:
								Tower.activeTower=HanoiTowers.towers.get(2);
								break;
							case KeyEvent.VK_DOWN:
								Tower.activeTower.getLastBlock().rest();
								break;
							case KeyEvent.VK_UP:
								try {
									Tower.activeTower.getLastBlock().moveUp();
								} catch (NullPointerException e) {

								}
								break;

							default :
								break;
						}
					}
				});


				windowFrame.add(hanoiTowers);
				windowFrame.setVisible(true);

			}
		};
		SwingUtilities.invokeLater(runnable);
	}
}

class Tower extends Rectangle {
	public ArrayList<Block> blocks;
	public Location location;
	static Tower activeTower;
	public Location towerLocation;
	public Tower(int x, Location c) {
		this.width=200;
		this.location=c;
		blocks=new ArrayList<Block>();
		this.x=x;
		this.y=HanoiTowers.FHEIGHT-50;
		this.height=20;

	}

	public void createBlock(Block block) {
		block.width*=20;
		block.location=this.location;//todo need?
		this.addBlock(block);
	}
	public Block getLastBlock() {
		try {
			return this.blocks.get(this.blocks.size()-1);
		} catch (ArrayIndexOutOfBoundsException e) {
			//System.err.println("No blocks in tower.");
			return null;
		}

	}
	public void addBlock(Block block) {
		//this.blocks.remove(block);

		if (this.blocks.size()==0) {
			block.y=HanoiTowers.FHEIGHT-(50+20);
		}else {
			block.y=this.blocks.get(this.blocks.size()-1).y-20;
		}
		block.isMovable=false;
		this.blocks.add(block);

		//this.lastBlock=blocks.get(blocks.size()-1);
	}
	public void draw(Graphics g) {
		Color temp=g.getColor();
		g.setColor(Color.GREEN);
		if (this.equals(activeTower)) {
			g.fillRect(this.x,this.y,this.width,this.height);
		}
		g.drawRect(this.x,this.y,this.width,this.height);
		g.setColor(temp);

	}
}

@SuppressWarnings("serial")
class Block extends Rectangle{
	//final public int width;
	public Location location;
	public boolean isMovable=false;

	public Block(int width) {
		this.width=width;
		this.height=10;
		//this.y=40;
		this.x=10;
		this.location=Location.L;
	}

	public void rest() {

		if (HanoiTowers.getTower(this.location).getLastBlock()!=null ) {
			if(HanoiTowers.getTower(this.location).getLastBlock().width>=this.width) {
				this.moveFrom(Tower.activeTower);
				HanoiTowers
				.getTower(this.location)
				.addBlock(this);
			}else {
				System.err.println("You cannot add a block there.");
			}
		}else {
			this.moveFrom(Tower.activeTower);
			HanoiTowers
			.getTower(this.location)
			.addBlock(this);
		}
	}

	public void draw(Graphics g) {
		if (this==Tower.activeTower.getLastBlock()) {
			g.setColor(Color.YELLOW);
			g.drawRoundRect(this.x, this.y, this.width, 10, 10, 20);
			g.setColor(Color.GRAY);
		}

		g.fillRoundRect(this.x, this.y, this.width, 10, 10, 20);
		g.setColor(Color.BLACK);
	}
	public void moveFrom(Tower tower) {
		//tower.lastBlock=tower.blocks.get(tower.blocks.size()-1);
		tower.blocks.remove(this);

	}
	public void moveTo(Tower tower) {
		if (this.isMovable) {
			this.x=tower.x;
			this.location=tower.location;
		}
		if (this.location==null) {
			//return;
		}
		//this.y=20;


	}

	public void moveUp() {
		this.isMovable=true;
		this.y=100;
	}

	}



