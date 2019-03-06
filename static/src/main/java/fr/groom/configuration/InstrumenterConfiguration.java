
package fr.groom.configuration;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Apk Instrumenter schema
 * <p>
 * This schema describes available configurations for the program
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "targetApk",
    "description",
    "zipalignPath",
    "apksignerPath",
    "pathToKeystore",
    "keyPassword",
    "categorizedSourcesFile",
    "categorizedSinksFile",
    "performStaticAnalysis",
    "repackageApk",
    "instrumentedApkDirectory",
    "sootInstrumentationConfiguration",
    "fridaInstrumenterConfiguration",
    "staticAnalysisConfiguration",
    "sootConfiguration",
    "databaseConfiguration"
})
public class InstrumenterConfiguration implements Serializable
{

    /**
     * path to the target apk
     * 
     */
    @JsonProperty("targetApk")
    @JsonPropertyDescription("path to the target apk")
    private String targetApk = "";
    /**
     * optional description for the apk
     * 
     */
    @JsonProperty("description")
    @JsonPropertyDescription("optional description for the apk")
    private String description = "";
    /**
     * path to zipalign binary
     * 
     */
    @JsonProperty("zipalignPath")
    @JsonPropertyDescription("path to zipalign binary")
    private String zipalignPath = "/Users/lgitzing/Library/Android/sdk/build-tools/27.0.2/zipalign";
    /**
     * path to apksigner binary
     * 
     */
    @JsonProperty("apksignerPath")
    @JsonPropertyDescription("path to apksigner binary")
    private String apksignerPath = "/Users/lgitzing/Library/Android/sdk/build-tools/27.0.2/apksigner";
    /**
     * path to keystore (sign)
     * 
     */
    @JsonProperty("pathToKeystore")
    @JsonPropertyDescription("path to keystore (sign)")
    private String pathToKeystore = "/Users/lgitzing/.android/keystore";
    /**
     * sign key password
     * 
     */
    @JsonProperty("keyPassword")
    @JsonPropertyDescription("sign key password")
    private String keyPassword = "";
    /**
     * path to the categorized_sources.txt file
     * 
     */
    @JsonProperty("categorizedSourcesFile")
    @JsonPropertyDescription("path to the categorized_sources.txt file")
    private String categorizedSourcesFile = "required_files/categorized_sources.txt";
    /**
     * path to the categorized_sinks.txt file
     * 
     */
    @JsonProperty("categorizedSinksFile")
    @JsonPropertyDescription("path to the categorized_sinks.txt file")
    private String categorizedSinksFile = "required_files/categorized_sinks.txt";
    /**
     * Perform a global static analysis to extract static data
     * 
     */
    @JsonProperty("performStaticAnalysis")
    @JsonPropertyDescription("Perform a global static analysis to extract static data")
    private boolean performStaticAnalysis = true;
    /**
     * Repackage apk with soot
     * 
     */
    @JsonProperty("repackageApk")
    @JsonPropertyDescription("Repackage apk with soot")
    private boolean repackageApk = true;
    /**
     * repository to output instrumented apks
     * 
     */
    @JsonProperty("instrumentedApkDirectory")
    @JsonPropertyDescription("repository to output instrumented apks")
    private String instrumentedApkDirectory = "instrumented";
    /**
     * soot instrumentation configuration
     * 
     */
    @JsonProperty("sootInstrumentationConfiguration")
    @JsonPropertyDescription("soot instrumentation configuration")
    private SootInstrumentationConfiguration sootInstrumentationConfiguration;
    /**
     * frida configuration for the program
     * 
     */
    @JsonProperty("fridaInstrumenterConfiguration")
    @JsonPropertyDescription("frida configuration for the program")
    private FridaInstrumenterConfiguration fridaInstrumenterConfiguration;
    /**
     * parameters concerning dynamic analysis
     * 
     */
    @JsonProperty("staticAnalysisConfiguration")
    @JsonPropertyDescription("parameters concerning dynamic analysis")
    private StaticAnalysisConfiguration staticAnalysisConfiguration;
    /**
     * soot configuration for the program
     * 
     */
    @JsonProperty("sootConfiguration")
    @JsonPropertyDescription("soot configuration for the program")
    private SootConfiguration sootConfiguration;
    /**
     * database configuration for mongodb instance
     * 
     */
    @JsonProperty("databaseConfiguration")
    @JsonPropertyDescription("database configuration for mongodb instance")
    private DatabaseConfiguration databaseConfiguration;
    private final static long serialVersionUID = 1739061584179303344L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public InstrumenterConfiguration() {
    }

