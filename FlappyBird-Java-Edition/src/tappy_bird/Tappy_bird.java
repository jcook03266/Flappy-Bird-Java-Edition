package tappy_bird;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**Custom GUI Project, Java Implementation of the world famous mobile game 'Flappy Bird' 
 * @author Justin Cook, 2020
 * 
 * The Tappy_bird class enables the creation of the bird model which the game relies upon for the user to control and navigate 
 * around the pipe obstacles presented. In this class we essentially make the bird's model 'smart', in the sense that it has a bevy of parameters tied to it
 * such as different models depending on the animation state, its coordinate position via x and y variables, color, and animation cycle stage tracking for the hovering
 * animation you see on the start screen. The bird can be 3 different colors, yellow, red, or blue and this decision is handled by the Master_Frame class in which it's randomly selected
 * with the help of the appropriate methods from this class
 * 
 */

public class Tappy_bird {
	private int bird_x_coordinate = 50;//Starting position of the bird horizontally
	private int bird_y_coordinate = 226;//Starting position of the bird vertically
	private final int bird_x__default_cord = 50;//default x position for the start screen animation
	private final int bird_y__default_cord = 226;//default y position for the start screen animation
	private BufferedImage bird_model_Yellow;//Up-flap model animation cycle stage 1 for the yellow bird model
	private BufferedImage bird_model_Yellow2;//Mid-flap model animation cycle stage 2 for the yellow bird model
	private BufferedImage bird_model_Yellow3;//Down-flap model animation cycle stage 3 for the yellow bird model
	private BufferedImage bird_model_Red;
	private BufferedImage bird_model_Red2;
	private BufferedImage bird_model_Red3;
	private BufferedImage bird_model_Blue;
	private BufferedImage bird_model_Blue2;
	private BufferedImage bird_model_Blue3;
	private BufferedImage bird_model_default; //stage 1 which is kept constant
	private BufferedImage bird_model; //Start of animation cycle, bird starts with wing up
	private BufferedImage bird_model2;//Second stage of the animation cycle, mid flap
	private BufferedImage bird_model3;//Last stage of the animation cycle, down flap, this is when the cycle goes backwards and alternates 
	private int animation_cycle_stage = 1;//Sets the first stage of the animation cycle this is altered by the main driver class
	private String bird_color = "Yellow";//Default color for the bird
	private int last_cycle_stage = 1;//Sets the last stage of the animation cycle, this is altered by the main driver class


	//Constructor imports all of the different bird model sprite images and resolves them to their appropriate variables 
	public Tappy_bird() throws IOException {
		//Yellow bird model, this is the default model
		bird_model_Yellow = ImageIO.read(new File("Resources/Sprites/Animation-cycle-sprites/Yellow-bird-sprites/yellowbird-upflap.png"));
		this.bird_model = bird_model_Yellow;
		this.bird_model_default = bird_model_Yellow;
		bird_model_Yellow2 = ImageIO.read(new File("Resources/Sprites/Animation-cycle-sprites/Yellow-bird-sprites/yellowbird-midflap.png"));
		this.bird_model2 = bird_model_Yellow2;
		bird_model_Yellow3 = ImageIO.read(new File("Resources/Sprites/Animation-cycle-sprites/Yellow-bird-sprites/yellowbird-downflap.png"));
		this.bird_model3 = bird_model_Yellow3;
		//Red bird model 
		bird_model_Red = ImageIO.read(new File("Resources/Sprites/Animation-cycle-sprites/Red-bird-sprites/redbird-upflap.png"));
		bird_model_Red2 = ImageIO.read(new File("Resources/Sprites/Animation-cycle-sprites/Red-bird-sprites/redbird-midflap.png"));
		bird_model_Red3 = ImageIO.read(new File("Resources/Sprites/Animation-cycle-sprites/Red-bird-sprites/redbird-downflap.png"));
		//Blue bird model
		bird_model_Blue = ImageIO.read(new File("Resources/Sprites/Animation-cycle-sprites/Blue-bird-sprites/bluebird-upflap.png"));
		bird_model_Blue2 = ImageIO.read(new File("Resources/Sprites/Animation-cycle-sprites/Blue-bird-sprites/bluebird-midflap.png"));
		bird_model_Blue3 = ImageIO.read(new File("Resources/Sprites/Animation-cycle-sprites/Blue-bird-sprites/bluebird-downflap.png"));
	}

