package tappy_bird;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;

/**Custom GUI Project, Java Implementation of the world famous mobile game 'Flappy Bird' 
 * @author Justin Cook, 2020
 * 
 * The Level_floor class is simple enough, it loads up the image used for the floor of the level, and sets its coordinate, namely the x coordinate 
 * because it's kept at a static y coordinate continuously throughout the entire duration of the game, thus we only need to manipulate the x.
 * With this class we can now simulate a moving floor by continuously getting and setting the floor image's x coordinate with respect to time as provided 
 * by the timer listener in the Master_Frame class.
 * 
 */

public class Level_pipe {
	private BufferedImage pipe_model_red;//Pipe image right side up/ regular orientation with a red color variation
	private BufferedImage pipe_model_green;//Pipe image right side up/ regular orientation with a green color variation
	private BufferedImage pipe_model_red_flipped;//Pipe image flipped upside down with a red color variation
	private BufferedImage pipe_model_green_flipped;//Pipe image flipped upside down with a green color variation
	private BufferedImage pipe_model;//Pipe image base variable
	private Random randnum = new Random();//Random number generator;
	private String pipe_color = null;//Pipe color;
	private int pipe_height;//Pipe image's height
	private int pipe_width;//Pipe image's width
	private int gap_y;// Vertical gap between sets of pipe objects, this is where the bird has to navigate through
	private int pipe_x;// Displacement between pairs of pipe objects 
	private int x;// x coordinate value of the image (horizontal displacement)
	private int y;// y coordinate value of the image (vertical displacement)


	//Constructor imports all of the pipe image models and sets them to their respective variables, and then sets the height and width variables accordingly 
	public Level_pipe() throws IOException {
		pipe_model_red = ImageIO.read(new File("Resources/Sprites/Environment-sprites/pipe-red.png"));//Import red Pipe's image
		pipe_model_green = ImageIO.read(new File("Resources/Sprites/Environment-sprites/pipe-green.png"));//Import Green Pipe's image
		pipe_model_red_flipped= ImageIO.read(new File("Resources/Sprites/Environment-sprites/pipe-red-flipped.png"));//Import red Pipe's flipped image
		pipe_model_green_flipped = ImageIO.read(new File("Resources/Sprites/Environment-sprites/pipe-green-flipped.png"));//Import Green Pipe's flipped image
		pipe_height = pipe_model_red.getHeight();
		pipe_width = pipe_model_red.getWidth();
	}

	/**Various getter and setter methods 
	 *Return random pipe model, 50/50 chance of it being either red or green when combined with the getrandomPipeColor() method, which it is in the Master_Frame class 
	 *Takes the arguments of color, screen width, gap size between pipes, base y value which is screenheight * 0.79 which minimizes the usable area of the screen which is then used to calculate the vertical gap size
	 */
	public ArrayList<Level_pipe> getrandomPipe(String color, double BaseY, int screenwidth, int pipegap) throws IOException {
		//We create an array list to store all of our pipe objects, 4 in total, 2 top 2 bottom, that can be on the screen at any point in time to give off the illusion of a continuous stream of endless pipes without weird lapses in continuity 
		ArrayList<Level_pipe> randompipeset = new ArrayList<Level_pipe>();
		Level_pipe randomPipe1 = new Level_pipe();//Pipe object 1 added at index 0,stores the flipped version of the pipe that goes on the top of the level
		Level_pipe randomPipe2 = new Level_pipe();//Pipe object 2 added at index 1,stores the regular non flipped version of the pipe that goes on the bottom of the level
		Level_pipe randomPipe3 = new Level_pipe();//Pipe object 3 added at index 2,stores the flipped version of the pipe that goes on the top of the level
		Level_pipe randomPipe4 = new Level_pipe();//Pipe object 4 added at index 3,stores the regular non flipped version of the pipe that goes on the bottom of the level

		//Color options for the pipes
		if(color.equals("Red")) {
			randomPipe1.setpipemodel(pipe_model_red_flipped);
			randomPipe2.setpipemodel(pipe_model_red);
			randomPipe3.setpipemodel(pipe_model_red_flipped);
			randomPipe4.setpipemodel(pipe_model_red);
		}
		if(color.equals("Green")) {
			randomPipe1.setpipemodel(pipe_model_green_flipped);
			randomPipe2.setpipemodel(pipe_model_green);
			randomPipe3.setpipemodel(pipe_model_green_flipped);
			randomPipe4.setpipemodel(pipe_model_green);
		}

		//Calculate the vertical and horizontal displacement between each pipe
		//Pipe_x is the standard displacement between sets of pipes, meaning every top and bottom pair is separated from the next pair by the specified amount
		//gap_y is the vertical displacement between the top and bottom pipes, this is where our flappy bird has to navigate through so we make it just large enough to fit through every single time
		gap_y = randnum.nextInt((int) (BaseY * 0.6 - pipegap)); 
		gap_y += BaseY * 0.2;
		pipe_x = screenwidth + 10;

		//Set the x and y coordinates of the first pipe set (1 & 2)
		randomPipe1.setx(pipe_x);
		randomPipe2.setx(pipe_x);
		randomPipe1.sety(gap_y - randomPipe1.getPipeHeight());
		randomPipe2.sety(gap_y + pipegap);

		//recalculate the y displacement for the second set of pipes
		gap_y = randnum.nextInt((int) (BaseY * 0.6 - pipegap)); 
		gap_y += BaseY * 0.2;

		//Set the x and y coordinates of the second pipe set (3 & 4)
		randomPipe3.setx(pipe_x + 150);
		randomPipe4.setx(pipe_x + 150);
		randomPipe3.sety(gap_y - randomPipe1.getPipeHeight());
		randomPipe4.sety(gap_y + pipegap);

		//Add all of our newly decked out pipe objects to the array and send it out to wherever this method was called
		randompipeset.add(randomPipe1);//Upside down / flipped pipe at index 0
		randompipeset.add(randomPipe2);//Right side up pipe in index 1
		randompipeset.add(randomPipe3);//Upside down / flipped pipe at index 2
		randompipeset.add(randomPipe4);//Right side up pipe in index 3

		return randompipeset;//Return our randomly generated pipe 
	}

