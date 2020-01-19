package timboe.vev.manager;

import com.badlogic.gdx.Gdx;

import java.util.Iterator;

import timboe.vev.Param;
import timboe.vev.VEVGame;
import timboe.vev.entity.Warp;
import timboe.vev.enums.FSM;
import timboe.vev.screen.GameScreen;
import timboe.vev.screen.TitleScreen;

public class StateManager {
  private static StateManager ourInstance;
  public static StateManager getInstance() {
    return ourInstance;
  }
  public static void create() { ourInstance = new StateManager(); }

  private GameScreen theGameScreen;
  public TitleScreen theTitleScreen;
  private VEVGame game;

  public FSM fsm;

  private StateManager() {
    reset();
  }

  private void reset() {
    fsm = FSM.kNO_STATE;
  }

  public void init(VEVGame vev) {
    this.theTitleScreen = new TitleScreen();
    this.theGameScreen = new GameScreen();
    this.game = vev;
    setToTitleScreen();
  }

  public void transitionToGameScreen() {
    if (fsm != FSM.kINTRO) {
      Gdx.app.error("transitionToGameScreen","Unexpected called from "+fsm);
    }
    fsm = FSM.kTRANSITION_TO_GAME;
    Gdx.input.setInputProcessor(null);
    theTitleScreen.transitionOutTimers[0] = 1f;
    Sounds.getInstance().pulse();
  }

  public void transitionToTitleScreen() {
    if (fsm != FSM.kGAME && fsm != FSM.kGAME_OVER) {
      Gdx.app.error("transitionToTitleScreen","Unexpected called from "+fsm);
    }
    fsm = (fsm == FSM.kGAME ? FSM.kTRANSITION_TO_INTRO_SAVE : FSM.kTRANSITION_TO_INTRO_NOSAVE);
    Gdx.app.log("transitionToTitleScreen","State now "+fsm);
    Gdx.input.setInputProcessor(null);
    theGameScreen.transitionOutTimers[0] = 1f;
    Sounds.getInstance().pulse();
  }

  public void setToTitleScreen() {
    if (fsm != FSM.kTRANSITION_TO_INTRO_SAVE && fsm != FSM.kTRANSITION_TO_INTRO_NOSAVE && fsm != FSM.kNO_STATE) {
      Gdx.app.error("setToTitleScreen","Unexpected called from "+fsm);
    }
    final boolean doSave = (fsm == FSM.kTRANSITION_TO_INTRO_SAVE);
    fsm = FSM.kFADE_TO_INTRO;
    game.setScreen(theTitleScreen);
    theTitleScreen.fadeIn = 100f;
    Gdx.input.setInputProcessor(null);
    GameState.getInstance().clearPathingCache();
    if (doSave) {
      Persistence.getInstance().trySaveGame();
      World.getInstance().reset(false);
      Persistence.getInstance().flushSaveGame();
    } else {
      World.getInstance().reset(false);
      Persistence.getInstance().deleteSave();
    }
    UIIntro.getInstance().resetTitle("main");
  }

  public void titleScreenFadeComplete() {
    if (fsm != FSM.kFADE_TO_INTRO) {
      Gdx.app.error("titleScreenFadeComplete","Unexpected called from "+fsm);
    }
    fsm = FSM.kINTRO;
    theTitleScreen.doInputHandles();
  }

  public void setToGameScreen() {
    if (fsm != FSM.kTRANSITION_TO_GAME) {
      Gdx.app.error("setToGameScreen","Unexpected called from "+fsm);
    }
    fsm = FSM.kFADE_TO_GAME;
    game.setScreen(theGameScreen);
    theGameScreen.setMultiplexerInputs();
    theGameScreen.fadeIn = 100f;
    Gdx.input.setInputProcessor(null);
    GameState.getInstance().clearPathingCache();
    UI.getInstance().resetGame();
    Iterator it = GameState.getInstance().getWarpMap().values().iterator();
    Warp toFocusOn = (Warp)it.next();
    Camera.getInstance().setCurrentPos(
            toFocusOn.getX() + (Param.WARP_SIZE/2f * Param.TILE_S),
            toFocusOn.getY() + (Param.WARP_SIZE/2f * Param.TILE_S));
  }

  public void gameScreenFadeComplete() {
    if (fsm != FSM.kFADE_TO_GAME) {
      Gdx.app.error("gameScreenFadeComplete","Unexpected called from "+fsm);
    }
    fsm = FSM.kGAME;
    theGameScreen.doInputHandles();
    GameState.getInstance().doRightClick();
  }

  public void gameOver() {
    if (fsm != FSM.kGAME) {
      Gdx.app.error("gameOver","Unexpected called from "+fsm);
    }
    fsm = FSM.kGAME_OVER;
    UI.getInstance().showFin();
  }

  public void dispose() {
    theTitleScreen.dispose();
    theGameScreen.dispose();
    ourInstance = null;
  }



}
