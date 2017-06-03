package student.pl.edu.pb.geodeticapp.data.entities;

import java.io.Serializable;

public class GeoPoint implements Serializable{
    private long id;
    private String name;
    private double latitude;
    private double longitude;
    private double xGK;
    private double yGK;

    public GeoPoint(){

    }

    public GeoPoint(GeoPoint point){
        this.id = point.getId();
        this.name = point.getName();
        this.latitude = point.getLatitude();
        this.longitude = point.getLongitude();
        this.xGK = point.getxGK();
        this.yGK = point.getyGK();
    }

    public GeoPoint(String name){
        this.name = name;
    }

    public GeoPoint(long id, String name, double latitude, double longitude, double xGK, double yGK){
        this(name);
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.xGK = xGK;
        this.yGK = yGK;
    }

    public long getId() {
        return id;
    }

    public GeoPoint setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public GeoPoint setName(String name) {
        this.name = name;
        return this;
    }

    public double getLatitude() {
        return latitude;
    }

    public GeoPoint setLatitude(double latitude) {
        this.latitude = latitude;
        return this;
    }

    public double getLongitude() {
        return longitude;
    }

    public GeoPoint setLongitude(double longitude) {
        this.longitude = longitude;
        return this;
    }

    public double getxGK() {
        return xGK;
    }

    public GeoPoint setxGK(double xGK) {
        this.xGK = xGK;
        return this;
    }

    public double getyGK() {
        return yGK;
    }

    public GeoPoint setyGK(double yGK) {
        this.yGK = yGK;
        return this;
    }
}
