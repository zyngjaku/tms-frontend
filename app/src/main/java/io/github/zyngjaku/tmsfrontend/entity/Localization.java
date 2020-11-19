package io.github.zyngjaku.tmsfrontend.entity;

import androidx.annotation.NonNull;

import java.util.Date;

public class Localization {
    private Long id;
    private Float latitude;
    private Float longitude;
    private int heading;
    private int speed;
    private Date lastUpdate;

    public Localization(Float latitude, Float longitude, int heading, int speed) {
        setLatitude(latitude);
        setLongitude(longitude);
        setHeading(heading);
        setSpeed(speed);
    }

    public Float getLatitude() {
        return latitude;
    }
    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }
    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public int getHeading() {
        return heading;
    }
    public void setHeading(int heading) {
        this.heading = heading;
    }

    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate() {
        this.lastUpdate = new Date();
    }

    @NonNull
    @Override
    public String toString() {
        return "Heading: " + heading + "\nSpeed: " + speed;
    }
}