    /**
     * 
     * @param categorizedSinksFile
     * @param zipalignPath
     * @param databaseConfiguration
     * @param targetApk
     * @param description
     * @param instrumentedApkDirectory
     * @param performStaticAnalysis
     * @param pathToKeystore
     * @param sootInstrumentationConfiguration
     * @param fridaInstrumenterConfiguration
     * @param keyPassword
     * @param sootConfiguration
     * @param categorizedSourcesFile
     * @param apksignerPath
     * @param repackageApk
     * @param staticAnalysisConfiguration
     */
    public InstrumenterConfiguration(String targetApk, String description, String zipalignPath, String apksignerPath, String pathToKeystore, String keyPassword, String categorizedSourcesFile, String categorizedSinksFile, boolean performStaticAnalysis, boolean repackageApk, String instrumentedApkDirectory, SootInstrumentationConfiguration sootInstrumentationConfiguration, FridaInstrumenterConfiguration fridaInstrumenterConfiguration, StaticAnalysisConfiguration staticAnalysisConfiguration, SootConfiguration sootConfiguration, DatabaseConfiguration databaseConfiguration) {
        super();
        this.targetApk = targetApk;
        this.description = description;
        this.zipalignPath = zipalignPath;
        this.apksignerPath = apksignerPath;
        this.pathToKeystore = pathToKeystore;
        this.keyPassword = keyPassword;
        this.categorizedSourcesFile = categorizedSourcesFile;
        this.categorizedSinksFile = categorizedSinksFile;
        this.performStaticAnalysis = performStaticAnalysis;
        this.repackageApk = repackageApk;
        this.instrumentedApkDirectory = instrumentedApkDirectory;
        this.sootInstrumentationConfiguration = sootInstrumentationConfiguration;
        this.fridaInstrumenterConfiguration = fridaInstrumenterConfiguration;
        this.staticAnalysisConfiguration = staticAnalysisConfiguration;
        this.sootConfiguration = sootConfiguration;
        this.databaseConfiguration = databaseConfiguration;
    }

    /**
     * path to the target apk
     * 
     */
    @JsonProperty("targetApk")
    public String getTargetApk() {
        return targetApk;
    }

    /**
     * path to the target apk
     * 
     */
    @JsonProperty("targetApk")
    public void setTargetApk(String targetApk) {
        this.targetApk = targetApk;
    }

    /**
     * optional description for the apk
     * 
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * optional description for the apk
     * 
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * path to zipalign binary
     * 
     */
    @JsonProperty("zipalignPath")
    public String getZipalignPath() {
        return zipalignPath;
    }

    /**
     * path to zipalign binary
     * 
     */
    @JsonProperty("zipalignPath")
    public void setZipalignPath(String zipalignPath) {
        this.zipalignPath = zipalignPath;
    }

    /**
     * path to apksigner binary
     * 
     */
    @JsonProperty("apksignerPath")
    public String getApksignerPath() {
        return apksignerPath;
    }

    /**
     * path to apksigner binary
     * 
     */
    @JsonProperty("apksignerPath")
    public void setApksignerPath(String apksignerPath) {
        this.apksignerPath = apksignerPath;
    }

    /**
     * path to keystore (sign)
     * 
     */
    @JsonProperty("pathToKeystore")
    public String getPathToKeystore() {
        return pathToKeystore;
    }

