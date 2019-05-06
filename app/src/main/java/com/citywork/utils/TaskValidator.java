package com.citywork.utils;

public class TaskValidator {
    public boolean isValid(String task) {
        if (task == null || task.trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }
}
