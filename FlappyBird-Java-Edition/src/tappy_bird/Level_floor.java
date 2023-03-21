package tappy_bird;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

public class Level_floor {
	private int floor_x_coordinate = 0;//X coordinate of the floor
	private BufferedImage floor_image;//The floor's image is stored here

	//Default constructor that imports the floor image
	public Level_floor() throws IOException {
		BufferedImage floor_image = ImageIO.read(new File("Resources/Sprites/Environment-sprites/base.png"));
		this.floor_image = floor_image;
	}

	//Overloaded constructor that takes the x coordinate of the floor image as argument 
	public Level_floor(int floor_x_coordinate) throws IOException{
		this.floor_x_coordinate = floor_x_coordinate;
		BufferedImage floor_image = ImageIO.read(new File("Resources/Sprites/Environment-sprites/base.png"));
		this.floor_image = floor_image;
	}

	//Get the current x coordinate of the floor
	public int get_x_cord() {
		return this.floor_x_coordinate;
	}

	//Set the x coordinate of the floor
	public void set_x_cord(int floor_x_coordinate) {
		this.floor_x_coordinate = floor_x_coordinate;
	}

	//get the level's floor image model
	public BufferedImage get_buffedimage() {
		return this.floor_image;
	}

	//Set the level's floor image model
	public void set_buffedimage(BufferedImage floor_image) {
		this.floor_image = floor_image;
	}

}
