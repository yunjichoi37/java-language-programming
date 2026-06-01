package assign3;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MultiBallThread extends Frame implements ActionListener {
	
	private Canvas canvas;
	private Button s, c;
	private boolean isRunning = false;
	private ArrayList<Ball> balls = new ArrayList<>(); // Ball 자료형 arraylist 만들어주기
	
	public MultiBallThread(String name) {
		super(name);
		canvas = new Canvas() {
			public void paint(Graphics g) {
				for (Ball ball : balls)
					ball.draw(g);
			}
		};
		canvas.setBackground(Color.BLACK);
		add("Center", canvas);
		
		Panel p = new Panel();
		s = new Button("Start");
		c = new Button("Close");
		p.add(s); p.add(c);
		s.addActionListener(this);
		c.addActionListener(this);
		p.setBackground(Color.BLACK);
		add("South", p);
	}
	
	public void actionPerformed(ActionEvent evt) {
		if (evt.getActionCommand().equals("Start")) {
			if (!isRunning) {
				start(); // start 버튼 눌렀을 때 실행하기
				isRunning = true; // isRunning을 true로 바꿔서 잘 실행될 수 있게 만들어주기.
				s.setEnabled(false); // 버튼 다시 눌러도 영향 없게
			}
		} else if (evt.getActionCommand().equals("Close")) {
			System.exit(0);
		}
	}
	
	public void start() {
		
		balls.add(new Ball(canvas, 190, 90, 2 * Math.cos(-Math.PI / 2), 2 * Math.sin(-Math.PI / 2), 16));
		balls.add(new Ball(canvas, 210, 100, 2 * Math.cos(0), 2 * Math.sin(0), 16));
		balls.add(new Ball(canvas, 205, 120, 2 * Math.cos(2 * Math.PI / 7), 2 * Math.sin(2 * Math.PI / 7), 16));
		balls.add(new Ball(canvas, 180, 120, 2 * Math.cos(5 * Math.PI / 7), 2 * Math.sin(5 * Math.PI / 7), 16));
		balls.add(new Ball(canvas, 170, 105, 2 * Math.cos(Math.PI), 2 * Math.sin(Math.PI), 16));
		
		new Thread(() -> {
			while(isRunning) { // 움직이고 있는 공이 있다면
				update(); // 공의 변화를 업데이트 시켜주기
				canvas.repaint(); // 업데이트 된 상태를 다시 그려주기
				
				if(balls.isEmpty()) { // 만약 balls arraylist가 비어있다면 공이 다 사라졌다는 뜻
					isRunning = false; // false로 바꿔서 while문 탈출하게 해주기
					System.out.println("공이 모두 사라졌어요 이제 끝"); 
					System.exit(0);
				}
				
				try { Thread.sleep(15); } 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start(); // 스레드 실행시키기
	}
	
	public void update() {
		ArrayList<Ball> updateBalls = new ArrayList<>();
		
		for (int i = 0; i < balls.size(); i++) {
			Ball thisBall = balls.get(i);
			thisBall.move();
			
			// 이중 for을 쓸 때 j값의 설정이 중요하다. 
			// 만약 j가 0에서 시작한다면 i랑 같을 때 자기 자신이랑도 충돌했다고 판단해서 이상한 결과가 나옴.
			for (int j = i + 1; j < balls.size(); j++) { 
				Ball otherBall = balls.get(j);
				
				if(thisBall.isCollided(otherBall)) {
					ArrayList<Ball> splitThis = thisBall.split();
					updateBalls.addAll(splitThis);
					ArrayList<Ball> splitOther = otherBall.split();
					updateBalls.addAll(splitOther);
					
					thisBall.setRemoved(true);
					otherBall.setRemoved(true);
				}
			}
			
			// 공이 없어지지 않았고 지름이 2보다 크다면 계속 실행시켜줘야 함.
			// 여기서 updateBalls에 더해주지 않으면 공이 그냥 제외되어버리기 때문에 반드시 추가해야 함.
			if(!thisBall.isRemoved() && thisBall.getDiameter() >= 2)
				updateBalls.add(thisBall);
		}
		
		balls = updateBalls;
	}
	
	public static void main(String[] args) {
		MultiBallThread f = new MultiBallThread("공을 튀겨보자");
		
		WindowDestroyer listener = new WindowDestroyer();
		f.addWindowListener(listener);

		f.setSize(400, 300);
		f.setVisible(true);
	}
}

class WindowDestroyer extends WindowAdapter {
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}
}

class Ball {
	private Canvas box;
	private int x, y;
	private double dx, dy;
	private int diameter;
	private boolean removed = false; // 공이 없어졌는지 상태를 확인할 수 있게끔 설정
	
	public Ball(Canvas box, int x, int y, double dx, double dy, int diameter) {
		this.box = box;
		this.x = x; this.y = y;
		this.dx = dx; this.dy = dy;
		this.diameter = diameter;
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.PINK);
		g.fillOval(x, y, diameter, diameter);
	}
	
	public void move() {
		
		x += dx; y += dy;
		Dimension d = box.getSize();
		
		if (x < 0) { x = 0; dx = -dx; }
		if (x > d.width - diameter) { x = d.width - diameter; dx = -dx; }
		if (y < 0) { y = 0; dy = -dy; }
		if (y > d.height - diameter) { y = d.height - diameter; dy = -dy; }
	}
	
	public boolean isCollided(Ball other) {
		
		// this 중심에서 other 중심까지 거리 구하기
		int distX = this.x + this.diameter / 2 - (other.x + other.diameter / 2);
		int distY = this.y + this.diameter / 2 - (other.y + other.diameter / 2);
		
		/*
		dx = this.x - other.x;
		dy = this.y - other.y;
		이렇게 하지 않은 까닭은 this.x, other.x, this.y, other.y가 
		원을 감싸는 사각형의 왼쪽 꼭짓점을 나타내기 때문이다.
		정확하게 "중심"을 연산하려면 dx, dy에 각 원의 반지름을 더해주는 과정이 필요하다.
		*/
		
		double distance = Math.sqrt(distX * distX + distY * distY);
		int diameterSum = this.diameter / 2 + other.diameter / 2;
		
		if (distance <= diameterSum) {
			
			// 공이 쪼개지기 전에 분열된 공이 잘 튕겨나갈 수 있는지 확인하는 과정이 필요함.
			// 만약 공이 쪼개졌을 때, 쪼개진 공들이 운이 안 좋게도 충돌 범위 안에 있을 가능성이 있음.
			// 충돌 범위 안에 있으면 다시 충돌이 되는 걸로 여겨져서 한번 더 분열이 일어남.
			// 이걸 방지하기 위해 공이 한번 더 움직였을 시점의 거리를 계산해서 if문을 써줘야 함.

			// 밑의 6줄을 주석처리 하고 return true; 만 실행한다면 차이가 확실히 보임
			// 리턴만 할 경우 쪼개진 공이 추가적으로 실행되지 않고 바로 사라지는 케이스가 있음.
			double nextDistX = (this.x + this.dx + this.diameter / 2) - (other.x + other.dx + other.diameter / 2);
			double nextDistY = (this.y + this.dy + this.diameter / 2) - (other.y + other.dy + other.diameter / 2);
			double nextDist = Math.sqrt(nextDistX * nextDistX + nextDistY * nextDistY);
			
			if(distance > nextDist)
				return true;
			//return true;
		}
		return false;
	}
	
	public ArrayList<Ball> split() {
		
		ArrayList<Ball> newBalls = new ArrayList<>();
		int setDiameter = diameter / 2;
		
		if(setDiameter < 1) { // split 해봤더니 공의 크기가 1보다 작아지면
			return newBalls; // 그냥 return 
		}
		
		// 공의 크기가 유효하다면 newBalls 어레이 리스트에 추가한 후 리턴하기
		// update에서 더 큰 어레이리스트에 추가될 예정임.
		newBalls.add(new Ball(box, x + setDiameter / 2, y + setDiameter / 2, dx + Math.cos(Math.PI / 4), dy + Math.sin(Math.PI / 4), setDiameter));
		newBalls.add(new Ball(box, x - setDiameter / 2, y - setDiameter / 2, -dx - Math.cos(Math.PI / 4), -dy - Math.cos(Math.PI / 4), setDiameter));
		
		return newBalls;
	}
	
	public int getDiameter() {
		return diameter;
	}
	
	public boolean isRemoved() {
		return removed;
	}
	
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
}