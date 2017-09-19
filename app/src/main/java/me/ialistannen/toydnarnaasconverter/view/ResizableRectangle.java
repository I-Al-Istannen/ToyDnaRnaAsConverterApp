package me.ialistannen.toydnarnaasconverter.view;

import static me.ialistannen.toydnarnaasconverter.view.ResizableRectangle.DraggingPoint.Direction.BOTTOM;
import static me.ialistannen.toydnarnaasconverter.view.ResizableRectangle.DraggingPoint.Direction.LEFT;
import static me.ialistannen.toydnarnaasconverter.view.ResizableRectangle.DraggingPoint.Direction.RIGHT;
import static me.ialistannen.toydnarnaasconverter.view.ResizableRectangle.DraggingPoint.Direction.TOP;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.EnumMap;
import java.util.Map;
import me.ialistannen.toydnarnaasconverter.util.PixelUtil;
import me.ialistannen.toydnarnaasconverter.view.ResizableRectangle.DraggingPoint.Direction;

/**
 * A resizable rectangle view
 */
public class ResizableRectangle extends View {

  private Map<Direction, DraggingPoint> draggingPoints;
  private float width;
  private float height;
  private float topLeftX;
  private float topLeftY;

  private DraggingPoint draggedPoint;

  public ResizableRectangle(Context context) {
    super(context);
  }

