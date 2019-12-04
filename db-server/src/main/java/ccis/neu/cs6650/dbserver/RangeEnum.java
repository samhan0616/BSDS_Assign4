package ccis.neu.cs6650.dbserver;

public enum  RangeEnum {
    DAY("1d"), HOUR("1h");

    private String name;
    RangeEnum(String s) {
        this.name = s;
    }

    public String getName() {
        return name;
    }
}
