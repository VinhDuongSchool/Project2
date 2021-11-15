package bounce.client;

import bounce.common.Character;
import bounce.common.*;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


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
    public static final String PROJECTILE = "bounce/resource/projectile.png";

	public final int ScreenWidth;
	public final int ScreenHeight;

    public final Vector screen_center;
    public SpriteSheet game_sprites;
    public float screenox;
    public float screenoy;
    public boolean is_connected;
	public Character character; //The character class.
	public ArrayList<Enemy> enemies; //Enemies
    public ArrayList<Projectile> projectiles;

    public ConcurrentLinkedQueue<Message> in_messages;
    public ObjectOutputStream out_stream;
    public int ID;
    public TileMap grid;
    public HashMap< Integer, Character> allies;
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
	public ExplorerGameClient(String title, int width, int height, boolean connected) throws IOException {
		super(title);

        is_connected = connected;
        if(is_connected){
            server_setup();
        } else {
            ID = 0;
        }
		ScreenHeight = height;
		ScreenWidth = width;
		Entity.setCoarseGrainedCollisionBoundary(Entity.CIRCLE);
        Entity.setDebug(true);
		enemies = new ArrayList<>(10); // Initialize the arrayList
        projectiles = new ArrayList<>();
        screen_center = new Vector(ScreenWidth/2.0f,ScreenHeight/2.0f);
        System.out.println(ID);
	}

    private void server_setup() throws IOException {
        //(Kevin) initialize data structres needed for server communication
        in_messages = new ConcurrentLinkedQueue<>();
        allies = new HashMap<>();

        Socket conn = new Socket("localhost", 8989);

        //(Kevin) inputstream needs a thread because it blocks
        out_stream = new ObjectOutputStream(conn.getOutputStream());
        final var ois = new ObjectInputStream(conn.getInputStream());

        //(Kevin) protocol is first communication with server is retrieving the client ID number
        ID = ois.readInt();
        lib.make_and_start_reader(in_messages, ois);

        System.out.println("running");
    }

    public void handle_message(Message m){
        System.out.println("recieved " + m.type);

        switch (m.type){
            case INIT_CHARACTER:
                //(Kevin) retrieve a character initialization object from the server
                //(might be some better way to do this, but cant send entities directly they arnt serializable)
                if(m.id != ID){
                    var character_data_arr = (Object[]) m.data;
                    var pos = (Vector) character_data_arr[0];
                    var velocity = (Vector) character_data_arr[1];
                    var spritex = (int) character_data_arr[2];
                    var spritey = (int) character_data_arr[3];

                    allies.put(m.id, new Character(
                            pos.getX(), pos.getY(),
                            velocity.getX(), velocity.getY(),
                            game_sprites.getSprite(spritex, spritey),
                            m.id
                    ));
                }
                break;

            case NEW_POSITION:
                allies.get(m.id).gamepos = (Vector) m.data;
        }
    }

	@Override
	public void initStatesList(GameContainer container) throws SlickException {
        System.out.println("init states list");
        addState(new ClientPlayingState());
		addState(new StartUpState());
		addState(new GameOverState());



		// the sound resource takes a particularly long time to load,
		// we preload it here to (1) reduce latency when we first play it
		// and (2) because loading it will load the audio libraries and
		// unless that is done now, we can't *disable* sound as we
		// attempt to do in the startUp() method.

		// preload all the resources to avoid warnings & minimize latency...

        ResourceManager.loadImage(SPRITES);
        ResourceManager.loadImage(PROJECTILE);
        game_sprites = ResourceManager.getSpriteSheet(SPRITES, 64,64);
        screenox = 0;
        screenoy = 0;

        // character will always render in the center of the screen
        int sprite_x = 0;
        int sprite_y = 10;
        character = new Character(
                screen_center.getX(), screen_center.getY(),
                0,0,
                game_sprites.getSprite(sprite_x, sprite_y),
                ID);  //Set up the character.

        grid = new TileMap(100,100, game_sprites);
        //(Kevin) send this clients character to everyone else
        if (is_connected) {
            allies.put(ID, character);
            try {
                out_stream.writeObject(new Message(Message.MSG_TYPE.INIT_CHARACTER, new Object[]{
                        character.gamepos,
                        character.getVelocity(),
                        sprite_x,
                        sprite_y
                }, ID));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

	public static void main(String[] args) {
		AppGameContainer app;
        //(Kevin) is_connected may be passed through program args
        boolean connected;
        if (args.length > 0){
            connected = Boolean.parseBoolean(args[0].toLowerCase());
        } else {
            connected = false;
        }

		try {
			app = new AppGameContainer(new ExplorerGameClient("Bounce!", 800, 600,connected));
			app.setDisplayMode(800, 600, false);
			app.setVSync(true);
			app.start();
		} catch (SlickException | IOException e) {
			e.printStackTrace();
		}

	}


}
