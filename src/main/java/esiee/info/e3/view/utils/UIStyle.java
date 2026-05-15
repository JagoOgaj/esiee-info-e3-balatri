package esiee.info.e3.view.utils;

import java.awt.Color;
import java.awt.Font;

public record UIStyle(
        Color backgroundColor,
        Color hoverBackgroundColor,
        Color borderColor,
        int borderRadius,
        int padding, int margin, float borderWidth,
        Color textColor, Font font,
        Color shadowColor, int shadowOffset,
        int maxWidth, int maxHeight
) {
    public static class Builder {
        private Color bg = new Color(0, 0, 0, 0);
        private Color hoverBg = null;
        private Color border = new Color(0, 0, 0, 0);
        private int radius = 0;
        private int padding = 0, margin = 0;
        private float borderW = 0f;
        private Color text = Color.WHITE;
        private Font f = null;
        private Color shadowC = new Color(0, 0, 0, 0);
        private int shadowO = 0;
        private int maxW = 0, maxH = 0;

        public Builder bg(Color c) {
            this.bg = c;
            return this;
        }

        public Builder hoverBg(Color c) {
            this.hoverBg = c;
            return this;
        }

        public Builder border(Color c, float w) {
            this.border = c;
            this.borderW = w;
            return this;
        }

        public Builder radius(int r) {
            this.radius = r;
            return this;
        }

        public Builder padding(int p) {
            this.padding = p;
            return this;
        }

        public Builder margin(int m) {
            this.margin = m;
            return this;
        }

        public Builder text(Color c) {
            this.text = c;
            return this;
        }

        public Builder font(Font f) {
            this.f = f;
            return this;
        }

        public Builder shadow(Color c, int o) {
            this.shadowC = c;
            this.shadowO = o;
            return this;
        }

        public Builder maxWidth(int w) {
            this.maxW = w;
            return this;
        }

        public Builder maxHeight(int h) {
            this.maxH = h;
            return this;
        }

        public UIStyle build() {
            return new UIStyle(bg, hoverBg, border, radius, padding, margin, borderW, text, f, shadowC, shadowO, maxW, maxH);
        }
    }
}