package timboe.destructor;

public class Param {

  public static final int ZONES_X = 3; // Constant... or not to constant
  public static final int ZONES_Y = 3;

  public static final int TILES_X = 256;
  public static final int TILES_Y = 256;

  public static final int TILE_S = 16;

  public static final int MIN_GREEN_ZONE = 4;

  public static int MAX_KRINKLE = 3; // Must be ODD
  public static int NEAR_TO_EDGE = MAX_KRINKLE * 2; // Min close-able space. If one went +ve and the other -ve,
  public static int EDGE_ADJUSTMENT = 2;

  public static final int DISPLAY_X = 1920;
  public static final int DISPLAY_Y = 1080;

  public static final int MAX_FRAMES = 1;

  public static final int N_GRASS_VARIANTS = 13;
  public static final int N_BORDER_VARIANTS = 4;

  public static final float SCROLL_ZOOM = 0.1f;

  public static final float ZOOM_MIN = 0.1f;
  public static final float ZOOM_MAX = 4.0f;

  public static final float DESIRED_FPS = 60; // FPS ANIM_SPEED is tuned for
  public static final float FRAME_TIME = (1f/DESIRED_FPS);
}
