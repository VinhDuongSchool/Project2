package bounce.client;

import bounce.common.Character;
import bounce.common.*;
import bounce.server.ExplorerGameServer;
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
    public static final String UD = "bounce/resource/UD.png";
    public static final String LR = "bounce/resource/LR.png";
    public static final String UR = "bounce/resource/UR.png";
    public static final String DR = "bounce/resource/DR.png";


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
    public long ID;
    public TileMap grid;
    public HashMap<Long, Character> allies;



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
        allies = new HashMap<Long, Character>();

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
        System.out.println("recieved " + m.type + " " +( m.etype == null ? "" : m.etype));

        switch (m.type){
            case INIT_CHARACTER:
            {
                //(Kevin) retrieve a character initialization object from the server
                //(might be some better way to do this, but cant send entities directly they arnt serializable)
                assert m.gamepos != null;
                if(m.id != ID){
                    var character_data_arr = (Object[]) m.data;
                    var spritex = (int) character_data_arr[0];
                    var spritey = (int) character_data_arr[1];

                    allies.put(m.id, (new Character(
                            m.gamepos,
                            new Vector(0,0),
                            game_sprites.getSprite(spritex, spritey),
                            m.id
                    )));
                }
                break;
            }
            case NEW_POSITION:

                assert m.gamepos != null;
                switch (m.etype){
                    case CHARACTER:
                        allies.get(m.id).gamepos = m.gamepos;
                        break;
                    case PROJECTILE:
                        for (Projectile p : projectiles){
                            if(p.id == m.id){
                                p.gamepos = m.gamepos;
                                break;
                            }
                        }
                        break;
                    case ENEMY:
                        for (Enemy e : enemies){
                            if(e.id == m.id){
                                e.gamepos = m.gamepos;
                                break;
                            }
                        }
                        break;
                }
                break;
            case ADD_ENTITY:
            {
                assert m.gamepos != null;

                switch (m.etype){
                    case ENEMY:
                        var e_data_arr = (Object[]) m.data;
                        var spritex = (int) e_data_arr[0];
                        var spritey = (int) e_data_arr[1];
                        enemies.add(new Enemy(m.gamepos, m.velocity, game_sprites.getSprite(spritex,spritey), m.id));
                        break;
                    case PROJECTILE:
                        projectiles.add(new Projectile(m.gamepos, m.velocity, ResourceManager.getImage(ExplorerGameServer.PROJECTILE), m.id, null));
                        break;
                }
                break;
            }
            case REMOVE_ENTITY:
            {
                switch (m.etype){
                    case ENEMY:
                        enemies.remove(enemies.stream().filter(e -> e.id == m.id).findFirst().get());
                        break;
                    case PROJECTILE:
                        projectiles.remove(projectiles.stream().filter(p -> p.id == m.id).findFirst().get());
                        break;
                }
                break;
            }
            case SET_DIR:
            {
                if(m.etype == Message.ENTITY_TYPE.CHARACTER && m.id != ID){
                    allies.get(m.id).curdir = m.dir;
                }
                break;
            }
            case SET_HP:
            {
                switch (m.etype){
                    case ENEMY:
                        enemies.stream()
                                .filter(e -> e.id == m.id)
                                .findFirst()
                                .ifPresentOrElse(
                                        e -> e.setHealth(m.HP),
                                        () -> System.out.println("missed entity id"));
                }
            }

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
        ResourceManager.loadImage(UD);
        ResourceManager.loadImage(LR);
        ResourceManager.loadImage(UR);
        ResourceManager.loadImage(DR);
        game_sprites = ResourceManager.getSpriteSheet(SPRITES, 64,64);
        screenox = 0;
        screenoy = 0;

        // character will always render in the center of the screen
        int sprite_x = 0;
        int sprite_y = 10;
        character = new Warrior(
                32*5, 32*5,
                0,0,
                game_sprites.getSprite(sprite_x, sprite_y),
                ID);  //Set up the character.

        grid = new TileMap(100,100, game_sprites);
        //(Kevin) send this clients character to everyone else
        if (is_connected) {
            allies.put(ID, character);
            try {
                var m = new Message(Message.MSG_TYPE.INIT_CHARACTER, new Object[]{sprite_x, sprite_y}, ID);
                m.gamepos = character.gamepos;
                out_stream.writeObject(m);
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
            System.out.println("making app");
			app = new AppGameContainer(new ExplorerGameClient("Bounce!", 800, 600,connected));
			app.setDisplayMode(800, 600, false);
			app.setVSync(true);
            System.out.println("starting");
			app.start();
            System.out.println("started");
		} catch (SlickException | IOException e) {
			e.printStackTrace();
		}

	}


}
