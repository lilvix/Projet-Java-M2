import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.io.IOException;

// j'ai mit toutes les classes dans un seul fichier , car je trouve ca plus simple
// j'ai enlevé le son , du jeu , car meme dans la version de base ca faisait une erreur

class gameutil {
	//simplifier la récupération d'image , je comprend rien à ce qu'il y avait dans le programme original
	public static Image getImage(String path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(path));
		} catch (IOException e) {
            e.printStackTrace();
        }
		return img;
	}
}

class Bullet {
// ca c'est les trucs qu'on doit attraper
	double speed=15;
	double degree;
	public String type;
	public double x,y;
	public int width,height;
	Image img;

	public Bullet(String img_path,String type)  {
		img=gameutil.getImage(img_path);
		degree = Math.random()*Math.PI*2;
		x=monjeu.width/2;
		y=monjeu.height/2;
        width = 10;
        height = 10;
		this.type=type;
		if (type.equals("ennemie")){
			width = 15;
			height = 20;
		}
	}

	public void draw(Graphics g){
		g.drawImage(img, (int)x, (int)y, null);
		x += speed*Math.cos(degree);
		y += speed*Math.sin(degree);
		if(x>monjeu.width-width||x<width){
			degree=Math.PI-degree;
		}
		if(y>monjeu.height-height||y<height){
			degree=-degree;
		}
	}
}

class Plane {
// ca c'est l'image du joueure
	boolean left,right,down,up;
	public int x,y,width,height;
	Image img ;

	public void draw(Graphics g){
		g.drawImage(img, x, y, null);
		move();
	}

	public Plane(String img_path,int x, int y)  {
		this.img = gameutil.getImage(img_path);
		this.x = x;
		this.y = y;
		width = img.getWidth(null);
		height = img.getWidth(null);
	}

	public void move(){
		if(left&&x>=10){
			x -= 10;
		}
		if(up&&y>=30){
			y -= 10;
		}
		if(right&&x<=monjeu.width-60){
			x += 10;
		}
		if(down&&y<=monjeu.height -60){
			y += 10;
		}
	}

	public void KeyPressedControlDirection(KeyEvent e){
		// ca c'est pour qu'il enregistre les commandes au clavier
		int key_code = e.getKeyCode();
		if(key_code == 37){
			left = true;
		}
		if(key_code == 38){
			up = true;
		}
		if(key_code == 39){
			right = true;
		}
		if(key_code == 40){
			down = true;
		}
	}

	public void KeyRelasedControlDirection(KeyEvent e){
		int key_code = e.getKeyCode();
		if(key_code == 37){
			left = false;
		}
		if(key_code == 38){
			up =false;
		}
		if(key_code == 39){
			right = false;
		}
		if(key_code == 40){
			down = false;
		}
	}

}

class menuselector {
	boolean left, right, up, space,down;
	public int x, y, width, height,position,nb=-1,execution=0,temp=0;
	Image img ;

	public void draw(Graphics g){
		g.drawImage(img, x, y, null);
	}

	public menuselector(String img_path,int x, int y)  {
		this.img = gameutil.getImage(img_path);
		this.x = x;
		this.y = y;
		width = img.getWidth(null);
		height = img.getWidth(null);
	}

    public void movemenuprincipal(){
        if (position==2){
            this.x = 380;
            this.y=310;
        }
        if(position==1){
            this.x = 40;
            this.y=310;
        }
        if(position==3){
            this.x = 750;
            this.y=310;
        }
        if(position==10){
            this.x = 40;
            this.y=270;
        }
        if(position==19){
            this.x = 40;
            this.y=210;
        }
        if(position==28){
            this.x = 40;
            this.y=170;
        }

    }

