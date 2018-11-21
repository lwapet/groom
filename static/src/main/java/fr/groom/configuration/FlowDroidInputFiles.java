
package fr.groom.configuration;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * input files required to run FlowDroid
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "androidCallbacks",
    "taintWrapperSource",
    "sourcesAndSinksFiles"
})
public class FlowDroidInputFiles implements Serializable
{

    /**
     * path to the target apk
     * 
     */
    @JsonProperty("androidCallbacks")
    @JsonPropertyDescription("path to the target apk")
    private String androidCallbacks = "required_files/AndroidCallbacks.txt";
    /**
     * path to the target apk
     * 
     */
    @JsonProperty("taintWrapperSource")
    @JsonPropertyDescription("path to the target apk")
    private String taintWrapperSource = "required_files/EasyTaintWrapperSource.txt";
    /**
     * how to handle sources and sinks
     * 
     */
    @JsonProperty("sourcesAndSinksFiles")
    @JsonPropertyDescription("how to handle sources and sinks")
    private SourcesAndSinksFiles sourcesAndSinksFiles;
    private final static long serialVersionUID = -8272724691789763729L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public FlowDroidInputFiles() {
    }

    /**
     * 
     * @param sourcesAndSinksFiles
     * @param androidCallbacks
     * @param taintWrapperSource
     */
    public FlowDroidInputFiles(String androidCallbacks, String taintWrapperSource, SourcesAndSinksFiles sourcesAndSinksFiles) {
        super();
        this.androidCallbacks = androidCallbacks;
        this.taintWrapperSource = taintWrapperSource;
        this.sourcesAndSinksFiles = sourcesAndSinksFiles;
    }

    /**
     * path to the target apk
     * 
     */
    @JsonProperty("androidCallbacks")
    public String getAndroidCallbacks() {
        return androidCallbacks;
    }

    /**
     * path to the target apk
     * 
     */
    @JsonProperty("androidCallbacks")
    public void setAndroidCallbacks(String androidCallbacks) {
        this.androidCallbacks = androidCallbacks;
    }

    /**
     * path to the target apk
     * 
     */
    @JsonProperty("taintWrapperSource")
    public String getTaintWrapperSource() {
        return taintWrapperSource;
    }

    /**
     * path to the target apk
     * 
     */
    @JsonProperty("taintWrapperSource")
    public void setTaintWrapperSource(String taintWrapperSource) {
        this.taintWrapperSource = taintWrapperSource;
    }

    /**
     * how to handle sources and sinks
     * 
     */
    @JsonProperty("sourcesAndSinksFiles")
    public SourcesAndSinksFiles getSourcesAndSinksFiles() {
        return sourcesAndSinksFiles;
    }

    /**
     * how to handle sources and sinks
     * 
     */
    @JsonProperty("sourcesAndSinksFiles")
    public void setSourcesAndSinksFiles(SourcesAndSinksFiles sourcesAndSinksFiles) {
        this.sourcesAndSinksFiles = sourcesAndSinksFiles;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(FlowDroidInputFiles.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("androidCallbacks");
        sb.append('=');
        sb.append(((this.androidCallbacks == null)?"<null>":this.androidCallbacks));
        sb.append(',');
        sb.append("taintWrapperSource");
        sb.append('=');
        sb.append(((this.taintWrapperSource == null)?"<null>":this.taintWrapperSource));
        sb.append(',');
        sb.append("sourcesAndSinksFiles");
        sb.append('=');
        sb.append(((this.sourcesAndSinksFiles == null)?"<null>":this.sourcesAndSinksFiles));
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
        result = ((result* 31)+((this.sourcesAndSinksFiles == null)? 0 :this.sourcesAndSinksFiles.hashCode()));
        result = ((result* 31)+((this.androidCallbacks == null)? 0 :this.androidCallbacks.hashCode()));
        result = ((result* 31)+((this.taintWrapperSource == null)? 0 :this.taintWrapperSource.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof FlowDroidInputFiles) == false) {
            return false;
        }
        FlowDroidInputFiles rhs = ((FlowDroidInputFiles) other);
        return ((((this.sourcesAndSinksFiles == rhs.sourcesAndSinksFiles)||((this.sourcesAndSinksFiles!= null)&&this.sourcesAndSinksFiles.equals(rhs.sourcesAndSinksFiles)))&&((this.androidCallbacks == rhs.androidCallbacks)||((this.androidCallbacks!= null)&&this.androidCallbacks.equals(rhs.androidCallbacks))))&&((this.taintWrapperSource == rhs.taintWrapperSource)||((this.taintWrapperSource!= null)&&this.taintWrapperSource.equals(rhs.taintWrapperSource))));
    }

}
