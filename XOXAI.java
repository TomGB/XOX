import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

public class XOXAI extends JFrame{
	static final long serialVersionUID = 1L;

	Dimension size = new Dimension(500,550);

	int x=0, y=0, circleRad=30, largeRad=100;
	float boxSpacing;
	char turn='X';
	char grid[][];
	boolean selectable[][];
	char winGrid[][];
	int numberIn[][];

	char winningPlayer='E';

	boolean gameOver;
	boolean testingMode=false, ai = true;

	final static BasicStroke minor = new BasicStroke(2f);
	final static BasicStroke major = new BasicStroke(4f);
	final static BasicStroke fat = new BasicStroke(8f);
	final static BasicStroke fatter = new BasicStroke(12f);

	public void initilise(){
		grid = new char[9][9];
		selectable = new boolean[3][3];
		winGrid = new char[3][3];
		numberIn = new int[3][3];
		for(int i=0; i<3; i++){
			for(int j=0; j<3; j++){
				numberIn[i][j]=0;
			}
		}

		gameOver=false;

		for(int i=0; i<3; i++){
			for(int j=0; j<3; j++){
				selectable[i][j]=true;
				winGrid[i][j]='E';
			}
		}

		for(int i=0; i<9; i++){
			for(int j=0; j<9; j++){
				grid[i][j]='E';
			}
		}
	}

	public XOXAI(){

		initilise();
		//gameOver=true;

		boxSpacing=size.width/9;

		setTitle("XOXAI, A game derived from Tic Tac Toe");
		setSize(size);
		this.setMinimumSize(new Dimension(size.width+12,size.height+35)); //sets the minimum size of the window to stop elements from collapsing
		setLocationRelativeTo(null); //sets the starting location
		setDefaultCloseOperation(EXIT_ON_CLOSE); //exit application when x is clicked

		JPanel canvas = new JPanel(){
			static final long serialVersionUID = 1L;
			public void paint(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				AffineTransform at = g2.getTransform();

				for(int i=0; i<9; i++){
					for(int j=0; j<9; j++){
						if(grid[i][j]=='X'){
							g.setColor(Color.red);
							g.fillRect((int)(i*boxSpacing), (int)(j*boxSpacing), (int)(boxSpacing), (int)(boxSpacing));
							g2.setStroke(major);
							g.setColor(Color.black);
							g.drawLine((int)(i*boxSpacing)+10, (int)(j*boxSpacing)+10, (int)((i+1)*boxSpacing)-10, (int)((j+1)*boxSpacing)-10);
							g.drawLine((int)(i*boxSpacing)+10, (int)((j+1)*boxSpacing)-10, (int)((i+1)*boxSpacing)-10, (int)(j*boxSpacing)+10);
						}
						if(grid[i][j]=='O'){
							g.setColor(Color.blue);
							g.fillRect((int)(i*boxSpacing), (int)(j*boxSpacing), (int)(boxSpacing), (int)(boxSpacing));
							g2.setStroke(major);
							g.setColor(Color.black);
							g.drawOval((int)(i*boxSpacing-circleRad/2+boxSpacing/2), (int)(j*boxSpacing-circleRad/2+boxSpacing/2),circleRad,circleRad);
						}
						//g.drawRect((int)(i*boxSpacing), (int)(j*boxSpacing), (int)(boxSpacing), (int)(boxSpacing));
					}
				}

				g.setColor(new Color(0f,0f,0f,0.3f));
				for(int i=0; i<3; i++){
					for(int j=0; j<3; j++){
						if(!selectable[i][j]){
							g.fillRect((int)(i*boxSpacing*3), (int)(j*boxSpacing*3), (int)(boxSpacing*3), (int)(boxSpacing*3));
						}
					}
				}

				for(int i=0; i<9; i++){
					g.setColor(Color.black);
					if(i%3==0){
						g2.setStroke(major);
					}else{
						g2.setStroke(minor);
					}
					g.drawLine(0,(int)(i*boxSpacing), size.height, (int)(i*boxSpacing));
					g.drawLine((int)(i*boxSpacing), 0, (int)(i*boxSpacing), size.height);
				}

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //anti alias to make lines smooth

				for(int i=0; i<3; i++){
					for (int j=0; j<3; j++) {
						if(winGrid[i][j]=='X'){
							g.setColor(Color.white);
							g2.setStroke(fatter);
							g.drawLine((int)(i*boxSpacing*3+30), (int)(j*boxSpacing*3+30), (int)((i+1)*boxSpacing*3-30), (int)((j+1)*boxSpacing*3-30));
							g.drawLine((int)(i*boxSpacing*3+30), (int)((j+1)*boxSpacing*3-30), (int)((i+1)*boxSpacing*3-30), (int)(j*boxSpacing*3+30));

							g.setColor(Color.black);
							g2.setStroke(fat);
							g.drawLine((int)(i*boxSpacing*3+30), (int)(j*boxSpacing*3+30), (int)((i+1)*boxSpacing*3-30), (int)((j+1)*boxSpacing*3-30));
							g.drawLine((int)(i*boxSpacing*3+30), (int)((j+1)*boxSpacing*3-30), (int)((i+1)*boxSpacing*3-30), (int)(j*boxSpacing*3+30));
						}
						if(winGrid[i][j]=='O'){
							g.setColor(Color.white);
							g2.setStroke(fatter);
							g.drawOval((int)(i*boxSpacing*3+30), (int)(j*boxSpacing*3+30), largeRad, largeRad);

							g.setColor(Color.black);
							g2.setStroke(fat);
							g.drawOval((int)(i*boxSpacing*3+30), (int)(j*boxSpacing*3+30), largeRad, largeRad);
						}
					}
				}

				if(gameOver){
					g.setColor(Color.white);
					g.fillRect(100,200,300,100);
					g.setColor(Color.black);
					g.drawString("Game Over, Player "+winningPlayer+" has WON!", 150,240);
					g.drawString("Click Here to play again!", 150,260);
					repaint();
				}

				g.setColor(Color.black);
				g.fillRect(0,(int)boxSpacing*9,size.width, 50);
				if(turn=='X'){
					g.setColor(Color.red);
				}else{
					g.setColor(Color.blue);
				}
				g.fillRect(10, (int)boxSpacing*9+10, 30, 30);
			}
		};

		canvas.addMouseListener(new MouseAdapter(){
			public void mouseMoved(MouseEvent e){}
			public void mouseDragged(MouseEvent e){}
			public void mouseReleased(MouseEvent e){
				if(gameOver){
					initilise();
				}else{
					if(ai&&turn=='O'){

						int posX=0, posY=0;
						ArrayList<Integer> choiceX = new ArrayList<Integer>();
						ArrayList<Integer> choiceY = new ArrayList<Integer>();

						for(int i=0; i<3; i++){
							for(int j=0; j<3; j++){
								if(selectable[i][j]){
									posX=i;
									posY=j;
								}
							}
						}
						for(int i=0; i<3; i++){
							for(int j=0; j<3; j++){
								if(grid[posX*3+i][posY*3+j]=='E'){
									choiceX.add(posX*3+i);
									choiceY.add(posY*3+j);
								}
							}
						}

						setSelectable();

						int selectionNum=r(choiceX.size());
						x=choiceX.get(selectionNum);
						y=choiceY.get(selectionNum);

						makeMove();

					}else if(!(e.getX()<0||e.getX()>=size.width||e.getY()<0||e.getY()>=size.height)){ //click within game area
						if(selectable[(int)(e.getX()/boxSpacing)/3][(int)(e.getY()/boxSpacing)/3]){ //click within valid area
							if(grid[(int)(e.getX()/boxSpacing)][(int)(e.getY()/boxSpacing)]=='E'){

								setSelectable();

								x=(int)(e.getX()/boxSpacing);
								y=(int)(e.getY()/boxSpacing);

								makeMove();

								repaint();
							}
						}
					}
				}
			}
		});
		add("Center", canvas);
		repaint();
		this.pack(); //pack the elements into the frame
		setVisible(true);
	}

