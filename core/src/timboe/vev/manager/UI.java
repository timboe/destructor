package timboe.vev.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.text.DecimalFormat;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import timboe.vev.DistanceField.LabelDF;
import timboe.vev.DistanceField.TextButtonDF;
import timboe.vev.DistanceField.TextTooltipDF;
import timboe.vev.Pair;
import timboe.vev.Param;
import timboe.vev.entity.Building;
import timboe.vev.entity.Sprite;
import timboe.vev.entity.Warp;
import timboe.vev.enums.BuildingType;
import timboe.vev.enums.Particle;
import timboe.vev.enums.QueueType;
import timboe.vev.enums.UIMode;
import timboe.vev.input.BuildingButton;
import timboe.vev.input.ParticleSelectButton;
import timboe.vev.input.QueueButton;
import timboe.vev.input.QueueLengthSlider;
import timboe.vev.input.StandingOrderButton;
import timboe.vev.input.UpgradeBuildingButton;
import timboe.vev.input.YesNoButton;

public class UI {

  private static UI ourInstance = null;
  public static UI getInstance() {
    return ourInstance;
  }
  public static void create() { ourInstance = new UI(); }
  public static boolean constructed() { return ourInstance != null; }
  public void dispose() { ourInstance = null; }
  private static DecimalFormat df2 = new DecimalFormat(".##");


  public UIMode uiMode = UIMode.kNONE;

  private final int SIZE_S = 32;
  private final int SIZE_M = 2*SIZE_S;
  private final int SIZE_L = 2*SIZE_M;

  private final int PAD = 4;

  private Table tableIntro;
  private Table table;
  private Skin skin;
  public ShaderProgram dfShader;
  public ShaderProgram dfShader_medium;
  private ShaderProgram dfShader_large;

  private Table mainWindow;
  private Table selectWindow;
  private Table titleWindow;

  private float displayPlayerEnergy;
  private float displayWarpEnergy;
  private float time;
  public final DecimalFormat formatter = new DecimalFormat("###,###");
  LabelDF displayPlayerEnergyLabel;
  LabelDF displayWarpEnergyLabel;

  private final YesNoButton yesNoButton = new YesNoButton();
  private final StandingOrderButton standingOrderButton = new StandingOrderButton();
  private final UpgradeBuildingButton upgradeBuildingButton = new UpgradeBuildingButton();

  public Button selectParticlesButton;
  public Button showPathsButton;

  private EnumMap<BuildingType, Button> buildBuildingButtons = null;
  private EnumMap<BuildingType, TextTooltipDF> buildBuildingTooltips = null;

  private EnumMap<BuildingType, Table> buildingWindow;
  public EnumMap<BuildingType, Button> buildingWindowQSimple;
  public EnumMap<BuildingType, Button> buildingWindowQSpiral;
  public EnumMap<BuildingType, LabelDF> buildingWindowQSize;
  public EnumMap<BuildingType, LabelDF> buildingWindowQPrice;
  public EnumMap<BuildingType, Slider> buildingWindowQSlider;
  public EnumMap<BuildingType, LabelDF> buildingWindowTimeLabelA;
  public EnumMap<BuildingType, LabelDF> buildingWindowTimeLabelB;
  public EnumMap<BuildingType, LabelDF> buildingWindowTimeLabelC;
  public EnumMap<BuildingType, Button> buildingWindowUpgradeButton;

  private EnumMap<BuildingType, Table> buildingSelectWindow;
  public EnumMap<BuildingType, ProgressBar> buildingSelectProgress;
  public EnumMap<BuildingType, EnumMap<Particle, Button>> buildingSelectStandingOrder;
  public EnumMap<BuildingType, LabelDF> buildingUpgradeLabelCost;
  public EnumMap<BuildingType, LabelDF> buildingUpgradeLabelTime;
  public EnumMap<BuildingType, LabelDF> buildingUpgradeLabelBonus;

  public EnumMap<Particle, Button> selectButton;
  private EnumMap<Particle, Label> selectLabel;
  private Button selectCross;

  private Set<Image> ballImages = new HashSet<Image>();

  // Settings cache
  float cacheMusic, cacheSfx;
  EnumMap<Particle, Integer> cacheHue = new EnumMap<Particle, Integer>(Particle.class);

  float perSec = 0;

