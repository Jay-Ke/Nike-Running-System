package demo.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class CurrentPosition {

    private String runningId;
    private Point location;
    private RunnerStatus runnerStatus = RunnerStatus.NONE;
    private double speed;
    private double heading;
    private MedicalInfo medicalInfo;

    public CurrentPosition(){

    }
}
