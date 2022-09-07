package ar.edu.itba.models;

import java.nio.file.Path;

public class Pair<V> {
    V x;
    V y;

    public Pair(V x, V y){
        this.x=x;
        this.y=y;
    }

    public V getX(){
        return x;
    }

    public V getY(){
        return y;
    }

}