  private UI() {
    ShaderProgram.pedantic = false;

    dfShader_large = new ShaderProgram(Gdx.files.internal("font.vert"), Gdx.files.internal("font_large.frag"));
    if (!dfShader_large.isCompiled()) Gdx.app.error("dfShader_large", "compilation failed:\n" + dfShader_large.getLog());

    dfShader_medium = new ShaderProgram(Gdx.files.internal("font.vert"), Gdx.files.internal("font_medium.frag"));
    if (!dfShader_medium.isCompiled()) Gdx.app.error("dfShader_medium", "compilation failed:\n" + dfShader_medium.getLog());

    dfShader = new ShaderProgram(Gdx.files.internal("font.vert"), Gdx.files.internal("font.frag"));
    if (!dfShader.isCompiled()) Gdx.app.error("fontShader", "compilation failed:\n" + dfShader.getLog());

    skin = new Skin(Gdx.files.internal("uiskin.json"));
    skin.addRegions(Textures.getInstance().getUIAtlas());
  }

  private void separator(Table w, int colspan) {
    w.add(new Image( Textures.getInstance().getTexture("separator", false) )).fillX().height(4).pad(6,2,6,2).colspan(colspan);
    w.row();
  }

  private LabelDF getLabel(String label) {
    LabelDF l = new LabelDF(label, skin, "default", dfShader);
    l.setAlignment(Align.center);
    l.setWidth(SIZE_S);
    l.setHeight(SIZE_S);
    return l;
  }

  private Button getTextButton(String label) {
    TextButtonDF b = new TextButtonDF(label, skin, "default");
    b.getLabelCell().pad(10,30,10,30);
    return b;
  }

  private Image getImage(String image) {
    Image i = new Image( Textures.getInstance().getTexture(image, false) );
    i.setUserObject(image);
    i.setAlign(Align.center);
    return i;
  }

  public void retextureSprites() {
    for (Image i : ballImages) {
      String tex = (String)i.getUserObject();
      i.setDrawable( new TextureRegionDrawable( Textures.getInstance().getTexture(tex, false) ) );
    }
  }

  private Table getWindow() {
    Table t = new Table();
    t.setBackground(new NinePatchDrawable(new NinePatch(skin.getRegion("window"), 10, 10, 10, 10)));
    return t;
  }

  private Button getImageButton(String image, String style, int resize) {
    Button ib = new Button(skin, style);
    if (image.equals("tick") || image.equals("cross")) {
      ib.setUserObject( image.equals("cross") ? 0 : 1);
      ib.addListener(yesNoButton);
      resize = SIZE_L;
    }

    Image im = new Image( Textures.getInstance().getTexture(image, false) );
    Container<Actor> cont = new Container<Actor>();
    cont.setActor(im);
    if (resize != 0) cont.width( resize ).height( resize );

    ib.setProgrammaticChangeEvents(false);
    ib.add(cont);
    ib.align(Align.center);
    return ib;
  }

  private Button getImageButton(String image) {
    return getImageButton(image, "default", 0);
  }

  private void addToWin(Table w, Actor a, int size) {
    w.add(a).width(size).height(size).pad(PAD);
  }

  private void addToWin(Table w, Actor a, int sizeX, int sizeY, int colspan) {
    w.add(a).colspan(colspan).width(sizeX).height(sizeY).pad(PAD);
  }

  private Button getAndAddStandingOrderButton(BuildingType bt, Particle p) {
    String path = (p == Particle.kBlank ? "queue_full" : "ball_" + p.getColourFromParticle().getString());
    Button b = getImageButton(path, "toggle", SIZE_M);
    b.setUserObject(new Pair<BuildingType, Particle>(bt, p));
    b.addListener(standingOrderButton);
    buildingSelectStandingOrder.get(bt).put(p, b);
    Image arrow = getImage("arrow");
    Container<Actor> ac = new Container<Actor>(arrow);
    ac.width(SIZE_M).height(SIZE_M);
    b.add(ac).padLeft(PAD*2);
    if (p != Particle.kBlank) {
      b.row();
      b.add(getLabel(p.getString())).colspan(2);
    }
    return b;
  }

