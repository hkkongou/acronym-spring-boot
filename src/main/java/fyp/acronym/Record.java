package fyp.acronym;

public class Record {
    private String field1;
    private String field2;
    private int integerValue;

    public Record(String field1, String field2, int integerValue) {
        this.field1 = field1;
        this.field2 = field2;
        this.integerValue = integerValue;
    }

    public String getField1() {
        return field1;
    }

    public String getField2() {
        return field2;
    }

    public int getIntegerValue() {
        return integerValue;
    }

    @Override
    public String toString() {
        return field1 + " " + field2 + " " + integerValue;
    }
}
