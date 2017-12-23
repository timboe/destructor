package timboe.destructor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class ShapeRendererExtended extends ShapeRenderer {

  private final ImmediateModeRenderer renderer;
  private final Color color;

  public ShapeRendererExtended() {
    super();
    renderer = super.getRenderer();
    color = super.getColor();
  }

  /** Draws an arc using {@link ShapeType#Line} or {@link ShapeType#Filled}. */
  @Override
  public void arc (float x, float y, float radius, float start, float degrees) {
    int segments = (int)(6 * (float)Math.cbrt(radius) * (degrees / 360.0f));

    if (segments <= 0) throw new IllegalArgumentException("segments must be > 0.");
    float colorBits = color.toFloatBits();
    float theta = (2 * MathUtils.PI * (degrees / 360.0f)) / segments;
    float cos = MathUtils.cos(theta);
    float sin = MathUtils.sin(theta);
    float cx = radius * MathUtils.cos(start * MathUtils.degreesToRadians);
    float cy = radius * MathUtils.sin(start * MathUtils.degreesToRadians);

    for (int i = 0; i < segments; i++) {
      renderer.color(colorBits);
      renderer.vertex(x + cx, y + cy, 0);
      float temp = cx;
      cx = cos * cx - sin * cy;
      cy = sin * temp + cos * cy;
      renderer.color(colorBits);
      renderer.vertex(x + cx, y + cy, 0);
    }
  }
}
