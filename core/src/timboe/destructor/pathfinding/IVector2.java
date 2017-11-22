package timboe.destructor.pathfinding;

public class IVector2 implements Comparable {
  public int x;
  public int y;

  public IVector2(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public IVector2(IVector2 v) {
    x = v.x;
    y = v.y;
  }

  public IVector2() {
    x = 0;
    y = 0;
  }

  public void set(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public int compareTo(Object o) {
    IVector2 v = (IVector2)o;
    return  (x*x + y*y) - (v.x*v.x - v.y*v.y);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || !(o instanceof IVector2)) return false;
    IVector2 v = (IVector2)o;
    return (x == v.x && y == v.y);
  }

  @Override
  public String toString() {
    return "(" + x + "," + y +")";
  }
}
