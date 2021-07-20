package controller.DB;

import java.util.LinkedList;

public interface DBset <T>{
    T get(int ID);
    LinkedList<T> getAll();
    void add(T t);
    boolean remove(T t);
}
