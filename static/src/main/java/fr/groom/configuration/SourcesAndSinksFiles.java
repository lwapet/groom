
package fr.groom.configuration;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * how to handle sources and sinks
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "categorizedSourcesAndSinks",
    "sourceAndSinksTxtFile",
    "categorizedSourcesFile",
    "categorizedSinksFile"
})
public class SourcesAndSinksFiles implements Serializable
{

    /**
     * Does FlowDroid should use categorized sources and sinks
     * 
     */
    @JsonProperty("categorizedSourcesAndSinks")
    @JsonPropertyDescription("Does FlowDroid should use categorized sources and sinks")
    private boolean categorizedSourcesAndSinks = true;
    /**
     * path to the SourcesAndSinks.txt file
     * 
     */
    @JsonProperty("sourceAndSinksTxtFile")
    @JsonPropertyDescription("path to the SourcesAndSinks.txt file")
    private String sourceAndSinksTxtFile = "required_files/SourcesAndSinks.txt";
    /**
     * path to the categorized_sources.txt file
     * 
     */
    @JsonProperty("categorizedSourcesFile")
    @JsonPropertyDescription("path to the categorized_sources.txt file")
    private String categorizedSourcesFile = "required_files/catSources_Short.txt";
    /**
     * path to the categorized_sinks.txt file
     * 
     */
    @JsonProperty("categorizedSinksFile")
    @JsonPropertyDescription("path to the categorized_sinks.txt file")
    private String categorizedSinksFile = "required_files/catSinks_Short.txt";
    private final static long serialVersionUID = -576706607436197397L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public SourcesAndSinksFiles() {
    }

    /**
     * 
     * @param categorizedSinksFile
     * @param categorizedSourcesFile
     * @param sourceAndSinksTxtFile
     * @param categorizedSourcesAndSinks
     */
    public SourcesAndSinksFiles(boolean categorizedSourcesAndSinks, String sourceAndSinksTxtFile, String categorizedSourcesFile, String categorizedSinksFile) {
        super();
        this.categorizedSourcesAndSinks = categorizedSourcesAndSinks;
        this.sourceAndSinksTxtFile = sourceAndSinksTxtFile;
        this.categorizedSourcesFile = categorizedSourcesFile;
        this.categorizedSinksFile = categorizedSinksFile;
    }

    /**
     * Does FlowDroid should use categorized sources and sinks
     * 
     */
    @JsonProperty("categorizedSourcesAndSinks")
    public boolean isCategorizedSourcesAndSinks() {
        return categorizedSourcesAndSinks;
    }

    /**
     * Does FlowDroid should use categorized sources and sinks
     * 
     */
    @JsonProperty("categorizedSourcesAndSinks")
    public void setCategorizedSourcesAndSinks(boolean categorizedSourcesAndSinks) {
        this.categorizedSourcesAndSinks = categorizedSourcesAndSinks;
    }

    /**
     * path to the SourcesAndSinks.txt file
     * 
     */
    @JsonProperty("sourceAndSinksTxtFile")
    public String getSourceAndSinksTxtFile() {
        return sourceAndSinksTxtFile;
    }

    /**
     * path to the SourcesAndSinks.txt file
     * 
     */
    @JsonProperty("sourceAndSinksTxtFile")
    public void setSourceAndSinksTxtFile(String sourceAndSinksTxtFile) {
        this.sourceAndSinksTxtFile = sourceAndSinksTxtFile;
    }

    /**
     * path to the categorized_sources.txt file
     * 
     */
    @JsonProperty("categorizedSourcesFile")
    public String getCategorizedSourcesFile() {
        return categorizedSourcesFile;
    }

    /**
     * path to the categorized_sources.txt file
     * 
     */
    @JsonProperty("categorizedSourcesFile")
    public void setCategorizedSourcesFile(String categorizedSourcesFile) {
        this.categorizedSourcesFile = categorizedSourcesFile;
    }

    /**
     * path to the categorized_sinks.txt file
     * 
     */
    @JsonProperty("categorizedSinksFile")
    public String getCategorizedSinksFile() {
        return categorizedSinksFile;
    }

    /**
     * path to the categorized_sinks.txt file
     * 
     */
    @JsonProperty("categorizedSinksFile")
    public void setCategorizedSinksFile(String categorizedSinksFile) {
        this.categorizedSinksFile = categorizedSinksFile;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SourcesAndSinksFiles.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("categorizedSourcesAndSinks");
        sb.append('=');
        sb.append(this.categorizedSourcesAndSinks);
        sb.append(',');
        sb.append("sourceAndSinksTxtFile");
        sb.append('=');
        sb.append(((this.sourceAndSinksTxtFile == null)?"<null>":this.sourceAndSinksTxtFile));
        sb.append(',');
        sb.append("categorizedSourcesFile");
        sb.append('=');
        sb.append(((this.categorizedSourcesFile == null)?"<null>":this.categorizedSourcesFile));
        sb.append(',');
        sb.append("categorizedSinksFile");
        sb.append('=');
        sb.append(((this.categorizedSinksFile == null)?"<null>":this.categorizedSinksFile));
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
        result = ((result* 31)+((this.categorizedSourcesFile == null)? 0 :this.categorizedSourcesFile.hashCode()));
        result = ((result* 31)+((this.categorizedSinksFile == null)? 0 :this.categorizedSinksFile.hashCode()));
        result = ((result* 31)+((this.sourceAndSinksTxtFile == null)? 0 :this.sourceAndSinksTxtFile.hashCode()));
        result = ((result* 31)+(this.categorizedSourcesAndSinks? 1 : 0));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SourcesAndSinksFiles) == false) {
            return false;
        }
        SourcesAndSinksFiles rhs = ((SourcesAndSinksFiles) other);
        return (((((this.categorizedSourcesFile == rhs.categorizedSourcesFile)||((this.categorizedSourcesFile!= null)&&this.categorizedSourcesFile.equals(rhs.categorizedSourcesFile)))&&((this.categorizedSinksFile == rhs.categorizedSinksFile)||((this.categorizedSinksFile!= null)&&this.categorizedSinksFile.equals(rhs.categorizedSinksFile))))&&((this.sourceAndSinksTxtFile == rhs.sourceAndSinksTxtFile)||((this.sourceAndSinksTxtFile!= null)&&this.sourceAndSinksTxtFile.equals(rhs.sourceAndSinksTxtFile))))&&(this.categorizedSourcesAndSinks == rhs.categorizedSourcesAndSinks));
    }

}
