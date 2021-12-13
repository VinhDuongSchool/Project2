package bounce.client;

import bounce.common.*;
import jig.Vector;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This state is active when the Game is being played. In this state, sound is
 * turned on, the bounce counter begins at 0 and increases until 10 at which
 * point a transition to the Game Over state is initiated. The user can also
 * control the ball using the WAS & D keys.
 *
 * Transitions From StartUpState
 *
 * Transitions To GameOverState
 */
public class ClientPlayingState extends BasicGameState {

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
        ExplorerGameClient egc = (ExplorerGameClient)game;

        if(egc.is_connected)
            return;

        container.setSoundOn(true);

        egc.grid.MakePath(new ArrayList<Vector>( List.of(egc.character.getGamepos())));

        egc.enemies.add(new Enemy(64,32, 0, 0, egc.game_sprites.getSprite(0, 9))); //Add the enemies
//        egc.enemies.add(new Enemy(32*3,32*5, 0, 0, egc.game_sprites.getSprite(0, 9)));
//        egc.enemies.add(new Enemy(32,32, 0, 0, egc.game_sprites.getSprite(1, 8)));


	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
        ExplorerGameClient egc = (ExplorerGameClient)game;


        var screen_offset = lib.to_screen(egc.character.getGamepos().scale(-1), egc.screen_center);
        egc.character.setPosition(egc.screen_center);
        egc.screenox = screen_offset.getX();
        egc.screenoy = screen_offset.getY();

        egc.grid.render(g,screen_offset, egc.character.getGamepos());



        // testing stuff
//        var v = lib.to_screen(egc.character.gamepos, new Vector(egc.screenox, egc.screenoy));
//        g.drawLine(0,0, v.getX(), v.getY());
//        draw game pos on screen
        g.setColor(Color.blue);
        g.drawRect(egc.character.getGamepos().getX(), egc.character.getGamepos().getY(), 32, 32);
        g.drawRect(egc.grid.tiles[10][10].gamepos.getX(),egc.grid.tiles[10][10].gamepos.getY(), 32,32);
        for (var e : egc.enemies){
            g.drawRect(e.getGamepos().getX(), e.getGamepos().getY(),32,32);
        }
        g.setColor(Color.gray);
//        System.out.print(egc.character.gamepos + " ");
//        System.out.println(Math.floor(egc.character.gamepos.getX() / 32.0f));
//        var p = new Polygon();
//        var v = new Vector(0,0);
//        var v2 = lib.to_screen(v, new Vector(egc.screenox, egc.screenoy));
//        p.addPoint(v2.getX(), v2.getY());
//        v = new Vector(32,32);
//        v2 = lib.to_screen(v, new Vector(egc.screenox, egc.screenoy));
//        p.addPoint(v2.getX(), v2.getY());
//        v = new Vector(0,32);
//        v2 = lib.to_screen(v, new Vector(egc.screenox, egc.screenoy));
//        p.addPoint(v2.getX(), v2.getY());
//        v = new Vector(32,0);
//        v2 = lib.to_screen(v, new Vector(egc.screenox, egc.screenoy));
//        p.addPoint(v2.getX(), v2.getY());
//        g.draw(p);


        for (Enemy e : egc.enemies) {//Render all the enemies.
            e.setPosition(lib.to_screen(e.getGamepos(), new Vector(egc.screenox, egc.screenoy)));
            e.render(g);
        }
        egc.character.render(g); //Render the character onto the screen.

        egc.projectiles.stream().forEach(p -> {
            p.setPosition(lib.to_screen(p.getGamepos(), new Vector(egc.screenox, egc.screenoy)));
            p.render(g);
        });

        if(egc.is_connected){
            for (var c : egc.allies.values()){
                if (c.client_id != egc.ID){
                    c.setPosition(lib.to_screen(c.getGamepos(), new Vector(egc.screenox, egc.screenoy)));
                    c.render(g);
                }
            }
        }
        //System.out.println(egc.character.gamepos);
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
        Input input = container.getInput();
        ExplorerGameClient egc = (ExplorerGameClient) game;

        if(!egc.is_connected)
            egc.grid.MakePath(new ArrayList<Vector>( List.of(egc.character.getGamepos())));


        //(Kevin) deal with user input
        // will need to change movement stuff to make it easier to do different sprites for different directions
        Vector characterVector = new Vector(0,0);
        var inp = List.of( new Boolean[]{input.isKeyDown(Input.KEY_W), input.isKeyDown(Input.KEY_A), input.isKeyDown(Input.KEY_S), input.isKeyDown(Input.KEY_D)});
        var characterDir = lib.wasd_to_dir(inp);
        if (characterDir != null){
//            System.out.println(d);
            var UP_V = new Vector(0.2f,0).unit().scale(.2f);
            var LEFT_V = new Vector(0,-.2f).unit().scale(.2f);
            // todo fix long ass switch
            switch (characterDir){
                case NORTH:
                    characterVector = UP_V;
                    break;
                case SOUTH:
                    characterVector = UP_V.scale(-1);
                    break;
                case WEST:
                    characterVector = LEFT_V;
                    break;
                case EAST:
                    characterVector = LEFT_V.scale(-1);
                    break;
                case NORTHEAST:
                    characterVector = UP_V.add(LEFT_V.scale(-1));
                    break;
                case NORTHWEST:
                    characterVector = UP_V.add(LEFT_V);
                    break;
                case SOUTHEAST:
                    characterVector = UP_V.scale(-1).add(LEFT_V.scale(-1));
                    break;
                case SOUTHWEST:
                    characterVector = UP_V.scale(-1).add(LEFT_V);
                    break;
                default:
                    assert false : "unreachable";
                    break;
            }

        }

