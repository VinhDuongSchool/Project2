package bounce.client;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

public class WinState extends BasicGameState {



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


    }

    @Override
    public void update(GameContainer container, StateBasedGame game,
                       int delta) throws SlickException {




    }

    @Override
    public int getID() {
        return ExplorerGameClient.WINSTATE;
    }


}
