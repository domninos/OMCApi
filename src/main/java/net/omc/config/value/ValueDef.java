package net.omc.config.value;

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
        return value != null ? value.toString() : "";
    }

    public int asInt() {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    public boolean asBool() {
        if (value instanceof Boolean)
            return (Boolean) value;
        if (value instanceof String s)
            return Boolean.parseBoolean(s);
        return false;
    }

    public List<String> asStringList() {
        if (value instanceof List<?> list)
            return list.stream().map(Object::toString).collect(Collectors.toList());

        return EMPTY_LIST;
    }

    public Object getValue() {
        return value;
    }
}