	//Various getter and setter methods
	//Get the x coordinate of the bird's model, this doesn't change at all, the bird is constantly at the same x coordinate, the illusion of movement is produced by the pipes and ground moving towards and away from the player
	public int get_x_cord() {
		return this.bird_x_coordinate;
	}

	//set the x coordinate of the bird's model 
	public void set_x_cord(int bird_x_coordinate) {
		this.bird_x_coordinate = bird_x_coordinate;
	}

	//get the y coordinate of the bird's model, this coordinate value does change as you obviously see from when the bird's model goes up and down throughout the environment
	public int get_y_cord() {
		return this.bird_y_coordinate;
	}

	//set the y coordinate of the bird's model 
	public void set_y_cord(int bird_y_coordinate) {
		this.bird_y_coordinate = bird_y_coordinate;
	}

	//Get the default y coordinate, this is used when you need to reset a variable using an original condition, in this case the y coordinate
	public int get_y__default_cord() {
		return this.bird_y__default_cord;
	}

	//Get the default x coordinate, this is used when you need to reset a variable using an original condition, in this case the x coordinate, this is mostly useless, but is implemented for parallelism in functionality for both variables
	public int get_x__default_cord() {
		return this.bird_x__default_cord;
	}

	//Set the bird's current model depending on the integer 'stage' given, this is used to change the sprite's animation state in the 3 part cycle for this particular sprite
	public void set_bird_model(int stage) {
		if(stage == 1)
			this.bird_model = bird_model_default;//up-flap
		if(stage == 2)
			this.bird_model = bird_model2;//mid-flap
		if(stage == 3)
			this.bird_model = bird_model3;//down-flap
	}

	//Get the first bird model which is up-flap by default 
	public BufferedImage get_bird_model() {
		return this.bird_model;
	}

	//Get the second bird model which is mid-flap
	public BufferedImage get_bird_model2() {
		return this.bird_model2;
	}

	//Get the third bird model which is down-flap
	public BufferedImage get_bird_model3() {
		return this.bird_model3;
	}

	//Used to set the bird to a blue bird model 
	public void set_bluebird() {
		this.bird_model_default = bird_model_Blue;
		this.bird_model = bird_model_Blue;
		this.bird_model2 = bird_model_Blue2;
		this.bird_model3 = bird_model_Blue3;
		this.bird_color = "Blue";
	}

	//Used to set the bird to a red bird model
	public void set_redbird() {
		this.bird_model_default = bird_model_Red;
		this.bird_model = bird_model_Red;
		this.bird_model2 = bird_model_Red2;
		this.bird_model3 = bird_model_Red3;
		this.bird_color = "Red";
	}

	//Used to set the bird to a yellow bird model;
	public void set_yellowbird() {
		this.bird_model_default = bird_model_Yellow;
		this.bird_model = bird_model_Yellow;
		this.bird_model2 = bird_model_Yellow2;
		this.bird_model3 = bird_model_Yellow3;
		this.bird_color = "Yellow";
	}

	//Get the blue bird sprite image model
	public BufferedImage get_blue_bird_model() {
		return this.bird_model_Blue;
	}

	//Get the red bird sprite image model
	public BufferedImage get_red_bird_model() {
		return this.bird_model_Red;
	}

	//Get the yellow bird sprite image model
	public BufferedImage get_yellow_bird_model() {
		return this.bird_model_Yellow;
	}

	//Get the color of the bird
	public String get_bird_color() {
		return this.bird_color;
	}

	//Get the current stage of the animation cycle in order to go to the next stage
	public int get_animation_cycle_stage() {
		return this.animation_cycle_stage;
	}

	//Set the current stage of the animation cycle 
	public void set_animation_cycle_stage(int animation_cycle_stage) {
		this.animation_cycle_stage = animation_cycle_stage;
	}

	//get the last stage of the animation cycle, necessary for tracking the alternating sequence's position
	public int get_last_cycle_stage() {
		return this.last_cycle_stage;
	}

	//Set the last stage of the animation cycle 
	public void set_last_cycle_stage(int last_cycle_stage) {
		this.last_cycle_stage = last_cycle_stage;
	}

}
