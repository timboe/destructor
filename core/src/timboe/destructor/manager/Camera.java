package timboe.destructor.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import timboe.destructor.Param;
import timboe.destructor.Util;

public class Camera {

  private static Camera ourInstance;
  public static Camera getInstance() {
    return ourInstance;
  }
  public static void create() { ourInstance = new Camera(); }
  public void dispose() {  ourInstance = null; }

  private Rectangle cullBox = new Rectangle(0, 0, Param.DISPLAY_X, Param.DISPLAY_Y);

  private float currentZoom = 1f;
  private float desiredZoom = 1f;

  private Vector2 currentPos = new Vector2(0,0);
  private Vector2 desiredPos = new Vector2(0,0);
  private Vector2 velocity = new Vector2(0,0);

  private OrthographicCamera camera;
  private FitViewport viewport;

  private Camera() {
    reset();
  }

  public void updateUI() {
    camera.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0f);
    camera.zoom = 1f;
    camera.update();
  }


  public FitViewport getViewport() {
    return viewport;
  }

  public OrthographicCamera getCamera() {
    return camera;
  }

  public Rectangle getCullBox() {
    return cullBox;
  }

  public void reset() {
    camera = new OrthographicCamera();
    viewport = new FitViewport(Param.DISPLAY_X, Param.DISPLAY_Y, camera);
  }

  public void translate(float x, float y) {
    desiredPos.add(x, y);
  }

  public void velocity(float x, float y) {
    this.velocity.set(x, y);
  }

  public void modZoom(float z){
    desiredZoom += z;
    desiredZoom = Util.clamp(desiredZoom, Param.ZOOM_MIN, Param.ZOOM_MAX);
  }

  public void setZoom(float z) {
    desiredZoom = z;
    desiredZoom = Util.clamp(desiredZoom, Param.ZOOM_MIN, Param.ZOOM_MAX);
  }

  public float getZoom() {
    return desiredZoom;
  }

  public void update(float delta) {
    float frames = delta / Param.FRAME_TIME;

    desiredPos.add(velocity);
    velocity.scl((float)Math.pow(0.9f, frames));

    currentPos = desiredPos;
    currentZoom = desiredZoom;

    camera.position.set(currentPos, 0);
    camera.zoom = currentZoom;
    camera.update();

    int startX = (int)camera.position.x - viewport.getScreenWidth()/2;
    int startY = (int)camera.position.y - viewport.getScreenHeight()/2;
    cullBox.set(startX, startY, viewport.getScreenWidth(), viewport.getScreenHeight());
  }

}