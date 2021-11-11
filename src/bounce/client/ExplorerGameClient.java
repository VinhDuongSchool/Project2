package bounce.client;

import bounce.common.*;
import jig.Entity;
import java.util.ArrayList;
import jig.ResourceManager;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;


/**
 * @author Kevin Zavadlov
 * @author Riley Barnes
 * @author Vinh Doung
 *
 */
public class ExplorerGameClient extends StateBasedGame {

	public static final int STARTUPSTATE = 0;
	public static final int PLAYINGSTATE = 1;
	public static final int GAMEOVERSTATE = 2;

    public static final String SPRITES = "bounce/resource/sprites.png";
//	public static final String BALL_BALLIMG_RSC = "bounce/resource/ball.png";
//	public static final String BALL_BROKENIMG_RSC = "bounce/resource/brokenball.png";
//	public static final String GAMEOVER_BANNER_RSC = "bounce/resource/gameover.png";
//	public static final String STARTUP_BANNER_RSC = "bounce/resource/PressSpace.png";
//	public static final String BANG_EXPLOSIONIMG_RSC = "bounce/resource/explosion.png";
//	public static final String BANG_EXPLOSIONSND_RSC = "bounce/resource/explosion.wav";

	public final int ScreenWidth;
	public final int ScreenHeight;


    public SpriteSheet game_sprites;
    public float screenox;
    public float screenoy;
    public boolean is_connected;
	public CharacterClass character; //The character class.
	public ArrayList<EnemyClass> enemies; //Enemies

	/**
	 * Create the BounceGame frame, saving the width and height for later use.
	 *
	 * @param title
	 *            the window's title
	 * @param width
	 *            the window's width
	 * @param height
	 *            the window's height
	 */
	public ExplorerGameClient(String title, int width, int height, boolean connected) {
		super(title);
		ScreenHeight = height;
		ScreenWidth = width;
        is_connected = connected;
		Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);
		enemies = new ArrayList<EnemyClass>(10); // Initialize the arrayList
	}


	@Override
	public void initStatesList(GameContainer container) throws SlickException {
        addState(new PlayingState());
		addState(new StartUpState());
		addState(new GameOverState());


		// the sound resource takes a particularly long time to load,
		// we preload it here to (1) reduce latency when we first play it
		// and (2) because loading it will load the audio libraries and
		// unless that is done now, we can't *disable* sound as we
		// attempt to do in the startUp() method.
//		ResourceManager.loadSound(BANG_EXPLOSIONSND_RSC);

		// preload all the resources to avoid warnings & minimize latency...
//		ResourceManager.loadImage(BALL_BALLIMG_RSC);
//		ResourceManager.loadImage(BALL_BROKENIMG_RSC);
//		ResourceManager.loadImage(GAMEOVER_BANNER_RSC);
//		ResourceManager.loadImage(STARTUP_BANNER_RSC);
//		ResourceManager.loadImage(BANG_EXPLOSIONIMG_RSC);
        ResourceManager.loadImage(SPRITES);
        game_sprites = ResourceManager.getSpriteSheet(SPRITES, 64,64);
        screenox = 0;
        screenoy = 0;
        character = new CharacterClass(400,300, 0, 0, game_sprites.getSprite(0, 10));  //Set up the character.

	}

	public static void main(String[] args) {
		AppGameContainer app;
		try {
			app = new AppGameContainer(new ExplorerGameClient("Bounce!", 800, 600, false));
			app.setDisplayMode(800, 600, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}


}
