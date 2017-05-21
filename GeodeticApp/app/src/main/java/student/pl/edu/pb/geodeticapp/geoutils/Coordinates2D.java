package student.pl.edu.pb.geodeticapp.geoutils;

public class Coordinates2D {
    public static Coordinates2D ofArray(double[] xyArray){
        return new Coordinates2D(xyArray[0], xyArray[1]);
    }

    private double x,y;

    public Coordinates2D(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double[] toArray(){
        return new double[]{x,y};
    }

    @Override
    public String toString() {
        return "Coordinates2D{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
