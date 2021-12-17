package bounce.client;

import bounce.common.Message;
import bounce.common.entities.Character;
import bounce.common.entities.*;
import bounce.common.items.BaseItem;
import bounce.common.items.PileOfGold;
import bounce.common.items.Potion;
import bounce.common.level.TileMap;
import bounce.common.lib;
import jig.Entity;
import jig.Vector;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
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
    public static final int CHARACTERSELECTSTATE = 3;
    public static final int WINSTATE = 4;

	public final int ScreenWidth;
	public final int ScreenHeight;

    public final Vector screen_center;
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
    public ArrayList<BaseItem> items;

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
        items = new ArrayList<>();
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
        if(m.type != Message.MSG_TYPE.NEW_POSITION)
            System.out.println("recieved " + m.type + " " +( m.etype == null ? "" : m.etype));

        switch (m.type){
            case INIT_CHARACTER:
            {
                //(Kevin) retrieve a character initialization object from the server
                //(might be some better way to do this, but cant send entities directly they arnt serializable)
                assert m.gamepos != null;
                assert m.velocity != null;

                if(m.id != ID){
                    var ct = (Class<? extends Character>) m.data;

                    allies.put(m.id, Character.dyn(ct, m.gamepos, m.velocity, m.id));
                }
                break;
            }
            case NEW_POSITION:

                assert m.gamepos != null;
                switch (m.etype){
                    case CHARACTER:
                        allies.get(m.id).setGamepos(m.gamepos);
                        break;
                    case PROJECTILE:
                        for (Projectile p : projectiles){
                            if(p.id == m.id){
                                p.setGamepos(m.gamepos);
                                break;
                            }
                        }
                        break;
                    case ENEMY:
                        for (Enemy e : enemies){
                            if(e.id == m.id){
                                e.setGamepos(m.gamepos);
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
                    case ZOMBIE:
                    {
                        var e_data_arr = (Object[]) m.data;
                        var spritex = (int) e_data_arr[0];
                        var spritey = (int) e_data_arr[1];
                        enemies.add(new Zombie(m.gamepos, m.velocity, lib.game_sprites.getSprite(0,9), m.id));
                        break;
                    }
                    case SHADOWARCHER:
                    {
                        var e_data_arr = (Object[]) m.data;
                        var spritex = (int) e_data_arr[0];
                        var spritey = (int) e_data_arr[1];
                        enemies.add(new ShadowArcher(m.gamepos, m.velocity, lib.game_sprites.getSprite(3,8), m.id));
                        break;
                    }
                    case PROJECTILE:
                        projectiles.add(new Projectile(m.gamepos, m.velocity,  m.id, m.dir));
                        break;
                    case GOLDPILE:
                        items.add(new PileOfGold(m.gamepos.getX(), m.gamepos.getY()));
                        break;
                    case POTION:
                        items.add(new Potion(m.gamepos.getX(), m.gamepos.getY()));
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

                    case GOLDPILE:
                    case POTION:
                        items.remove(items.stream().filter(i -> i.id == m.id).findFirst().get());
                        break;
                }
                break;
            }
            case SET_DIR:
            {
                if(m.etype == Message.ENTITY_TYPE.CHARACTER && m.id != ID){
                    allies.get(m.id).setCurdir(m.dir);
                }
                break;
            }
            case MOUSE_IDX:
            {
                if (m.id != ID) {
                    allies.get(m.id).lookingDirIdx = m.intData;
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
//                                        () -> System.out.println("missed entity id"));
                                        // if this throws you can comment it out, but it shouldnt throw
                                        () -> {throw new RuntimeException("enemy id missing");});
                        break;
                    case CHARACTER:
                        allies.get(m.id).health = m.HP;
                        break;
                }
                break;
            }
            case SET_ATTACK_TIMER:
            {
                allies.get(m.id).attack_timer = m.intData;
                break;
            }
            case COMPLETE_ROOM:
            {
                grid.rooms.get((int) m.id).open();
                break;
            }
            case CLOSE_ROOM:
            {
                grid.rooms.get((int) m.id).close();
                break;
            }
            case DEAD:
            {
                allies.get(m.id).dead = true;
                allies.get(m.id).dieScene();
                break;
            }

        }
    }


    public void setCharacter(Class<? extends Character> ct, Vector gp, Vector v){
        character = Character.dyn(ct,gp,v, ID);

        //(Kevin) send this clients character to everyone else
        if (is_connected) {
            allies.put(ID, character);
            try {
                out_stream.writeObject(Message.builder(Message.MSG_TYPE.INIT_CHARACTER, ID)

                        .setGamepos(character.getGamepos())
                        .setVelocity(character.getVelocity())
                        .setData(ct));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        System.out.println("init states list");
        addState(new CharacterSelectScreen());
        addState(new ClientPlayingState());
		addState(new StartUpState());
		addState(new GameOverState());
        addState(new WinState());


		// the sound resource takes a particularly long time to load,
		// we preload it here to (1) reduce latency when we first play it
		// and (2) because loading it will load the audio libraries and
		// unless that is done now, we can't *disable* sound as we
		// attempt to do in the startUp() method.

		// preload all the resources to avoid warnings & minimize latency...
        lib.LOAD_SPRITES_ONCE();

        //Kevin, also initialize grid
        grid = new TileMap(100,100);
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
