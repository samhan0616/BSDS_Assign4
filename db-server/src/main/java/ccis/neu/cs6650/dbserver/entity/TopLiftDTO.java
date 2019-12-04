package ccis.neu.cs6650.dbserver.entity;

public class TopLiftDTO {
    private String lift_ID;
    private int times;


    public String getLift_ID() {
        return lift_ID;
    }

    public void setLift_ID(String lift_ID) {
        this.lift_ID = lift_ID;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }
}
