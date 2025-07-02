package net.omc.config;

public class ValueDef {

    private final Object value;

    private ValueDef(Object value) {
        this.value = value;
    }

    public static ValueDef from(String string) {
        return new ValueDef(string);
    }

    public static ValueDef from(int integer) {
        return new ValueDef(integer);
    }

    public static ValueDef from(boolean bool) {
        return new ValueDef(bool);
    }

    public String asString() {
        return (String) value;
    }

    public int asInt() {
        return (int) value;
    }

    public boolean asBool() {
        return (boolean) value;
    }

}
