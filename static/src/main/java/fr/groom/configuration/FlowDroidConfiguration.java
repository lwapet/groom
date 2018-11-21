
package fr.groom.configuration;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * program can run flow droid after its main operations
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "contextSensitive",
    "flowSensitive",
    "ignoreFlowsInSystemPackages",
    "flowDroidInputFiles"
})
public class FlowDroidConfiguration implements Serializable
{

    /**
     * Does FlowDroid should run in context sensitive mode
     * 
     */
    @JsonProperty("contextSensitive")
    @JsonPropertyDescription("Does FlowDroid should run in context sensitive mode")
    private boolean contextSensitive = false;
    /**
     * Does FlowDroid should run in flow sensitive mode
     * 
     */
    @JsonProperty("flowSensitive")
    @JsonPropertyDescription("Does FlowDroid should run in flow sensitive mode")
    private boolean flowSensitive = true;
    /**
     * Does FlowDroid should ignore system package flows
     * 
     */
    @JsonProperty("ignoreFlowsInSystemPackages")
    @JsonPropertyDescription("Does FlowDroid should ignore system package flows")
    private boolean ignoreFlowsInSystemPackages = false;
    /**
     * input files required to run FlowDroid
     * 
     */
    @JsonProperty("flowDroidInputFiles")
    @JsonPropertyDescription("input files required to run FlowDroid")
    private FlowDroidInputFiles flowDroidInputFiles;
    private final static long serialVersionUID = 5414678921285840482L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public FlowDroidConfiguration() {
    }

    /**
     * 
     * @param ignoreFlowsInSystemPackages
     * @param flowSensitive
     * @param flowDroidInputFiles
     * @param contextSensitive
     */
    public FlowDroidConfiguration(boolean contextSensitive, boolean flowSensitive, boolean ignoreFlowsInSystemPackages, FlowDroidInputFiles flowDroidInputFiles) {
        super();
        this.contextSensitive = contextSensitive;
        this.flowSensitive = flowSensitive;
        this.ignoreFlowsInSystemPackages = ignoreFlowsInSystemPackages;
        this.flowDroidInputFiles = flowDroidInputFiles;
    }

    /**
     * Does FlowDroid should run in context sensitive mode
     * 
     */
    @JsonProperty("contextSensitive")
    public boolean isContextSensitive() {
        return contextSensitive;
    }

    /**
     * Does FlowDroid should run in context sensitive mode
     * 
     */
    @JsonProperty("contextSensitive")
    public void setContextSensitive(boolean contextSensitive) {
        this.contextSensitive = contextSensitive;
    }

    /**
     * Does FlowDroid should run in flow sensitive mode
     * 
     */
    @JsonProperty("flowSensitive")
    public boolean isFlowSensitive() {
        return flowSensitive;
    }

    /**
     * Does FlowDroid should run in flow sensitive mode
     * 
     */
    @JsonProperty("flowSensitive")
    public void setFlowSensitive(boolean flowSensitive) {
        this.flowSensitive = flowSensitive;
    }

    /**
     * Does FlowDroid should ignore system package flows
     * 
     */
    @JsonProperty("ignoreFlowsInSystemPackages")
    public boolean isIgnoreFlowsInSystemPackages() {
        return ignoreFlowsInSystemPackages;
    }

    /**
     * Does FlowDroid should ignore system package flows
     * 
     */
    @JsonProperty("ignoreFlowsInSystemPackages")
    public void setIgnoreFlowsInSystemPackages(boolean ignoreFlowsInSystemPackages) {
        this.ignoreFlowsInSystemPackages = ignoreFlowsInSystemPackages;
    }

    /**
     * input files required to run FlowDroid
     * 
     */
    @JsonProperty("flowDroidInputFiles")
    public FlowDroidInputFiles getFlowDroidInputFiles() {
        return flowDroidInputFiles;
    }

    /**
     * input files required to run FlowDroid
     * 
     */
    @JsonProperty("flowDroidInputFiles")
    public void setFlowDroidInputFiles(FlowDroidInputFiles flowDroidInputFiles) {
        this.flowDroidInputFiles = flowDroidInputFiles;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(FlowDroidConfiguration.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("contextSensitive");
        sb.append('=');
        sb.append(this.contextSensitive);
        sb.append(',');
        sb.append("flowSensitive");
        sb.append('=');
        sb.append(this.flowSensitive);
        sb.append(',');
        sb.append("ignoreFlowsInSystemPackages");
        sb.append('=');
        sb.append(this.ignoreFlowsInSystemPackages);
        sb.append(',');
        sb.append("flowDroidInputFiles");
        sb.append('=');
        sb.append(((this.flowDroidInputFiles == null)?"<null>":this.flowDroidInputFiles));
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
        result = ((result* 31)+(this.ignoreFlowsInSystemPackages? 1 : 0));
        result = ((result* 31)+(this.flowSensitive? 1 : 0));
        result = ((result* 31)+((this.flowDroidInputFiles == null)? 0 :this.flowDroidInputFiles.hashCode()));
        result = ((result* 31)+(this.contextSensitive? 1 : 0));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof FlowDroidConfiguration) == false) {
            return false;
        }
        FlowDroidConfiguration rhs = ((FlowDroidConfiguration) other);
        return ((((this.ignoreFlowsInSystemPackages == rhs.ignoreFlowsInSystemPackages)&&(this.flowSensitive == rhs.flowSensitive))&&((this.flowDroidInputFiles == rhs.flowDroidInputFiles)||((this.flowDroidInputFiles!= null)&&this.flowDroidInputFiles.equals(rhs.flowDroidInputFiles))))&&(this.contextSensitive == rhs.contextSensitive));
    }

}
