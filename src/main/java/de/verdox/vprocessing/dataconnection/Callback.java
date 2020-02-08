package de.verdox.vprocessing.dataconnection;

@FunctionalInterface
public interface Callback<T> {
    // Lets you execute things
    void taskDone(T obj);
}