    /**
     * path to keystore (sign)
     * 
     */
    @JsonProperty("pathToKeystore")
    public void setPathToKeystore(String pathToKeystore) {
        this.pathToKeystore = pathToKeystore;
    }

    /**
     * sign key password
     * 
     */
    @JsonProperty("keyPassword")
    public String getKeyPassword() {
        return keyPassword;
    }

    /**
     * sign key password
     * 
     */
    @JsonProperty("keyPassword")
    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
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

    /**
     * Perform a global static analysis to extract static data
     * 
     */
    @JsonProperty("performStaticAnalysis")
    public boolean isPerformStaticAnalysis() {
        return performStaticAnalysis;
    }

    /**
     * Perform a global static analysis to extract static data
     * 
     */
    @JsonProperty("performStaticAnalysis")
    public void setPerformStaticAnalysis(boolean performStaticAnalysis) {
        this.performStaticAnalysis = performStaticAnalysis;
    }

    /**
     * Repackage apk with soot
     * 
     */
    @JsonProperty("repackageApk")
    public boolean isRepackageApk() {
        return repackageApk;
    }

    /**
     * Repackage apk with soot
     * 
     */
    @JsonProperty("repackageApk")
    public void setRepackageApk(boolean repackageApk) {
        this.repackageApk = repackageApk;
    }

    /**
     * repository to output instrumented apks
     * 
     */
    @JsonProperty("instrumentedApkDirectory")
    public String getInstrumentedApkDirectory() {
        return instrumentedApkDirectory;
    }

    /**
     * repository to output instrumented apks
     * 
     */
    @JsonProperty("instrumentedApkDirectory")
    public void setInstrumentedApkDirectory(String instrumentedApkDirectory) {
        this.instrumentedApkDirectory = instrumentedApkDirectory;
    }

    /**
     * soot instrumentation configuration
     * 
     */
    @JsonProperty("sootInstrumentationConfiguration")
    public SootInstrumentationConfiguration getSootInstrumentationConfiguration() {
        return sootInstrumentationConfiguration;
    }

    /**
     * soot instrumentation configuration
     * 
     */
    @JsonProperty("sootInstrumentationConfiguration")
    public void setSootInstrumentationConfiguration(SootInstrumentationConfiguration sootInstrumentationConfiguration) {
        this.sootInstrumentationConfiguration = sootInstrumentationConfiguration;
    }

    /**
     * frida configuration for the program
     * 
     */
    @JsonProperty("fridaInstrumenterConfiguration")
    public FridaInstrumenterConfiguration getFridaInstrumenterConfiguration() {
        return fridaInstrumenterConfiguration;
    }

    /**
     * frida configuration for the program
     * 
     */
    @JsonProperty("fridaInstrumenterConfiguration")
    public void setFridaInstrumenterConfiguration(FridaInstrumenterConfiguration fridaInstrumenterConfiguration) {
        this.fridaInstrumenterConfiguration = fridaInstrumenterConfiguration;
    }

    /**
     * parameters concerning dynamic analysis
     * 
     */
    @JsonProperty("staticAnalysisConfiguration")
    public StaticAnalysisConfiguration getStaticAnalysisConfiguration() {
        return staticAnalysisConfiguration;
    }

    /**
     * parameters concerning dynamic analysis
     * 
     */
    @JsonProperty("staticAnalysisConfiguration")
    public void setStaticAnalysisConfiguration(StaticAnalysisConfiguration staticAnalysisConfiguration) {
        this.staticAnalysisConfiguration = staticAnalysisConfiguration;
    }

    /**
     * soot configuration for the program
     * 
     */
    @JsonProperty("sootConfiguration")
    public SootConfiguration getSootConfiguration() {
        return sootConfiguration;
    }

    /**
     * soot configuration for the program
     * 
     */
    @JsonProperty("sootConfiguration")
    public void setSootConfiguration(SootConfiguration sootConfiguration) {
        this.sootConfiguration = sootConfiguration;
    }

