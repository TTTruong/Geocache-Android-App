package project.csci.geocaching;

public class Cache {

    private int cacheID;
    private String name;
    private double latitude;
    private double longitude;
    private String description;

    public double getLong() {
        return longitude;
    }

    public void setLong(double longitude) {
        this.longitude = longitude;
    }

    public Cache(){
        this.cacheID = -1;
        this.name = "";
        this.latitude = 0;
        this.longitude = 0;
        this.description = "";
    }

    public Cache(int cacheID, String name, String description, double longitude, double latitude) {
        this.cacheID = cacheID;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public int getCacheID() {
        return cacheID;
    }

    public void setCacheID(int cacheID) {
        this.cacheID = cacheID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return latitude;
    }

    public void setLat(double latitude) {
        this.latitude = latitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Cache{" +
                "cacheID=" + cacheID +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", description='" + description + '\'' +
                '}';
    }

    public String getLoc(){
        return " " + latitude +
        "," + longitude;
    }
}