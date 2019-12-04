package ccis.neu.cs6650.dbserver.entity;

public class TopSkierDTO {
    private String skier_ID;
    private int times;


    public String getSkier_ID() {
        return skier_ID;
    }

    public void setSkier_ID(String skier_ID) {
        this.skier_ID = skier_ID;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}
