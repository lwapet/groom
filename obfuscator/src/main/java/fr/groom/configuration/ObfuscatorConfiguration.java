
package fr.groom.configuration;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * Crawler schema
 * <p>
 * This schema describes available configurations for the program
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "androidPlatforms",
    "dex2jarPath",
    "dxPath",
    "zipCommandPath",
    "apksignerPath",
    "andResJarPath",
    "pathToKeystore",
    "keyPassword",
    "proguardConfigPath",
    "useEncryption",
    "applyProguard"
})
public class ObfuscatorConfiguration {

    /**
     * path to android platforms
     * 
     */
    @JsonProperty("androidPlatforms")
    @JsonPropertyDescription("path to android platforms")
    private String androidPlatforms = "/Users/lgitzing/Library/Android/sdk/platforms";
    /**
     * path to dex2jar script
     * 
     */
    @JsonProperty("dex2jarPath")
    @JsonPropertyDescription("path to dex2jar script")
    private String dex2jarPath = "/Users/lgitzing/Development/work/dex-tools/d2j-dex2jar.sh";
    /**
     * path to dx script
     * 
     */
    @JsonProperty("dxPath")
    @JsonPropertyDescription("path to dx script")
    private String dxPath = "/Users/lgitzing/Library/Android/sdk/build-tools/28.0.2/dx";
    /**
     * path to zip command
     * 
     */
    @JsonProperty("zipCommandPath")
    @JsonPropertyDescription("path to zip command")
    private String zipCommandPath = "/usr/bin/zip";
    /**
     * path to apksigner binary
     * 
     */
    @JsonProperty("apksignerPath")
    @JsonPropertyDescription("path to apksigner binary")
    private String apksignerPath = "/Users/lgitzing/Library/Android/sdk/build-tools/27.0.2/apksigner";
    /**
     * path to AndResGuard binary
     * 
     */
    @JsonProperty("andResJarPath")
    @JsonPropertyDescription("path to AndResGuard binary")
    private String andResJarPath = "/Users/lgitzing/Development/work/Groom/obfuscator/AndResGuard-cli-1.2.15.jar";
    /**
     * path to keystore (sign)
     * 
     */
    @JsonProperty("pathToKeystore")
    @JsonPropertyDescription("path to keystore (sign)")
    private String pathToKeystore = "/Users/lgitzing/.android/master_keystore";
    /**
     * sign key password
     * 
     */
    @JsonProperty("keyPassword")
    @JsonPropertyDescription("sign key password")
    private String keyPassword = "";
    /**
     * path to proguard configuration
     * 
     */
    @JsonProperty("proguardConfigPath")
    @JsonPropertyDescription("path to proguard configuration")
    private String proguardConfigPath = "./test_original.pro";
    /**
     * whether to use string encryption in obfuscation or not
     * 
     */
    @JsonProperty("useEncryption")
    @JsonPropertyDescription("whether to use string encryption in obfuscation or not")
    private Boolean useEncryption = true;
    /**
     * execute proguard on given apk
     * 
     */
    @JsonProperty("applyProguard")
    @JsonPropertyDescription("execute proguard on given apk")
    private Boolean applyProguard = true;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * path to android platforms
     * 
     */
    @JsonProperty("androidPlatforms")
    public String getAndroidPlatforms() {
        return androidPlatforms;
    }

    /**
     * path to android platforms
     * 
     */
    @JsonProperty("androidPlatforms")
    public void setAndroidPlatforms(String androidPlatforms) {
        this.androidPlatforms = androidPlatforms;
    }

    /**
     * path to dex2jar script
     * 
     */
    @JsonProperty("dex2jarPath")
    public String getDex2jarPath() {
        return dex2jarPath;
    }

    /**
     * path to dex2jar script
     * 
     */
    @JsonProperty("dex2jarPath")
    public void setDex2jarPath(String dex2jarPath) {
        this.dex2jarPath = dex2jarPath;
    }

    /**
     * path to dx script
     * 
     */
    @JsonProperty("dxPath")
    public String getDxPath() {
        return dxPath;
    }

    /**
     * path to dx script
     * 
     */
    @JsonProperty("dxPath")
    public void setDxPath(String dxPath) {
        this.dxPath = dxPath;
    }

    /**
     * path to zip command
     * 
     */
    @JsonProperty("zipCommandPath")
    public String getZipCommandPath() {
        return zipCommandPath;
    }