	public void KeyPressedControlDirection(KeyEvent e) {
        //		// ca c'est pour qu'il enregistre les commandes au clavier
		int key_code = e.getKeyCode();
		if (key_code == 37) {
            this.right = false;
            this.left=true;
            this.space = false;
            e.setKeyCode(0);
            if (this.position==3){
                e.setKeyCode(0);
                this.position=2;
            }
            else {
                e.setKeyCode(0);
                this.position=1;
            }
		}
		if (key_code == 38) {
			this.up = true;
            e.setKeyCode(0);
		}
        if (key_code == 40) {
            this.down = true;
            e.setKeyCode(0);
        }
		if (key_code == 39) {
			this.right = true;
			this.left=false;
			this.space = false;
            e.setKeyCode(0);
            if (this.position==1){
                e.setKeyCode(0);
                this.position=2;
            }
            else {
                e.setKeyCode(0);
                this.position=3;
            }
		}
		if (key_code == 32 && !this.space) {
			this.space = true;
            e.setKeyCode(0);
		}
        else if (key_code == 32 && this.space){
            this.space=false;
            e.setKeyCode(0);
        }
        if (key_code >= 48 && key_code<=(57)) {
            nb=key_code-48;
            e.setKeyCode(0);
        }
	}
}

class resultat {
    public int score;
    public String nom;

    public resultat(int scorejoeure,String nomjoueur){
        this.score=scorejoeure;
        this.nom=nomjoueur;
    }

}

public class monjeu extends Frame {

	public static int width = 1079, height = 650;
	public static int compteurtour = 1, score = 0, nbcafe = 0, nbvigile = 0,maxvigile=2,maxramassable=15;
    public static int maxbonus=1 , tempsreductioncafe=5;
    int starTime,endTime=50 ;
    public String gameState ="tutoriel";

	Image bg = gameutil.getImage("src/images/scrd.jpg"); // le fond d'ecran
	Plane plane = new Plane("src/images/etud.png", 200, 200);
	menuselector menugameover = new menuselector("src/images/trait3.png", 500, 500);
	Image bg2 = gameutil.getImage("src/images/bg2.jpg");
	LinkedList<Bullet> bulletList = new LinkedList<>();

	PaintThread paintThread;

	public void loadGame() {
		// lance le jeu
		starTime = 0;
		launchFrame();
	}

	public void launchFrame() {
		setSize(width, height);
		setResizable(false);
		setLocation(200, 20);
		setVisible(true);
		addKeyListener(new KeyMoniter());
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
        gameState = "tutoriel";
		lancepartie();
	}

	public void lancepartie() {
		bulletList.clear();
		for (int i = 0; i < maxramassable; i++) {
			Bullet bullet = new Bullet("src/images/pyrd2.png", "consommable");
			bulletList.add(bullet);
		}
		menugameover.left = false;
		menugameover.right = false;
		menugameover.space = false;
		starTime = 0;
		compteurtour = 1;
		score = 0;
		nbcafe = 0;
		nbvigile = 0;
		paintThread = new PaintThread();
		paintThread.start();
	}

    public void menuprincipal(Graphics graphics){
        graphics.drawImage(bg2, 0, 0, null);
        printInfo(graphics, "Jeux du MIMO", 80, 270, 200);
        printInfo(graphics, "lancer une partie", 30, 50, 450);
        printInfo(graphics, "quitter le jeux", 30, 800, 450);
        printInfo(graphics, "paramètre", 30, 450, 450);
        addKeyListener(new KeyMonitermenu());
        if (menugameover.position==1) {
            menugameover.movemenuprincipal();
            menugameover.draw(graphics);
            if (menugameover.space) {
                paintThread.interrupt();
                gameState="jeux";
                lancepartie();
            }
        }
        if(menugameover.position==3) {
            menugameover.movemenuprincipal();
            menugameover.draw(graphics);
            if (menugameover.space) {
                paintThread.interrupt();
                System.exit(0);
            }
        }
        if (menugameover.position==2){
            menugameover.movemenuprincipal();
            menugameover.draw(graphics);
            if (menugameover.space) {
                gameState="parametre";
                menuparametre( graphics);
            }


        }
    }

