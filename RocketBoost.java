import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.util.Random;
import java.awt.geom.AffineTransform;
import java.awt.image.*;

public class test extends JPanel implements ActionListener, KeyListener 
{
    public static Timer timer;

    public Image startScreen;
    public Image deathScreen;
    public Image Instructions;
    public Image rocket_thrust;
    public Image rocket_no_thrust;
    public Image background;
    public Image apple;
    public Image spike;

    String applePath = "resources/apple.wav";
    String spikePath = "resources/spike.wav";
    String backgroundPath = "resources/backgroundMusic.wav";

    public static int spikeNum;
    public static int spikeIndex;
    public static int spikeX[] = {0, 0, 0, 0, 0};
    public static int spikeY[] = {0, 0, 0, 0, 0};
    public static final int spikeRadius = 75;

    public static int appleX;
    public static int appleY;
    public static int score;
    public static int playerHealth;
    public static final int appleRadius = 50;

    public static int x;
    public static int y;
    public static double vectorMag;
    public static int vectorAngle;
    public static double vectorTheta;
    public static double vector[] = {0, 0};
    public static double xVel = 0;
    public static double yVel = 0;

    public static final double GRAVITY = 0.1;
    public static final int MAX_HORIZ_ACCEL = 7;
    public static final int MAX_VERT_ACCEL = 7;
    public static final int MAX_VEC_MAG = 7;
    public static final int PLAYER_WIDTH = 50;
    public static final int PLAYER_HEIGHT = 100;
    public static final int WINDOW_WIDTH = 1000;
    public static final int WINDOW_HEIGHT = 1000;
    public static final int DELAY_TIME = 10;

    public static boolean alive;
    public static boolean leftPressed;
    public static boolean rightPressed;
    public static boolean upPressed;
    public static boolean started;
    public static boolean quit;
    public static boolean restart;
    public static boolean instructions;


    Random rand = new Random();

    public test()
    {
        x = 500;
        y = 500;

        vectorMag = 0;
        vectorTheta = 90;
        vectorAngle = 90;
        playerHealth = 3;

        leftPressed = false;
        rightPressed = false;
        upPressed = false;
        alive = true;
        started = false;
        quit = false;
        instructions = false;
        score = 0;

        //Randomize location of first apple
        appleX = rand.nextInt(WINDOW_WIDTH - appleRadius * 2);
        appleY = rand.nextInt(WINDOW_HEIGHT - appleRadius * 2);

        spikeNum = 0;
        spikeIndex = 0;

        //Randomize location of first spike
        for(int i = 0; i < 5; i++)
        {
            spikeX[i] = rand.nextInt(WINDOW_WIDTH - spikeRadius * 2);
            spikeY[i] = rand.nextInt(WINDOW_WIDTH - spikeRadius * 2);
        }

        startScreen = new ImageIcon("resources/startScreen.png").getImage();
        deathScreen = new ImageIcon("resources/deathScreen.png").getImage();
        Instructions = new ImageIcon("resources/instructions.png").getImage();
        rocket_thrust = new ImageIcon("resources/rocket-with-thrust.png").getImage();
        rocket_no_thrust = new ImageIcon("resources/rocket-no-thrust.png").getImage();
        background = new ImageIcon("resources/background.png").getImage();
        apple = new ImageIcon("resources/apple.png").getImage();
        spike = new ImageIcon("resources/spike.png").getImage();

        playBackgroundMusic(backgroundPath);

        //Allow input
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
        //Initiate timer
        timer = new Timer(DELAY_TIME, this);
        timer.start();
    }

    public void paint(Graphics game)
    {
        Graphics2D g = (Graphics2D) game;
        //Clear screen
        super.paintComponent(g);

        if(!started)
        {
            if(instructions)
                drawInstructions(g);
            else
                drawStartScreen(g);
        }
        else
        {
            if(!alive)
                drawDeathScreen(g);
            else
            {
                //Draw background
                drawBackground(g);
                //Draw lives
                drawLives(g);
                //Draw player
                drawPlayer(g);
                //Draw apple
                drawApple(g);
                //Draw spikes
                drawSpikes(g);
            }
        }

        //Prevent low fps on linux
        Toolkit.getDefaultToolkit().sync();
    }

