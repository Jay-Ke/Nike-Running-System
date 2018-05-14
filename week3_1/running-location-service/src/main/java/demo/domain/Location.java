package demo.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Entity
@Table(name="LOCATIONS")
public class Location {

    enum GpsStatus{
        EXCELLENT,OK,UNRELIABLE,BAD,NOFIX,UNKNOWN;
    }

    public enum RunnerMovementType{
        STOPPED,IN_MOTION;
    }

    @Id
    @GeneratedValue
    private long id;

    @Embedded
    @AttributeOverride(name="bandMake", column = @Column(name="unit_band_make"))
    private UnitInfo unitinfo;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="fmi", column = @Column(name="medical_fmi")),
            @AttributeOverride(name="bfr", column = @Column(name="medical_bfr"))
    })
    private MedicalInfo medicalInfo;

    private double latitude;
    private double longitude;
    private String heading;
    private double gpsSpeed;
    private GpsStatus gpsStatus;
    private double odometer;
    private double totalRunningTime;
    private double totalIdleTime;
    private double totalCalorieBurnt;
    private String address;
    private Date timestamp = new Date();
    private String gearProvider;
    private RunnerMovementType runnerMovementType;
    private String serviceType;

    public Location(){
        this.unitinfo = null;
    }

    @JsonCreator
    public Location(@JsonProperty("runningId") String runningId){
        this.unitinfo = new UnitInfo(runningId);
    }

    public Location(UnitInfo unitInfo){
        this.unitinfo = unitInfo;
    }

    public String getRunningId(){
        return this.unitinfo==null? null: this.unitinfo.getRunningId();
    }

}
