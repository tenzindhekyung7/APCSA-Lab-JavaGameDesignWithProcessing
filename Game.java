/* Game Class Starter File
 * Authors: Joel A. Bianchi
 * Last Edit: 5/26/25
 * Added example for using grid method setAllMarks()
 */

//import processing.sound.*;

import processing.core.PApplet;
import processing.core.PImage;

public class Game extends PApplet {

  // ------------------ GAME VARIABLES --------------------//

  // VARIABLES: Processing variable to do Processing things
  PApplet p;

  // VARIABLES: Title Bar
  String titleText = "PEANUT CHESS SKY HORSE 2";
  String extraText = "CurrentLevel?";
  String name = "Undefined";

  // VARIABLES: Whole Game
  AnimatedSprite runningHorse;
  boolean doAnimation;

  // VARIABLES: splashScreen
  Screen splashScreen;
  PImage splashBg;
  String splashBgFile = "images/apcsa.png";
  // SoundFile song;

  // VARIABLES: forestWorld Screen (characters move by pixels)
  World forestWorld;
  PImage forestWorldBg;
  String forestWorldBgFile = "images/forst.png";
  Sprite naruto; // Use Sprite for a pixel-based Location
  String narutoFile = "images/naruto.png";
  int narutoStartX = 50;
  int narutoStartY = 300;
  Platform plat;
  int health = 100;

  // VARIABLES: endScreen
  World endScreen;
  PImage endBg;
  String endBgFile = "images/youwin.png";

  // VARIABLES: Tracking the current Screen being displayed
  Screen currentScreen;
  CycleTimer slowCycleTimer;

  boolean start = true;

  // ------------------ REQUIRED PROCESSING METHODS --------------------//

  // Processing method that runs once for screen resolution settings
  public void settings() {
    // SETUP: Match the screen size to the background image size
    size(800, 600); // these will automatically be saved as width & height

    // Allows p variable to be used by other classes to access PApplet methods
    p = this;

  }

  // Required Processing method that gets run once
  public void setup() {

    p.imageMode(p.CORNER); // Set Images to read coordinates at corners
    // fullScreen(); //only use if not using a specfic bg image

    // SETUP: Set the title on the title bar
    surface.setTitle(titleText);

    // SETUP: Load BG images used in all screens
    splashBg = p.loadImage(splashBgFile);
    forestWorldBg = p.loadImage(forestWorldBgFile);
    endBg = p.loadImage(endBgFile);

    // SETUP: If non-moving, Resize all BG images to exactly match the screen size
    splashBg.resize(p.width, p.height);
    forestWorldBg.resize(p.width, p.height);
    endBg.resize(p.width, p.height);

    // SETUP: Construct each Screen, World, Grid
    splashScreen = new Screen(p, "splash", splashBg);
    forestWorld = new World(p, "forest", forestWorldBgFile, 1.0f, 0.0f, 0.0f); // moveable World constructor
    endScreen = new World(p, "end", endBg);
    currentScreen = splashScreen;

    // SETUP: Construct Game objects used in All Screens
    runningHorse = new AnimatedSprite(p, "sprites/horse_run.png", "sprites/horse_run.json", 50.0f, 75.0f, 1.0f);

    // SETUP: Setup more forestWorld objects
    naruto = new Sprite(p, narutoFile, 0.25f);
    naruto.moveTo(narutoStartX, narutoStartY);
    forestWorld.addSprite(naruto);
    forestWorld.addSpriteCopyTo(runningHorse, 100, 200); // example Sprite added to a World at a location, with a speed
    forestWorld.printWorldSprites();

    plat = new Platform(p, PColor.MAGENTA, 500.0f, 100.0f, 200.0f, 20.0f);
    plat.setOutlineColor(PColor.BLACK);
    // plat.startGravity(5.0f); //sets gravity to a rate of 5.0
    // forestWorld.addSprite(plat);
    System.out.println("Done loading forestWorld ...");

    // SETUP: Sound
    // Load a soundfile from the sounds folder of the sketch and play it back
    // song = new SoundFile(p, "sounds/Lenny_Kravitz_Fly_Away.mp3");
    // song.play();

    System.out.println("Game started...");

  } // end setup()

  // Required Processing method that automatically loops
  // (Anything drawn on the screen should be called from here)
  public void draw() {

    // DRAW LOOP: Update Screen Visuals
    updateTitleBar();
    updateScreen();

    // DRAW LOOP: Set Timers
    int cycleTime = 1; // milliseconds
    int slowCycleTime = 300; // milliseconds
    if (slowCycleTimer == null) {
      slowCycleTimer = new CycleTimer(p, slowCycleTime);
    }

    // DRAW LOOP: Populate & Move Sprites
    if (slowCycleTimer.isDone()) {
      populateSprites();
      moveSprites();
    }

    // DRAW LOOP: Pause Game Cycle
    currentScreen.pause(cycleTime); // slows down the game cycles

    // DRAW LOOP: Check for end of game
    if (isGameOver()) {
      endGame();
    }

  } // end draw()

  // ------------------ USER INPUT METHODS --------------------//

