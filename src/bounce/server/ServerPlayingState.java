package bounce.server;

import bounce.common.Enemy;
import bounce.common.Message;
import bounce.common.Projectile;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.util.Arrays;


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
public class ServerPlayingState extends BasicGameState {


	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
        ExplorerGameServer egs = (ExplorerGameServer)game;


        egs.out_messages.add(Message.add_entity(0,32, 0, 0, 0, 9, Message.ENTITY_TYPE.ENEMY));
        egs.out_messages.add(Message.add_entity(32*3,32*5, 0, 0, 0, 9,  Message.ENTITY_TYPE.ENEMY));
        egs.out_messages.add(Message.add_entity(0,0, 0, 0, 1, 8, Message.ENTITY_TYPE.ENEMY));

    }
    @Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {

	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {

        ExplorerGameServer egs = (ExplorerGameServer) game;

        //(Kevin) read all the messages
        for(var m = egs.in_messages.poll(); m != null; m = egs.in_messages.poll()){
            egs.handle_message(m);
        }

        //(Kevin) update all the characters
        Arrays.stream(egs.characters).forEach((c) ->{
            var oldpos = c.gamepos;
            c.update(delta);
            if(!oldpos.equals(c.gamepos))
                egs.out_messages.add(new Message( c.gamepos, c.client_id));
        });

        //(Kevin) remove dead/hit/etc stuff
        for (int i = egs.enemies.size()-1; i >= 0; i--){
            if (egs.enemies.get(i).getHealth() <= 0){
                egs.enemies.remove(i);
                egs.out_messages.add(new Message(Message.MSG_TYPE.REMOVE_ENTITY, i, Message.ENTITY_TYPE.ENEMY));
            }
        }
        for (int i = egs.projectiles.size()-1; i >= 0; i--){
            if (egs.projectiles.get(i).getHit()){
                egs.projectiles.remove(i);
                egs.out_messages.add(new Message(Message.MSG_TYPE.REMOVE_ENTITY, i, Message.ENTITY_TYPE.PROJECTILE));
            }
        }


        //(Kevin) update all other entities
        egs.projectiles.forEach(p -> {
            p.update(delta);
            egs.out_messages.add(new Message(p.gamepos, Message.ENTITY_TYPE.PROJECTILE));
        });

        for (Enemy e : egs.enemies) { //Check if arrow collied with an alive enemy.
            for (Projectile p : egs.projectiles){
                if (p.collides(e) != null){
                    e.setHealth(e.getHealth() - p.damage);
                    p.setHit(true);
                }
            }
        }



	}

	@Override
	public int getID() {
		return ExplorerGameServer.PLAYINGSTATE;
	}

}