    /**
     * database configuration for mongodb instance
     * 
     */
    @JsonProperty("databaseConfiguration")
    public DatabaseConfiguration getDatabaseConfiguration() {
        return databaseConfiguration;
    }

    /**
     * database configuration for mongodb instance
     * 
     */
    @JsonProperty("databaseConfiguration")
    public void setDatabaseConfiguration(DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(InstrumenterConfiguration.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("targetApk");
        sb.append('=');
        sb.append(((this.targetApk == null)?"<null>":this.targetApk));
        sb.append(',');
        sb.append("description");
        sb.append('=');
        sb.append(((this.description == null)?"<null>":this.description));
        sb.append(',');
        sb.append("zipalignPath");
        sb.append('=');
        sb.append(((this.zipalignPath == null)?"<null>":this.zipalignPath));
        sb.append(',');
        sb.append("apksignerPath");
        sb.append('=');
        sb.append(((this.apksignerPath == null)?"<null>":this.apksignerPath));
        sb.append(',');
        sb.append("pathToKeystore");
        sb.append('=');
        sb.append(((this.pathToKeystore == null)?"<null>":this.pathToKeystore));
        sb.append(',');
        sb.append("keyPassword");
        sb.append('=');
        sb.append(((this.keyPassword == null)?"<null>":this.keyPassword));
        sb.append(',');
        sb.append("categorizedSourcesFile");
        sb.append('=');
        sb.append(((this.categorizedSourcesFile == null)?"<null>":this.categorizedSourcesFile));
        sb.append(',');
        sb.append("categorizedSinksFile");
        sb.append('=');
        sb.append(((this.categorizedSinksFile == null)?"<null>":this.categorizedSinksFile));
        sb.append(',');
        sb.append("performStaticAnalysis");
        sb.append('=');
        sb.append(this.performStaticAnalysis);
        sb.append(',');
        sb.append("repackageApk");
        sb.append('=');
        sb.append(this.repackageApk);
        sb.append(',');
        sb.append("instrumentedApkDirectory");
        sb.append('=');
        sb.append(((this.instrumentedApkDirectory == null)?"<null>":this.instrumentedApkDirectory));
        sb.append(',');
        sb.append("sootInstrumentationConfiguration");
        sb.append('=');
        sb.append(((this.sootInstrumentationConfiguration == null)?"<null>":this.sootInstrumentationConfiguration));
        sb.append(',');
        sb.append("fridaInstrumenterConfiguration");
        sb.append('=');
        sb.append(((this.fridaInstrumenterConfiguration == null)?"<null>":this.fridaInstrumenterConfiguration));
        sb.append(',');
        sb.append("staticAnalysisConfiguration");
        sb.append('=');
        sb.append(((this.staticAnalysisConfiguration == null)?"<null>":this.staticAnalysisConfiguration));
        sb.append(',');
        sb.append("sootConfiguration");
        sb.append('=');
        sb.append(((this.sootConfiguration == null)?"<null>":this.sootConfiguration));
        sb.append(',');
        sb.append("databaseConfiguration");
        sb.append('=');
        sb.append(((this.databaseConfiguration == null)?"<null>":this.databaseConfiguration));
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
        result = ((result* 31)+((this.categorizedSinksFile == null)? 0 :this.categorizedSinksFile.hashCode()));
        result = ((result* 31)+((this.zipalignPath == null)? 0 :this.zipalignPath.hashCode()));
        result = ((result* 31)+((this.databaseConfiguration == null)? 0 :this.databaseConfiguration.hashCode()));
        result = ((result* 31)+((this.targetApk == null)? 0 :this.targetApk.hashCode()));
        result = ((result* 31)+((this.description == null)? 0 :this.description.hashCode()));
        result = ((result* 31)+((this.instrumentedApkDirectory == null)? 0 :this.instrumentedApkDirectory.hashCode()));
        result = ((result* 31)+(this.performStaticAnalysis? 1 : 0));
        result = ((result* 31)+((this.pathToKeystore == null)? 0 :this.pathToKeystore.hashCode()));
        result = ((result* 31)+((this.sootInstrumentationConfiguration == null)? 0 :this.sootInstrumentationConfiguration.hashCode()));
        result = ((result* 31)+((this.fridaInstrumenterConfiguration == null)? 0 :this.fridaInstrumenterConfiguration.hashCode()));
        result = ((result* 31)+((this.keyPassword == null)? 0 :this.keyPassword.hashCode()));
        result = ((result* 31)+((this.sootConfiguration == null)? 0 :this.sootConfiguration.hashCode()));
        result = ((result* 31)+((this.categorizedSourcesFile == null)? 0 :this.categorizedSourcesFile.hashCode()));
        result = ((result* 31)+((this.apksignerPath == null)? 0 :this.apksignerPath.hashCode()));
        result = ((result* 31)+(this.repackageApk? 1 : 0));
        result = ((result* 31)+((this.staticAnalysisConfiguration == null)? 0 :this.staticAnalysisConfiguration.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof InstrumenterConfiguration) == false) {
            return false;
        }
        InstrumenterConfiguration rhs = ((InstrumenterConfiguration) other);
        return (((((((((((((((((this.categorizedSinksFile == rhs.categorizedSinksFile)||((this.categorizedSinksFile!= null)&&this.categorizedSinksFile.equals(rhs.categorizedSinksFile)))&&((this.zipalignPath == rhs.zipalignPath)||((this.zipalignPath!= null)&&this.zipalignPath.equals(rhs.zipalignPath))))&&((this.databaseConfiguration == rhs.databaseConfiguration)||((this.databaseConfiguration!= null)&&this.databaseConfiguration.equals(rhs.databaseConfiguration))))&&((this.targetApk == rhs.targetApk)||((this.targetApk!= null)&&this.targetApk.equals(rhs.targetApk))))&&((this.description == rhs.description)||((this.description!= null)&&this.description.equals(rhs.description))))&&((this.instrumentedApkDirectory == rhs.instrumentedApkDirectory)||((this.instrumentedApkDirectory!= null)&&this.instrumentedApkDirectory.equals(rhs.instrumentedApkDirectory))))&&(this.performStaticAnalysis == rhs.performStaticAnalysis))&&((this.pathToKeystore == rhs.pathToKeystore)||((this.pathToKeystore!= null)&&this.pathToKeystore.equals(rhs.pathToKeystore))))&&((this.sootInstrumentationConfiguration == rhs.sootInstrumentationConfiguration)||((this.sootInstrumentationConfiguration!= null)&&this.sootInstrumentationConfiguration.equals(rhs.sootInstrumentationConfiguration))))&&((this.fridaInstrumenterConfiguration == rhs.fridaInstrumenterConfiguration)||((this.fridaInstrumenterConfiguration!= null)&&this.fridaInstrumenterConfiguration.equals(rhs.fridaInstrumenterConfiguration))))&&((this.keyPassword == rhs.keyPassword)||((this.keyPassword!= null)&&this.keyPassword.equals(rhs.keyPassword))))&&((this.sootConfiguration == rhs.sootConfiguration)||((this.sootConfiguration!= null)&&this.sootConfiguration.equals(rhs.sootConfiguration))))&&((this.categorizedSourcesFile == rhs.categorizedSourcesFile)||((this.categorizedSourcesFile!= null)&&this.categorizedSourcesFile.equals(rhs.categorizedSourcesFile))))&&((this.apksignerPath == rhs.apksignerPath)||((this.apksignerPath!= null)&&this.apksignerPath.equals(rhs.apksignerPath))))&&(this.repackageApk == rhs.repackageApk))&&((this.staticAnalysisConfiguration == rhs.staticAnalysisConfiguration)||((this.staticAnalysisConfiguration!= null)&&this.staticAnalysisConfiguration.equals(rhs.staticAnalysisConfiguration))));
    }

}
