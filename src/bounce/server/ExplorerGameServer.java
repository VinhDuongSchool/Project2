package bounce.server;

import bounce.common.Message;
import bounce.common.entities.Character;
import bounce.common.entities.Enemy;
import bounce.common.entities.Projectile;
import bounce.common.level.TileMap;
import bounce.common.lib;
import jig.Entity;
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

	public static final int PLAYINGSTATE = 0;


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
                var ct = (Class<? extends Character>) m.data;

                characters[(int) m.id] = Character.dyn(ct, m.gamepos, m.velocity, m.id);
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
                break;
            }
            case SET_DIR:
            {
                assert m.etype == null;
                characters[(int) m.id].setCurdir(m.dir);
                out_messages.add(m);
                break;
            }
            case MOUSE_IDX:
            {
                characters[(int) m.id].lookingDirIdx = m.intData;
                out_messages.add(m);
                break;
            }
        }
    }


    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        addState(new ServerPlayingState());

        // the sound resource takes a particularly long time to load,
        // we preload it here to (1) reduce latency when we first play it
        // and (2) because loading it will load the audio libraries and
        // unless that is done now, we can't *disable* sound as we
        // attempt to do in the startUp() method.

        lib.LOAD_SPRITES_ONCE();
        grid = new TileMap(100,100);

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