    /**
     * path to zip command
     * 
     */
    @JsonProperty("zipCommandPath")
    public void setZipCommandPath(String zipCommandPath) {
        this.zipCommandPath = zipCommandPath;
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
     * path to AndResGuard binary
     * 
     */
    @JsonProperty("andResJarPath")
    public String getAndResJarPath() {
        return andResJarPath;
    }

    /**
     * path to AndResGuard binary
     * 
     */
    @JsonProperty("andResJarPath")
    public void setAndResJarPath(String andResJarPath) {
        this.andResJarPath = andResJarPath;
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
     * path to proguard configuration
     * 
     */
    @JsonProperty("proguardConfigPath")
    public String getProguardConfigPath() {
        return proguardConfigPath;
    }

    /**
     * path to proguard configuration
     * 
     */
    @JsonProperty("proguardConfigPath")
    public void setProguardConfigPath(String proguardConfigPath) {
        this.proguardConfigPath = proguardConfigPath;
    }

    /**
     * whether to use string encryption in obfuscation or not
     * 
     */
    @JsonProperty("useEncryption")
    public Boolean getUseEncryption() {
        return useEncryption;
    }

    /**
     * whether to use string encryption in obfuscation or not
     * 
     */
    @JsonProperty("useEncryption")
    public void setUseEncryption(Boolean useEncryption) {
        this.useEncryption = useEncryption;
    }

    /**
     * execute proguard on given apk
     * 
     */
    @JsonProperty("applyProguard")
    public Boolean getApplyProguard() {
        return applyProguard;
    }

    /**
     * execute proguard on given apk
     * 
     */
    @JsonProperty("applyProguard")
    public void setApplyProguard(Boolean applyProguard) {
        this.applyProguard = applyProguard;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ObfuscatorConfiguration.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("androidPlatforms");
        sb.append('=');
        sb.append(((this.androidPlatforms == null)?"<null>":this.androidPlatforms));
        sb.append(',');
        sb.append("dex2jarPath");
        sb.append('=');
        sb.append(((this.dex2jarPath == null)?"<null>":this.dex2jarPath));
        sb.append(',');
        sb.append("dxPath");
        sb.append('=');
        sb.append(((this.dxPath == null)?"<null>":this.dxPath));
        sb.append(',');
        sb.append("zipCommandPath");
        sb.append('=');
        sb.append(((this.zipCommandPath == null)?"<null>":this.zipCommandPath));
        sb.append(',');
        sb.append("apksignerPath");
        sb.append('=');
        sb.append(((this.apksignerPath == null)?"<null>":this.apksignerPath));
        sb.append(',');
        sb.append("andResJarPath");
        sb.append('=');
        sb.append(((this.andResJarPath == null)?"<null>":this.andResJarPath));
        sb.append(',');
        sb.append("pathToKeystore");
        sb.append('=');
        sb.append(((this.pathToKeystore == null)?"<null>":this.pathToKeystore));
        sb.append(',');
        sb.append("keyPassword");
        sb.append('=');
        sb.append(((this.keyPassword == null)?"<null>":this.keyPassword));
        sb.append(',');
        sb.append("proguardConfigPath");
        sb.append('=');
        sb.append(((this.proguardConfigPath == null)?"<null>":this.proguardConfigPath));
        sb.append(',');
        sb.append("useEncryption");
        sb.append('=');
        sb.append(((this.useEncryption == null)?"<null>":this.useEncryption));
        sb.append(',');
        sb.append("applyProguard");
        sb.append('=');
        sb.append(((this.applyProguard == null)?"<null>":this.applyProguard));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
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
        result = ((result* 31)+((this.dex2jarPath == null)? 0 :this.dex2jarPath.hashCode()));
        result = ((result* 31)+((this.andResJarPath == null)? 0 :this.andResJarPath.hashCode()));
        result = ((result* 31)+((this.proguardConfigPath == null)? 0 :this.proguardConfigPath.hashCode()));
        result = ((result* 31)+((this.pathToKeystore == null)? 0 :this.pathToKeystore.hashCode()));
        result = ((result* 31)+((this.keyPassword == null)? 0 :this.keyPassword.hashCode()));
        result = ((result* 31)+((this.applyProguard == null)? 0 :this.applyProguard.hashCode()));
        result = ((result* 31)+((this.useEncryption == null)? 0 :this.useEncryption.hashCode()));
        result = ((result* 31)+((this.zipCommandPath == null)? 0 :this.zipCommandPath.hashCode()));
        result = ((result* 31)+((this.apksignerPath == null)? 0 :this.apksignerPath.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        result = ((result* 31)+((this.dxPath == null)? 0 :this.dxPath.hashCode()));
        result = ((result* 31)+((this.androidPlatforms == null)? 0 :this.androidPlatforms.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ObfuscatorConfiguration) == false) {
            return false;
        }
        ObfuscatorConfiguration rhs = ((ObfuscatorConfiguration) other);
        return (((((((((((((this.dex2jarPath == rhs.dex2jarPath)||((this.dex2jarPath!= null)&&this.dex2jarPath.equals(rhs.dex2jarPath)))&&((this.andResJarPath == rhs.andResJarPath)||((this.andResJarPath!= null)&&this.andResJarPath.equals(rhs.andResJarPath))))&&((this.proguardConfigPath == rhs.proguardConfigPath)||((this.proguardConfigPath!= null)&&this.proguardConfigPath.equals(rhs.proguardConfigPath))))&&((this.pathToKeystore == rhs.pathToKeystore)||((this.pathToKeystore!= null)&&this.pathToKeystore.equals(rhs.pathToKeystore))))&&((this.keyPassword == rhs.keyPassword)||((this.keyPassword!= null)&&this.keyPassword.equals(rhs.keyPassword))))&&((this.applyProguard == rhs.applyProguard)||((this.applyProguard!= null)&&this.applyProguard.equals(rhs.applyProguard))))&&((this.useEncryption == rhs.useEncryption)||((this.useEncryption!= null)&&this.useEncryption.equals(rhs.useEncryption))))&&((this.zipCommandPath == rhs.zipCommandPath)||((this.zipCommandPath!= null)&&this.zipCommandPath.equals(rhs.zipCommandPath))))&&((this.apksignerPath == rhs.apksignerPath)||((this.apksignerPath!= null)&&this.apksignerPath.equals(rhs.apksignerPath))))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))))&&((this.dxPath == rhs.dxPath)||((this.dxPath!= null)&&this.dxPath.equals(rhs.dxPath))))&&((this.androidPlatforms == rhs.androidPlatforms)||((this.androidPlatforms!= null)&&this.androidPlatforms.equals(rhs.androidPlatforms))));
    }

}