        var mousePos = new Vector(input.getMouseX(), input.getMouseY());

        //Kevin, m is mouse cords on screen, character is always in the sceen center,
        //angleto gives the angle in degrees rotated by 180 for some reason,
        //divide by 45 to convert into 8 directions, then round to get the angle index,
        int diridx = (int)Math.round((mousePos.angleTo(egc.screen_center)+180)/45);

        //Kevin, attack when left mouse or I is pressed, (my mouse isnt recognized so i needed the i key lol)
        if(input.isKeyPressed(Input.MOUSE_LEFT_BUTTON) || input.isKeyPressed(Input.KEY_I)){
            //0 and 8 map to the same value
            ArrayList<lib.DIRS> attack_dirs;
            if (diridx == 0 || diridx == 8){
                //Kevin, deal with edge case
                attack_dirs = new ArrayList<>(List.of(new lib.DIRS[]{lib.DIRS.NORTHEAST, lib.DIRS.NORTH, lib.DIRS.EAST}));
            } else {
                //Kevin, otherwise get neighbors directly
                attack_dirs = new ArrayList<>(List.of(new lib.DIRS[]{lib.angle_index_to_dir[diridx-1], lib.angle_index_to_dir[diridx], lib.angle_index_to_dir[diridx+1]}));
            }
            //Kevin, player melee converts directions into the correct hit boxes
            egc.character.playermelee(attack_dirs);
        }

//        Kevin, commented out until its used for something
//        for(Enemy e : egc.enemies){
//            if(egc.character.collides(e)!= null){
//                System.out.println("character collided with an enemy");
//            }
//        }


        if(egc.is_connected){ //Kevin, run with a server
            var messages = new ArrayList<Message>();
            if (!egc.character.getVelocity().equals(characterVector)){
                messages.add(new Message(Message.MSG_TYPE.SET_VELOCITY, characterVector, egc.ID));
                egc.character.setVelocity(characterVector);
            }
            if(egc.character.curdir != characterDir){
                messages.add(Message.builder(Message.MSG_TYPE.SET_DIR, egc.ID).setEtype(Message.ENTITY_TYPE.CHARACTER));
                egc.character.curdir = characterDir;
            }


            if(input.isKeyPressed(Input.KEY_F))
                messages.add(Message.builder(Message.MSG_TYPE.FIRE_PROJECTILE, egc.ID));

            if(diridx >= 0)
                messages.add(Message.builder(Message.MSG_TYPE.MOUSE_IDX, egc.ID));

            messages.stream().forEach(m -> {
                try {
                    egc.out_stream.writeObject(m);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            for(var m = egc.in_messages.poll(); m != null; m = egc.in_messages.poll()){
                egc.handle_message(m);
            }

        } else { //Kevin, update stuff as a solo program
            egc.character.curdir  =  characterDir;
            if(input.isKeyPressed(Input.KEY_F))
                egc.projectiles.add(new Projectile(egc.character.getGamepos(), egc.character.getVelocity(), 0, lib.angle_index_to_dir[diridx])); //Set the initial location to the player.

            //(Kevin) handle stuff when client isnt connected
            egc.character.setVelocity(characterVector);
            egc.character.update(delta); //Update the position of the player

            //Kevin, check collision with the 8 neighbor tiles of the character and undo their movement if there is a collision
            egc.grid.getNeighbors(egc.character.getGamepos()).stream()
                    .filter(t -> t.type == TileMap.TYPE.WALL)
                    .map(egc.character::collides) // stream of collisions that may be null
                    .filter(Objects::nonNull)
                    .findAny().ifPresent(c -> { // the actual collision object isnt useful, the minpentration doesnt work at all
                        egc.character.setVelocity(egc.character.getVelocity().scale(-1));
                        egc.character.update(delta);
                    });


            //(Kevin) update all other entities
            egc.projectiles.stream().forEach(p -> p.update(delta));
            egc.enemies.stream().forEach(e -> e.update(delta, egc.grid.getTile(e.getGamepos())));

            //Kevin, check if projectiles collide with enemies
            for (Projectile p : egc.projectiles){
                for (Enemy e : egc.enemies) {
                    if (p.collides(e) != null){
                        e.setHealth(e.getHealth() - p.damage);
                        p.setHit(true);
                        break; // each projectile should only collide with a single entity
                    }
                }
            }

            //(Kevin) remove dead/hit/etc stuff
            egc.enemies.removeIf(e -> e.getHealth() <=0);
            egc.projectiles.removeIf(Projectile::getHit);
        }
    }



    @Override
    public int getID() {
        return ExplorerGameClient.PLAYINGSTATE;
	}

}
