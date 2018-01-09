package timboe.destructor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import timboe.destructor.enums.QueueType;

public class Param {

  public static final int ZONES_X = 3; // Constant... or not to constant
  public static final int ZONES_Y = 3;
  public static final int ZONES_MAX = Math.max(ZONES_X, ZONES_Y);

  public static final int TILES_X = 128+32;
  public static final int TILES_Y = 128+32;
  public static final int TILES_MIN = Math.min(TILES_X, TILES_Y);
  public static final int TILES_MAX = Math.max(TILES_X, TILES_Y);

  public static final int TILE_S = 16;

  public static final int SPRITE_SCALE = 2;
  public static final float PARTICLE_VELOCITY = 32f*2f;
  public static final float PARTICLE_AT_TARGET = 2f;
  public static final float NEW_PARTICLE_MEAN = 25f;
  public static final float NEW_PARTICLE_WIDTH = 10f;
  public static final int NEW_PARTICLE_MAX = 100;

  public static final int MIN_GREEN_ZONE = 4;
  public static final int MAX_GREEN_ZONE = 6;

  public static final int MIN_GREEN_HILL = 2;
  public static final int MIN_RED_HILL = 2;

  public static final int KRINKLE_OFFSET = (TILES_MIN/ZONES_MAX) / 10; // To leave room for edge-red-green-hill1-hill2 x2
  public static final int KRINKLE_GAP = 3; // Tiles to leave clear between krinkles
  public static final int MAX_KRINKLE = 3; // Must be ODD
  public static final int NEAR_TO_EDGE = (MAX_KRINKLE * 2) + 1; // Min close-able space. If one went +ve and the other -ve,
  public static final int EDGE_ADJUSTMENT = (MAX_KRINKLE/2)+1;

  public static final int N_PATCH_TRIES = 25;
  public static final int WIGGLE = 8; // Random pixel offset for foliage
  public static final float PATCH_DENSITY = 0.5f;

  public static final int FOREST_SIZE = 7;
  public static final int MIN_FORESTS = 2;
  public static final int MAX_FORESTS = 5;

  public static final int TIBERIUM_SIZE = 5;
  public static final int MIN_TIBERIUM_PATCH = 2;
  public static final int MAX_TIBERIUM_PATCH = 3;

  public static final int WARP_SIZE = 10;
  public static final int MIN_WARP = 2;
  public static final int MAX_WARP = 3;
  public static final float WARP_TRANSPARENCY = 0.5f;
  public static final float WARP_ROTATE_SPEED = 3f;

  public static final int N_TIBERIUM = 4; // Number of sprites
  public static final int N_BALLS = 6; // number of sprites
  public static final int MAX_FRAMES = Math.max(N_TIBERIUM, N_BALLS);

  public static final float HILL_IN_HILL_PROB = .2f;
  public static final float STAIRS_PROB = .8f;
  public static final float FOLIAGE_PROB = .008f;
  public static final float TREE_PROB = .3f; // Given foliage, % of it being a tree

  public static final int MIN_STAIRCASES = 1;

  public static final int MIN_DIST = 2; // Minimum number of steps to do for a feature
  public static final int MAX_DIST = 7; // Maximum number of steps to do for a feature

  public static final int N_GRASS_VARIANTS = 13;
  public static final int N_BORDER_VARIANTS = 4;

  public static final int N_BUSH = 4;
  public static final int N_TREE = 4;
  public static final int N_BUILDING = 5;

  public static final float SCROLL_ZOOM = 0.1f;

  public static final float ZOOM_MIN = 0.1f;
  public static final float ZOOM_MAX = 4.0f;

  public static final float DESIRED_FPS = 60; // FPS ANIM_SPEED is tuned for
  public static final float FRAME_TIME = (1f/DESIRED_FPS);
  public static int FRAME = 0;

  public static final float ANIM_TIME = 1/20f; // I.e. 12 frames per second

  public static final Color HIGHLIGHT_GREEN = new Color(0f, 1f, 0f, 1f);
  public static final Color HIGHLIGHT_RED = new Color(1f, 0f, 0f, 1f);
  public static final Color HIGHLIGHT_YELLOW = new Color(1f, 1f, 0f, 1f);

  public static final int DISPLAY_X = Gdx.graphics.getWidth();
  public static final int DISPLAY_Y = Gdx.graphics.getHeight();

  public static final float PLAYER_ENERGY = 1000000;

  public static final Color PARTICLE_H = new Color(60/255f, 52/255f, 123/255f, 1f);
  public static final Color PARTICLE_W = new Color(206 / 255f, 101 / 255f, 80 / 255f, 1f);
  public static final Color PARTICLE_Z = new Color(101 / 255f, 143 / 255f, 135 / 255f, 1f);
  public static final Color PARTICLE_E = new Color(220/255f, 138/255f, 92/255f, 1f);
  public static final Color PARTICLE_M = new Color(147 / 255f, 178 / 255f, 155 / 255f, 1f);
  public static final Color PARTICLE_Q = new Color(64/255f, 141/255f, 174/255f, 1f);

  public static int DEBUG = 0;

  // Mutable

  public static int QUEUE_SIZE = 9;
  public static QueueType QUEUE_TYPE = QueueType.kSIMPLE;

}