    public void menuparametre(Graphics graphics){
        graphics.drawImage(bg2, 0, 0, null);
        printInfo(graphics, "Paramètre de la partie", 80, 150, 200);
        printInfo(graphics, "Retour menu principal", 30, 750, 450);
        printInfo(graphics, "Paramètre par défaut", 30, 390, 450);


        addKeyListener(new KeyMonitermenu());
        if (menugameover.position==1 || menugameover.position==10 || menugameover.position==19 || menugameover.position==28) {
            menugameover.movemenuprincipal();
            menugameover.draw(graphics);
            if (menugameover.up) {
                menugameover.up=false;
                menugameover.space=false;
                menugameover.position=menugameover.position+9;
                if (menugameover.position==37){
                    menugameover.position=28;
                }
                menugameover.draw(graphics);
            }
            if (menugameover.down){
                menugameover.down=false;
                menugameover.space=false;
                menugameover.position=menugameover.position-9;
                if (menugameover.position==-8){
                    menugameover.position=1;
                }
                menugameover.draw(graphics);
            }
        }
        if (menugameover.space && menugameover.position==1){
            printInfo(graphics, "temps de la partie : "+endTime, 30, 40, 450);
            if (menugameover.nb!=-1 && menugameover.execution==0)
            {
                menugameover.temp=menugameover.nb;
                menugameover.execution=menugameover.execution+1;
                menugameover.nb=-1;
                menugameover.execution=menugameover.execution+1;
                endTime=menugameover.temp;
            }
            if (menugameover.nb!=-1 && menugameover.execution==2){
                menugameover.temp=(menugameover.temp*10)+menugameover.nb;
                menugameover.execution=0;
                endTime=menugameover.temp;
                menugameover.temp=0;
                menugameover.nb=-1;
            }

        }
        else {
            printInfo(graphics, "temps de la partie : "+endTime, 30, 40, 450);
        }

        if (menugameover.space && menugameover.position==10){
            printInfo(graphics, "nombre de vigil : "+maxvigile,30, 40, 400);
            if (menugameover.nb!=-1 && menugameover.execution==0)
            {
                menugameover.temp=menugameover.nb;
                menugameover.execution=menugameover.execution+1;
                menugameover.nb=-1;
                menugameover.execution=menugameover.execution+1;
                maxvigile=menugameover.temp;
            }
            if (menugameover.nb!=-1 && menugameover.execution==2){
                menugameover.temp=(menugameover.temp*10)+menugameover.nb;
                menugameover.execution=0;
                maxvigile=menugameover.temp;
                menugameover.temp=0;
                menugameover.nb=-1;
            }

        }
        else {
            printInfo(graphics, "Nombre de vigil : "+maxvigile,30, 40, 400);
        }
        if (menugameover.space && menugameover.position==19){
            printInfo(graphics, "Nombre d'objet ramassable : "+maxramassable, 30, 40, 350);
            if (menugameover.nb!=-1 && menugameover.execution==0)
            {
                menugameover.temp=menugameover.nb;
                menugameover.execution=menugameover.execution+1;
                menugameover.nb=-1;
                menugameover.execution=menugameover.execution+1;
                maxramassable=menugameover.temp;
            }
            if (menugameover.nb!=-1 && menugameover.execution==2){
                menugameover.temp=(menugameover.temp*10)+menugameover.nb;
                menugameover.execution=0;
                maxramassable=menugameover.temp;
                menugameover.temp=0;
                menugameover.nb=-1;
            }

        }
        else {
            printInfo(graphics, "nombre d'objet ramassable : "+maxramassable, 30, 40, 350);
        }

        if (menugameover.space && menugameover.position==28){
            printInfo(graphics, "Nombre de bonus : "+maxbonus, 30, 40, 300);
            if (menugameover.nb!=-1 && menugameover.execution==0)
            {
                menugameover.temp=menugameover.nb;
                menugameover.execution=menugameover.execution+1;
                menugameover.nb=-1;
                menugameover.execution=menugameover.execution+1;
                maxbonus=menugameover.temp;
            }
            if (menugameover.nb!=-1 && menugameover.execution==2){
                menugameover.temp=(menugameover.temp*10)+menugameover.nb;
                menugameover.execution=0;
                maxbonus=menugameover.temp;
                menugameover.temp=0;
                menugameover.nb=-1;
            }

        }
        else {
            printInfo(graphics, "Nombre de bonus : "+maxbonus, 30, 40, 300);
        }

        if (menugameover.position==2){
            menugameover.movemenuprincipal();
            menugameover.draw(graphics);
            if (menugameover.space) {
                menugameover.space=false;
                defaultparam();
            }
        }
        if(menugameover.position==3) {
            menugameover.movemenuprincipal();
            menugameover.draw(graphics);
            if (menugameover.space) {
                menugameover.space=false;
                gameState="menuprincipal";
                menuprincipal(graphics);
            }
        }
    }


