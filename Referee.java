
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Referee extends User {

    private List<LocalDate> gameDateAvailabilityDates;
    private List<Game> schedule;

    public Referee() {
        this.gameDateAvailabilityDates = new ArrayList<LocalDate>();
        this.schedule = new ArrayList<>();
    }

    public List<LocalDate> getGameDatesAvailability() {
        return gameDateAvailabilityDates;
    }

    public List<Game> getSchedule() {
        return schedule;
    }

    public void addGameDateAvailability(LocalDate date) {
        this.gameDateAvailabilityDates.add(date);
    }

    public void addToSchedule(Game game) {
        this.schedule.add(game);
    }

    public void deleteGameDate(LocalDate date) {
        this.gameDateAvailabilityDates.remove(date);
    }

    public void deleteGameFromSchedule(Game game) {
        this.schedule.remove(game);
    }

    public boolean isAvailableOnDate(LocalDate date) {
        return this.gameDateAvailabilityDates.contains(date);
    }

}
