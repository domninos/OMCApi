package net.omc.config;

import java.util.List;
import java.util.stream.Collectors;

public class ValueDef {

    public static final List<String> EMPTY_LIST = List.of();

    private final Object value;

    private ValueDef(Object value) {
        this.value = value;
    }

    public static ValueDef none() {
        return null;
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

    public static ValueDef from(List<String> stringList) {
        return new ValueDef(stringList);
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

    public List<String> asStringList() {
        if (value instanceof List<?> list)
            return list.stream().map(Object::toString).collect(Collectors.toList());

        return EMPTY_LIST;
    }

}
