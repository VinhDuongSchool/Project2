package bounce.server;

import bounce.common.Message;
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
                egs.out_messages.add(new Message(Message.MSG_TYPE.NEW_POSITION, c.gamepos, c.client_id));
        });
	}

	@Override
	public int getID() {
		return ExplorerGameServer.PLAYINGSTATE;
	}

}