    public void tutoriel (Graphics graphics){
        graphics.drawImage(bg2, 0, 0, null);
        printInfo(graphics, "L'objectif du jeu est de marquer le maximum de points avant la fin du temps impartie", 20, 80, 300);
        printInfo(graphics, "Déplacez vous avec les flèches directionnelles dans le jeu et les menus", 20, 80, 340);
        printInfo(graphics, "Appuyez sur la bar d'espace pour sélectionner une option dans les menus", 20, 80, 380);
        printInfo(graphics, "Pour marquer un point déplacez vous sur les symboles qui se déplacent à l'écran", 20, 80, 420);
        printInfo(graphics, "Attention touchez un garde diminue le temps qu'il vous reste et prendre un café l'augmente (max 5 café)", 20, 80, 460);
        addKeyListener(new KeyMonitermenu());
        if (menugameover.space){
            gameState="menuprincipal";
        }
    }


    public void defaultparam(){
        maxbonus=1;
        maxramassable=15;
        maxvigile=2;
        endTime=50;
    }

	@Override
	public void paint(Graphics graphics) {
		// la fonction qui se répète et qui a chaque fois change ce qui est affiché à l'écran
        if (gameState.equals("menuprincipal")){
            menuprincipal(graphics);
        }
        if (gameState.equals("parametre")){
            menuparametre(graphics);
        }
        if (gameState.equals("tutoriel")){
            tutoriel(graphics);
        }
		if (gameState.equals("jeux")) {
			graphics.drawImage(bg, 0, 0, null);
			plane.draw(graphics);


		if (nbvigile < maxvigile) {
			Bullet vigile = new Bullet("src/images/vigile7040.png", "ennemie");
			bulletList.add(vigile);
			nbvigile = nbvigile + 1;
		}
		if (nbcafe < maxbonus) {
			Bullet bulletd = new Bullet("src/images/cafer.png", "powerup");
			bulletList.add(bulletd);
			nbcafe = nbcafe + 1;
		}
			for (int i = 0; i < bulletList.size(); i++) {
				Bullet bullet = bulletList.get(i);
				bullet.draw(graphics);
                // gère les collisions
				Rectangle bulletRectangle = new Rectangle((int) bullet.x, (int) bullet.y, bullet.width, bullet.height);
				Rectangle planeRectangle = new Rectangle(plane.x, plane.y, plane.width, plane.height);
				boolean collide = bulletRectangle.intersects(planeRectangle);
				if (collide) {
					if (bullet.type.equals("powerup")) {
						bulletList.remove(i);
						nbcafe = nbcafe - 1;
						starTime = starTime - tempsreductioncafe;
                        tempsreductioncafe=tempsreductioncafe-1;
                        if (tempsreductioncafe==-1){
                            tempsreductioncafe=0;
                        }
					} else if (bullet.type.equals("ennemie")) {
						bulletList.remove(i);
						nbvigile = nbvigile - 1;
						starTime = starTime + 5;
					} else {
						bulletList.remove(i);
						score = score + 1;
						//crée un nouveau ellement quand en supprime un autre , avant le jeu devenais vide
						Bullet newbullet = new Bullet("src/images/pyrd2.png", "rammassable");
						bulletList.add(newbullet);
					}
				}
			}
            //utilise le fait que avec le tread l'affichage se répète toute les 40 miliseconde ,
			// pour modifier le compteur quand on atteint 1 seconde
			// avant faisait un truc bizzare avec des format date , je fais tous en int
			if (compteurtour == 24) {
				starTime = starTime + 1;
				compteurtour = 1;
			}
			int count_time = endTime - starTime;
			printInfo(graphics, "temps restant " + count_time + " secondes", 20, 750, 50);
			compteurtour = compteurtour + 1;
			//affichage du score, y avait pas avant
			printInfo(graphics, "SCORE " + score, 20, 50, 50);

			if (starTime >= endTime) {
				// pour finir la partie
				gameState = "menufin";
			}
		}
        else if (gameState.equals("menufin")){
			gameOver(graphics);
		}
	}


