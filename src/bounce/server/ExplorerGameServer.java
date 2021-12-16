package bounce.server;

import bounce.common.Character;
import bounce.common.*;
import bounce.common.level.TileMap;
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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Kevin Zavadlov
 * @author Riley Barnes
 * @author Vinh Doung
 *
 */
public class ExplorerGameServer extends StateBasedGame {

	public static final int STARTUPSTATE = 0;
	public static final int PLAYINGSTATE = 1;
	public static final int GAMEOVERSTATE = 2;


    public static final String SPRITES = "bounce/resource/sprites.png";
    public static final String PROJECTILE = "bounce/resource/projectile.png";
    public final int ScreenWidth;
    public final int ScreenHeight;

    public ConcurrentLinkedQueue<Message> in_messages;
    public ConcurrentLinkedQueue<Message> out_messages;


    public SpriteSheet game_sprites;
    public Character[] characters; //The character class.
    public ArrayList<Enemy> enemies; //Enemies
    public ArrayList<Projectile> projectiles;
    public TileMap grid;

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
    public ExplorerGameServer(String title, int width, int height, int n_players) throws IOException {
        super(title);
        ScreenHeight = height;
        ScreenWidth = width;

        Entity.setCoarseGrainedCollisionBoundary(Entity.AABB);

        //(Kevin) initialize data structures
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        in_messages = new ConcurrentLinkedQueue<>();
        out_messages = new ConcurrentLinkedQueue<>();
        characters = new Character[n_players];
        final ArrayList<ObjectOutputStream> out_streams = new ArrayList<>();
        int client_id = 0;

        //(Kevin) basic server socket loop
        ServerSocket s  = new ServerSocket(8989);
        while (client_id < n_players) {
            System.out.println("waiting for conn");
            Socket conn = s.accept();
            System.out.println("accepted conn");
            final ObjectInputStream ois = new ObjectInputStream(conn.getInputStream());

            //(Kevin) Handle input streams
            lib.make_and_start_reader(in_messages, ois);

            //(Kevin) Handle output streams
            final var oos = new ObjectOutputStream(conn.getOutputStream());
            oos.writeInt(client_id++);
            oos.flush();
            out_streams.add(oos);
        }

        //(Kevin) Handle output streams to each client
        new Thread(() -> {
            while (true){
                var m = out_messages.poll();
                if (m == null) continue;
                for(var oos : out_streams){
                    try {
                        oos.writeObject(m);
                        oos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }
            }
        }).start();
        System.out.println("running");

    }


    public void handle_message(Message m){
        System.out.println("recieved " + m.type + " " +( m.etype == null ? "" : m.etype));
        switch (m.type){
            case SET_VELOCITY:
                characters[(int) m.id].setVelocity((Vector) m.data);
                break;

            //(Kevin) read a character from one client and broadcast it to all the others
            case INIT_CHARACTER:
            {
                var character_data_arr = (Object[]) m.data;
                var spritex = (int) character_data_arr[0];
                var spritey = (int) character_data_arr[1];
                var ct = (Class<? extends Character>) character_data_arr[2];


                characters[(int) m.id] = Character.dyn(ct, m.gamepos, m.velocity, spritex, spritey, m.id);
                out_messages.add(m);
                break;
            }
            case ADD_ENTITY:
            {
                assert false : "unreachable m = " + m.type;
                var e_data_arr = (Object[]) m.data;
                var spritex = (int) e_data_arr[0];
                var spritey = (int) e_data_arr[1];
                switch (m.etype){
                    case ENEMY:
                        enemies.add(new Enemy(m.gamepos, m.velocity, game_sprites.getSprite(spritex,spritey)));
                        m.id = enemies.get(enemies.size() - 1).id;
                        break;
//                    case PROJECTILE:
//                        projectiles.add(new Projectile(m.gamepos, m.velocity, ResourceManager.getImage(ExplorerGameServer.PROJECTILE), null));
//                        m.id = projectiles.get(projectiles.size() - 1).id;
//                        break;

                }

                out_messages.add(m);

                break;
            }
            case PRIMARY:
            {
                characters[(int)m.id].primary().ifPresent(projs -> {
                    projectiles.addAll(projs);
                    projs.stream().map(p -> (Message.builder(Message.MSG_TYPE.ADD_ENTITY, p.id)
                                .setEtype(Message.ENTITY_TYPE.PROJECTILE)
                                .setGamepos(p.getGamepos())
                                .setDir(p.curdir)
                                .setVelocity(p.getVelocity())))
                            .forEach(out_messages::add);
                });
//                var p = new Projectile(c.getGamepos(), new Vector(0.1f, 0.1f), c.getCurdir());
//                var nm = new Message(Message.MSG_TYPE.ADD_ENTITY, , p.id, Message.ENTITY_TYPE.PROJECTILE);
//                nm.gamepos = p.getGamepos();
//                nm.velocity = p.getVelocity();
//                projectiles.add(p);
//                out_messages.add(nm);
                break;
            }
            case SET_DIR:
            {
                if(m.etype == Message.ENTITY_TYPE.CHARACTER){
                    characters[(int) m.id].setCurdir(m.dir);
                }
                out_messages.add(m);
                break;
            }
            case MOUSE_IDX:
            {
                characters[(int) m.id].lookingDirIdx = m.intData;
            }
        }
    }


    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        addState(new ServerPlayingState());
        addState(new GameOverState());
        addState(new StartUpState());

        // the sound resource takes a particularly long time to load,
        // we preload it here to (1) reduce latency when we first play it
        // and (2) because loading it will load the audio libraries and
        // unless that is done now, we can't *disable* sound as we
        // attempt to do in the startUp() method.

        ResourceManager.loadImage(SPRITES);
        ResourceManager.loadImage(PROJECTILE);
        game_sprites = ResourceManager.getSpriteSheet(SPRITES, 64,64);
        lib.LOAD_SPRITES_ONCE();
        grid = new TileMap(100,100, game_sprites);

        //(Kevin) dont start run server until all clients are connected
        while (Arrays.stream(characters).anyMatch(Objects::isNull)){
            for(var m = in_messages.poll(); m != null; m = in_messages.poll()){
                if (m.type == Message.MSG_TYPE.INIT_CHARACTER)
                    handle_message(m);
            }
        }

    }

    public static void main(String[] args) {
        AppGameContainer app;

        //(Kevin) read number of players from args or default 2
        int n_players = 2;
        if (args.length > 0){
            n_players = Integer.parseInt(args[0]);
        }

        try {
            app = new AppGameContainer(new ExplorerGameServer("Bounce!", 800, 600, n_players));
            app.setDisplayMode(800, 600, false);
            app.setVSync(true);
            app.start();
		} catch (SlickException | IOException e) {
			e.printStackTrace();
		}

	}


}
