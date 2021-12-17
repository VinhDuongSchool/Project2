package bounce.client;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
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

        ExplorerGameClient egc = (ExplorerGameClient)game;
        g.drawString("Your Score: " + egc.character.gold,200,150);
        g.drawString("GAMEOVER\n you won and now you can go spend the gold\n press space",200,200);


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
        return ExplorerGameClient.WINSTATE;
    }


}
