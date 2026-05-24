package esiee.info.e3.view.components;

import esiee.info.e3.domain.enums.JokerType;
import esiee.info.e3.view.ViewMain;
import esiee.info.e3.view.interfaces.UIComponent;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;

public class UIJoker implements UIComponent {
    private final JokerType joker;
    private final ViewMain context;
    private final Runnable onClick;
    private final double ratio;
    private boolean isHovered;

    public UIJoker(JokerType joker, ViewMain context, Runnable onClick, double ratio) {
        this.joker = joker;
        this.context = Objects.requireNonNull(context);
        this.onClick = onClick;
        this.ratio = ratio;
    }

    private Rectangle getBounds(int x, int y, int width, int height) {
        int finalW = width;
        int finalH = (int) (width / this.ratio);

        if (finalH > height) {
            finalH = height;
            finalW = (int) (height * this.ratio);
        }

        int drawX = x + (width - finalW) / 2;
        int drawY = y + (height - finalH) / 2;

        return new Rectangle(drawX, drawY, finalW, finalH);
    }

    @Override
    public void render(Graphics2D g, int x, int y, int width, int height) {
        if (joker == null) return;

        Rectangle bounds = this.getBounds(x, y, width, height);

        g.setColor(new Color(0, 0, 0, 120));
        g.fillRoundRect(bounds.x + 4, bounds.y + 4, bounds.width, bounds.height, 12, 12);

        var img = context.getImage(joker.getImagePath());
        if (img != null) {
            var oldClip = g.getClip();
            g.setClip(new RoundRectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12));
            g.drawImage(img, bounds.x, bounds.y, bounds.width, bounds.height, null);
            g.setClip(oldClip);
        } else {
            g.setColor(new Color(90, 30, 140));
            g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);
            g.setColor(Color.WHITE);
            g.setFont(context.getGameFont().deriveFont(14f));
            String name = joker.name().replace("JOKER_", "");
            int strW = g.getFontMetrics().stringWidth(name);
            g.drawString(name, bounds.x + (bounds.width - strW) / 2, bounds.y + bounds.height / 2);
        }

        if (this.isHovered && this.onClick != null) {
            g.setColor(new Color(255, 255, 255, 40));
            g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);
            g.setColor(Color.CYAN);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);
        }
    }

    @Override
    public boolean handlePointerClick(int mx, int my, int x, int y, int width, int height) {
        Rectangle bounds = this.getBounds(x, y, width, height);
        if (bounds.contains(mx, my)) {
            if (onClick != null) {
                onClick.run();
            }
            return true;
        }
        return false;
    }

    @Override
    public void handlePointerMove(int mx, int my, int x, int y, int width, int height) {
        Rectangle bounds = this.getBounds(x, y, width, height);
        this.isHovered = bounds.contains(mx, my);
    }
}