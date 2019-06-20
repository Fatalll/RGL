package ru.ifmo.rgl.gameobjects;

import org.jetbrains.annotations.NotNull;

/**
 * interface for objects which can be drawn on the GUI
 *
 * @param <T> drawable type
 */
public interface Drawable<T> {
    @NotNull
    T display();
}
