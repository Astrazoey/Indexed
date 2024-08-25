package com.astrazoey.indexed;

public class IndexedClassHelper implements IndexedInterfaceHelper {

    public static boolean boolValue;

    public static ThreadLocal<Boolean> booleanThreadLocal = new ThreadLocal<Boolean>();

    @Override
    public void setBool(boolean value) {
        boolValue = value;
    }

    @Override
    public boolean getBool(IndexedClassHelper helper) {
        return boolValue;
    }
}