	private void gameOver(Graphics graphics) {
		graphics.drawImage(bg2, 0, 0, null);
		printInfo(graphics, "Fin de la partie", 80, 400, 100);
		printInfo(graphics, "Score final " + score + " bravo", 40, 450, 150);
        printInfo(graphics, "Relancer une partie", 30, 50, 450);
		printInfo(graphics, "Quitter le jeu", 30, 800, 450);
        printInfo(graphics, "Retour menu principal", 30, 350, 450);

		addKeyListener(new KeyMonitermenu());
		if (menugameover.position==1) {
            menugameover.movemenuprincipal();
			menugameover.draw(graphics);
			if (menugameover.space) {
				paintThread.interrupt();
                gameState="jeux";
                menugameover.space=false;
				lancepartie();
		}
	}
        if (menugameover.position==2){
            menugameover.movemenuprincipal();
            menugameover.draw(graphics);
            if (menugameover.space){
                menugameover.space=false;
                gameState="menuprincipal";
                menuprincipal(graphics);
            }
        }
		if(menugameover.position==3) {
            menugameover.movemenuprincipal();
            menugameover.draw(graphics);
		if (menugameover.space) {
			paintThread.interrupt();
			System.exit(0);
		}
	}
}
		// ce qui doit s'afficher quand la partie est fini
			//lance le paint et l'affichage toute les 40 millisseconde
	Image ImageBuffer = null;
	Graphics GraImage = null;

	@Override
	public void update(Graphics g) {
		ImageBuffer = createImage(this.getWidth(), this.getHeight());
		GraImage = ImageBuffer.getGraphics();
		paint(GraImage);
		GraImage.dispose();
		g.drawImage(ImageBuffer, 0, 0, this);
	}

	class KeyMoniter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			plane.KeyPressedControlDirection(e);
		}
		@Override
		public void keyReleased(KeyEvent e) {
			plane.KeyRelasedControlDirection(e);
		}
	}

	class KeyMonitermenu extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {menugameover.KeyPressedControlDirection(e);
		}

	}

	public void printInfo(Graphics g, String message, int size, int x, int y) {
		g.setColor(Color.white);
		Font f = new Font("defaut", Font.BOLD, size);
		g.setFont(f);
		g.drawString(message, x, y);
	}

	class PaintThread extends Thread {
		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				//lance le paint et l'affichage toute les 40 millisseconde
				repaint();
				try {
					Thread.sleep(40);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

		public static void main(String[] args) {
			monjeu game = new monjeu();
			game.loadGame();
		}
	}