  private void addBuildingBlurb(Table bw, BuildingType bt) {
    if (bt == BuildingType.kMINE || bt == BuildingType.kWARP) return;
    for (int mode = 0; mode < BuildingType.N_MODES; ++mode) {
      Particle from = bt.getInput(mode);
      Pair<Particle, Particle> to = bt.getOutputs(mode);
      addToWin(bw, getImage("ball_" + from.getColourFromParticle().getString()), SIZE_S);
      addToWin(bw, getImage("arrow"), SIZE_S);
      addToWin(bw, getImage("zap"), SIZE_S);
      if (to.getKey() != null) {
        addToWin(bw, getImage("ball_" + to.getKey().getColourFromParticle().getString()), SIZE_S);
      } else {
        addToWin(bw, new Container<Actor>(), SIZE_S);
      }
      if (to.getValue() != null) {
        addToWin(bw, getImage("ball_" + to.getValue().getColourFromParticle().getString()), SIZE_S);
      } else {
        addToWin(bw, new Container<Actor>(), SIZE_S);
      }
      LabelDF disassembleTimeLabel = getLabel("");
      addToWin(bw, disassembleTimeLabel, SIZE_S);
      switch(mode) {
        case 0: buildingWindowTimeLabelA.put(bt, disassembleTimeLabel); break;
        case 1: buildingWindowTimeLabelB.put(bt, disassembleTimeLabel); break;
        case 2: buildingWindowTimeLabelC.put(bt, disassembleTimeLabel); break;
      }
      bw.row();
      addToWin(bw, getLabel(from.getString()), SIZE_S);
      addToWin(bw, new Container<Actor>(), SIZE_S);
      addToWin(bw, getLabel(formatter.format(bt.getOutputEnergy(mode))), SIZE_S);
      if (to.getKey() != null) {
        addToWin(bw, getLabel(to.getKey().getString()), SIZE_S);
      } else {
        addToWin(bw, new Container<Actor>(), SIZE_S);
      }
      if (to.getValue() != null) {
        addToWin(bw, getLabel(to.getValue().getString()), SIZE_S);
      } else {
        addToWin(bw, new Container<Actor>(), SIZE_S);
      }
      addToWin(bw, new Container<Actor>(), SIZE_S);
      bw.row();
    }
  }

  public void cancelSettingsChanges() {
    Gdx.app.log("cancelSettingsChanges","Cancelling");
    for (Particle p : Particle.values()) {
      if (p == Particle.kBlank) continue;
      Param.PARTICLE_HUE.put(p, cacheHue.get(p));
    }
    Textures.getInstance().updateParticleHues();
    Param.MUSIC_LEVEL = cacheMusic;
    Param.SFX_LEVEL = cacheSfx;
  }

  public void updateButtonPriceStatus() {
    if (buildBuildingButtons == null) {
      return;
    }
    for (BuildingType bt : BuildingType.values()) {
      if (bt == BuildingType.kWARP) continue;
      final int price = GameState.getInstance().buildingPrices.get(bt);
      buildBuildingTooltips.get(bt).getActor().setText("Price:"+formatter.format( GameState.getInstance().buildingPrices.get(bt) ));
      final boolean disabled = price > GameState.getInstance().playerEnergy;
      buildBuildingButtons.get(bt).setDisabled(disabled);
      for (Actor a : buildBuildingButtons.get(bt).getChildren()) {
        a.setVisible(!disabled);
      }
    }
  }

