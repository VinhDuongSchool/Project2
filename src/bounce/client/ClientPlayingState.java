package bounce.client;

import bounce.common.Enemy;
import bounce.common.Message;
import bounce.common.Projectile;
import bounce.common.lib;
import jig.Vector;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;


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


        container.setSoundOn(true);

        egc.enemies.add(new Enemy(0,32, 0, 0, egc.game_sprites.getSprite(0, 9))); //Add the enemies
        egc.enemies.add(new Enemy(32*3,32*5, 0, 0, egc.game_sprites.getSprite(0, 9)));
        egc.enemies.add(new Enemy(0,0, 0, 0, egc.game_sprites.getSprite(1, 8)));


	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
        ExplorerGameClient egc = (ExplorerGameClient)game;


        var screen_offset = lib.to_screen(new Vector( -egc.character.gamepos.getX(), -egc.character.gamepos.getY()), egc.screen_center);
        egc.screenox = screen_offset.getX();
        egc.screenoy = screen_offset.getY();

        egc.grid.render(g,screen_offset, egc.character.gamepos);



        // testing stuff
//        g.drawLine(0,0, egc.screenox, egc.screenoy);
//        g.drawRect(egc.screenox, egc.screenoy, 64, 64);
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
	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
        Input input = container.getInput();
        ExplorerGameClient egc = (ExplorerGameClient) game;

        //(Kevin) deal with user input
        // will need to change movement stuff to make it easier to do different sprites for different directions
        Vector v = new Vector(0,0);
        var UP_V = new Vector(0.2f,-0.2f);
        var LEFT_V = new Vector(-0.1f,-0.1f);
        if (input.isKeyDown(Input.KEY_W)){ //Move the player in the direction of the key pressed.
            v = v.add(UP_V);
        }
        if (input.isKeyDown(Input.KEY_A)){
            v = v.add( LEFT_V);
        }
        if (input.isKeyDown(Input.KEY_S)){
            v = v.add( UP_V.scale(-1));
        }
        if (input.isKeyDown(Input.KEY_D)){
            v = v.add( LEFT_V.scale(-1));
        }
        if (input.isKeyPressed(Input.KEY_F)){ //Use the f key to fire a projectile.
            if (egc.is_connected){
                try {
                    var m = Message.add_entity(egc.character.getX(), egc.character.getY(), 0.1f, 0.1f, 0,0, Message.ENTITY_TYPE.PROJECTILE);
                    m.id = egc.ID;
                    egc.out_stream.writeObject(m);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                egc.projectiles.add(new Projectile(egc.character.gamepos.getX(), egc.character.gamepos.getY(), 0.1f, 0.1f)); //Set the initial location to the player.
            }
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
            egc.character.setVelocity(v);
            egc.character.update(delta); //Update the position of the player

            //(Kevin) remove dead/hit/etc stuff
            egc.enemies.removeIf(e -> e.getHealth() <=0);
            egc.projectiles.removeIf(Projectile::getHit);

            //(Kevin) update all other entities
            egc.projectiles.stream().forEach(p -> p.update(delta));

            for (Enemy e : egc.enemies) { //Check if arrow collied with an alive enemy.
                for (Projectile p : egc.projectiles){
                    if (p.collides(e) != null){
                        e.setHealth(e.getHealth() - p.damage);
                        p.setHit(true);
                    }
                }
            }
        }





	}

	@Override
	public int getID() {
		return ExplorerGameClient.PLAYINGSTATE;
	}

}
