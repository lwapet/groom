
package fr.groom.configuration;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * frida configuration for the program
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "instrumentApkWithFrida",
    "fridaLibZipFile",
    "hookActivities",
    "hookReceivers",
    "fridaSoFilesName"
})
public class FridaInstrumenterConfiguration implements Serializable
{

    /**
     * Does the apk needs to be instrumented
     * 
     */
    @JsonProperty("instrumentApkWithFrida")
    @JsonPropertyDescription("Does the apk needs to be instrumented")
    private boolean instrumentApkWithFrida = true;
    /**
     * path to the zip containing frida .so files
     * 
     */
    @JsonProperty("fridaLibZipFile")
    @JsonPropertyDescription("path to the zip containing frida .so files")
    private String fridaLibZipFile = "required_files/lib.zip";
    /**
     * Add Frida statement to each app activity
     * 
     */
    @JsonProperty("hookActivities")
    @JsonPropertyDescription("Add Frida statement to each app activity")
    private boolean hookActivities = true;
    /**
     * Add Frida statement to each app receiver
     * 
     */
    @JsonProperty("hookReceivers")
    @JsonPropertyDescription("Add Frida statement to each app receiver")
    private boolean hookReceivers = true;
    /**
     * filename given to frida .so files
     * 
     */
    @JsonProperty("fridaSoFilesName")
    @JsonPropertyDescription("filename given to frida .so files")
    private String fridaSoFilesName = "frida-gadget";
    private final static long serialVersionUID = 3495786596482353374L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public FridaInstrumenterConfiguration() {
    }

    /**
     * 
     * @param instrumentApkWithFrida
     * @param hookReceivers
     * @param fridaSoFilesName
     * @param hookActivities
     * @param fridaLibZipFile
     */
    public FridaInstrumenterConfiguration(boolean instrumentApkWithFrida, String fridaLibZipFile, boolean hookActivities, boolean hookReceivers, String fridaSoFilesName) {
        super();
        this.instrumentApkWithFrida = instrumentApkWithFrida;
        this.fridaLibZipFile = fridaLibZipFile;
        this.hookActivities = hookActivities;
        this.hookReceivers = hookReceivers;
        this.fridaSoFilesName = fridaSoFilesName;
    }

    /**
     * Does the apk needs to be instrumented
     * 
     */
    @JsonProperty("instrumentApkWithFrida")
    public boolean isInstrumentApkWithFrida() {
        return instrumentApkWithFrida;
    }

    /**
     * Does the apk needs to be instrumented
     * 
     */
    @JsonProperty("instrumentApkWithFrida")
    public void setInstrumentApkWithFrida(boolean instrumentApkWithFrida) {
        this.instrumentApkWithFrida = instrumentApkWithFrida;
    }

    /**
     * path to the zip containing frida .so files
     * 
     */
    @JsonProperty("fridaLibZipFile")
    public String getFridaLibZipFile() {
        return fridaLibZipFile;
    }

    /**
     * path to the zip containing frida .so files
     * 
     */
    @JsonProperty("fridaLibZipFile")
    public void setFridaLibZipFile(String fridaLibZipFile) {
        this.fridaLibZipFile = fridaLibZipFile;
    }

    /**
     * Add Frida statement to each app activity
     * 
     */
    @JsonProperty("hookActivities")
    public boolean isHookActivities() {
        return hookActivities;
    }

    /**
     * Add Frida statement to each app activity
     * 
     */
    @JsonProperty("hookActivities")
    public void setHookActivities(boolean hookActivities) {
        this.hookActivities = hookActivities;
    }

    /**
     * Add Frida statement to each app receiver
     * 
     */
    @JsonProperty("hookReceivers")
    public boolean isHookReceivers() {
        return hookReceivers;
    }

    /**
     * Add Frida statement to each app receiver
     * 
     */
    @JsonProperty("hookReceivers")
    public void setHookReceivers(boolean hookReceivers) {
        this.hookReceivers = hookReceivers;
    }

    /**
     * filename given to frida .so files
     * 
     */
    @JsonProperty("fridaSoFilesName")
    public String getFridaSoFilesName() {
        return fridaSoFilesName;
    }

    /**
     * filename given to frida .so files
     * 
     */
    @JsonProperty("fridaSoFilesName")
    public void setFridaSoFilesName(String fridaSoFilesName) {
        this.fridaSoFilesName = fridaSoFilesName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(FridaInstrumenterConfiguration.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("instrumentApkWithFrida");
        sb.append('=');
        sb.append(this.instrumentApkWithFrida);
        sb.append(',');
        sb.append("fridaLibZipFile");
        sb.append('=');
        sb.append(((this.fridaLibZipFile == null)?"<null>":this.fridaLibZipFile));
        sb.append(',');
        sb.append("hookActivities");
        sb.append('=');
        sb.append(this.hookActivities);
        sb.append(',');
        sb.append("hookReceivers");
        sb.append('=');
        sb.append(this.hookReceivers);
        sb.append(',');
        sb.append("fridaSoFilesName");
        sb.append('=');
        sb.append(((this.fridaSoFilesName == null)?"<null>":this.fridaSoFilesName));
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
        result = ((result* 31)+(this.instrumentApkWithFrida? 1 : 0));
        result = ((result* 31)+(this.hookReceivers? 1 : 0));
        result = ((result* 31)+((this.fridaSoFilesName == null)? 0 :this.fridaSoFilesName.hashCode()));
        result = ((result* 31)+(this.hookActivities? 1 : 0));
        result = ((result* 31)+((this.fridaLibZipFile == null)? 0 :this.fridaLibZipFile.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof FridaInstrumenterConfiguration) == false) {
            return false;
        }
        FridaInstrumenterConfiguration rhs = ((FridaInstrumenterConfiguration) other);
        return (((((this.instrumentApkWithFrida == rhs.instrumentApkWithFrida)&&(this.hookReceivers == rhs.hookReceivers))&&((this.fridaSoFilesName == rhs.fridaSoFilesName)||((this.fridaSoFilesName!= null)&&this.fridaSoFilesName.equals(rhs.fridaSoFilesName))))&&(this.hookActivities == rhs.hookActivities))&&((this.fridaLibZipFile == rhs.fridaLibZipFile)||((this.fridaLibZipFile!= null)&&this.fridaLibZipFile.equals(rhs.fridaLibZipFile))));
    }

}
