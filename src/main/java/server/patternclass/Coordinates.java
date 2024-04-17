package server.patternclass;

import lombok.Getter;
/**
 * Represents a set of coordinates with x and y values.
 *
 * The x value must be greater than -503 and cannot be null.
 * The y value must be greater than -664 and cannot be null.
 */
@Getter
public class Coordinates {
    private Long x; //Значение поля должно быть больше -503, Поле не может быть null
    private Long y; //Значение поля должно быть больше -664, Поле не может быть null

    public Coordinates(Long x, Long y) {
        this.x = x;
        this.y = y;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public void setY(Long y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "Coordinates{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}