    public void drawPlayer(Graphics2D g)
    {
        try {
            // Attempt to load the image
            BufferedImage image = ImageIO.read(new File("resources/rocket-no-thrust.png"));
            
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();

            if(upPressed){
                AffineTransform at = AffineTransform.getTranslateInstance(x, y);
                at.translate(imageWidth/2, imageHeight/2);
                at.rotate(-Math.toRadians(vectorAngle + 270));
                at.translate(-imageWidth/2, -imageHeight/2);
                g.drawImage(rocket_thrust, at, null);
            }else if(!upPressed){
                AffineTransform at = AffineTransform.getTranslateInstance(x, y);
                at.translate(imageWidth/2, imageHeight/2);
                at.rotate(-Math.toRadians(vectorAngle + 270));
                at.translate(-imageWidth/2, -imageHeight/2);
                g.drawImage(rocket_no_thrust, at, null);
            }
        } catch (IOException e) {
            // Handle the exception if the image can't be loaded
            System.err.println("Error loading image: " + e.getMessage());
        }
    }

    public void drawApple(Graphics2D g)
    {
        g.drawImage(apple, appleX, appleY, null);
    }

    public void drawSpikes(Graphics2D g)
    {
        for(int i = 0; i < spikeNum; i++)
        {
            g.drawImage(spike, spikeX[i], spikeY[i], null);
        }
    }

    public void drawStartScreen(Graphics2D g)
    {
        g.drawImage(startScreen, 0, -20, null);
    }

    public void drawDeathScreen(Graphics2D g)
    {
        g.drawImage(deathScreen, 0, 0, null);
    }

    public void drawInstructions(Graphics2D g)
    {
        g.drawImage(Instructions, 0, -20, null);
    }

    public void drawBackground(Graphics2D g)
    {
        g.drawImage(background, 0, 0, null);
    }

