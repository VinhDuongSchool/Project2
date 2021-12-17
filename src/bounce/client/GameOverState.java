package bounce.client;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


/**
 * This state is active when the Game is over. In this state, the ball is
 * neither drawn nor updated; and a gameover banner is displayed. A timer
 * automatically transitions back to the StartUp State.
 *
 * Transitions From PlayingState
 *
 * Transitions To StartUpState
 */
public class GameOverState extends BasicGameState {



	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {

	}


	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {

		ExplorerGameClient bg = (ExplorerGameClient)game;
		g.drawString("GAMEOVER\n you died but can still play again\n press space",200,200);



	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {


		Input input = container.getInput();
		ExplorerGameClient egc = (ExplorerGameClient) game;
		if(input.isKeyPressed(Input.KEY_SPACE)){
			egc.enterState(ExplorerGameClient.CHARACTERSELECTSTATE);
		}




	}

	@Override
	public int getID() {
		return ExplorerGameClient.GAMEOVERSTATE;
	}

}
