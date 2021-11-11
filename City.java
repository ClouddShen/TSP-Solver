import java.util.HashMap;

public class City {
    public int city;
    public double x;
    public double y;

    public static double[][] distances;
    
    public double distance(City b) {
        return distances[this.city][b.city];
    }
}