	//Returns a random pipe color between red and green with a 50/50 chance for either
	public String getrandomPipeColor() {
		String randomPipecolor = null;//Initialize the object with a value of null to prevent a system hang
		//Pick a random number between 0 and 1 
		int rand_int = randnum.nextInt(2); 
		switch(rand_int) {
		case 0:
			randomPipecolor = "Red";//Set the color variable of this pipe in order to generate an array of the same colored pipes
			break;
		case 1:
			randomPipecolor = "Green";
			break;
		}
		return randomPipecolor;//Return our randomly generated pipe color
	}

	//Get the top of the pipe's y coordinate, this is used for the pipes positioned on the floor / bottom part of the level, you 
	public double getPipeTop(){
		return this.y;
	}

	//Get the bottom of the pipe's y coordinate, this is used for the pipes positioned on the ceiling / upper part of the level
	public double getPipeBottom(){
		return this.y + this.pipe_height;
	}

	//Get the left edge of the pipe's x coordinate 
	public double getPipeEdge_left(){
		return this.x - this.pipe_width/2;
	}

	//Get the right edge of the pipe's x coordinate 
	public double getPipeEdge_right(){
		return this.x + this.pipe_width/2;
	}

	//Get the height of the pipe's image model
	public int getPipeHeight() {
		return this.pipe_height;
	}

	//Get the width of the pipe's image model
	public int getPipeWidth() {
		return this.pipe_width;
	}

	//Get the current pipe's color
	public String getPipecolor() {
		return this.pipe_color;
	}

	//Set the pipe's color 
	public void setPipecolor(String pipe_color) {
		this.pipe_color = pipe_color;
	}

	//Get the pipe's y, vertical gap / displacement from the other pipe 
	public int getPipegapy() {
		return this.gap_y;
	}

	//Get the pipe's x displacement 
	public int getpipex() {
		return this.pipe_x;
	}

	//Get the x coordinate 
	public int getx() {
		return this.x;
	}

	//Get the y coordinate 
	public int gety() {
		return this.y;
	}

	//Set the x coordinate
	public void setx(int x) {
		this.x = x;
	}

	//Set the y coordinate 
	public void sety(int y) {
		this.y = y;
	}

	//Set the pipe model by taking the pipe model you want to set it to as argument
	public void setpipemodel(BufferedImage pipe_model) {
		this.pipe_model = pipe_model;
	}

	//Get the pipe model in string form in order to compare and contrast it from the other models
	public String getpipemodel_string(){
		String pipe_model_string = null;

		if(this.getpipemodel().equals(pipe_model_red)) 
			pipe_model_string = "Red Right Side Up Pipe";

		if(this.getpipemodel().equals(pipe_model_red_flipped))
			pipe_model_string = "Red Flipped Pipe";

		if(this.getpipemodel().equals(pipe_model_green))
			pipe_model_string = "Green Right Side Up Pipe";

		if(this.getpipemodel().equals(pipe_model_green_flipped))
			pipe_model_string = "Green Flipped Pipe";

		return pipe_model_string;

	}

	//Get the current pipe model
	public BufferedImage getpipemodel(){
		return this.pipe_model;
	}

}

