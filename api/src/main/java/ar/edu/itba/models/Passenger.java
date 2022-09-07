package ar.edu.itba.models;

public class Passenger {
        private final String name;
        private final SeatCategory category;
        private Seat seat;

        Passenger(String name, SeatCategory category) {
                this.name = name;
                this.category = category;
        }

        public SeatCategory getCategory() {
                return category;
        }

        public String getName() {
                return name;
        }

        public void setSeat(Seat seat) {
                this.seat = seat;
        }

        public Seat getSeat() {
                return seat;
        }
}
