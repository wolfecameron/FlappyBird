import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Rectangle;

import javax.swing.Timer;
import javax.swing.JFrame;


public class FlappyBird implements ActionListener, MouseListener {
	
	//creates an instance of the flappy bird class
	public static FlappyBird flappyBird;
	//sets height of the jFrame
	public final int WIDTH = 1000, HEIGHT = 800;
	//creates an instance of renderer
	public Renderer renderer;
	//creates instance for the bird
	public Rectangle bird;
	//creates a list of rectangles in which to insert columns
	public ArrayList<Rectangle> columns;
	//creates instance of Random utility
	public Random rand;
	//saves high score
	public int highScore = 0;
	
	public boolean gameOver, gameStart, displayScore;
	
	public int ticks;
	public int ymotion;
	public int score;
	
	//constructor
	public FlappyBird() {
		JFrame jframe = new JFrame();
		Timer timer = new Timer(20, this);
		renderer = new Renderer();
		rand = new Random();
		
		jframe.add(renderer);
		jframe.addMouseListener(this);
		jframe.setDefaultCloseOperation(jframe.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH,HEIGHT);
		jframe.setTitle("Flappy Bird!");
		jframe.setVisible(true);
		jframe.setResizable(false);
		
		bird = new Rectangle(WIDTH/2-10,HEIGHT/2-10, 20,20);
		columns = new ArrayList<Rectangle>();
		
		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);
		
		gameOver = false;
		
		timer.start();

		
	}
	
	public void jump() {
		if(gameOver && !displayScore) {
			displayScore = true;
		}
		else if(gameOver && displayScore) {
			bird = new Rectangle(WIDTH/2-10,HEIGHT/2-10, 20,20);
			columns.clear();
			ymotion = 0;
			score = 0;
			
			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);
			
			gameOver = false;
			displayScore = false;
			
		}
	    if(!gameStart) {
	    	gameStart = true;
	    	
	    }else if(!gameOver) {
	    	if (ymotion >0) {
	    		ymotion = 0;
	    	}
	    	ymotion -=11;
	    }
	}
	
	
	public void addColumn(boolean start) {
		int space = 300; //space between columns
		int width = 100;
		int height = 50 + rand.nextInt(300); //parameter sets max rand value
		
		if(start) {
		//adds an element into array list
		columns.add(new Rectangle(WIDTH+width+columns.size()*300, HEIGHT -height -120,width,height));//bottom
		columns.add(new Rectangle(WIDTH +width + (columns.size()-1)*300,0,width, HEIGHT - height - space - 120));//top
		
	}else {
		columns.add(new Rectangle(columns.get(columns.size()-1).x +600,HEIGHT -height -120, width, height)); 
		columns.add(new Rectangle(columns.get(columns.size()-1).x,0,width, HEIGHT - height - space - 120));
	}
		}
	
	public void paintColumn(Graphics g, Rectangle column) {
		g.setColor(Color.gray.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		ticks++;
		
		int speed = 10;
		
		
		if(gameStart) {
		//allows columns to move
		for(int i = 0;i<columns.size();i++) {
			
			Rectangle column = columns.get(i);
			column.x -= speed;
			
		}
		
		//removes and adds columns as they move off screen
		for(int i = 0; i<columns.size();i++) {
			
			Rectangle column = columns.get(i);
			
			if(column.x + column.width < 0) {
				columns.remove(column);
				
				//must do this such that it will only add a column when the top column goes off the page (there are two columns)
				if(column.y== 0) {
				
					addColumn(false);
					
				}
			}
		}
		
		if(ticks % 2 ==0 && ymotion<15) {
			ymotion+=2;
		}
		
		bird.y += ymotion;
		}
		
		//check collision
		for (Rectangle column:columns) {
			if(column.intersects(bird)) {
				gameOver = true;
				
				bird.x = column.x - bird.width;
			}
			if((bird.x + bird.width/2) == (column.x + column.width/2) && column.y== 0) {
				score++;
				if(score>=highScore) {
					highScore = score;
				}
			}
		}
	
		
		if(bird.y > HEIGHT - 100 || bird.y < 0) {
			gameOver = true;
		}
		
		if(gameOver) {
			bird.y = HEIGHT - 120 - bird.height;
		}
		
		renderer.repaint();
		
	}
	
	public static void main(String[] args) {
		
		flappyBird = new FlappyBird();
		
	}

	public void repaint(Graphics g) {
		//creates sky
		g.setColor(Color.cyan.darker());
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//creates ground
		g.setColor(Color.red.darker());
		g.fillRect(0, HEIGHT-100,WIDTH,100);
		
		//creates grass
		g.setColor(Color.green.darker());
		g.fillRect(0,HEIGHT-120,WIDTH,20);
		
		//sets color of bird
		g.setColor(Color.yellow);
		g.fillRect(bird.x, bird.y, bird.width, bird.height);
		
		for(Rectangle column: columns) {
			paintColumn(g,column);
		}
		
		
		if(!gameStart) {
			g.setColor(Color.white);
			g.setFont(new Font("Arial",1,100));
			g.drawString("Click to Start!", 200, HEIGHT/2 - 50);
		}
		if(!gameOver && gameStart) {
			g.setColor(Color.red);
			g.setFont(new Font("Times New Roman",1,40));
			g.drawString("High Score: " + String.valueOf(highScore),15,40);
			g.setColor(Color.white);
			g.drawString(String.valueOf(score),480,80);
			
		}
		if(gameOver && !displayScore) {
			g.setColor(Color.white);
			g.setFont(new Font("Arial",1,100));
			g.drawString("Game Over", 225, HEIGHT/2 -50);
			g.setColor(Color.blue.darker());
			g.drawString("Final Score: " + String.valueOf(score), 170, HEIGHT/2+50);
			g.setColor(Color.red);
			g.drawString("High Score: " + String.valueOf(highScore), 170, HEIGHT/2+160);	
		}if(gameOver && displayScore) {
			g.setColor(Color.white);
			g.setFont(new Font("Arial",1,100));
			g.drawString("Click to Start Again!", 25, HEIGHT/2 - 50);
		}
		
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		jump();
	}


	@Override
	public void mouseEntered(MouseEvent e) {
	
		
	}


	@Override
	public void mouseExited(MouseEvent e) {

		
	}


	@Override
	public void mousePressed(MouseEvent e) {

		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
	
		
	}
}