  protected void resetTitle(String toShow) {
    tableIntro = new Table();
    tableIntro.setFillParent(true);
    tableIntro.debugAll();
    tableIntro.left();
    tableIntro.row().fillX();
    tableIntro.pad(Param.TILE_S * 2);

    GameState.getInstance().getUIStage().clear();
    GameState.getInstance().getUIStage().addActor(tableIntro);


    LabelDF vev = new LabelDF("VEV", skin, "title", dfShader_large);
    vev.setFontScale(25f);
    tableIntro.add(vev).padLeft(45);

    tableIntro.add(new Actor()).expand();

    titleWindow = getWindow();

    if (toShow.equals("main")) {

      uiMode = UIMode.kNONE;

      Button newGame = getTextButton("NEW GAME");
      newGame.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          if (!World.getInstance().getGenerated()) World.getInstance().launchAfterGen = true;
          else if (GameState.getInstance().theTitleScreen.fadeTimer == 0)
            GameState.getInstance().transitionToGameScreen();
        }
      });
      newGame.addListener(new TextTooltipDF("You can press this", skin));
      titleWindow.add(newGame).pad(SIZE_S).colspan(2).fillX();

      titleWindow.row();
      separator(titleWindow, 2);
      Button loadGame = getTextButton("LOAD GAME");
      loadGame.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          World.getInstance().doLoad = true;
        }
      });
      if (World.getInstance().loadedSave == null) {
        loadGame.setDisabled(true);
      }
      titleWindow.add(loadGame).pad(SIZE_S).colspan(2).fillX();

      titleWindow.row();
      separator(titleWindow, 2);
      Button settingsButton = getTextButton("SETTINGS");
      settingsButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          resetTitle("settings");
        }
      });
      titleWindow.add(settingsButton).pad(SIZE_S).colspan(2).fillX();

      titleWindow.row();
      separator(titleWindow, 2);
      Button howToPlayButton = getTextButton("HOW TO PLAY");
      howToPlayButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
        }
      });
      titleWindow.add(howToPlayButton).pad(SIZE_S).colspan(2).fillX();;

      titleWindow.row();
      separator(titleWindow, 2);
      Button exitGame = getTextButton("EXIT");
      exitGame.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          Gdx.app.exit();
        }
      });
      titleWindow.add(exitGame).pad(SIZE_S).colspan(2).fillX();

    } else if (toShow.equals("settings")) {

      uiMode = UIMode.kSETTINGS;

      LabelDF musicL = getLabel("M");
      titleWindow.add(musicL).pad(SIZE_S).fillX();
      Slider musicSlider = new Slider(0, 1, .01f, false, skin, "default-horizontal");
      musicSlider.setValue(Param.MUSIC_LEVEL);
      musicSlider.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          Param.MUSIC_LEVEL = ((Slider)actor).getValue();
          Sounds.getInstance().musicVolume();
        }
      });
      titleWindow.add(musicSlider).pad(SIZE_S).width(SIZE_S+SIZE_M+SIZE_L).fillX();

      titleWindow.row();
      LabelDF sfxL = getLabel("S");
      titleWindow.add(sfxL).pad(SIZE_S).fillX();
      Slider sfxSlider = new Slider(0, 1, .01f, false, skin, "default-horizontal");
      sfxSlider.setValue(Param.SFX_LEVEL);
      sfxSlider.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          Param.SFX_LEVEL = ((Slider)actor).getValue();
        }
      });
      addToWin(titleWindow, sfxSlider, SIZE_S+SIZE_M+SIZE_L, SIZE_S, 1);

      titleWindow.row();
      separator(titleWindow, 2);

      ballImages.clear();
      for (Particle p : Particle.values()) {
        if (p == Particle.kBlank) continue;
        Image i = getImage("ball_" + p.getColourFromParticle().getString());
        ballImages.add(i);
        addToWin(titleWindow, i, SIZE_M);
        Slider pSlider = new Slider(0, 360*3, 1, false, skin, "default-horizontal");
        pSlider.setValue( Param.PARTICLE_HUE.get(p) );
        pSlider.setUserObject(p);
        pSlider.addListener(new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            Param.PARTICLE_HUE.put( (Particle)actor.getUserObject(), (int) ((Slider)actor).getValue() );
            Textures.getInstance().updateParticleHues();
          }
        });
        addToWin(titleWindow, pSlider, SIZE_M+SIZE_L, SIZE_M, 1);
        titleWindow.row();
        cacheHue.put(p, Param.PARTICLE_HUE.get(p));
      }

      cacheMusic = Param.MUSIC_LEVEL;
      cacheSfx = Param.SFX_LEVEL;

      separator(titleWindow, 2);

      addToWin(titleWindow, getImageButton("tick"), SIZE_L, SIZE_L, 1);
      addToWin(titleWindow, getImageButton("cross"), SIZE_L, SIZE_L, 1);


    } else {
      Gdx.app.error("Intro UI","Unknown mode "+toShow);
      Gdx.app.exit();
    }


    tableIntro.add(titleWindow).right().top();

    LabelDF cred = getLabel("A game by Tim Martin");
    LabelDF music1 = getLabel("Music by Chris Zabriskie (CC v4)");
    LabelDF music2 = getLabel("    Is That You Or Are You You? / Divider / CGI Snake");
    LabelDF art = getLabel("Open Game Art by Buch");

    tableIntro.row();
    tableIntro.bottom().left();
    tableIntro.add(cred).left().colspan(3);
    tableIntro.row();
    tableIntro.add(music1).left().colspan(3);
    tableIntro.row();
    tableIntro.add(music2).left().colspan(3);
    tableIntro.row();
    tableIntro.add(art).left().colspan(3);

    Gdx.app.log("resetTitle", "made intro UI");
  }

  protected void resetGame() {
    Gdx.app.log("resetGame", "made game UI");

    table = new Table();
    table.setFillParent(true);
    table.row().fillY();
    table.top();
    table.right();
    table.pad(Param.TILE_S);

    GameState.getInstance().getUIStage().clear();
    GameState.getInstance().getUIStage().addActor(table);

    BuildingButton buildingButtonHandler = new BuildingButton();
    QueueLengthSlider queueLengthSlider = new QueueLengthSlider();
    QueueButton queueButton = new QueueButton();
    ParticleSelectButton particleSelectButton = new ParticleSelectButton();

    mainWindow = getWindow();
    selectWindow = getWindow();


    // Main window
    buildBuildingButtons = new EnumMap<BuildingType, Button>(BuildingType.class);
    buildBuildingTooltips = new EnumMap<BuildingType, TextTooltipDF>(BuildingType.class);
    addToWin(mainWindow, getImage("zap"), SIZE_S, SIZE_S, 1);
    displayPlayerEnergyLabel = getLabel("");
    addToWin(mainWindow, displayPlayerEnergyLabel, SIZE_L, SIZE_S, 1);
    mainWindow.row();
    separator(mainWindow, 2);
    for (BuildingType bt : BuildingType.values()) {
      if (bt == BuildingType.kWARP) continue;
//      if (bt == BuildingType.kMINE) separator(mainWindow, 2);

      Image ib = new Image( Textures.getInstance().getTexture("building_" + bt.ordinal(), false) );
      Container<Actor> cont = new Container<Actor>();
      cont.setActor(ib);
      cont.width( ib.getWidth() * 2 ).height( ib.getHeight() * 2 );

      Button b = new Button(skin, "default");
      b.add(cont);

      if (bt != BuildingType.kMINE) {
        Table vert = new Table();
        for (int mode = 0; mode < BuildingType.N_MODES; ++mode) {
          Image ip = new Image(Textures.getInstance().getTexture("ball_" + bt.getInput(mode).getColourFromParticle().getString(), false));
          Container<Actor> contIp = new Container<Actor>();
          contIp.setActor(ip);
          contIp.width(ip.getWidth() * 2).height(ip.getHeight() * 2);
          vert.add(contIp);
          vert.row();
        }
        b.add(vert).padLeft(SIZE_S / 2);
      }

      b.setUserObject(bt);
      b.addListener(buildingButtonHandler);
      TextTooltipDF ttt = new TextTooltipDF("L", skin);
      ttt.setInstant(true);
      b.addListener( ttt );
      buildBuildingButtons.put(bt, b);
      buildBuildingTooltips.put(bt, ttt);
      addToWin(mainWindow, b, SIZE_L+SIZE_M, SIZE_L, 2);
      mainWindow.row();
    }
    separator(mainWindow, 2);
    selectParticlesButton = getImageButton("select", "toggle", SIZE_M);
    selectParticlesButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        if (((Button)actor).isChecked()) GameState.getInstance().startSelectingAndroid();
      }
    });
    addToWin(mainWindow, selectParticlesButton, SIZE_L, SIZE_L, 1);
    showPathsButton = getImageButton("show_arrows", "toggle", SIZE_M);
    addToWin(mainWindow, showPathsButton, SIZE_L, SIZE_L, 1);
    mainWindow.row();
    Button settings = getImageButton("settings", "default", SIZE_M);
    addToWin(mainWindow, settings, SIZE_L, SIZE_L, 2);

    // Selected window
    selectButton = new EnumMap<Particle, Button>(Particle.class);
    selectLabel = new EnumMap<Particle, Label>(Particle.class);
    selectCross = getImageButton("cross");
    for (Particle p : Particle.values()) {
      if (p == Particle.kBlank) continue;
      Button b = getImageButton("ball_" + p.getColourFromParticle().getString(), "default", SIZE_M);
      b.setUserObject(p);
      b.addListener(particleSelectButton);
      Label lab = getLabel("");
      b.row();
      b.add(lab);
      selectButton.put(p, b); // Added to window dynamically
      selectLabel.put(p, lab);
    }

    // Building build windows
    buildingWindow = new EnumMap<BuildingType, Table>(BuildingType.class);
    buildingWindowQSimple = new EnumMap<BuildingType, Button>(BuildingType.class);
    buildingWindowQSpiral = new EnumMap<BuildingType, Button>(BuildingType.class);
    buildingWindowQSize = new EnumMap<BuildingType, LabelDF>(BuildingType.class);
    buildingWindowQPrice = new EnumMap<BuildingType, LabelDF>(BuildingType.class);
    buildingWindowQSlider = new EnumMap<BuildingType, Slider>(BuildingType.class);
    buildingWindowTimeLabelA = new EnumMap<BuildingType, LabelDF>(BuildingType.class);
    buildingWindowTimeLabelB = new EnumMap<BuildingType, LabelDF>(BuildingType.class);
    buildingWindowTimeLabelC = new EnumMap<BuildingType, LabelDF>(BuildingType.class);
    buildingWindowUpgradeButton = new EnumMap<BuildingType, Button>(BuildingType.class);
    for (final BuildingType bt : BuildingType.values()) {
      Table bw = getWindow();
      buildingWindow.put(bt, bw);
      addToWin(bw, getImage("zap"), SIZE_S, SIZE_S, bt != BuildingType.kMINE ? 3 : 1); // Player Energy
      addToWin(bw, displayPlayerEnergyLabel, SIZE_L, SIZE_S,  bt != BuildingType.kMINE ? 3 : 1); // Player energy
      bw.row(); // Player energy
      separator(bw,  bt != BuildingType.kMINE ? 6 : 2); // Player energy
      addBuildingBlurb(bw, bt);
      int colspan = 2;
      /////////////////////
      if (bt != BuildingType.kMINE) {
        colspan = 6;
        separator(bw, colspan);
        Button qSimple = getImageButton("queue_simple", "toggle", 0);
        buildingWindowQSimple.put(bt, qSimple);
        qSimple.setUserObject(QueueType.kSIMPLE);
        addToWin(bw, qSimple, SIZE_L, SIZE_L, 3);
        Button qSpiral = getImageButton("queue_spiral", "toggle", 0);
        buildingWindowQSpiral.put(bt, qSpiral);
        qSpiral.setUserObject(QueueType.kSPIRAL);
        addToWin(bw, qSpiral, SIZE_L, SIZE_L, 3);
        qSimple.addListener(queueButton);
        qSpiral.addListener(queueButton);
        bw.row();
        ///////////////////////
        Slider slider = new Slider(1, 99, 1, false, skin, "default-horizontal");
        slider.addListener(queueLengthSlider);
        buildingWindowQSlider.put(bt, slider);
        addToWin(bw, getImage("queue_g_E_N"), SIZE_S, SIZE_S, 1);
        bw.add(slider).height(SIZE_M).width(SIZE_L + SIZE_M + SIZE_S).colspan(4);
        LabelDF sliderLabel = getLabel("");
        buildingWindowQSize.put(bt, sliderLabel);
        addToWin(bw, sliderLabel, SIZE_S);
        bw.row();
      }
      addToWin(bw, getImage("zap"), SIZE_S, SIZE_S, 1);
      LabelDF buildingCost = getLabel( formatter.format(GameState.getInstance().buildingPrices.get(bt)) );
      buildingWindowQPrice.put(bt, buildingCost);
      addToWin(bw, buildingCost, SIZE_L, SIZE_S, colspan-1);
      bw.row();
      separator(bw, colspan);
      ///////////////////////
      addToWin(bw, getImageButton("tick"), SIZE_L, SIZE_L, colspan/2);
      addToWin(bw, getImageButton("cross"), SIZE_L, SIZE_L, colspan/2);
    }

    // Building info windows
    buildingSelectWindow = new EnumMap<BuildingType, Table>(BuildingType.class);
    buildingSelectProgress = new EnumMap<BuildingType, ProgressBar>(BuildingType.class);
    buildingSelectStandingOrder = new EnumMap<BuildingType, EnumMap<Particle, Button>>(BuildingType.class);
    buildingUpgradeLabelCost = new EnumMap<BuildingType, LabelDF>(BuildingType.class);
    buildingUpgradeLabelTime = new EnumMap<BuildingType, LabelDF>(BuildingType.class);
    buildingUpgradeLabelBonus = new EnumMap<BuildingType, LabelDF>(BuildingType.class);
    for (final BuildingType bt : BuildingType.values()) {
      buildingSelectStandingOrder.put(bt, new EnumMap<Particle, Button>(Particle.class));
      Table bw = getWindow();
      buildingSelectWindow.put(bt, bw);
      //////////////////////
      addToWin(bw, getImage("zap"), SIZE_S, SIZE_S, 3); // Player Energy
      addToWin(bw, displayPlayerEnergyLabel, SIZE_L, SIZE_S, 3); // Player energy
      bw.row(); // Player energy
      separator(bw, 6); // Player energy
      //////////////////////
      if (bt == BuildingType.kWARP) {
        addToWin(bw, getImage("zap"), SIZE_S, SIZE_S, 1);
        displayWarpEnergyLabel = getLabel("");
        addToWin(bw, displayWarpEnergyLabel, SIZE_L+SIZE_M-SIZE_S, SIZE_S, 5);
        bw.row();
        for (Particle p : Particle.values()) {
          if (p == Particle.kBlank) continue;
          Button b = getAndAddStandingOrderButton(bt, p);
          addToWin(bw, b, SIZE_L+SIZE_M, SIZE_L, 6);
          bw.row();
        }
      } else {
        if (bt != BuildingType.kMINE) {
          addBuildingBlurb(bw, bt);
          separator(bw, 6);
          for (int i = 0; i < BuildingType.N_MODES; ++i) {
            Particle p = bt.getOutputs(i).getKey(); // Key and value are always the same
            if (p == null) continue;
            Button b = getAndAddStandingOrderButton(bt, p);
            addToWin(bw, b, SIZE_L + SIZE_M, SIZE_L, 6);
            bw.row();
          }
          Button bBlank = getAndAddStandingOrderButton(bt, Particle.kBlank);
          addToWin(bw, bBlank, SIZE_L + SIZE_M, SIZE_M + SIZE_S, 6);
          bw.row();
          separator(bw, 6);
        }
        //////////////////////
        ProgressBar progressBar = new ProgressBar(0, 1, 0.01f, false, skin, "default-horizontal");
        addToWin(bw, progressBar, SIZE_L+SIZE_L, SIZE_M, 6);
        buildingSelectProgress.put(bt, progressBar);
        bw.row();
        separator(bw, 6);
        //////////////////////////////
        Button ib = getImageButton("clock", "toggle", SIZE_M);
        ib.addListener(upgradeBuildingButton);
        addToWin(ib, getImage("zap"), SIZE_S, SIZE_S, 1);
        LabelDF cost = getLabel("10000");
        buildingUpgradeLabelCost.put(bt, cost);
        ib.add( cost );
        ib.row();
        LabelDF bonus = getLabel("x1.00");
        buildingUpgradeLabelBonus.put(bt, bonus);
        ib.add( bonus );
        LabelDF time = getLabel("10 s");
        buildingUpgradeLabelTime.put(bt, time);
        ib.add( time ).colspan(2);
        addToWin(bw, ib, 2*SIZE_L, SIZE_L, 6);
        buildingWindowUpgradeButton.put(bt, ib);
      }
      bw.row();
//      separator(bw, 6);
      addToWin(bw, getImageButton("tick"), SIZE_L, SIZE_L, 3);
      addToWin(bw, getImageButton("cross"), SIZE_L, SIZE_L, 3);
    }

    updateButtonPriceStatus();
    showMain();

  }

  public void act(float delta) {
    time += delta;

    perSec += delta;
    if (perSec > 1) {
      perSec -= 1f;
      updateButtonPriceStatus();
    }

    if (time < Param.FRAME_TIME) return;
    time -= Param.FRAME_TIME;
    // Check for existance of main UI component
    if (displayPlayerEnergyLabel != null) {
      if (Math.abs(displayPlayerEnergy - GameState.getInstance().playerEnergy) > .5f) {
        displayPlayerEnergy += (GameState.getInstance().playerEnergy - displayPlayerEnergy) * 0.05f;
        displayPlayerEnergyLabel.setText(formatter.format(Math.round(displayPlayerEnergy)));
      }
      if (Math.abs(displayWarpEnergy - GameState.getInstance().warpEnergy) > .5f) {
        displayWarpEnergy += (GameState.getInstance().warpEnergy - displayWarpEnergy) * 0.05f;
        displayWarpEnergyLabel.setText(formatter.format(Math.round(displayWarpEnergy)));
      }
    }
  }

  public void showBuildBuilding(BuildingType bt) {
    table.clear();
    GameState.getInstance().buildingBeingPlaced = bt;
    if (bt != BuildingType.kMINE) {
      buildingWindowQSimple.get(bt).setChecked(GameState.getInstance().queueType == QueueType.kSIMPLE);
      buildingWindowQSpiral.get(bt).setChecked(GameState.getInstance().queueType == QueueType.kSPIRAL);
      buildingWindowQSlider.get(bt).setValue(GameState.getInstance().queueSize);
    }
    if (bt != BuildingType.kWARP) {
      buildingWindowQPrice.get(bt).setText( formatter.format( GameState.getInstance().buildingPrices.get(bt) ) );
    }
    if (bt != BuildingType.kMINE && bt != BuildingType.kWARP) {
      buildingWindowTimeLabelA.get(bt).setText("");
      buildingWindowTimeLabelB.get(bt).setText("");
      buildingWindowTimeLabelC.get(bt).setText("");
    }
    table.add(buildingWindow.get(bt));
    if (GameState.getInstance().debug > 0) table.debugAll();
    uiMode = UIMode.kPLACE_BUILDING;
    GameState.getInstance().doingPlacement = true;
  }

  public void showBuildingInfo(Building b) {
    table.clear();
    GameState.getInstance().selectedBuilding = b.id;
    table.add(buildingSelectWindow.get(b.getType()));
    for (Particle p : Particle.values()) {
      if (!buildingSelectStandingOrder.get(b.getType()).containsKey(p)) continue;
      buildingSelectStandingOrder.get(b.getType()).get(p).setChecked(false);
    }
    if (GameState.getInstance().debug > 0) table.debugAll();
    uiMode = UIMode.kWITH_BUILDING_SELECTION;
    refreshBuildingLabels(b);
  }

  public void refreshBuildingLabels(Building b) {
    if (b.getType() != BuildingType.kMINE && b.getType() != BuildingType.kWARP) {
      buildingWindowTimeLabelA.get(b.getType()).setText(String.valueOf(Math.round(b.getDisassembleTime(0))) + "s");
      buildingWindowTimeLabelB.get(b.getType()).setText(String.valueOf(Math.round(b.getDisassembleTime(1))) + "s");
      buildingWindowTimeLabelC.get(b.getType()).setText(String.valueOf(Math.round(b.getDisassembleTime(2))) + "s");
    }
    if (b.getType() != BuildingType.kWARP) {
      buildingWindowUpgradeButton.get(b.getType()).setChecked( b.doUpgrade );
      float progress = 0;
      if (b.doUpgrade) progress = b.timeUpgrade / b.getUpgradeTime();
      else if (b.spriteProcessing != 0)  progress = b.timeDisassemble / b.getTimeDisassembleMax;
      buildingSelectProgress.get(b.getType()).setValue(progress);
      buildingUpgradeLabelBonus.get(b.getType()).setText( "x" + df2.format(1f/b.getUpgradeFactor()) );
      buildingUpgradeLabelCost.get(b.getType()).setText( String.valueOf(b.getUpgradeCost()) );
      buildingUpgradeLabelTime.get(b.getType()).setText( df2.format(b.getUpgradeTime()) + " s" );
    }
  }

  public void doSelectParticle(final Set<Integer> selected) {
    Set<Particle> selectedParticles = new HashSet<Particle>();
    int counter[] = new int[Particle.values().length];
    for (final int sID : selected) {
      Sprite s = GameState.getInstance().getParticleMap().get(sID);
      selectedParticles.add(s.getParticle());
      ++counter[s.getParticle().ordinal()];
    }
    selectWindow.clear();
    /////////////////////
    addToWin(selectWindow, getImage("zap"), SIZE_S, SIZE_S, 1); // Player Energy
    addToWin(selectWindow, displayPlayerEnergyLabel, SIZE_L, SIZE_S, 1); // Player energy
    selectWindow.row(); // Player energy
    separator(selectWindow, 2); // Player energy
    /////////////////////
    for (Particle p : selectedParticles) {
      selectLabel.get(p).setText(p.getString() + " (" + counter[p.ordinal()] + ")");
      selectButton.get(p).setChecked(false);
      addToWin(selectWindow, selectButton.get(p), SIZE_L+SIZE_M, SIZE_L, 2);
      selectWindow.row();
    }
    addToWin(selectWindow, selectCross, SIZE_L+SIZE_M, SIZE_L, 2);
    table.clear();
    table.add(selectWindow);
    if (GameState.getInstance().debug > 0) table.debugAll();
    uiMode = UIMode.kWITH_PARTICLE_SELECTION;
    Sounds.getInstance().selectOrder();
  }

  public void showMain() {
    Gdx.app.log("showMain", "Show main In Game UI");
    table.clear();
    table.add(mainWindow);
    if (GameState.getInstance().debug > 0) table.debugAll();
    uiMode = UIMode.kNONE;
    GameState.getInstance().doingPlacement = false;
    if (GameState.getInstance().selectedBuilding != 0) {
      GameState.getInstance().getBuildingMap().get( GameState.getInstance().selectedBuilding ).cancelUpdatePathingList();
    }
    GameState.getInstance().selectedBuilding = 0;
    GameState.getInstance().selectedBuildingStandingOrderParticle = null;
    GameState.getInstance().buildingBeingPlaced = null;
  }

}
