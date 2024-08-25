package com.astrazoey.indexed;



public interface IndexedInterfaceHelper {

    static void set(IndexedClassHelper helper, boolean value) {
        ((IndexedInterfaceHelper) helper).setBool(value);
    }

    static boolean get(IndexedClassHelper helper) {
        return helper.getBool(helper);
    }

    void setBool(boolean value);
    boolean getBool(IndexedClassHelper helper);

}
