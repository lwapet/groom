
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
    "runFlowDroid",
    "flowDroidConfiguration"
})
public class StaticAnalysisConfiguration implements Serializable
{

    /**
     * Does the program should run FlowDroid or not
     * 
     */
    @JsonProperty("runFlowDroid")
    @JsonPropertyDescription("Does the program should run FlowDroid or not")
    private boolean runFlowDroid = false;
    /**
     * program can run flow droid after its main operations
     * 
     */
    @JsonProperty("flowDroidConfiguration")
    @JsonPropertyDescription("program can run flow droid after its main operations")
    private FlowDroidConfiguration flowDroidConfiguration;
    private final static long serialVersionUID = -4137545771915461074L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public StaticAnalysisConfiguration() {
    }

    /**
     * 
     * @param runFlowDroid
     * @param flowDroidConfiguration
     */
    public StaticAnalysisConfiguration(boolean runFlowDroid, FlowDroidConfiguration flowDroidConfiguration) {
        super();
        this.runFlowDroid = runFlowDroid;
        this.flowDroidConfiguration = flowDroidConfiguration;
    }

    /**
     * Does the program should run FlowDroid or not
     * 
     */
    @JsonProperty("runFlowDroid")
    public boolean isRunFlowDroid() {
        return runFlowDroid;
    }

    /**
     * Does the program should run FlowDroid or not
     * 
     */
    @JsonProperty("runFlowDroid")
    public void setRunFlowDroid(boolean runFlowDroid) {
        this.runFlowDroid = runFlowDroid;
    }

    /**
     * program can run flow droid after its main operations
     * 
     */
    @JsonProperty("flowDroidConfiguration")
    public FlowDroidConfiguration getFlowDroidConfiguration() {
        return flowDroidConfiguration;
    }

    /**
     * program can run flow droid after its main operations
     * 
     */
    @JsonProperty("flowDroidConfiguration")
    public void setFlowDroidConfiguration(FlowDroidConfiguration flowDroidConfiguration) {
        this.flowDroidConfiguration = flowDroidConfiguration;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(StaticAnalysisConfiguration.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("runFlowDroid");
        sb.append('=');
        sb.append(this.runFlowDroid);
        sb.append(',');
        sb.append("flowDroidConfiguration");
        sb.append('=');
        sb.append(((this.flowDroidConfiguration == null)?"<null>":this.flowDroidConfiguration));
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
        result = ((result* 31)+(this.runFlowDroid? 1 : 0));
        result = ((result* 31)+((this.flowDroidConfiguration == null)? 0 :this.flowDroidConfiguration.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof StaticAnalysisConfiguration) == false) {
            return false;
        }
        StaticAnalysisConfiguration rhs = ((StaticAnalysisConfiguration) other);
        return ((this.runFlowDroid == rhs.runFlowDroid)&&((this.flowDroidConfiguration == rhs.flowDroidConfiguration)||((this.flowDroidConfiguration!= null)&&this.flowDroidConfiguration.equals(rhs.flowDroidConfiguration))));
    }

}