    public void drawLives(Graphics2D g)
	{
        String lives = Integer.toString(playerHealth);
        Color blue = new Color(0, 125, 198);
		g.setColor(blue);
		g.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 500));

        if(playerHealth < 10){
		    g.drawString(lives, 350, 650);
        }else if(playerHealth < 100){
            g.drawString(lives, 180, 650);
        }else if(playerHealth < 1000){
            g.drawString(lives, 80, 650);
        }
	}

    public void playBackgroundMusic(String path)
    {
        try
        {
            File musicPath = new File(path);
            
            if(musicPath.exists())
            {
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(musicPath);
                Clip clip = AudioSystem.getClip();
                clip.open(inputStream);
                clip.start();
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public void playSound(String path)
    {
        try
        {
            File musicPath = new File(path);
            
            if(musicPath.exists())
            {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    //Update player position
    public void updatePlayer()
    {
        //Set boundaries
        if(x <= 0){
            x = WINDOW_WIDTH;
        }else if(x >= WINDOW_WIDTH){
            x = 0;
        }else if(y <= 0){
            y = WINDOW_HEIGHT;
        }else if(y >= WINDOW_HEIGHT){
            y = 0;
        }

        //Set max values
        if(vectorMag >= MAX_VEC_MAG)
            vectorMag = MAX_VEC_MAG;

        if(yVel >= MAX_VERT_ACCEL)
            yVel = MAX_VERT_ACCEL;

        if(yVel <= -MAX_VERT_ACCEL)
            yVel = -MAX_VERT_ACCEL;

        if(xVel <= -MAX_HORIZ_ACCEL)
            xVel = -MAX_HORIZ_ACCEL;
        
        if(xVel >= MAX_HORIZ_ACCEL)
            xVel = MAX_HORIZ_ACCEL;

        //Calculate vector direction
        vectorTheta = -Math.toRadians(vectorAngle);
        vector[0] = vectorMag * Math.cos(vectorTheta);
        vector[1] = vectorMag * Math.sin(vectorTheta);

        //Rotate the rocket
        if(leftPressed)
            vectorAngle += 4;
        if(rightPressed)
            vectorAngle -= 4;

        if(vectorAngle > 360){
            vectorAngle = 0;
        }
        if(vectorAngle < 0){
            vectorAngle = 360;
        }

        //Update player position (reduce the decimal to make the rocket feel more slippery)
        if(upPressed){
            xVel += vector[0] * 0.3;
            yVel += vector[1] * 0.3;
            vectorMag += 0.1;
        }else if(!upPressed){
            vectorMag = 0;
        }

        x += xVel;
        y += yVel;
}

    //Check if an object is touching another objext (AABB Collision Detection)
    public boolean checkTouching(int playerX, int playerY, int playerWidth, int playerHeight, int objectX, int objectY, int objectRadius)
    {
        //If player is within x bounds of the object
        if((playerX + playerWidth <= objectX + objectRadius && playerX  + playerWidth >= objectX) || 
           (playerX >= objectX && playerX <= objectX + objectRadius))
           {
            //If player is within y bounds of the object
			if((playerY + playerHeight <= objectY + objectRadius && playerY + playerHeight >= objectY) || 
               (playerY >= objectY && playerY <= objectY + objectRadius)){
				return true;
			}
		}
        return false;
    }

    //Check if an object is touching another object (SAT Collision Detection)
    public static boolean checkTouchingSAT(float verticesA [][], float verticesB [][])
    {
        float A_X, A_Y, B_X, B_Y, Edge_X, Edge_Y, Axis_X, Axis_Y;

        float MinMax_A[];
        float MinMax_B[];

        //Loop through the vertices of object A
        for(int i = 0; i < verticesA.length; i++){
            //Object A first point
            A_X = verticesA[i][0];
            A_Y = verticesA[i][1];

            //Object A next point
            B_X = verticesA[(i + 1) % verticesA.length][0];
            B_Y = verticesA[(i + 1) % verticesA.length][1];

            //Subtract points to find edge vector
            Edge_X = B_X - A_X;
            Edge_Y = B_Y - A_Y;

            //Normalize it to find axis of projection
            Axis_X = -Edge_Y;
            Axis_Y = Edge_X;

            //Project edges of both objects
            MinMax_A = ProjectVertices(verticesA, Axis_X, Axis_Y);
            MinMax_B = ProjectVertices(verticesB, Axis_X, Axis_Y);

            //If there is a gap the shapes are not touching
            if(MinMax_A[0] >= MinMax_B[1] || MinMax_B[0] >= MinMax_A[1])
            {
                return false;
            }
        }
        //Loop through the vertices of object B
        for(int i = 0; i < verticesB.length; i++){
            //Object B first point
            A_X = verticesB[i][0];
            A_Y = verticesB[i][1];

            //Object B next point
            B_X = verticesB[(i + 1) % verticesB.length][0];
            B_Y = verticesB[(i + 1) % verticesB.length][1];

            //Subtract points to find edge vector
            Edge_X = B_X - A_X;
            Edge_Y = B_Y - A_Y;

            //Normalize it to find axis of projection
            Axis_X = -Edge_Y;
            Axis_Y = Edge_X;

            //Project edges of both objects
            MinMax_A = ProjectVertices(verticesA, Axis_X, Axis_Y);
            MinMax_B = ProjectVertices(verticesB, Axis_X, Axis_Y);

            //If there is a gap the shapes are not touching
            if(MinMax_A[0] >= MinMax_B[1] || MinMax_B[0] >= MinMax_A[1])
            {
                return false;
            }
        }
        return true;
    }

    //Find all the rotated and transformed vertices of a given rectangle
    public static float[][] findVertices(float x, float y, float playerWidth, float playerHeight, float angle)
    {
        /* P1       P2
            ---------
         * |        |
         * |        |     
         * |        |
         * |        |
         * |        |
         * ----------
         * P4       P3
         */
        float vertices [][] = {{0, 0}, {0, 0}, {0, 0}, {0, 0}};

        float tempX = 0;
        float tempY = 0;

        vertices[0][0] = x;  //Point 1
        vertices[0][1] = y;

        vertices[1][0] = vertices[0][0] + playerWidth;  //Point 2
        vertices[1][1] = vertices[0][1];

        vertices[2][0] = vertices[0][0] + playerWidth; //Point 3
        vertices[2][1] = vertices[0][1] + playerHeight;

        vertices[3][0] = vertices[0][0];                //Point 4
        vertices[3][1] = vertices[0][1] + playerHeight;

        //Loop through vertices
        for(int i = 0; i < vertices.length; i++){ 
            vertices[i][0] -= x + (playerWidth/2); //Subtract the point of rotation from the x and y of the vector
            vertices[i][1] -= y + (playerHeight/2);

            //Perform the rotations and add the point of rotation back onto the vertices
            //Xprime = x * cos(theta) - y * sin(theta)
            tempX = vertices[i][0]*(float)(Math.cos(Math.toRadians(-angle))) - vertices[i][1]* (float)(Math.sin(Math.toRadians(-angle)));
            tempX += x + (playerWidth/2);;

            //Yprime = x * sin(theta) + y * cos(theta)
            tempY = vertices[i][0]*(float)(Math.sin(Math.toRadians(-angle))) + vertices[i][1]*(float)(Math.cos(Math.toRadians(-angle)));
            tempY += y + (playerHeight/2);;
            
            //Subtract the point of rotation from the x and y of the vector
            vertices[i][0] = tempX;
            vertices[i][1] = tempY;
        }

        return vertices;
    }

    public static float[] ProjectVertices(float verticesA[][], float Axis_X, float Axis_Y)
    {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        float x;
        float y;
        float proj;
        float result[] = {0, 0};

        //Loop through object vertices
        for(int i = 0; i < verticesA.length; i++)
        {
            x = verticesA[i][0];
            y = verticesA[i][1];

            //Project the point
            proj = (x * Axis_X) + (y * Axis_Y);
            
            //Check if its a min or max
            if(proj < min) {min = proj;}
            if(proj > max) {max = proj;}
        }

        result[0] = min;
        result[1] = max;

        return result;
    }

    //Update spike locations after player touches it
    public void updateSpikes()
    {
        //Number of spikes increases with score
        if(score >= 25)
            spikeNum = 5;
        else if(score >= 20)
            spikeNum = 4;
        else if(score >= 15)
            spikeNum = 3;
        else if(score >= 10)
            spikeNum = 2;
        else if(score >= 5)
            spikeNum = 1;

        boolean isTouching;
        //If player is touching spike move it to a random location, update health and play damage noise
        for(int i = 0; i < spikeNum; i++)
        {
            if((isTouching = checkTouchingSAT(findVertices(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, vectorAngle + 270),
             findVertices(spikeX[i], spikeY[i], spikeRadius, spikeRadius, 0))) == true)
            {
                spikeX[i] = rand.nextInt(WINDOW_WIDTH - spikeRadius * 2);
                spikeY[i] = rand.nextInt(WINDOW_HEIGHT - spikeRadius * 2);
                playerHealth -= 1;
                if(playerHealth <= 0)
                    alive = false;
                playSound(spikePath);
            }
        }
    }

    //Update apple and spike location after player touches it
    public void updateApple()
	{
		//Check if player is touching apple
		boolean isTouching = checkTouchingSAT(findVertices(x, y, PLAYER_WIDTH, PLAYER_HEIGHT, vectorAngle + 270),
        findVertices(appleX, appleY, appleRadius, appleRadius, 0));

        //If player is touching it randomize apple location, update player health, play apple sound and move one spike
        if(isTouching)
        {
            appleX = rand.nextInt(WINDOW_WIDTH - appleRadius * 2);
            appleY = rand.nextInt(WINDOW_HEIGHT - appleRadius * 2);

            score++;
            playerHealth += 1;
            playSound(applePath);
            
            //Loop through which spike changes location
            if(spikeIndex >= spikeNum)
                spikeIndex = 0;

            spikeX[spikeIndex] = rand.nextInt(WINDOW_WIDTH - spikeRadius * 2);
            spikeY[spikeIndex] = rand.nextInt(WINDOW_HEIGHT - spikeRadius * 2);

            spikeIndex++;
        }
	}

    //Called whenever an actionevent is triggered by the timer
    public void actionPerformed(ActionEvent e)
    {
        //Update the game variables
        updatePlayer();
        updateApple();
        updateSpikes();
        //Refresh the screen
        repaint();
    }

    //Reset game values
    public void restart()
    {
        score = 0;
        spikeNum = 0;
        playerHealth = 3;

        x = 500;
        y = 500;
        xVel = 0;
        yVel = 0;
        vectorAngle = 90;

        appleX = rand.nextInt(WINDOW_WIDTH - appleRadius * 2);
        appleY = rand.nextInt(WINDOW_HEIGHT - appleRadius * 2);
    }

    //Take inputs
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e)
    {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_LEFT){
            leftPressed = true;
        }else if(code == KeyEvent.VK_RIGHT){
            rightPressed = true;
        }else if(code == KeyEvent.VK_UP){
            upPressed = true;
        }else if(code == KeyEvent.VK_ENTER){
            if(!started && !instructions){
                started = true;
                alive = true;
            }
        }else if(code == KeyEvent.VK_I){
            instructions = true;
        }else if(code == KeyEvent.VK_R){
            if(!instructions){
                started = true;
                alive = true;
            }
        }else if(code == KeyEvent.VK_Q){
            if(!started && instructions){
                instructions = false;
            }else if(started){
                started = false;
            }else if(!started){
                System.exit(0);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) 
    {
        int code = e.getKeyCode();

        if(code == KeyEvent.VK_LEFT){
            leftPressed = false;
        }else if(code == KeyEvent.VK_RIGHT){
            rightPressed = false;
        }else if(code == KeyEvent.VK_UP){
            upPressed = false;
        }else if(code == KeyEvent.VK_ENTER){
        }else if(code == KeyEvent.VK_R){
            restart = false;
            if(!instructions)
                restart(); //Odd place to put function but its to avoid restarting multiple times due to restart being true multiple times per keypress
        }
    }        

    //Create JFrame
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Brooks little flappy game :)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.add(new test());
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }
}