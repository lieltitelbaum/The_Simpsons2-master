package lielTitel.thesimpsons;

public class Person implements Comparable<Person>{

    private String name;
    private int score;
    private double latitude;
    private double longitude;

    public Person(String name, int score, double latitude, double longitude) {
        setName(name);
        setScore(score);
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getScore() {
        return this.score;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(Person o) {
        return o.getScore() - score;
    }
}
