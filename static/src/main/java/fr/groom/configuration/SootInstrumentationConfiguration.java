
package fr.groom.configuration;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * soot instrumentation configuration
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "instrumentApkWithSoot",
    "groomPath"
})
public class SootInstrumentationConfiguration implements Serializable
{

    /**
     * inject log statement in apk byte code for dynamic analysis purpose
     * 
     */
    @JsonProperty("instrumentApkWithSoot")
    @JsonPropertyDescription("inject log statement in apk byte code for dynamic analysis purpose")
    private boolean instrumentApkWithSoot = false;
    /**
     * path to groom dex file, the helper used to hook methods and statements
     * 
     */
    @JsonProperty("groomPath")
    @JsonPropertyDescription("path to groom dex file, the helper used to hook methods and statements")
    private String groomPath = "required_files/Groom.dex";
    private final static long serialVersionUID = -6908020069566246697L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public SootInstrumentationConfiguration() {
    }

    /**
     * 
     * @param groomPath
     * @param instrumentApkWithSoot
     */
    public SootInstrumentationConfiguration(boolean instrumentApkWithSoot, String groomPath) {
        super();
        this.instrumentApkWithSoot = instrumentApkWithSoot;
        this.groomPath = groomPath;
    }

    /**
     * inject log statement in apk byte code for dynamic analysis purpose
     * 
     */
    @JsonProperty("instrumentApkWithSoot")
    public boolean isInstrumentApkWithSoot() {
        return instrumentApkWithSoot;
    }

    /**
     * inject log statement in apk byte code for dynamic analysis purpose
     * 
     */
    @JsonProperty("instrumentApkWithSoot")
    public void setInstrumentApkWithSoot(boolean instrumentApkWithSoot) {
        this.instrumentApkWithSoot = instrumentApkWithSoot;
    }

    /**
     * path to groom dex file, the helper used to hook methods and statements
     * 
     */
    @JsonProperty("groomPath")
    public String getGroomPath() {
        return groomPath;
    }

    /**
     * path to groom dex file, the helper used to hook methods and statements
     * 
     */
    @JsonProperty("groomPath")
    public void setGroomPath(String groomPath) {
        this.groomPath = groomPath;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SootInstrumentationConfiguration.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("instrumentApkWithSoot");
        sb.append('=');
        sb.append(this.instrumentApkWithSoot);
        sb.append(',');
        sb.append("groomPath");
        sb.append('=');
        sb.append(((this.groomPath == null)?"<null>":this.groomPath));
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
        result = ((result* 31)+((this.groomPath == null)? 0 :this.groomPath.hashCode()));
        result = ((result* 31)+(this.instrumentApkWithSoot? 1 : 0));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SootInstrumentationConfiguration) == false) {
            return false;
        }
        SootInstrumentationConfiguration rhs = ((SootInstrumentationConfiguration) other);
        return (((this.groomPath == rhs.groomPath)||((this.groomPath!= null)&&this.groomPath.equals(rhs.groomPath)))&&(this.instrumentApkWithSoot == rhs.instrumentApkWithSoot));
    }

}
