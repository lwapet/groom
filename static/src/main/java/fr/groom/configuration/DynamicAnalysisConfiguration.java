
package fr.groom.configuration;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * parameters concerning dynamic analysis
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "avdName",
    "executionTime"
})
public class DynamicAnalysisConfiguration implements Serializable
{

    /**
     * Existing Avd name that will be used to install and run the apk
     * 
     */
    @JsonProperty("avdName")
    @JsonPropertyDescription("Existing Avd name that will be used to install and run the apk")
    private String avdName = "Nexus_5X_API_27";
    /**
     * execution analysis time in seconds
     * 
     */
    @JsonProperty("executionTime")
    @JsonPropertyDescription("execution analysis time in seconds")
    private int executionTime = 60;
    private final static long serialVersionUID = 6226015430539487348L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public DynamicAnalysisConfiguration() {
    }

    /**
     * 
     * @param executionTime
     * @param avdName
     */
    public DynamicAnalysisConfiguration(String avdName, int executionTime) {
        super();
        this.avdName = avdName;
        this.executionTime = executionTime;
    }

    /**
     * Existing Avd name that will be used to install and run the apk
     * 
     */
    @JsonProperty("avdName")
    public String getAvdName() {
        return avdName;
    }

    /**
     * Existing Avd name that will be used to install and run the apk
     * 
     */
    @JsonProperty("avdName")
    public void setAvdName(String avdName) {
        this.avdName = avdName;
    }

    /**
     * execution analysis time in seconds
     * 
     */
    @JsonProperty("executionTime")
    public int getExecutionTime() {
        return executionTime;
    }

    /**
     * execution analysis time in seconds
     * 
     */
    @JsonProperty("executionTime")
    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(DynamicAnalysisConfiguration.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("avdName");
        sb.append('=');
        sb.append(((this.avdName == null)?"<null>":this.avdName));
        sb.append(',');
        sb.append("executionTime");
        sb.append('=');
        sb.append(this.executionTime);
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.avdName == null)? 0 :this.avdName.hashCode()));
        result = ((result* 31)+ this.executionTime);
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DynamicAnalysisConfiguration) == false) {
            return false;
        }
        DynamicAnalysisConfiguration rhs = ((DynamicAnalysisConfiguration) other);
        return (((this.avdName == rhs.avdName)||((this.avdName!= null)&&this.avdName.equals(rhs.avdName)))&&(this.executionTime == rhs.executionTime));
    }

}