	public void setSelectable(){
		if(!testingMode){
			for(int i=0; i<3; i++){
				for(int j=0; j<3; j++){
					selectable[i][j]=false;
				}
			}
		}
	}

	public void makeMove(){
		grid[x][y]=turn;

		numberIn[x/3][y/3]++;		
		selectable[x%3][y%3]=true;

		if(winGrid[x/3][y/3]=='E'){
			testWin(grid, x/3*3, y/3*3);
		}
		testWin(winGrid, 0, 0);

		if(numberIn[x%3][y%3]==9){
			for(int i=0; i<3; i++){
				for (int j=0; j<3; j++) {
					selectable[i][j]=true;
				}
			}
		}

		if(turn=='X'){turn='O';}else{turn='X';}
		repaint();
	}

	public void testWin(char[][] array, int baseX, int baseY){
		for(int j=0; j<2; j++){
			boolean win = false;
			char tempTurn = (j==0?'X':'O');
			for(int i=0; i<3; i++){
				if(
					array[baseX][baseY+i]==tempTurn&&array[baseX+1][baseY+i]==tempTurn&&array[baseX+2][baseY+i]==tempTurn||
					array[baseX+i][baseY]==tempTurn&&array[baseX+i][baseY+1]==tempTurn&&array[baseX+i][baseY+2]==tempTurn 
				){
					win=true;
				}
			}
			if(
				array[baseX][baseY]==tempTurn&&array[baseX+1][baseY+1]==tempTurn&&array[baseX+2][baseY+2]==tempTurn||
				array[baseX][baseY+2]==tempTurn&&array[baseX+1][baseY+1]==tempTurn&&array[baseX+2][baseY]==tempTurn
			){
				win=true;
			}
			if(win&&array==grid){
				winGrid[baseX/3][baseY/3]=tempTurn;
			}else if(win&&array==winGrid){
				p("Player: "+turn+", has WON!!!");
				winningPlayer=turn;
				gameOver=true;
			}
		}
	}

	public static void p(Object input){System.out.println(input);} //print method
	public static int r(int input){return (int)(Math.random()*input);}
	public static void main(String args[]){
		new XOXAI();
	}
}