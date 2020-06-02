package com.kairos.shiftplanning;

import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Application {

    public static void main(String... args) {
        Quarkus.run(ShiftPlanningSolver.class, args);
    }
}
