package OverThink;

import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.Color;
import java.awt.geom.Point2D;

import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.Robot;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.TurnCompleteCondition;
import robocode.WinEvent;



public class CarroDaPamonha extends AdvancedRobot{
	
	AdvancedEnemyBot2 enemy = new  AdvancedEnemyBot2();

	


	
	//double x;
	//double y;
	
	//AdvancedEnemyBot enemy = new AdvancedEnemyBot();
	


	@Override
	public void run() {
		iniciar();
		

		
		
		
		
		
		while(true) {
			
			turnRadarRight(20);
			
			setTurnRight(90);
			setAhead(100);
			
			//execute();
		}
	}
	


	/*
	 * O inciar é a função que será chamada somente uma vez
	 * com o objetivo de colocar as cores no robo, pegar algumas informações
	 * e configurar a questão da arma e do radar
	 */
	
	private void iniciar() {
		
	
		
		
		setBodyColor(Color.yellow);
		setGunColor(Color.yellow);
		setRadarColor(Color.yellow);
		setBulletColor(Color.yellow);
		setScanColor(Color.yellow);

		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
	}

	@Override
	public void onHitWall(HitWallEvent event) {
		setBack(150);
		setTurnRight(45);
	}
	
	


	/*
	 * Essa função é responsavel por mirar e atirar
	 * Chamando outra função, a doGun()
	 */
	public void onScannedRobot(ScannedRobotEvent e) {



		
		//gun = getGunHeading();
		//radar = getRadarHeading();
		
		
		//x = robot.getX() + Math.sin(Math.toRadians(absBearingDeg)) * e.getDistance();
		

		//rodarEmVolta();

		//mirar = normalRelativeAngleDegrees(radar - gun);

		
		//turnGunRight((normalRelativeAngleDegrees(getHeading() - getGunHeading() + e.getBearing())));

		/*
		 * 
		e.getDistance(); //pega a distancia do robo adversario
 		e.getVelocity(); //pega a velocidade do robo adversario
 		e.getEnergy(); //pega a vida do robo adversario
		*/
		//System.out.println(normalRelativeAngleDegrees(e.getHeading()));
		
		///////////////////////
		//Distance = Rate * Time → CHAVE para prever onde a BALA estará
		
		//double prever = (20 - (3*power)) * ;
		
		
		
		
		if ( enemy.none() || e.getDistance() < enemy.getDistance() - 70 ||
				e.getName().equals(enemy.getName())) {

			enemy.update(e, this);
		}
		
		if(e.getDistance() < 500) doGun();
		
				
		/*
		if (e.getDistance() > 400) {
			setTurnRight((normalRelativeAngleDegrees(getHeading() - getGunHeading() + e.getBearing())));
		}
		*/
		
		//if (e.getDistance() < 200) {
			//E o e.getVelocity() a velocidade, sendo a velocidade máxima: 8 pixels/s
			//fire(3);
		//}
		//else if (e.getDistance() > 400){
			//fire(1);

		//}
		//else{
			//fire(2);

			
			//setTurnRight(mirar);
			//Senão vire para onde o radar tava olhando e ande para frente
			
			//Mas essa parte esta muito ruim, vou modificar ainda
			//waitFor(new TurnCompleteCondition(this));
			//setAhead(100);

		//}

		//turnGunLeft(mirar);
		//turnRadarRight(90);
		
		
		//turnRadarRight((normalRelativeAngleDegrees(e.getBearing()+getHeading()-getRadarHeading())));
		//scan();
		
		//wait = false;
		confirmar();
		
		
		

	}
	
	private void confirmar() {
		//Esse é o ponto forte do meu robo, pois isso salva MUITO tempo
		
		//O confirmar irá voltar um pouco o radar após escanear o inimigo,
		//pois ele não precisara fazer um 360º
		
		turnRadarRight(-20);
		//waitFor(new RadarTurnCompleteCondition(this));
		/*
		if(!wait) {
		turnRadarRight(40);
		waitFor(new RadarTurnCompleteCondition(this));
		}
		*/
	}
	
