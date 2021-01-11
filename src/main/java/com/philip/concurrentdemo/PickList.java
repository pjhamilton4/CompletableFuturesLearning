package com.philip.concurrentdemo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PickList {
    String projectionsApplied;
    String aDetails;
    String bDetails;
    String projection1;
    String projection2;
    String projection3;
    String projection4;
    String projection5;
    String projection6;
    List<String> errors = new ArrayList<>();
}