  public ResizableRectangle(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ResizableRectangle(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  private void init() {
    height = width = PixelUtil.dpToPixels(getContext(), 64);

    topLeftX = getWidth() / 2 - width;
    topLeftY = getHeight() / 2 - height;

    draggingPoints = new EnumMap<>(Direction.class);
    for (Direction direction : Direction.values()) {
      draggingPoints.put(direction, new DraggingPoint(0, 0, direction));
    }
    repositionDragPoints();
  }

  /**
   * @return A snapshot of the current location
   */
  public Location getLocationSnapshot() {
    return new Location(topLeftX, topLeftY, width, height);
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);

    if (draggingPoints == null) {
      init();
    }

    Paint dimPaint = new Paint();
    dimPaint.setColor(Color.argb(100, 0, 0, 0));
    dimPaint.setStrokeWidth(10);
    fillOutside(dimPaint, canvas, topLeftX, topLeftY, width, height);

    for (DraggingPoint point : draggingPoints.values()) {
      Paint circlePaint = new Paint();
      switch (point.direction) {
        case TOP:
          circlePaint.setColor(Color.CYAN);
          break;
        case BOTTOM:
          circlePaint.setColor(Color.RED);
          break;
        case LEFT:
          circlePaint.setColor(Color.GREEN);
          break;
        case RIGHT:
          circlePaint.setColor(Color.YELLOW);
          break;
      }
      canvas.drawCircle(point.x, point.y, point.getRadius(getContext()), circlePaint);
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        draggedPoint = null;

        float x = event.getX();
        float y = event.getY();

        for (DraggingPoint draggingPoint : draggingPoints.values()) {
          float dX = x - draggingPoint.x;
          float dY = y - draggingPoint.y;

          if (distance(dX, dY) < draggingPoint.getRadius(getContext())) {
            draggedPoint = draggingPoint;
            break;
          }

        }

        break;
      case MotionEvent.ACTION_MOVE:
        if (draggedPoint == null) {
          if (!isInside(event.getX(), event.getY())) {
            return true;
          }

          float newTopLeftY = event.getY() - height / 2;
          float newTopLeftX = event.getX() - width / 2;

          if (!checkIsInsideDrawableRange(newTopLeftX, newTopLeftY, width, height)) {
            return true;
          }

          topLeftY = newTopLeftY;
          topLeftX = newTopLeftX;

          repositionDragPoints();
          invalidate();
          return true;
        }

        float topModX = 0;
        float topModY = 0;
        float widthMod = 0;
        float heightMod = 0;

        switch (draggedPoint.direction) {
          case RIGHT:
            widthMod = event.getX() - draggedPoint.x;

            // don't allow moving the right one past the left one
            if (event.getX() < topLeftX + draggedPoint.getRadius(getContext()) && widthMod < 0) {
              return true;
            }

            break;
          case LEFT:
            topModX = event.getX() - draggedPoint.x;

            // don't allow moving the left one past the right one
            if (event.getX() > topLeftX + width - draggedPoint.getRadius(getContext())
                && topModX > 0) {
              return true;
            }

            widthMod -= topModX;
            break;
          case BOTTOM:
            heightMod = event.getY() - draggedPoint.y;

            // don't allow moving the bottom one past the top one
            if (event.getY() < topLeftY + draggedPoint.getRadius(getContext()) && heightMod < 0) {
              return true;
            }

            break;
          case TOP:
            topModY = event.getY() - draggedPoint.y;

            // don't allow moving the top one past the bottom one
            if (event.getY() > topLeftY + height - draggedPoint.getRadius(getContext())
                && topModY > 0) {
              return true;
            }

            heightMod -= topModY;
            break;
        }

        if (!checkIsInsideDrawableRange(topLeftX + topModX, topLeftY + topModY,
            width + widthMod, height + heightMod)) {
          return true;
        }

        topLeftX += topModX;
        topLeftY += topModY;
        width += widthMod;
        height += heightMod;

        repositionDragPoints();

        invalidate();
        break;
    }

    return true;
  }

  private boolean checkIsInsideDrawableRange(float newTopX, float newTopY, float newWidth,
      float newHeight) {
    return newTopX >= 0 && newTopY >= 0
        && newTopX + newWidth <= getLayoutParams().width
        && newTopY + newHeight <= getLayoutParams().height;
  }

  private void repositionDragPoints() {
    draggingPoints.get(TOP).setLocation(
        topLeftX + width / 2, topLeftY
    );

    draggingPoints.get(BOTTOM).setLocation(
        topLeftX + width / 2, topLeftY + height
    );

    draggingPoints.get(LEFT).setLocation(
        topLeftX, topLeftY + height / 2
    );

    draggingPoints.get(RIGHT).setLocation(
        topLeftX + width, topLeftY + height / 2
    );
  }

  private boolean isInside(float x, float y) {
    return x >= topLeftX && x <= topLeftX + width
        && y >= topLeftY && y <= topLeftY + height;
  }

  private float distance(float x, float y) {
    return (float) Math.sqrt(x * x + y * y);
  }

  private void fillOutside(Paint paint, Canvas canvas, float x, float y, float width,
      float height) {
    //    /--------------\
    //    |11112222223333|
    //    |1111|----|3333|
    //    |1111|    |3333|
    //    |1111|----|3333|
    //    |11114444443333|
    //    \--------------/

    // 1
    canvas.drawRect(0, 0, x, getHeight(), paint);

    // 2
    canvas.drawRect(x, 0, x + width, y, paint);

    // 3
    canvas.drawRect(x + width, 0, getWidth(), getHeight(), paint);

    // 4
    canvas.drawRect(x, y + height, width + x, getHeight(), paint);
  }

  /**
   * The location the rectangle was at at a specific moment in time.
   */
  public static class Location {

    private float topLeftX;
    private float topLeftY;
    private float width;
    private float height;

    Location(float topLeftX, float topLeftY, float width, float height) {
      this.topLeftX = topLeftX;
      this.topLeftY = topLeftY;
      this.width = width;
      this.height = height;
    }

    public int getWidth() {
      return (int) width;
    }

    public int getHeight() {
      return (int) height;
    }

    public int getTopLeftX() {
      return (int) topLeftX;
    }

    public int getTopLeftY() {
      return (int) topLeftY;
    }
  }

  static class DraggingPoint {

    private static final int RADIUS = 10;

    private float x;
    private float y;
    private Direction direction;

    DraggingPoint(float x, float y, Direction direction) {
      this.x = x;
      this.y = y;
      this.direction = direction;
    }

    private int getRadius(Context context) {
      return PixelUtil.dpToPixels(context, DraggingPoint.RADIUS);
    }

    private void setLocation(float x, float y) {
      this.x = x;
      this.y = y;
    }

    enum Direction {
      LEFT, RIGHT, TOP, BOTTOM
    }
  }
}
