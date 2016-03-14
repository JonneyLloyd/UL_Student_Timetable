package ie.gavin.ulstudenttimetable;

/**
 * Created by Oliver on 05/03/2016.
 */
public class StudentTimetableException extends Exception {
    public StudentTimetableException() {
        super();
    }

    public StudentTimetableException(String message) {
        super(message);
    }

    public StudentTimetableException(String message, Throwable cause) {
        super(message, cause);
    }

    public StudentTimetableException(Throwable cause) {
        super(cause);
    }

}
