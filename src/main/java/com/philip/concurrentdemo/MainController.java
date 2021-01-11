package com.philip.concurrentdemo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final ProjectionService projectionService;

    public MainController(ProjectionService projectionService){
        this.projectionService = projectionService;
    }

    @GetMapping("/projection")
    @ResponseBody
    public PickList getPickList(String id, @RequestParam String projectionsList){
        PickList out = new PickList();

        PickList appliedProjections = projectionService.buildProjections(out,
                Arrays.stream(projectionsList.split(",")).collect(Collectors.toList()));

        out.setProjectionsApplied(appliedProjections.getProjectionsApplied());

        return out;
    }

}
