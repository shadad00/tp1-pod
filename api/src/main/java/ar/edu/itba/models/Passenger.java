package ar.edu.itba.models;

import java.io.Serializable;

public class Passenger implements Serializable {
        private final String name;

        public Passenger(String name) {
                this.name = name;
        }
}
