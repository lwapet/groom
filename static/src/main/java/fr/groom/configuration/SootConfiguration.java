
package fr.groom.configuration;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * soot configuration for the program
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "androidPlatforms",
    "outputDirectory"
})
public class SootConfiguration implements Serializable
{

    /**
     * path to the android platform folder
     * 
     */
    @JsonProperty("androidPlatforms")
    @JsonPropertyDescription("path to the android platform folder")
    private String androidPlatforms = "/Users/lgitzing/Development/work/android-platforms";
    /**
     * where to output soot instrumented apk
     * 
     */
    @JsonProperty("outputDirectory")
    @JsonPropertyDescription("where to output soot instrumented apk")
    private String outputDirectory = "./sootOutput";
    private final static long serialVersionUID = 9169432055127332276L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public SootConfiguration() {
    }

    /**
     * 
     * @param outputDirectory
     * @param androidPlatforms
     */
    public SootConfiguration(String androidPlatforms, String outputDirectory) {
        super();
        this.androidPlatforms = androidPlatforms;
        this.outputDirectory = outputDirectory;
    }

    /**
     * path to the android platform folder
     * 
     */
    @JsonProperty("androidPlatforms")
    public String getAndroidPlatforms() {
        return androidPlatforms;
    }

    /**
     * path to the android platform folder
     * 
     */
    @JsonProperty("androidPlatforms")
    public void setAndroidPlatforms(String androidPlatforms) {
        this.androidPlatforms = androidPlatforms;
    }

    /**
     * where to output soot instrumented apk
     * 
     */
    @JsonProperty("outputDirectory")
    public String getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * where to output soot instrumented apk
     * 
     */
    @JsonProperty("outputDirectory")
    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SootConfiguration.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("androidPlatforms");
        sb.append('=');
        sb.append(((this.androidPlatforms == null)?"<null>":this.androidPlatforms));
        sb.append(',');
        sb.append("outputDirectory");
        sb.append('=');
        sb.append(((this.outputDirectory == null)?"<null>":this.outputDirectory));
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
        result = ((result* 31)+((this.outputDirectory == null)? 0 :this.outputDirectory.hashCode()));
        result = ((result* 31)+((this.androidPlatforms == null)? 0 :this.androidPlatforms.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SootConfiguration) == false) {
            return false;
        }
        SootConfiguration rhs = ((SootConfiguration) other);
        return (((this.outputDirectory == rhs.outputDirectory)||((this.outputDirectory!= null)&&this.outputDirectory.equals(rhs.outputDirectory)))&&((this.androidPlatforms == rhs.androidPlatforms)||((this.androidPlatforms!= null)&&this.androidPlatforms.equals(rhs.androidPlatforms))));
    }

}
