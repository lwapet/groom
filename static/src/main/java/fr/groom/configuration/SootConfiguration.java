
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
    "androidPlatforms"
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
    private final static long serialVersionUID = 7926547952987226078L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public SootConfiguration() {
    }

    /**
     * 
     * @param androidPlatforms
     */
    public SootConfiguration(String androidPlatforms) {
        super();
        this.androidPlatforms = androidPlatforms;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SootConfiguration.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("androidPlatforms");
        sb.append('=');
        sb.append(((this.androidPlatforms == null)?"<null>":this.androidPlatforms));
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
        return ((this.androidPlatforms == rhs.androidPlatforms)||((this.androidPlatforms!= null)&&this.androidPlatforms.equals(rhs.androidPlatforms)));
    }

}
