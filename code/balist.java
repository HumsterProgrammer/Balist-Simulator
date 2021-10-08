/*@author DenisKochetkov
@ver 1.0
*/

/*
Специальные клавиши:\n
+ 0 - возращение к стартовому положению точки отсчета\n
+ g - добавление сетки\n
+ f5 - запуск остановка симуляции\n
*/

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.MouseInputListener;

import java.util.Date;
import java.util.ArrayList;
import java.util.Scanner;

import java.net.URI;

import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class balist{
	private int fps = 100;
	public String working_file = "./in.txt";
	
	private	Graph g;
	private JFrame f;
	private Balist_Redactor br = new Balist_Redactor();
	
	public int sost = 0;
	
	public static final int Redactor = 0;
	public static final int Simulator = 1;
	
	balist(){
		f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(1000,800);
		
		g = new Graph(this);
		g.setBounds(-10,0,1000,800);
		g.setFocusable(true);
		SpecListener sl = new SpecListener();
		g.addSpecifedListener(sl);
		f.add(g);
		
		g.readMass(working_file);
	
		
		f.setMenuBar(this.br);
		
		f.setLayout(null);
		f.setVisible(true);
		
		RepaintManager rm = new RepaintManager();
		long delt = 0;
		while(true){
			Date d0 = new Date();
			f.repaint(0,0,f.getWidth(),f.getHeight());
			g.setBounds(0,0,f.getWidth(), f.getHeight());
			g.update(delt);
			rm.paintDirtyRegions();
			Date d = new Date();
			delt = d.getTime() - d0.getTime();
			try{
				Thread.currentThread().sleep(1000/fps-delt);
				delt = 1000/fps;
			}catch(Exception e){System.out.println(e);}
			if(sost == Redactor){
				delt = 0;
			}
			sl.listenerSost();
		}
		
	}
	public static void main(String[] args){
		new balist();
	}
	public class SpecListener implements SpecifedListener{ // функция опроса и реакции на события
		private int x0 = 0;
		private int y0 = 0;
		private boolean canDo = true;
		
		public void listenerSost(){
			if(!canDo) return;
			int e = br.getSost();
			if(sost == Redactor){
				if(e == 0){ // ничего
					
				}else if(e == 1){// создать
					g.removeAllMass();
					
				}else if(e == 2){// открыть
					FileDialog fd = new FileDialog(f,"open",FileDialog.LOAD);
					fd.setDirectory("./");
					fd.setFile("*.txt");
					fd.setVisible(true);
					String filename = fd.getFile();
					if(filename != null){
						working_file = fd.getFiles()[0].getPath();
					}
					g.removeAllMass();
					g.readMass(working_file);
				}else if(e == 3){// сохранить
					FileDialog fd = new FileDialog(f,"save",FileDialog.SAVE);//создание диалогового окна или стандартного окна сохранения файла в директории
					fd.setDirectory("./");
					fd.setFile("new.txt");
					fd.setVisible(true);
					String filename = fd.getFile();
					if(filename != null){
						working_file = fd.getFiles()[0].getPath();
					}
					g.writeMass(working_file);
				}else if(e == 4){// добавить новую точку
					g.addMass(1,0,0,10,1);
				}else if(e == 5){// настроить скорость в данный момент функция недоступна, так как работает с ошибками
					boolean isTrue = true;
					ArrayList<Mass> a = g.getMass();
					for(int i =0; i< a.size(); i++){
						if(a.get(i).getOtsl()){
							isTrue = false;
							if(a.get(i).getType() == 2){
								InputWindow iw = new InputWindow(this,f, new String[]{"Начальная координата x","Начальная координата y", "Координата точки x","Координата точки y"}, new String[]{
									Double.toString(a.get(i).getX0()),
									Double.toString(a.get(i).getY0()),
									Double.toString(g.getDotes().get(a.get(i).getDote())[0]),
									Double.toString(g.getDotes().get(a.get(i).getDote())[1])
								});
								this.canDo = false;
								while (!iw.isDown()); 
								this.canDo = true; 
								double[] d = iw.getParams();
								g.setMinMass(i, d[0],d[1],d[2],d[3]);
								iw.dispose();
							}else{
								InputWindow iw = new InputWindow(this,f, new String[]{"Начальная координата x","Начальная координата y", "скорость","угол"}, new String[]{
									Double.toString(a.get(i).getX0()),Double.toString(a.get(i).getY0()),Double.toString(a.get(i).getV0()),Double.toString(a.get(i).getA0())
								});
								this.canDo = false;
								while (!iw.isDown()); // не выходит из цикла. В чем проблема?
								this.canDo = true; 
								double[] d = iw.getParams();
								g.setMass(i,1,d[0],d[1],d[2],Math.tan(Math.toRadians(d[3])));
								iw.dispose();
							}
							break;
						}
					}
					if(isTrue){
						JOptionPane.showMessageDialog(null, "Точка не выбрана. Выберите точку, с помощью левой клавиши мыши", "ErrorMessage",JOptionPane.INFORMATION_MESSAGE);
					}
					
				}else if(e == 6){//добавить минимальную точку
					g.addMinMass(0,0,10,10);
				}else if(e == 7){ // настройки
					JOptionPane.showMessageDialog(null, "Специальные клавиши:\n+ 0 - возращение к стартовому положению точки отсчета\n+ g - добавление сетки\n+ f5 - запуск остановка симуляции\n","Управление",JOptionPane.INFORMATION_MESSAGE);
				}else{
					
				}
			}
			if(e == 8){// справка
				try{
					Desktop.getDesktop().browse(new URI("https://www.youtube.com/watch?v=va0u0o0cc1A"));
				}catch(Exception err){}
			}
		}
		
		public void mouseExited(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseReleased(MouseEvent e){}
		public void mousePressed(MouseEvent e){
			if(!canDo) return;
			if(e.getButton() == MouseEvent.BUTTON3){
				this.x0 = e.getX();
				this.y0 = e.getY();
			}else if(e.getButton() == MouseEvent.BUTTON1){
				g.findInfo(e.getX(),e.getY());
			}
		}
		public void mouseClicked(MouseEvent e){}
		public void mouseWheelMoved(MouseWheelEvent e){
			if(!canDo) return;
			double k = e.getWheelRotation()/10.0;
			double dx = (e.getX()-g.getCenterX())*k;
			double dy = (e.getY()-g.getCenterY())*k;
			g.updateMp(1-k);
			g.updateCenter((int)dx,(int)dy);
		}
		public void mouseMoved(MouseEvent e){}
		public void mouseDragged(MouseEvent e){
			if(!canDo) return;
			if((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0){
				g.updateCenter(e.getX()-this.x0, e.getY()-this.y0);
				this.x0 = e.getX();
				this.y0 = e.getY();
			}
		}
		public void keyPressed(KeyEvent e){}
		public void keyReleased(KeyEvent e){
			if(!canDo) return;
			if(e.getKeyCode() == KeyEvent.VK_0){
				g.goToStart();
			}else if(e.getKeyCode() == KeyEvent.VK_F5){
				if(sost == Redactor){sost = Simulator;
				}else{
					sost = Redactor;
					g.toZero();
				}
			}else if(e.getKeyCode() == KeyEvent.VK_G){
				g.reverseGrid();
			}
		}
		public void keyTyped(KeyEvent e){}
		public void setCanDo(boolean c){this.canDo = c;}
}
}

class Graph extends Component{ // класс симуляции
	private double meter_pixels = 10;
	private int start_x = 500;
	private int start_y = 400;
	private boolean isGrid = false;
	
	private int center_x = this.start_x; // координата точки отсчета
	private int center_y = this.start_y;
	
	private balist parent;
	
	private double g = -9.8; // константа свободного ускорения
	
	private ArrayList<Mass> a = new ArrayList<Mass>(); // массив всех симулируемых точек
	private ArrayList<Double[]> d = new ArrayList<Double[]>(); // точки в которые должен попасть объект
	
	public Graph(balist p){
		this.parent = p;
	}
	
	public void addMass(int type, double x, double y, double v, double tg){ // функции добавления точек
		this.a.add(new Mass(type,-1,x,y,v,tg));
	}
	public void setMass(int index, int type, double x, double y, double v,double tg){
		this.a.set(index, new Mass(type, -1, x,y,v,tg));
	}
	public void addMinMass(double x0, double y0, double x, double y){
		double v = g*(y-y0 + Math.sqrt((x-x0)*(x-x0)+(y-y0)*(y-y0)));
		int k = 1;
		if(x< x0)  k = -1;
		this.a.add(new Mass(2, d.size(),x0, y0, k*Math.sqrt((-1)*v), v/g/(x-x0)));
		this.d.add(new Double[]{x,y});
	}
	public void setMinMass(int index, double x0, double y0, double x, double y){
		int i = this.a.get(index).getDote();
		double v = g*(y-y0 + Math.sqrt((x-x0)*(x-x0)+(y-y0)*(y-y0)));
		int k = 1;
		if(x< x0) k = -1;
		this.a.set(index, new Mass(2, i,x0, y0, k*Math.sqrt((-1)*v), v/g/(x-x0)));
		this.d.set(i, new Double[]{x,y});
	}
	
	public void removeAllMass(){this.a.clear(); this.d.clear(); }
	public void readMass(String path){ // загрузка информации из файла, по умолчанию загружается из in.txt
		try{
			Scanner scan = new Scanner(new FileInputStream(path));
			while(scan.hasNextLine()){
				int n = scan.nextInt();
				if(n == 1){ // по скорости и углу
					this.addMass(1,scan.nextDouble(),scan.nextDouble(), scan.nextDouble(), scan.nextDouble());
				}else if(n == 2){ // по точке
					this.addMinMass(scan.nextDouble(), scan.nextDouble(),scan.nextDouble(),scan.nextDouble());
				}
			}
			
			scan.close();
		}catch(Exception e){JOptionPane.showMessageDialog(null, "Файл не найден", "ErrorMessage",JOptionPane.INFORMATION_MESSAGE);}
	}
	public void writeMass(String path){ // сохранение всей информации о точках
		try{
			File f = new File(path);
			f.createNewFile();
			FileOutputStream os = new FileOutputStream(f);
			
			for(int i = 0; i< a.size()-1; i++){
				char[] text = new char[5];
				if(a.get(i).getType() == 2){
					text = (2+" "+String.format("%.2f",a.get(i).getX0())+" " + String.format("%.2f",a.get(i).getY0()) + " " + d.get(a.get(i).getDote())[0]+" "+d.get(a.get(i).getDote())[1]+"\n").toCharArray();
				}else if(a.get(i).getType() == 1){
					text = (a.get(i).toString()+"\n").toCharArray();
				}
				
				for(Character q:text){
					os.write(Character.hashCode(q));
				}
			}
			char[] text = new char[5];
			int i = a.size()-1;
			if(a.get(i).getType() == 2){
				text = (2+" "+String.format("%.2f",a.get(i).getX0())+" " + String.format("%.2f",a.get(i).getY0()) + " " + d.get(a.get(i).getDote())[0]+" "+d.get(a.get(i).getDote())[1]).toCharArray();
			}else if(a.get(i).getType() == 1){
				text = a.get(i).toString().toCharArray();
			}
			
			for(Character q:text){
				os.write(Character.hashCode(q));
			}
			
		}catch(IOException e){JOptionPane.showMessageDialog(null, "Файл не найден", "ErrorMessage",JOptionPane.INFORMATION_MESSAGE);}
	}
	public void addSpecifedListener(SpecifedListener e){
		this.addMouseListener(e);
		this.addMouseMotionListener(e);
		this.addMouseWheelListener(e);
		this.addKeyListener(e);
	}
	public void removeSpecifedListener(SpecifedListener e){
		this.removeMouseListener(e);
		this.removeMouseMotionListener(e);
		this.removeMouseWheelListener(e);
		this.removeKeyListener(e);
	}
	public void update(long delt){
		if(delt == 0) return;
		for(Mass i:a){
			i.update(g,delt,this.meter_pixels);
		}
	}
	public void toZero(){
		for(Mass i: a){
			i.goToStart();
		}
	}
	public boolean findInfo(int x, int y){ // поиск точки на которую нажал пользователь
		double deltp = 0.5;
		double rx = (x-this.center_x)/this.meter_pixels;
		double ry = (this.center_y-y)/this.meter_pixels;
		
		boolean isTrue = true;
		for(Mass i: a){
			if(isTrue == true){
				double maxX = i.getX()+i.getW()+deltp;
				double minX = i.getX()-i.getW()-deltp;
				double maxY = i.getY()+i.getW()+deltp;
				double minY = i.getY()-i.getW()-deltp;
				if(rx> minX && rx<maxX){
					if(ry> minY && ry<maxY){
						i.setOtsl(true);
						isTrue = false;
						continue;
					}
				}
			}
			i.setOtsl(false);
		}
		return !isTrue;
	}
	
	
	public void goToStart(){ // возвращение в начальное положение точки отсчета
		this.center_x = this.start_x;
		this.center_y = this.start_y;
	}
	public void updateMp(double k){
		this.meter_pixels*=k;
	}
	public void updateCenter(int dx, int dy){
		this.center_x += dx;
		this.center_y += dy;
	}
	public void reverseGrid(){
		this.isGrid = !this.isGrid;
	}
	public int getCenterX(){return this.center_x;}
	public int getCenterY(){return this.center_y;}
	public ArrayList<Mass> getMass(){return this.a;} // возвращает ссылку на массив всех точек симуляции
	public ArrayList<Double[]> getDotes(){return this.d;}
	
	@Override
	public void paint(Graphics g){ // метод отрисовки
		g.setColor(Color.black);
		g.fillRect(0,0,this.getWidth(),this.getHeight());
		
		if(this.isGrid){
			double delt = 1*meter_pixels;
			while (delt <= 10){
				delt *= 5;
			}
			int d = (int)delt;
			
			int shift_x, shift_y;
			if(this.center_x<0){
				shift_x = d - (-1)* center_x %d;
			}else{
				shift_x = center_x % d;
			}
			if(this.center_y<0){
				shift_y = d - (-1)*center_y %d;
			}else{
				shift_y = center_y % d;
			}
			g.setColor(Color.gray);
			for(int x = shift_x; x< this.getWidth(); x+= d){
				g.drawLine(x, 0, x, this.getHeight());
			}
			for(int y = shift_y; y< this.getHeight(); y+= delt){
				g.drawLine(0,y,this.getWidth(), y);
			}
			g.setColor(Color.white);
			g.drawString("Метров в Сетке "+d/this.meter_pixels, 10, 15);
		}
		g.setColor(Color.white);
		g.drawLine(center_x, center_y, (int)(center_x+meter_pixels), center_y);
		g.drawLine(center_x, center_y, center_x, (int)(center_y-meter_pixels));
		
		g.setColor(Color.orange);
		for(Double[] c: d){
			g.fillRect((int)(center_x+meter_pixels*c[0]),(int)(center_y - meter_pixels*c[1]),3,3);
		}
		
		
		
		for(int i = 0; i< a.size(); i++){
			g = a.get(i).paint(g,this.meter_pixels, this.center_x, this.center_y);
			if(a.get(i).getOtsl()){
				g.setColor(Color.white);
			}else{
				g.setColor(a.get(i).getColor());
			}
			
			g.drawString("скорость V0 = "+a.get(i).getV0()+" м/с", 10, 30+i*30);
			g.drawString("угол a0 = "+a.get(i).getA0()+" °", 10, 45+i*30);
		}
		
		
		g.setColor(Color.white);
		if(this.parent.sost == balist.Simulator){
			g.drawString("Симуляция", this.getWidth()- 140, 15);
			g.drawString("нажмите f5 для редактирования", this.getWidth() - 160,30);
		}else if(this.parent.sost == balist.Redactor){
			g.drawString("Редактирование", this.getWidth()- 140, 15);
			g.drawString("Нажмите f5 для симуляции", this.getWidth() - 160, 30);
		}
	}
}
class Mass{ // класс точки
	private double x = 0; // текущее положение по x
	private double y = 0; // текущее положение по y
	
	private double x0 = 0; // начальное положение по x
	private double y0 = 0; // начальное положение по y
	
	private double vx = 0;
	private double vy = 0;
	
	private double v0 = 0; // начальная скорость
	private double tg = 0;
	private double a0 = 0;
	
	private double w = 1;
	
	private Color color = Color.red;
	private int type = 0;
	private int dote = 0;
	
	private boolean otsl = false;
	
	private ArrayList<Double> x_t = new ArrayList<Double>();
	private ArrayList<Double> y_t = new ArrayList<Double>();
	
	
	Mass(int type,int dote, double x, double y, double v, double tg){
		this.v0 = v;
		this.tg = tg;
		this.a0 = Math.atan(tg);
		double cos = Math.cos(a0);//Math.sqrt(1/(tg*tg+1));
		double sin = Math.sin(a0);//Math.sqrt(1-cos*cos);
		this.a0 = Math.toDegrees(a0);
		
		this.x0 = x;
		this.y0 = y;
		this.x = x;
		this.y = y;
		this.vx = v*cos;
		this.vy = v*sin;
		this.type = type;
		
		this.dote = dote;
	}
	
	public void update(double g, long delt, double meter_pixels){ // метод обновления положения точек
		this.x_t.add(this.x);
		this.y_t.add(this.y);
		double d = delt/1000.0;
		this.vy += d*g;
		this.x += vx*d;
		this.y += vy*d;
	}
	public Graphics paint(Graphics g, double meter_pixels, int center_x, int center_y){ // метод отрисовки точек
		g.setColor(Color.white);
		for(int i =1 ; i< this.x_t.size(); i++){
			g.drawLine(center_x+(int)(this.x_t.get(i-1)*meter_pixels), center_y-(int)(this.y_t.get(i-1)*meter_pixels), center_x+(int)(this.x_t.get(i)*meter_pixels), center_y-(int)(this.y_t.get(i)*meter_pixels));
		}
		g.setColor(Color.red);
		g.fillRect(center_x+(int)((this.x-this.w)*meter_pixels), center_y-(int)((this.y+this.w)*meter_pixels), (int)(2*this.w*meter_pixels), (int)(2*this.w*meter_pixels));
		if(this.otsl){
			g.setColor(Color.white);
			g.drawRect(center_x+(int)((this.x-this.w)*meter_pixels), center_y-(int)((this.y+this.w)*meter_pixels), (int)(2*this.w*meter_pixels), (int)(2*this.w*meter_pixels));
		}
		
		g.setColor(Color.blue);
		g.drawLine(center_x+(int)(this.x*meter_pixels),center_y-(int)(this.y*meter_pixels),center_x+(int)((this.x+this.vx)*meter_pixels),center_y-(int)((this.y+this.vy)*meter_pixels));
		return g;
	}
	
	public void setV(double v){this.v0 = v;}
	public void setTg(double tg){this.tg = tg;}
	public void goToStart(){
		this.x = this.x0;
		this.y = this.y0;
		this.a0 = Math.toRadians(a0);
		double cos = Math.cos(a0);//Math.sqrt(1/(tg*tg+1));
		double sin = Math.sin(a0);//Math.sqrt(1-cos*cos);
		this.a0 = Math.toDegrees(a0);
		
		this.vx = this.v0*cos;
		this.vy = this.v0*sin;
		
		this.x_t.clear();
		this.y_t.clear();
	}
	public void setOtsl(boolean x){this.otsl = x;}
	
	public double getV0(){return this.v0;}
	public double getTg(){return this.tg;}
	public double getA0(){return this.a0;}
	public double getX0(){return this.x0;}
	public double getY0(){return this.y0;}
	public double getX(){return this.x;}
	public double getY(){return this.y;}
	public double getW(){return this.w;}
	public Color getColor(){return this.color;}
	public int getType(){return this.type;}
	public String toString(){return this.type +" "+ String.format("%.2f",this.x0) + " "+ String.format("%.2f",this.y0) +" "+ String.format("%.2f",this.v0)+" "+ String.format("%.2f",this.tg);}
	public boolean getOtsl(){return this.otsl;}
	public int getDote(){return this.dote;}
}

class Balist_Redactor extends MenuBar implements ActionListener{ // Класс верхнего меню окна
	private Menu file, sim, help;
	private MenuItem create, open, save, add, min, speed, setting, reference;
	
	private int sost = 0;
	
	Balist_Redactor(){
		file = new Menu("Файл");
		sim = new Menu("Симуляция");
		help = new Menu("?");
		
		create = new MenuItem("Создать");
		create.addActionListener(this);
		open = new MenuItem("Открыть");
		open.addActionListener(this);
		save = new MenuItem("Сохранить");
		save.addActionListener(this);
		
		file.add(create); file.add(open); file.add(save);
		
		add = new MenuItem("Добавить произвольную точку");
		add.addActionListener(this);
		min = new MenuItem("Добавить точку с минимальной скоростью");
		min.addActionListener(this);
		speed = new MenuItem("Настроить параметры точки"); // изменение параметров точки - не работает из-за возникающей ошибки
		speed.addActionListener(this);
		
		
		sim.add(add); sim.add(min); sim.add(speed);
		
		setting = new MenuItem("Справка"); // справка
		setting.addActionListener(this);
		reference = new MenuItem("Теория о балистике");
		reference.addActionListener(this);
		
		 help.add(reference); help.add(setting);
		
		this.add(file); this.add(sim);this.add(help);
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == create){
			this.sost = 1;
		}else if(e.getSource() == open){
			this.sost = 2;
		}else if(e.getSource() == save){
			this.sost = 3;
		}else if(e.getSource() == add){
			this.sost = 4;
		}else if(e.getSource() == speed){
			this.sost = 5;
		}else if(e.getSource() == min){
			this.sost = 6;
		}else if(e.getSource() == setting){
			this.sost = 7;
		}else if(e.getSource() == reference){
			this.sost = 8;
		}
		else{
			this.sost = 0;
		}
	}
	public int getSost(){
		if(this.sost == 0){
			return 0;
		}
		int x = this.sost+1;
		this.sost = 0;
		
		return x-1;
	}
} 

interface SpecifedListener extends MouseInputListener,MouseWheelListener, KeyListener{
		public void mouseExited(MouseEvent e);
		public void mouseEntered(MouseEvent e);
		public void mouseReleased(MouseEvent e);
		public void mousePressed(MouseEvent e);
		public void mouseClicked(MouseEvent e);
		public void mouseWheelMoved(MouseWheelEvent e);
		public void mouseMoved(MouseEvent e);
		public void mouseDragged(MouseEvent e);
		public void keyPressed(KeyEvent e);
		public void keyReleased(KeyEvent e);
		public void keyTyped(KeyEvent e);
		public void setCanDo(boolean c);
}


class InputWindow extends Dialog implements ActionListener, WindowListener{ // Окно для изменения параметров точек
	private String[] params;
	private TextField[] tf;
	private Label[] l;
	private double[] inParam;
	
	private SpecifedListener sl;
	
	private boolean isDown = false;
	InputWindow(SpecifedListener sl, Frame f, String[] param_name){ // объявление без базовых значений заполненных в полях
		super(f, "Параметры");
		this.setSize(300,400);
		this.setLocation(30,30);
		this.toFront();
		this.addWindowListener(this);
		
		this.sl = sl;
		this.params = param_name;
		this.tf = new TextField[param_name.length];
		this.l = new Label[param_name.length];
		this.inParam = new double[param_name.length];
		
		for(int i = 0; i< param_name.length; i++){
			this.l[i] = new Label(param_name[i]);
			this.l[i].setBounds(20, i *35+40, 150, 20);
			this.tf[i] = new TextField();
			this.tf[i].setBounds(20, i*35+ 60,150, 20);
			this.add(this.l[i]); this.add(this.tf[i]);
		}
		Button b = new Button("Сохранить");
		b.setBounds(100,(param_name.length+1) * 35 + 10, 30, 20);
		b.addActionListener(this);
		this.add(b);
		this.add(new Label("")); // почему последний добавленный элемент обязательно становится на задний фор и растягивается на все окно?
		
		this.setVisible(true);
	}
	InputWindow(SpecifedListener sl, Frame f, String[] param_name, String[] base_param){ // объявление с базовыми значениями
		super(f, "Параметры");
		this.setSize(300,400);
		this.setLocation(30,30);
		this.toFront();
		this.addWindowListener(this);
		
		this.sl = sl;
		this.params = param_name;
		this.tf = new TextField[param_name.length];
		this.l = new Label[param_name.length];
		this.inParam = new double[param_name.length];
		
		for(int i = 0; i< param_name.length; i++){
			this.l[i] = new Label(param_name[i]);
			this.l[i].setBounds(20, i *35+40, 150, 20);
			this.tf[i] = new TextField(base_param[i]);
			this.tf[i].setBounds(20, i*35+ 60,150, 20);
			this.add(this.l[i]); this.add(this.tf[i]);
		}
		Button b = new Button("Сохранить");
		b.setBounds(100,(param_name.length+1) * 35 + 10, 60, 20);
		b.addActionListener(this);
		this.add(b);
		this.add(new Label(""));
		
		this.setVisible(true);
	}
	
	public double[] getParams(){return this.inParam;}
	public boolean isDown(){System.out.println(1);return this.isDown;} // почему-то без вывода не работает эта функция
	public void actionPerformed(ActionEvent e){
		this.saveParams();
	}
	private void saveParams(){
		for(int i = 0; i< params.length; i++){
			this.inParam[i] = Double.parseDouble(this.tf[i].getText());
		}
		this.isDown = true;
	}
	public void windowActivated(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowClosing(WindowEvent e){this.saveParams();this.sl.setCanDo(true);this.isDown = false;this.dispose();}// когда окно закрывается програамма полностью ломается
	public void windowDeactivated(WindowEvent e){}
	public void windowDeiconified(WindowEvent e){}
	public void windowIconified(WindowEvent e){}
	public void windowOpened(WindowEvent e){}
}