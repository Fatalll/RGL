package gui;

import com.googlecode.lanterna.TextColor;

public class DisplayGameObject<T> {
    T display;
    TextColor foreColor;
    TextColor backColor;

    public DisplayGameObject(T display, TextColor foreColor) {
        this.foreColor = foreColor;
        this.backColor = TextColor.ANSI.BLACK;
        this.display= display;
    }

    public DisplayGameObject(T display, TextColor foreColor, TextColor backColor) {
        this.foreColor = foreColor;
        this.backColor = backColor;
        this.display= display;
    }

    public T getDisplay() {
        return display;
    }

    public TextColor getForeColor() {
        return foreColor;
    }

    public TextColor getBackColor() {
        return backColor;
    }
}

