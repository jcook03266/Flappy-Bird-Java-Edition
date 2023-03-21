package tappy_bird;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**Custom GUI Project, Java Implementation of the world famous mobile game 'Flappy Bird' 
 * @author Justin Cook, 2020
 * 
 * Master_Frame is the core of this program, it ties together all of the other classes, front end code, and the back end code.
 * In this class we create the JFrame and the JPanel within which the game is rendered in, we establish the timer settings that simulate a 
 * frame rate ranging from 30-50 frames per second, this is variable due to the contrasting speeds (frequency) of objects painted to the JPanel, we
 * import the images used for the level and character designs, import the sounds tied to game events such as collisions, and we also handle the hit box detection
 * and the logic tied to all of the proprietary game events, from the start screen all the way up to the end screen and everything in between.
 * 
 * 
 * 
 */

@SuppressWarnings("serial")
public class Master_Frame extends JFrame{

	//Our JFrame, the container for the JPanel used to display the game and contain its backend
	//The reason we don't use a JFrame for painting is because it doesn't have the double buffering feature which enables smooth animation by rendering content 2x, JPanel does, so we use it!
	public Master_Frame() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		Master_Panel Panel = new Master_Panel();//Add the content pane that the game will be displayed on
		ImageIcon img = new ImageIcon("Resources/Icon-image/Tappy.ico");//Import icon image from the Resources folder's Icon-image subfolder
		Panel.setcurrentscreen("Start");//Setting the current screen of the game to the start screen that prompts the user to get ready and tap the screen to begin
		setFocusable(true);//Sets the JFrame to a focusable state meaning focus can be requested
		setContentPane(Panel);//Sets the content pane of the JFrame to the newly created JPanel mentioned earlier 
		setLocationRelativeTo(null);//Makes the location relative to null which in turn make the frame render in the center of the user's display
		setTitle("Flappy Bird");//Titles the JFrame to of course: Flappy bird 
		setResizable(false);//Prevents user from resizing the JFrame, this is a mobile game so you can't exactly play this at 4k
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Default operation when the user presses the close ('x') button is to exit the program and end its running state
		setSize(288,512);//Default and only resolution is 288x512 (x) by (y)
		setVisible(true);//Set JFrame to visible once its properties are set, it's imperative to do this AFTER you handle the dimension properties and other similar qualities first
		setIconImage(img.getImage());//Set the icon for this program if ever needed to be compiled into an exe format
		Panel.addKeyListener(Panel);//Add key listener to JPanel where the game is displayed, if you don't add this here then expect spending hours trying to figure out what you did wrong
		Panel.setFocusable(true);//Grants the JPanel focus which in turn allows it to render its contents properly
		Panel.requestFocusInWindow();//Allows the JPanel to request focus in the window 
	}

	class Master_Panel extends JPanel implements KeyListener{
		private Random randnum = new Random();//Random number generator object
		private int floor_velocity = -28;//Velocity of the floor, the speed at which it moves at, this is the magnitude of the vector movement that controls the floor's animation cycle, the negative sign denotes the direction of this vector as well
		private int score_chart_bandwidth = 0;//The width of the score container, so if you have 2 score digit images side by side then the width of this is going to reflect the additive width of those two images' widths
		private int pipe_velocity_x = -4;//Pipe's x horizontal velocity (how fast the pipes come at the player, aka how many pixels the pipes move leftward by)
		private int bird_velocity_y = -9;//Bird's y vertical velocity (how many pixels the bird's model increases by or decreases by depending on the situation)
		private int bird_flap_acceleration = -9;//Flap acceleration of the bird 
		private int bird_acceleration = 1;//Acceleration of the bird
		private int screenheight = 512;//The height of our frame
		private int screenwidth = 288;//The width of our frame
		private int pipegap = 100; //Size of the spacing between the upper and lower pipes 
		private int current_screen_x = 50;//The x coordinate of the current game message notification image 
		private int current_screen_y = 58;//The y coordinate of the current game message notification image 
		private Integer points = 0;//User's score as an integer object in order for us to be able to convert it into a string and then get the characters of that string to further separate say: '10' into 1 and 0 at index 0 and 1
		private Level_floor fl1;//first floor image, same image as the second, but rendered at a different position from the second
		private Level_floor fl2;//Second floor image 
		private Timer timer = new Timer(30, new TimerListener());//Timer object that controls the movement of the floor as a factor of time, so we continually paint the floor over and over again until we tell this timer to stop 
		private Timer timer2 = new Timer(200, new TimerListener());//Controls the flapping of the bird's wings aka the animation cycle of the bird, switching between the 3 different models until told to stop
		private Timer timer3 = new Timer(25, new TimerListener());//Oscillating animation you see the bird doing on the start screen is controlled by this timer
		private Timer timer4 = new Timer(30, new TimerListener());//Responsible for the timing of the pipes coming towards the player when the game is in session, at 30fps roughly thus the 30ms timing shown here
		private BufferedImage backdrop_day;//Day time background image 
		private BufferedImage backdrop_night;//Night time background image 
		private BufferedImage backdrop;//Stores the current background image 
		private BufferedImage start_screen;//Start game message notification image 
		private BufferedImage end_screen;//Gameover message notification image 
		private BufferedImage current_screen;//Stores the image of the current screen which is the message notification displayed at certain events, so start screen, and end screen, active which is null to get rid of any assets from blocking the viewport when the game is being played
		private BufferedImage Active_screen = null;//Make the notification prompts disappear when the game is being played by making it = null
		private BufferedImage score_digit_0;//Flappy bird font image of the number 0
		private BufferedImage score_digit_1;//Flappy bird font image of the number 1
		private BufferedImage score_digit_2;//Flappy bird font image of the number 2
		private BufferedImage score_digit_3;//Flappy bird font image of the number 3
		private BufferedImage score_digit_4;//Flappy bird font image of the number 4
		private BufferedImage score_digit_5;//Flappy bird font image of the number 5
		private BufferedImage score_digit_6;//Flappy bird font image of the number 6
		private BufferedImage score_digit_7;//Flappy bird font image of the number 7
		private BufferedImage score_digit_8;//Flappy bird font image of the number 8
		private BufferedImage score_digit_9;//Flappy bird font image of the number 9
		private BufferedImage author_citation;//By Justin Cook in the flappy bird font
		//private BufferedImage Highscore_font;//The word High score in the flappy bird font
		private String currentscreenstring;//Stores the current screen in a string value, so start screen, game over, are all stored as a string and used accordingly 
		private String Pipe_color;//Stores the current pipe's color 
		private AffineTransform at; //Affine transform object, used to apply transformations to our character model
		private boolean isscorevisible = false;//tracks whether or not the score should currently be displayed, this is false for the start screen because we don't want a big 0 staring at the player
		private boolean hitpipe = false;//Tracks pipe collision events
		private boolean pointscored = false;//keeps track of whether or not a point was scored
		private boolean spawnpipes = false;//tracks whether or not to spawn the pipes in, this is true when the game is being actively played and false on game over and start screen events
		private boolean bounce = false; //Switches between the rising and falling animation of the bird's flight on the splash screen
		private boolean Flapped;//Boolean that keeps track of whether or not the user has flapped in order to restrict certain game logic for when its needed and not needed
		private double BaseY = screenheight * 0.79;//Vertical value used to calculate the appropriate gap between pipe image 
		private double theta = 0;//Angle of the bird, when starting off the angle is 0 which is neutral, when the bird starts flapping this angle therefore increases and is used to rotate the image accordingly in the drawing portion 
		private double score_chart_x_offset = (screenwidth - score_chart_bandwidth)/2;//X offset aka where to place the score digits when you increase its size over time while also wanting it to be in the middle of the screen
		private ArrayList<Level_pipe> Pipe_pair;//Array list for the pairs of pipes that contains all of the pipes which oscillate between their starting and ending positions continually 
		private ArrayList<BufferedImage> Score_chart;//Score chart that keeps track of the users score by indexing each digit in the array as its corresponding flappy bird font image
		private AudioInputStream audioInputStream;//Enables audio input stream in order for us to stream in the audio from the file at hand
		private Clip hit_sound;// Hit event sound
		private Clip die_sound;//Death event sound
		private Clip point_sound;//Point scored event sound
		private Clip wing_sound;//Wing flapped event sound
		private Tappy_bird bird;//Bird object, aka our character model

		public Master_Panel() throws UnsupportedAudioFileException, IOException, LineUnavailableException{

			importdigits();//Import the flappy bird font number images 
			importsounds();//Import the sounds that go with the game's logic 
			importlevelassets();//Import all level assets, background, ground, pipes 
			importcharactermodel();//Import the bird's character model image 
			setscorechart();//Set the score chart that will reflect the user's score 
			setbackdrop();//Setting the backdrop randomly between 2 options, night and day
			setcharactermodel();//Setting the bird character model's color randomly between 3 different options, yellow, blue, and red
			
			//The Start screen prompt is the default screen for the game on start, we call our function to get the notif
			//End = Game Over notif, Start = start game notif, Active = game in session so clear the splash screen
			setcurrentscreen("Start");currentscreenstring = "Start";

			//Start all of our timers for our starting 'hovering' animation where the bird oscillates between two different positions to give off the effect its flying while the ground moves and no pipes spawn in just yet
			timer.start();timer2.start();timer3.start();

			//Implements the ability for the user to user their mouse to control the flapping of the bird 
			addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {

					//if the current screen is not the game over screen then we do the following:
					if (!currentscreenstring.equals("End")) {
						setcurrentscreen("Active");//Set the current screen to active, signaling the start of the game 
						wing_sound.setMicrosecondPosition(0);//Reset the sound clip in order for it to play again, if you don't do this it will only play once
						//Try and catch that invokes a thread sleep for 1ms to prevent the sound clip from crackling / glitching out if the user spams the jump button
						try {
							Thread.sleep(1);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						wing_sound.start();//Start the wing sound, this plays when the player makes the bird jump / flap its wings 
						timer3.stop();//we stop timer 3 which is the oscillation animation on the start screen
						Flapped = true;//set flapped to true because the player just flapped the bird's wings
						bird_velocity_y = bird_flap_acceleration;//Set the bird's y vertical velocity component to the bird's flap acceleration 
						timer4.start();//Start timer 4 which is the pipe moving animation that brings all of the pipe objects towards the player
						spawnpipes = true;//Spawn pipes is true which gives the paint method the 'ok' it needs to paint the pipe objects coming towards the player
						isscorevisible = true;//Set score to visible because the game is now in session
					}

					//if the current screen is the game over screen and the bird's y coordinate is at the bottom of the screen then we do the following:
					//We have to wait for the y coordinates to be at 380 to prevent the player from spamming the jump button and skip through the entire gameover message by mistake since the notification triggers without delay, but waiting for the 380 is a good enough delay for aesthetic purposes 
					if (currentscreenstring.equals("End") && bird.get_y_cord() == 380) {
						setcurrentscreen("Start");//Reset the game back to the starting notification screen
						hit_sound.setMicrosecondPosition(0);//Reset the collision sound clip back to 0 because we need to play it again when the player hits something again
						die_sound.setMicrosecondPosition(0);//Reset the death sound clip back to 0
						theta = 0;//Reset the angle of the bird back to 0 for the start screen animation
						bird.set_x_cord(bird.get_x__default_cord());//Reset the x component of the bird, doesn't do anything, but in case a rare glitch occurs this will fix everything
						bird.set_y_cord(bird.get_y__default_cord());//Reset the y component of the bird which we need for the start screen animation
						timer4.stop();//Stop the pipe moving towards us animation
						timer.start();//Start the floor animation back up because it stops when the user loses the game
						bounce = false;//reset the bounce value which we need for the start screen animation where the bird bounces between two positions
						timer2.start();//start the flapping animation back up, this too is stopped on the game over event 
						timer3.start();//Start the oscillation animation back up for the start screen animation
						spawnpipes = false;//No more spawning in the pipes, paint method doesn't paint them anymore
						Pipe_pair.clear();//We clear the pipe array list and make a new one filled with new pipes at their original positions 
						hitpipe = false;//reset the collision tracking variable 
						setbackdrop();//Set the backdrop once more, choosing between night and day randomly 
						setcharactermodel();//Set the character model of the bird again, choosing between a bird colored red blue or yellow randomly 
						setscorechart();//Reset the score chart 
						points = 0;//Reset the player's points after they lost
						isscorevisible = false;//Prevent the score from being rendered by the paint method 
						//Get new pipe color
						try {
							Pipe_color = new Level_pipe().getrandomPipeColor();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						//Get new pipes for the level because everything has been reset 
						try {
							Pipe_pair = new ArrayList<Level_pipe>();
							Pipe_pair = (new Level_pipe().getrandomPipe(Pipe_color, BaseY, screenwidth, pipegap));
						} catch (IOException e3) {
							e3.printStackTrace();
						}
						repaint();//Repaint everything once more to reflect the changes at hand
					}
				}
			});
		}

		//Keypressed event not implemented 
		@Override
		public void keyPressed(KeyEvent e) {}

		@Override
		public void keyReleased(KeyEvent e) {
			//if the current screen is not the game over screen and the user presses space or w or arrow up then we do the following:
			if (!currentscreenstring.equals("End") && e.getKeyCode() == KeyEvent.VK_SPACE || !currentscreenstring.equals("End") && e.getKeyCode() == KeyEvent.VK_W || !currentscreenstring.equals("End") && e.getKeyCode() == KeyEvent.VK_UP) {
				setcurrentscreen("Active");//Set the current screen to active, signaling the start of the game 
				wing_sound.setMicrosecondPosition(0);//Reset the sound clip in order for it to play again, if you don't do this it will only play once
				//Try and catch that invokes a thread sleep for 1ms to prevent the sound clip from crackling / glitching out if the user spams the jump button
				try {
					Thread.sleep(1);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				wing_sound.start();//Start the wing sound, this plays when the player makes the bird jump / flap its wings 
				timer3.stop();//we stop timer 3 which is the oscillation animation on the start screen
				Flapped = true;//set flapped to true because the player just flapped the bird's wings
				bird_velocity_y = bird_flap_acceleration;//Set the bird's y vertical velocity component to the bird's flap acceleration 
				timer4.start();//Start timer 4 which is the pipe moving animation that brings all of the pipe objects towards the player
				spawnpipes = true;//Spawn pipes is true which gives the paint method the 'ok' it needs to paint the pipe objects coming towards the player
				isscorevisible = true;//Set score to visible because the game is now in session
			}

			//if the current screen is the game over screen and the user presses space or w or arrow up then we do the following:
			if (currentscreenstring.equals("End") && e.getKeyCode() == KeyEvent.VK_SPACE && bird.get_y_cord() == 380 || currentscreenstring.equals("End") && e.getKeyCode() == KeyEvent.VK_W && bird.get_y_cord() == 380 || currentscreenstring.equals("End") && e.getKeyCode() == KeyEvent.VK_UP && bird.get_y_cord() == 380) {
				setcurrentscreen("Start");//Reset the game back to the starting notification screen
				hit_sound.setMicrosecondPosition(0);//Reset the collision sound clip back to 0 because we need to play it again when the player hits something again
				die_sound.setMicrosecondPosition(0);//Reset the death sound clip back to 0
				theta = 0;//Reset the angle of the bird back to 0 for the start screen animation
				bird.set_x_cord(bird.get_x__default_cord());//Reset the x component of the bird, doesn't do anything, but in case a rare glitch occurs this will fix everything
				bird.set_y_cord(bird.get_y__default_cord());//Reset the y component of the bird which we need for the start screen animation
				timer4.stop();//Stop the pipe moving towards us animation
				timer.start();//Start the floor animation back up because it stops when the user loses the game
				bounce = false;//reset the bounce value which we need for the start screen animation where the bird bounces between two positions
				timer2.start();//start the flapping animation back up, this too is stopped on the game over event 
				timer3.start();//Start the oscillation animation back up for the start screen animation
				spawnpipes = false;//No more spawning in the pipes, paint method doesn't paint them anymore
				Pipe_pair.clear();//We clear the pipe array list and make a new one filled with new pipes at their original positions 
				hitpipe = false;//reset the collision tracking variable 
				setbackdrop();//Set the backdrop once more, choosing between night and day randomly 
				setcharactermodel();//Set the character model of the bird again, choosing between a bird colored red blue or yellow randomly 
				setscorechart();//Reset the score chart 
				points = 0;//Reset the player's points after they lost
				isscorevisible = false;//Prevent the score from being rendered by the paint method 
				//Get new pipe color
				try {
					Pipe_color = new Level_pipe().getrandomPipeColor();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				//Get new pipes for the level because everything has been reset 
				try {
					Pipe_pair = new ArrayList<Level_pipe>();
					Pipe_pair = (new Level_pipe().getrandomPipe(Pipe_color, BaseY, screenwidth, pipegap));
				} catch (IOException e3) {
					e3.printStackTrace();
				}
				repaint();//Repaint everything once more to reflect the changes at hand
			}

		}

		//KeyTyped event not implemented 
		@Override
		public void keyTyped(KeyEvent e) {}

		/**Timer listener is a class that encapsulates all of the timer events used by the game for its animation cycles and logic manipulation as a factor of time 
		 * it implements action listener which listens for any actions involving the objects in question, so when a timer is started this action is registered and the source
		 * of this action is referenced via an if statement that carries the corresponding logic for that timer. Timers are a must if you want your game to be more than 5 frames per second,
		 * you see, you simply add a for loop and those 5 frames turn into 30 when you add 'frames' in between, with those frames being new repainting calls that repaints components using new 
		 * position information.
		 */
		class TimerListener implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{

				//if the source of the action (timer.start(), timer.stop(), timer.restart() etc) is the first timer then we do the following:
				if(e.getSource() == timer){
					//floor image 1 is instructed to move to the starting position again when it's less than or equal to -336 which is when it's off of the screen
					if(fl1.get_x_cord() <= -336) {
						timer.restart();
						fl1.set_x_cord(336);
					}
					//floor image 2 is instructed to move to the starting position again when it's less than or equal to -336 which is when it's off of the screen
					if(fl2.get_x_cord() <= -336) {
						timer.restart();
						fl2.set_x_cord(336);
					}

					//sets the current x coordinate to floor 1's x coordinate, and then sets floor 1's x coordinate to that variable plus the floor's velocity vector
					int curr_x_cord = fl1.get_x_cord();
					fl1.set_x_cord(curr_x_cord + floor_velocity);

					//sets the current x coordinate to floor 2's x coordinate, and then sets floor 1's x coordinate to that variable plus the floor's velocity vector
					curr_x_cord = fl2.get_x_cord();
					fl2.set_x_cord(curr_x_cord + floor_velocity);

					repaint();//Repaint this frame using the new information
				}

				//if the source of the action (timer.start(), timer.stop(), timer.restart() etc) is the second timer then we do the following:
				else if(e.getSource() == timer2){

					//First stage of the bird flapping cycle oscillation animation where the bird has the up-flap model and the cycle stage isn't equal to 2, thus we tell it to go to the next 
					//stage and set the bird's model to the mid-flap model and set the last stage of the cycle to 1 in order to record the past events and reference it to further separate these if statements and prevent conflicting parameters
					if(bird.get_animation_cycle_stage() == 1 && bird.get_last_cycle_stage() != 2 ) {
						bird.set_animation_cycle_stage(2);
						bird.set_bird_model(2);
						bird.set_last_cycle_stage(1);
						repaint();
					}

					//Second stage of the bird flapping cycle oscillation animation where the bird has the mid-flap model and the cycle stage isn't equal to 3, thus we tell it to go to the next 
					//stage and set the bird's model to the down-flap model and set the last stage of the cycle to 2 in order to record the past events and reference it to further separate these if statements and prevent conflicting parameters
					if(bird.get_animation_cycle_stage() == 2 && bird.get_last_cycle_stage() == 3) {
						bird.set_animation_cycle_stage(1);
						bird.set_bird_model(1);
						bird.set_last_cycle_stage(2);
						repaint();
					}

					//Third stage of the bird flapping cycle oscillation animation where the bird has the down-flap model and the cycle stage isn't equal to 2, thus we tell it to go to the next 
					//stage and set the bird's model to the mid-flap model and set the last stage of the cycle to 3 in order to record the past events and reference it to further separate these if statements and prevent conflicting parameters
					if(bird.get_animation_cycle_stage() == 3 && bird.get_last_cycle_stage() == 2) {
						bird.set_animation_cycle_stage(2);
						bird.set_bird_model(2);
						bird.set_last_cycle_stage(3);
						repaint();
					}

					//Fourth stage of the bird flapping cycle oscillation animation where the bird has the mid-flap model and the cycle stage isn't equal to 3, thus we tell it to go to the next 
					//stage and set the bird's model to the up-flap model and set the last stage of the cycle to 2 in order to record the past events and reference it to further separate these if statements and prevent conflicting parameters
					if(bird.get_animation_cycle_stage() == 2 && bird.get_last_cycle_stage() != 3) {
						bird.set_animation_cycle_stage(3);
						bird.set_bird_model(3);
						bird.set_last_cycle_stage(2);
						repaint();
					}

					//Fifth and final stage of the bird flapping cycle oscillation animation where the bird has the up-flap model and the cycle stage is equal to 2, thus we tell it to go to the first 
					//stage of the cycle and restart this entire process over again by resetting everything 
					if(bird.get_animation_cycle_stage() == 1 && bird.get_last_cycle_stage() == 2 ) {
						bird.set_animation_cycle_stage(1);
						bird.set_bird_model(1);
						bird.set_last_cycle_stage(1);
						repaint();
					}
				}

				//if the source of the action (timer.start(), timer.stop(), timer.restart() etc) is the third timer then we do the following:
				else if(e.getSource() == timer3){
					//Start screen oscillation animation where the bird goes from bottom to top and top to bottom in its small range of motion
					//We start off the splash screen's bird flying animation with bounce = false meaning we can't bounce back down until we reach the ceiling of the animation
					if(bird.get_y_cord() >= bird.get_y__default_cord() && bounce == false) {
						bird.set_y_cord(bird.get_y_cord() + 1);//Make the bird move one pixel up every time to simulate smooth movement between positions 
						repaint(); 

						if(bird.get_y_cord() == (bird.get_y__default_cord() + 20))//if the bird reached the ceiling of the range of motion then we tell it to bounce back down by enabling the second if statement
							bounce = true;

					}
					//If we reach the top of the animation then bounce is true and we go back down
					else if(bird.get_y_cord() <= (bird.get_y__default_cord() + 20) && bounce == true) {
						bird.set_y_cord(bird.get_y_cord() - 1);//Make the bird move one pixel down every time to simulate smooth movement between positions 
						repaint(); 

						if(bird.get_y_cord() == (bird.get_y__default_cord()))//if the bird reached the bottom of the range of motion then we tell it to bounce up by enabling the first if statement once more with our boolean variable
							bounce = false;
					}

				}

				//if the source of the action (timer.start(), timer.stop(), timer.restart() etc) is the fourth timer then we do the following:
				else if(e.getSource() == timer4){		
					//Floor collision detector, detects if the bird has made contact with the floor, if so, stop everything, repaint everything in order to keep the pipes and other elements still painted to the screen, trigger the collision sound and then the gameover notification
					if(bird.get_y_cord() >= 380) {
						bird.set_y_cord(380);
						timer.stop();
						timer2.stop();
						timer4.stop();
						setcurrentscreen("End");
						spawnpipes = false;
						if(hitpipe == false)
							hit_sound.start();
						repaint();
					}

					//Triggers if the pipes are enabled for spawning 
					if(spawnpipes == true) {
						for(int i = 0; i < Pipe_pair.size(); i++) {
							Pipe_pair.get(i).setx(Pipe_pair.get(i).getx() + pipe_velocity_x);
						}

						//Upper pipe hit box, all of these parameters form the hit box for the pipes at the top of the level, and if the player's character model hits this hitbox then gameover, die sound triggers, hit sound triggers, and we prepare to restart the game
						if(bird.get_x_cord() >= Pipe_pair.get(0).getPipeEdge_left() && bird.get_x_cord() <= Pipe_pair.get(0).getPipeEdge_right() + bird.get_bird_model().getWidth()/2 && bird.get_y_cord() <= Pipe_pair.get(0).getPipeBottom()) {
							hitpipe = true;
							hit_sound.start();
							die_sound.start();
							setcurrentscreen("End");
							timer.stop();
							timer2.stop();
							if(theta <= 90) 
								theta += 30;
							bird.set_y_cord(bird.get_y_cord() + bird_velocity_y*2);
							spawnpipes = false;
							repaint();//Repaint in order to retain the pipes and other assets when the other timers are stopped which trigger their own repaints that might not contain these specific changes due to the difference in timing 
						}

						//Goal hitbox, this is the area in the middle of the pipe gap where the player must reach in order to score a point, once the user scores a point then the point scored sound plays, pointscored is set to true in order to reset the sound clip, and we call the updatescorechart method in order to update the score displayed in the middle of the screen
						if(bird.get_x_cord() >= Pipe_pair.get(1).getPipeEdge_left() + bird.get_bird_model().getWidth()/2 && bird.get_x_cord() <= Pipe_pair.get(1).getPipeEdge_right() - bird.get_bird_model().getWidth()/2 && bird.get_y_cord() < Pipe_pair.get(1).getPipeTop() && bird.get_y_cord() > Pipe_pair.get(0).getPipeBottom() && pointscored == false){
							pointscored = true;
							point_sound.start();
							updatescorechart();
						}

						//Reset the point scored sound after it's triggered once, this slight delay prevents again, the crackling / glitching audio you get when you reset audio and play it immediately after resetting it 
						if(pointscored == true && bird.get_x_cord() < Pipe_pair.get(1).getPipeEdge_left()) {
							point_sound.setMicrosecondPosition(0); 
							pointscored = false;
						}

						//Lower pipe hit box, all of these parameters form the hit box for the pipes at the bottom of the level, and if the player's character model hits this hitbox then gameover, die sound triggers, hit sound triggers, and we prepare to restart the game
						if(bird.get_x_cord() >= Pipe_pair.get(1).getPipeEdge_left() && bird.get_x_cord() <= Pipe_pair.get(1).getPipeEdge_right() + bird.get_bird_model().getWidth()/2 && bird.get_y_cord() >= Pipe_pair.get(1).getPipeTop() - bird.get_bird_model().getHeight()) {
							hitpipe = true;
							hit_sound.start();
							die_sound.start();
							setcurrentscreen("End");
							timer.stop();
							timer2.stop();
							if(theta <= 90) 
								theta += 30;
							bird.set_y_cord(bird.get_y_cord() + bird_velocity_y*2);
							spawnpipes = false;
							repaint();//Repaint in order to retain the pipes and other assets when the other timers are stopped which trigger their own repaints that might not contain these specific changes due to the difference in timing 
						}

						//Add pipes when the pipes in front go off the edge of the screen, thus placing them in the back of the arraylist and the other pipes in the front
						if(!Pipe_pair.isEmpty() && Pipe_pair.get(1).getx() < -52) {
							try {
								ArrayList<Level_pipe> p2 = new ArrayList<Level_pipe>();
								p2 = new Level_pipe().getrandomPipe(Pipe_color, BaseY, screenwidth, pipegap);
								Pipe_pair.add(p2.get(0));
								Pipe_pair.add(p2.get(1));
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}

						//Removes the first pipes that have gone off the edge of the screen 
						if(!Pipe_pair.isEmpty() && Pipe_pair.get(1).getx() < -52){
							Pipe_pair.remove(0);
							Pipe_pair.remove(0);
						}
					}

					//Enabled if the bird is continually above the ground's y coordinate aka when the player hasn't lost yet
					if(bird.get_y_cord() < 380) {

						//Detect whether the player has touched the ceiling of the level, if so then we make the bird go no higher than that position, if it does then it can easily go over the pipes without even trying 
						if(bird.get_y_cord() <= -25) {
							bird.set_y_cord(-25);
						}

						//If the player flapped the bird's wings aka pressed the jump button then set the angle of the image to -20 for a slight tilt, adjust it's y coordinate to move 9 pixels upwards, and repaint the frame using this new information
						if(Flapped == true) {
							theta = -20;//Set the angle of the bird to -20 degrees
							bird.set_y_cord(bird.get_y_cord() - 9);
							repaint();
						}

						//If the player hasn't flapped the bird's wings then the bird must fall back down to the ground until they do, but until then, the bird will achieve a maximum angle of displacement from its original angle, of 90 degrees
						//The bird will also fall much faster than the bird when it's flapping / moving upwards 
						if(Flapped == false) {

							//Maximum downward velocity of the bird is 10, and 10m/s downwards is in line with the actual acceleration due to gravity found here on Earth :)
							//To smoothen this animation out we just increase the bird's y velocity by 1 until it's less than 10
							if(bird_velocity_y < 10)
								bird_velocity_y += bird_acceleration;

							//the maximum angle of the bird is 90 degrees, and to smoothen this animation of the bird rotating into a full nose dive when it's falling back down to the ground we simply add 2 to the angle for every frame rendered 
							if(theta <= 90) 
								theta += 2;

							//set the y coordinate of the bird with the newly calculated information
							bird.set_y_cord(bird.get_y_cord() + bird_velocity_y);
							repaint();//repaint everything
						}
					}

					//If none of these are fulfilled then we reset flapped to false to indicate no player input and then we restart timer4, this is useless but can come in handy if a rare glitch occurs 
					Flapped = false;
					timer4.restart();
				}
			}
		}

		//Sets the background image of the game, there's a 50% chance of it being either night or day
		private void setbackdrop(){
			int rand_int = randnum.nextInt(2); 
			switch(rand_int){
			case 0:
				backdrop = backdrop_day;
				break;
			case 1:
				backdrop = backdrop_night;
				break;
			}
		}

		//Set the character model, randomly chooses between 3 different colors for our bird to be
		private void setcharactermodel() {
			int rand_int = randnum.nextInt(3); 
			switch(rand_int){
			case 0:
				bird.set_yellowbird();
				break;
			case 1:
				bird.set_bluebird();
				break;
			case 2:
				bird.set_redbird();
				break;
			}
		}

		//Set the current screen, so start screen, gameover screen, or no displayed message for when the user is actively playing the game 
		private void setcurrentscreen(String notif){
			currentscreenstring = notif;
			switch(notif) {
			case "Start"://Provides the splash screen 'tap here to start' notif
				current_screen = start_screen;
				current_screen_x = 50;
				current_screen_y = 58;
				revalidate();
				repaint();
				break;
			case "End"://Provides the splash screen 'Game Over' notif
				current_screen = end_screen;
				current_screen_x = 50;
				current_screen_y = 175;
				revalidate();
				repaint();
				break;
			case "Active"://Gets rid of the splash screen so that the user can play the game unimpeded 
				current_screen = Active_screen;	
				revalidate();
				repaint();
				break;
			}
		}

		//We import the bird character model by making a new Tappy_bird object that corresponds to all the parameters we need from it
		public void importcharactermodel() {
			try {
				bird = new Tappy_bird();
			} catch (IOException e2) {
				e2.printStackTrace();
			}

		}

		/**Import the level assets for the game such as the background (backdrop) image, the pipes most importantly, and the moving floor as well which we have 2 of that constantly alternating giving the effect it's a never ending field of grass
		 * we alternate between two images of the floor b/c together they're more than the total width of the screen thus allowing us to remove any flickering or gaps when you try to reset the position of 1 floor image back to its starting point 
		 * after it goes past the edge of the screen.
		 */
		public void importlevelassets() throws IOException{

			Pipe_color = new Level_pipe().getrandomPipeColor();
			Pipe_pair = new ArrayList<Level_pipe>();
			Pipe_pair = (new Level_pipe().getrandomPipe(Pipe_color, BaseY, screenwidth, pipegap));

			//Our background images 
			backdrop_night = ImageIO.read(new File("Resources/Sprites/Environment-sprites/background-night.png"));
			backdrop_day = ImageIO.read(new File("Resources/Sprites/Environment-sprites/background-day.png"));

			//Game event notification messages, so on game start event and on game over event these messages are rendered at 
			start_screen = ImageIO.read(new File("Resources/Sprites/Notification-sprites/message.png"));
			end_screen = ImageIO.read(new File("Resources/Sprites/Notification-sprites/gameover.png"));

			//Floor 1 and floor 2 images, floor 1 starts off behind floor 2, specifically the width of either images apart from floor 2  
			fl1 = new Level_floor(336);
			fl2 = new Level_floor(0);	
			

			//Author Citation
			author_citation = ImageIO.read(new File("Resources/String_Flappy_Font_Images/FlappyBirdFont_By_JustinCook.png"));
		
			//High Score Label
			//Highscore_font = ImageIO.read(new File("Resources/String_Flappy_Font_Images/FlappyBirdFont_High_Score.png"));
		}

		//Import the sounds for in game events such as collisions, flapping the bird's wings, and also dying
		public void importsounds() throws LineUnavailableException, IOException, UnsupportedAudioFileException{
			//Bird Hit/ Game over sound
			// create AudioInputStream object 
			audioInputStream =  
					AudioSystem.getAudioInputStream(new File("Resources/Audio/Wav-format-high-quality/hit.wav").getAbsoluteFile()); 
			// create clip reference 
			hit_sound = AudioSystem.getClip(); 
			// open audioInputStream to the clip 
			hit_sound.open(audioInputStream); 
			//Bird Hit/ Game over sound

			//Death sound/ Collision with pipe Sound 
			// create AudioInputStream object 
			audioInputStream =  
					AudioSystem.getAudioInputStream(new File("Resources/Audio/Wav-format-high-quality/die.wav").getAbsoluteFile()); 
			// create clip reference 
			die_sound = AudioSystem.getClip(); 
			// open audioInputStream to the clip 
			die_sound.open(audioInputStream); 
			//Death sound/ Collision with pipe Sound 

			//Point scored sound/ Bird intersecting goal sound
			// create AudioInputStream object 
			audioInputStream =  
					AudioSystem.getAudioInputStream(new File("Resources/Audio/Wav-format-high-quality/point.wav").getAbsoluteFile()); 
			// create clip reference 
			point_sound = AudioSystem.getClip(); 
			// open audioInputStream to the clip 
			point_sound.open(audioInputStream); 
			//Point scored sound/ Bird intersecting goal sound

			//Wing flapping sound/ Player flap event sound
			// create AudioInputStream object 
			audioInputStream =  
					AudioSystem.getAudioInputStream(new File("Resources/Audio/Wav-format-high-quality/wing.wav").getAbsoluteFile()); 
			// create clip reference 
			wing_sound = AudioSystem.getClip(); 
			// open audioInputStream to the clip 
			wing_sound.open(audioInputStream); 
		}

		//Import the score digit images that we'll use to show the user's score at the top of the screen in flappy bird font
		public void importdigits() throws IOException{
			score_digit_0 = ImageIO.read(new File("Resources/Sprites/Numerical-font-logic-sprites/0.png"));
			score_digit_1 = ImageIO.read(new File("Resources/Sprites/Numerical-font-logic-sprites/1.png"));
			score_digit_2 = ImageIO.read(new File("Resources/Sprites/Numerical-font-logic-sprites/2.png"));
			score_digit_3 = ImageIO.read(new File("Resources/Sprites/Numerical-font-logic-sprites/3.png"));
			score_digit_4 = ImageIO.read(new File("Resources/Sprites/Numerical-font-logic-sprites/4.png"));
			score_digit_5 = ImageIO.read(new File("Resources/Sprites/Numerical-font-logic-sprites/5.png"));
			score_digit_6 = ImageIO.read(new File("Resources/Sprites/Numerical-font-logic-sprites/6.png"));
			score_digit_7 = ImageIO.read(new File("Resources/Sprites/Numerical-font-logic-sprites/7.png"));
			score_digit_8 = ImageIO.read(new File("Resources/Sprites/Numerical-font-logic-sprites/8.png"));
			score_digit_9 = ImageIO.read(new File("Resources/Sprites/Numerical-font-logic-sprites/9.png"));
		}

		/**This method enables the scoring ability for the user by initializing an array to store the flappy bird font number images referencing the actual score's numbers at their specific index 
		 *to allow the updatescorechart function to even work we must fill the array up with a whole bunch of garbage we don't need until we need to reflect the score up to that specific size
		 *that garbage being Bufferedimages of any kind, but to maintain homogeneity I filled it with 0's to form a score limit of 9,999,999,999. Once the score hits 10 Billion it will glitch out
		 *and add the FULL new 10,000,000,000 to the head of the number that's already there and everything spills out of the viewport. But we don't worry about that unless you have maybe 320 years
		 *to play a perfect game, then that's your problem as a super being in a higher mortal plane of existence.
		 */
		public void setscorechart(){
			Score_chart = new ArrayList<BufferedImage>();
			Score_chart.add(getflappyfontdigit((char) 0));
			score_chart_bandwidth += Score_chart.get(0).getWidth();
			score_chart_x_offset = (screenwidth - score_chart_bandwidth)/2;
			//Adding the unused places in the arraylist for the maximum size of the score which is 1 Billion points which corresponds to the physical limitations of the current screen resolution
			Score_chart.add(score_digit_0);
			Score_chart.add(score_digit_0);
			Score_chart.add(score_digit_0);
			Score_chart.add(score_digit_0);
			Score_chart.add(score_digit_0);
			Score_chart.add(score_digit_0);
			Score_chart.add(score_digit_0);
			Score_chart.add(score_digit_0);
			Score_chart.add(score_digit_0);
		}

		public void updatescorechart(){
			char passedchar = 0; //Initialize the character data type we use in the for loop below
			int passedchar_int = 0;//Initialize the integer data type 
			score_chart_bandwidth = 0;//Initialize the size of the score container 
			points++;//Increase the user's score by 1 point

			for(int i = 0; i < points.toString().length(); i++) {
				//convert the user's score to a string, get the character at the current index 'i' and then get the value of that string in integer form whilst equating it to the character data type via casting of the int variation 
				passedchar_int = Integer.parseInt(String.valueOf(points.toString().charAt(i)));  
				passedchar = (char) passedchar_int;
				Score_chart.set(i, getflappyfontdigit(passedchar)); //We now pass our character data type with our specific 1 digit number stored as a character to the getflappyfontdigit() method after which we set the array at the specific index corresponding to the current
				//number's index to that flappyfont digit image 

				//Update the width of the score based on how many digits / images are currently in the array, so 10 = 2 digits in the array so our bandwidth is combination of these two digits' image widths
				score_chart_bandwidth += Score_chart.get(i).getWidth();
			}
		}

		//Convert the passed integer that was converted over to a character data type to a corresponding buffered image with the same number, and return that image back 
		public BufferedImage getflappyfontdigit(char c) {
			BufferedImage flappydigit = null;//Initialize our bufferedimage to avoid a 'variable might not be initialized' conflict
			//Switch statement to simplify this process even more
			switch(c) {
			case(0):
				flappydigit = score_digit_0;//Number 0, this is selected when the user's score is 0 or a part of their score includes a 0
			break;
			case(1):
				flappydigit = score_digit_1;
			break;
			case(2):
				flappydigit = score_digit_2;
			break;
			case(3):
				flappydigit = score_digit_3;
			break;
			case(4):
				flappydigit = score_digit_4;
			break;
			case(5):
				flappydigit = score_digit_5;
			break;
			case(6):
				flappydigit = score_digit_6;
			break;
			case(7):
				flappydigit = score_digit_7;
			break;
			case(8):
				flappydigit = score_digit_8;
			break;
			case(9):
				flappydigit = score_digit_9;
			break;
			}
			return flappydigit;
		}

		//Get the current state / screen of the game 
		public BufferedImage getcurrentscreen() {
			return this.current_screen;
		}

		//2D graphics based method that draws the current state / screen of the game aka notification message, i.e, "Start", "Game Over", and null for when the game is active which gets rid of any image blocking the viewport entirely
		private void doDrawing(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

			g2d.drawImage(getcurrentscreen(), current_screen_x, 
					current_screen_y, this);
		}

		@Override //Our paint method which controls the painting of all rendered objects present in the game
		protected void paintComponent(Graphics g){
			super.paintComponent(g);

			//Draws the backdrop first because everything else is painted on top of it, the painting process is from top to bottom, back to front, what's painted first gets painted ontop of
			score_chart_x_offset = (screenwidth - score_chart_bandwidth)/2;
			g.drawImage(backdrop,0,0,this);

			//Prints out the user's score when the game is active by replacing the regular numbers with flappy bird font digits 
			if(isscorevisible == true) {
				for(int i = 0; i < points.toString().length(); i++) {
					g.drawImage(Score_chart.get(i),(int) score_chart_x_offset, (int) (screenheight * 0.1), this);
					score_chart_x_offset += Score_chart.get(i).getWidth();
				}
			}

			//Draws the pipe images when the game is in its active state 
			g.drawImage(Pipe_pair.get(0).getpipemodel(),Pipe_pair.get(0).getx(), Pipe_pair.get(0).gety(), this);
			g.drawImage(Pipe_pair.get(1).getpipemodel(),Pipe_pair.get(1).getx(), Pipe_pair.get(1).gety(), this);
			g.drawImage(Pipe_pair.get(2).getpipemodel(),Pipe_pair.get(2).getx(), Pipe_pair.get(2).gety(), this);
			g.drawImage(Pipe_pair.get(3).getpipemodel(),Pipe_pair.get(3).getx(), Pipe_pair.get(3).gety(), this);

			//Draws the floor which is simulated as a moving object via the regular displacement of the x coordinates 
			g.drawImage(fl1.get_buffedimage(), fl1.get_x_cord(), 400, this);
			g.drawImage(fl2.get_buffedimage(), fl2.get_x_cord(), 400, this);

			doDrawing(g);//Draw the current screen of the game depending on the current game mode, so if it's at the start event then we draw the start notification etc
			Toolkit.getDefaultToolkit().sync();//Synchronize the graphics state for smoothing of the animations

			//We rotate the bird's image according to our input theta as its changed progressively to represent the change in the bird's angle
			//Translate the image as well because transformation reset all of this necessary information, thus, you have to do it again
			at = AffineTransform.getRotateInstance(0,0);//Get the rotate instance at x = 0 y = 0 which is the top of the JFrame
			at.translate(bird.get_x_cord(), bird.get_y_cord());
			at.rotate(Math.toRadians(theta),bird.get_bird_model().getWidth()/2,bird.get_bird_model().getHeight()/2);//Rotate around center

			//Draw the bird's image in front of everything else because it's the most important asset that needs to be rendered consistently and without impedance
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(bird.get_bird_model(), at, null);//in order to apply the affine transformation we needed to turn the image into a 2d graphic
		
			//Author Citation 
			g2d.drawImage(author_citation, 0, 450, (int)(author_citation.getWidth() * 0.3), (int)(author_citation.getHeight() * 0.3), null);
			/**
			*High Score title 
			*g2d.drawImage(Highscore_font, 0, 420, (int)(author_citation.getWidth() * 0.25), (int)(author_citation.getHeight() * 0.4), null);
			*/
		}

		@Override //Returns the preferred dimensions for the current JComponent 
		public Dimension getPreferredSize() {
			return new Dimension(screenwidth, screenheight);
		}

		@Override //Overridden method for the update component of the painting utility 
		public void update(Graphics g) {
			super.update(g);
			update(g);
		}

	}
	public static void main(String[] args) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		new Master_Frame();
	}
}