	/*
	 * A doGun é uma função com o objetivo de mirar no inimigo e atirar
	 * envolvendo tecnicas que torna capaz o fato do algoritmo prever onde
	 * o inimigo estará
	 */
	void doGun() {

		// don't shoot if I've got no enemy
		if (enemy.none())
			return;

		// calculate firepower based on distance
		double firePower = Math.min(500 / enemy.getDistance(), 3);
		// calculate speed of bullet
		double bulletSpeed = 20 - firePower * 3;
		// distance = rate * time, solved for time
		long time = (long)(enemy.getDistance() / bulletSpeed);

		// calculate gun turn to predicted x,y location
		double futureX = enemy.getFutureX(time);
		double futureY = enemy.getFutureY(time);
		double absDeg = absoluteBearing(getX(), getY(), futureX, futureY);
		// non-predictive firing can be done like this:
		//double absDeg = absoluteBearing(getX(), getY(), enemy.getX(), enemy.getY());

		// turn the gun to the predicted x,y location
		setTurnGunRight(normalizeBearing(absDeg - getGunHeading()));

		// if the gun is cool and we're pointed in the right direction, shoot!
		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
			setFire(firePower);
		}
	}
	
	/*
	 * O absoluteBearing é a função responsavel por localizar
	 * um ponto em 360º
	 */
	double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2-x1;
		double yo = y2-y1;
		double hyp = Point2D.distance(x1, y1, x2, y2);
		double arcSin = Math.toDegrees(Math.asin(xo / hyp));
		double bearing = 0;

		if (xo > 0 && yo > 0) { // both pos: lower-Left
			bearing = arcSin;
		} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
			bearing = 360 + arcSin; // arcsin is negative here, actually 360 - ang
		} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo < 0) { // both neg: upper-right
			bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
		}

		return bearing;
	}
	
	
	/*
	 * O normalizeBearing transforma o 360º em -180º/180º
	 */
	double normalizeBearing(double angle) {
		while (angle >  180) angle -= 360;
		while (angle < -180) angle += 360;
		return angle;
	}

	


	
	
	public void onWin(WinEvent event) {
		while(true) {
			stop();
			
			//Dancinha :v
			setTurnRadarLeft(720);
			turnRight(25);
			waitFor(new TurnCompleteCondition(this));
			turnLeft(25);
			waitFor(new TurnCompleteCondition(this));
			
			
		}
	
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	/*
	 * A partir daqui, terá somente funções que normalmente estariam em suas proprias classes
	 */
	
}
	/*
	 * O EnemyBot é a função responsável por coletar informações do inimigo
	 */
	

	
	 class EnemyBot2 extends AdvancedRobot {
		
	    private volatile double bearing;
	    private volatile double distance;
	    private volatile double energy;
	    private volatile double heading;
	    private volatile String name = "";
	    private volatile double velocity;

	    public double getDistance() {
	        return distance;
	    }

	    public void setDistance(double distance) {
	        this.distance = distance;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public double getBearing() {
	        return bearing;
	    }

	    public void setBearing(double bearing) {
	        this.bearing = bearing;
	    }

	    public double getEnergy() {
	        return energy;
	    }

	    public void setEnergy(double energy) {
	        this.energy = energy;
	    }

	    public double getHeading() {
	        return heading;
	    }

	    public void setHeading(double heading) {
	        this.heading = heading;
	    }

	    public double getVelocity() {
	        return velocity;
	    }

	    public void setVelocity(double velocity) {
	        this.velocity = velocity;
	    }

	    public void reset() {
	        bearing = 0.0;
	        distance = 0.0;
	        energy = 0.0;
	        heading = 0.0;
	        name = "";
	        velocity = 0.0;

	    }


	    public boolean none() {
	        return "".equals(name);
	    }

	    public void update(ScannedRobotEvent e) {
	        bearing = e.getBearing();
	        distance = e.getDistance();
	        energy = e.getEnergy();
	        heading = e.getHeading();
	        name = e.getName();
	        velocity = e.getVelocity();
	    }

	    @Override
	    public String toString() {
	        return "EnemyBot{" +
	                "bearing=" + bearing +
	                ", distance=" + distance +
	                ", energy=" + energy +
	                ", heading=" + heading +
	                ", name='" + name + '\'' +
	                ", velocity=" + velocity +
	                '}';
	    }
	 }
    
    
    
    
    
    
    
	 
    
	 /*
	  * O AdvancedEnemyBot coleta informações mais avançadas,
	  * como coletar o futuro x e y do inimigo
	  */
     class AdvancedEnemyBot2 extends EnemyBot2{
    	


        private double x;
        private double y;

        @Override
        public void reset() {
            super.reset();

            x = 0.0;
            y = 0.0;
        }

        public void update(ScannedRobotEvent e, Robot robot) {
            super.update(e);

            double absBearingDeg = (robot.getHeading() + e.getBearing());
            if (absBearingDeg < 0) absBearingDeg += 360;

            // yes, you use the _sine_ to get the X value because 0 deg is North
            x = robot.getX() + Math.sin(Math.toRadians(absBearingDeg)) * e.getDistance();

            // yes, you use the _cosine_ to get the Y value because 0 deg is North
            y = robot.getY() + Math.cos(Math.toRadians(absBearingDeg)) * e.getDistance();
        }

        public double getFutureX(long when){
            return x + Math.sin(Math.toRadians(getHeading())) * getVelocity() * when;
        }

        public double getFutureY(long when){
            return y + Math.cos(Math.toRadians(getHeading())) * getVelocity() * when;
        }



        private double sqr(double in){
            return in * in;
        }

    
	public void onRobotDeath(RobotDeathEvent e) {
		// see if the robot we were tracking died
		if (e.getName().equals(getName())) {
			reset();
		}
	}
	
	
    
	
}
