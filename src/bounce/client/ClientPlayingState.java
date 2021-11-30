package bounce.client;

import bounce.common.*;
import jig.Vector;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
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

    private int lastDelta;
    private Vector lastVector;


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

        egc.grid.MakePath(new ArrayList<Vector>( List.of(egc.character.gamepos)));

        egc.enemies.add(new Enemy(64,32, 0, 0, egc.game_sprites.getSprite(0, 9))); //Add the enemies
        egc.enemies.add(new Enemy(32*3,32*5, 0, 0, egc.game_sprites.getSprite(0, 9)));
        egc.enemies.add(new Enemy(32,32, 0, 0, egc.game_sprites.getSprite(1, 8)));

        egc.enemies.forEach(e -> {
            e.goal = egc.grid.getTile(e.gamepos).next;
            e.setVelocity(e.goal.gamepos.subtract(e.gamepos));
        });

	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
        ExplorerGameClient egc = (ExplorerGameClient)game;


        var screen_offset = lib.to_screen(new Vector( -egc.character.gamepos.getX(), -egc.character.gamepos.getY()), egc.screen_center);
        egc.character.setPosition(egc.screen_center);
        egc.screenox = screen_offset.getX();
        egc.screenoy = screen_offset.getY();

        egc.grid.render(g,screen_offset, egc.character.gamepos);



        // testing stuff
//        var v = lib.to_screen(egc.character.gamepos, new Vector(egc.screenox, egc.screenoy));
//        g.drawLine(0,0, v.getX(), v.getY());
//        draw game pos on screen
//        g.setColor(Color.blue);
//        g.drawRect(egc.character.gamepos.getX(), egc.character.gamepos.getY(), 32, 32);
//        g.drawRect(egc.grid.tiles[10][10].gamepos.getX(),egc.grid.tiles[10][10].gamepos.getY(), 32,32);
//        g.setColor(Color.gray);
//        for (var e : egc.enemies){
//            g.drawRect(e.gamepos.getX(),e.gamepos.getY(),32,32);
//        }
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
            e.setPosition(lib.to_screen(e.gamepos, new Vector(egc.screenox, egc.screenoy)));
            e.render(g);
        }
        egc.character.render(g); //Render the character onto the screen.

        egc.projectiles.stream().forEach(p -> {
            p.setPosition(lib.to_screen(p.gamepos, new Vector(egc.screenox, egc.screenoy)));
            p.render(g);
        });

        if(egc.is_connected){
            for (var c : egc.allies.values()){
                if (c.client_id != egc.ID){
                    c.setPosition(lib.to_screen(c.gamepos, new Vector(egc.screenox, egc.screenoy)));
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

        egc.grid.MakePath(new ArrayList<Vector>( List.of(egc.character.gamepos)));


        //(Kevin) deal with user input
        // will need to change movement stuff to make it easier to do different sprites for different directions
        Vector v = new Vector(0,0);
        var inp = List.of( new Boolean[]{input.isKeyDown(Input.KEY_W), input.isKeyDown(Input.KEY_A), input.isKeyDown(Input.KEY_S), input.isKeyDown(Input.KEY_D)});
        var d = lib.wasd_to_dir(inp);
        if (d != null){
//            System.out.println(d);
            var UP_V = new Vector(0.2f,0).unit().scale(.2f);
            var LEFT_V = new Vector(0,-.2f).unit().scale(.2f);
            // todo fix long ass switch
            egc.character.curdir  =  d;
            switch (d){
                case NORTH:
                    v = UP_V;
                    break;
                case SOUTH:
                    v = UP_V.scale(-1);
                    break;
                case WEST:
                    v = LEFT_V;
                    break;
                case EAST:
                    v = LEFT_V.scale(-1);
                    break;
                case NORTHEAST:
                    v = UP_V.add(LEFT_V.scale(-1));
                    break;
                case NORTHWEST:
                    v = UP_V.add(LEFT_V);
                    break;
                case SOUTHEAST:
                    v = UP_V.scale(-1).add(LEFT_V.scale(-1));
                    break;
                case SOUTHWEST:
                    v = UP_V.scale(-1).add(LEFT_V);
                    break;
                default:
                    break;
            }

        }
        egc.character.setVelocity(v);

        if (input.isKeyPressed(Input.KEY_F)){ //Use the f key to fire a projectile.
            if (egc.is_connected){
                try {
                    egc.out_stream.writeObject(new Message(Message.MSG_TYPE.FIRE_PROJECTILE, null, egc.ID));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                egc.projectiles.add(new Projectile(egc.character.gamepos.getX(), egc.character.gamepos.getY(), 0.1f, 0.1f)); //Set the initial location to the player.
            }
        }

        //Kevin, attack when left mouse or I is pressed, (my mouse isnt recognized so i needed the i key lol)
        if(input.isKeyPressed(Input.MOUSE_LEFT_BUTTON) || input.isKeyPressed(Input.KEY_I)){
            var m = new Vector(input.getMouseX(), input.getMouseY());
            //Kevin, m is mouse cords on screen, character is always in the sceen center,
            //angleto gives the angle in degrees rotated by 180 for some reason,
            //divide by 45 to convert into 8 directions, then round to get the angle index,
            //0 and 8 map to the same value
            var diridx = (int)Math.round((m.angleTo(egc.screen_center)+180)/45);
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

        if (egc.is_connected){
            if (!egc.character.getVelocity().equals(v)){
                try {
                    egc.out_stream.writeObject(new Message(Message.MSG_TYPE.SET_VELOCITY, v, egc.ID));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                egc.character.setVelocity(v);
            }
            for(var m = egc.in_messages.poll(); m != null; m = egc.in_messages.poll()){
                egc.handle_message(m);
            }

        } else {
            //(Kevin) handle stuff when client isnt connected
            egc.character.setVelocity(v);
            egc.character.update(delta); //Update the position of the player


            //(Kevin) update all other entities
            egc.projectiles.stream().forEach(p -> p.update(delta));
            egc.enemies.stream().forEach(e -> e.update(delta));

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

//        Kevin, commented out until its used for something
//        for(Enemy e : egc.enemies){
//            if(egc.character.collides(e)!= null){
//                System.out.println("character collided with an enemy");
//            }
//        }

        //Kevin, check collision with the 8 neighbor tiles of the character and undo their movement if there is a collision
        egc.grid.getNeighbors(egc.character.gamepos).stream()
                .filter(t -> t.type == TileMap.TYPE.WALL)
                .map(egc.character::collides) // stream of collisions that may be null
                .filter(Objects::nonNull)
                .findAny().ifPresent(c -> { // the actual collision object isnt useful, the minpentration doesnt work at all
                    egc.character.setVelocity(egc.character.getVelocity().scale(-1));
                    egc.character.update(delta);
        });

//        System.out.println(egc.character.gamepos);
//         Tile currentTile = egc.grid.getTile(egc.character.gamepos); //Get the current tile type.
//         if (currentTile.type == TileMap.TYPE.WALL && egc.character.collides(currentTile) != null) { //If the current tile is a wall and the player collides with it.
//            Vector reverseVector = lastVector.negate(); //Get the negation of the last vector.
//            egc.character.setVelocity(reverseVector); //Set the new velocity.
//            egc.character.update(lastDelta); //Update the last delta.
//         }

        lastDelta = delta;
        lastVector = v;


    }

	@Override
	public int getID() {
		return ExplorerGameClient.PLAYINGSTATE;
	}

}
