package esiee.info.e3.view.components;

import esiee.info.e3.view.interfaces.UIComponent;
import esiee.info.e3.view.utils.Bounds;
import esiee.info.e3.view.utils.UIStyle;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UIContainer implements UIComponent {

  private final List<GridChild> children = new ArrayList<>();
  private final UIStyle style;
  private final int rows;
  private final int cols;
  private boolean isHovered = false;

  public UIContainer(int rows, int cols, UIStyle style) {
    this.rows = rows;
    this.cols = cols;
    this.style = Objects.requireNonNull(style);
  }

  public void addComponent(UIComponent child, int row, int col, double weightH, double weightW) {
    this.children.add(new GridChild(child, row, col, weightH, weightW));
  }

  @Override
  public void render(Graphics2D g, int x, int y, int width, int height) {
    var drawArea = this.calculateDrawArea(x, y, width, height);

    var bgColor =
        (this.isHovered && this.style.hoverBackgroundColor() != null)
            ? this.style.hoverBackgroundColor()
            : this.style.backgroundColor();

    g.setColor(bgColor);
    g.fillRoundRect(
        drawArea.x(),
        drawArea.y(),
        drawArea.w(),
        drawArea.h(),
        this.style.borderRadius(),
        this.style.borderRadius());

    if (this.style.borderWidth() > 0) {
      g.setColor(this.style.borderColor());
      g.setStroke(new BasicStroke(this.style.borderWidth()));
      g.drawRoundRect(
          drawArea.x(),
          drawArea.y(),
          drawArea.w(),
          drawArea.h(),
          this.style.borderRadius(),
          this.style.borderRadius());
    }

    var innerArea = this.calculateInnerArea(drawArea);

    for (var child : this.children) {
      var cb = this.calculateChildBounds(child, innerArea);
      child.component().render(g, cb.x(), cb.y(), cb.w(), cb.h());
    }
  }

  @Override
  public boolean handlePointerClick(int mx, int my, int x, int y, int width, int height) {
    var drawArea = this.calculateDrawArea(x, y, width, height);
    var innerArea = this.calculateInnerArea(drawArea);

    for (var child : this.children) {
      var cb = this.calculateChildBounds(child, innerArea);
      if (child.component().handlePointerClick(mx, my, cb.x(), cb.y(), cb.w(), cb.h())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void handlePointerMove(int mx, int my, int x, int y, int width, int height) {
    var drawArea = this.calculateDrawArea(x, y, width, height);
    this.isHovered =
        (mx >= drawArea.x()
            && mx <= drawArea.x() + drawArea.w()
            && my >= drawArea.y()
            && my <= drawArea.y() + drawArea.h());

    var innerArea = this.calculateInnerArea(drawArea);

    for (var child : this.children) {
      var cb = this.calculateChildBounds(child, innerArea);
      child.component().handlePointerMove(mx, my, cb.x(), cb.y(), cb.w(), cb.h());
    }
  }

  public void clearChildren() {
    this.children.clear();
  }

  private Bounds calculateDrawArea(int x, int y, int width, int height) {
    var margin = this.style.margin();
    return new Bounds(x + margin, y + margin, width - (margin * 2), height - (margin * 2));
  }

  private Bounds calculateInnerArea(Bounds drawArea) {
    var padding = this.style.padding();
    return new Bounds(
        drawArea.x() + padding,
        drawArea.y() + padding,
        drawArea.w() - (padding * 2),
        drawArea.h() - (padding * 2));
  }

  private Bounds calculateChildBounds(GridChild child, Bounds innerArea) {
    var childW = (int) (innerArea.w() * child.weightW());
    var childH = (int) (innerArea.h() * child.weightH());
    var childX = innerArea.x() + (int) (innerArea.w() * ((double) child.col() / this.cols));
    var childY = innerArea.y() + (int) (innerArea.h() * ((double) child.row() / this.rows));
    return new Bounds(childX, childY, childW, childH);
  }

  private record GridChild(
      UIComponent component, int row, int col, double weightH, double weightW) {
    public GridChild {
      Objects.requireNonNull(component);
    }
  }
}