  // Known Processing method that automatically will run whenever a key is pressed
  public void keyPressed() {

    // check what key was pressed
    System.out.println("\nKey pressed: " + p.keyCode); // key gives you a character for the key pressed

    // What to do when a key is pressed?

    // if the 'n' key is pressed, ask for their name
    if (p.key == 'n') {
      name = Input.getString("What is your name?");
    }

    // if the 't' key is pressed, then toggle the animation on/off
    if (p.key == 't') {
      // Toggle the animation on & off
      doAnimation = !doAnimation;
      System.out.println("doAnimation: " + doAnimation);
    }

    if (currentScreen == forestWorld) {
      if (p.key == 'w') {
        naruto.move(0, 10);
        // plat.jump();
      }
    }

    // CHANGING SCREENS BASED ON KEYS
    // change to level1 if 1 key pressed, level2 if 2 key is pressed
    if (p.key == '1') {
      currentScreen = forestWorld;
    } else if (p.key == 'e') {
      currentScreen = endScreen;

      // reset the moving Platform every time the Screen is re-displayed
      // plat.moveTo(500.0f, 100.0f);
      // plat.setSpeed(0,0);
    }

  }

  // Known Processing method that automatically will run when a mouse click
  // triggers it
  public void mouseClicked() {

    // Print coordinates of mouse click
    System.out.println("\nMouse was clicked at (" + p.mouseX + "," + p.mouseY + ")");

    // Display color of pixel clicked
    int color = p.get(p.mouseX, p.mouseY);
    PColor.printPColor(p, color);

    // if the Screen is a Grid, print grid coordinate clicked
    if (currentScreen instanceof Grid) {
      System.out.println("Grid location --> " + ((Grid) currentScreen).getGridLocation());
    }

    // if the Screen is a Grid, "mark" the grid coordinate to track the state of the
    // Grid
    if (currentScreen instanceof Grid) {
      ((Grid) currentScreen).setMark("X", ((Grid) currentScreen).getGridLocation());
    }

  }

  // ------------------ CUSTOM GAME METHODS --------------------//

  // Updates the title bar of the Game
  public void updateTitleBar() {

    if (!isGameOver()) {

      extraText = currentScreen.getName();

      // set the title each loop
      surface.setTitle(
          titleText + "\t// CurrentScreen: " + extraText + " \t // Name: " + name + "\t // Health: " + health);

      // adjust the extra text as desired

    }
  }

  // Updates what is drawn on the screen each frame
  public void updateScreen() {

    // UPDATE: first lay down the Background
    currentScreen.showBg();

    // UPDATE: splashScreen
    if (currentScreen == splashScreen) {

      // Print an s in console when splashscreen is up
      System.out.print("s");

      // Change the screen to level 1 between 3 and 5 seconds
      if (splashScreen.getScreenTime() > 3000 && splashScreen.getScreenTime() < 5000) {
        currentScreen = forestWorld;
      }
    }

    // UPDATE: forestWorld Screen
    if (currentScreen == forestWorld) {

      // Print a '2' in console when forestWorld
      System.out.print("1");

      // Set speed of moving forestWorld background
      // forestWorld.moveBgXY(-0.3f, 0f);

    }

    // UPDATE: End Screen
    if (currentScreen == endScreen) {

    }

    // UPDATE: Any Screen
    if (doAnimation) {
      runningHorse.animateHorizontal(0.5f, 1.0f, true);
    }

    // UPDATE: Other built-in to current World/Grid/HexGrid
    currentScreen.show();

  }

  // Populates enemies or other sprites on the Screen
  public void populateSprites() {

    // What is the index for the last column?

    // Loop through all the rows in the last column

    // Generate a random number

    // 10% of the time, decide to add an enemy image to a Tile

  }

  // Moves around the enemies/sprites on the Screen
  public void moveSprites() {

    // Loop through all of the rows & cols in the grid

    // Store the current GridLocation

    // Store the next GridLocation

    // Check if the current tile has an image that is not piece1

    // Get image/sprite from current location

    // CASE 1: Collision with piece1

    // CASE 2: Move enemy over to new location

    // Erase image/sprite from old location

    // System.out.println(loc + " " + grid.hasTileImage(loc));

    // CASE 3: Enemy leaves screen at first column

  }

  // Checks if there is a collision between Sprites on the Screen
  public boolean checkCollision(GridLocation loc, GridLocation nextLoc) {

    // Check what image/sprite is stored in the CURRENT location
    // PImage image = grid.getTileImage(loc);
    // AnimatedSprite sprite = grid.getTileSprite(loc);

    // if empty --> no collision

    // Check what image/sprite is stored in the NEXT location

    // if empty --> no collision

    // check if enemy runs into player

    // clear out the enemy if it hits the player (using cleartTileImage() or
    // clearTileSprite() from Grid class)

    // Update status variable

    // check if a player collides into enemy

    return false; // <--default return
  }

  // Indicates when the main game is over
  public boolean isGameOver() {

    return false; // by default, the game is never over
  }

  // Describes what happens after the game is over
  public void endGame() {
    System.out.println("Game Over!");

    // Update the title bar

    // Show any end imagery
    currentScreen = endScreen;

  }

} // close class
