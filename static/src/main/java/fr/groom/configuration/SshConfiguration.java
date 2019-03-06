
package fr.groom.configuration;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * configuration used to scp apks between local and remote server
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "host",
    "port",
    "user",
    "pkeyPath",
    "pkeyPassphrase"
})
public class SshConfiguration implements Serializable
{

    /**
     * ssh server address
     * 
     */
    @JsonProperty("host")
    @JsonPropertyDescription("ssh server address")
    private String host = "gossple4.irisa.fr";
    /**
     * ssh port
     * 
     */
    @JsonProperty("port")
    @JsonPropertyDescription("ssh port")
    private int port = 22;
    /**
     * ssh username
     * 
     */
    @JsonProperty("user")
    @JsonPropertyDescription("ssh username")
    private String user = "lgitzing";
    /**
     * path to private key
     * 
     */
    @JsonProperty("pkeyPath")
    @JsonPropertyDescription("path to private key")
    private String pkeyPath = "./ssh/id_rsa_apk_management";
    /**
     * private key passphrase
     * 
     */
    @JsonProperty("pkeyPassphrase")
    @JsonPropertyDescription("private key passphrase")
    private String pkeyPassphrase = "louison";
    private final static long serialVersionUID = -1607890563069938623L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public SshConfiguration() {
    }

    /**
     * 
     * @param pkeyPath
     * @param pkeyPassphrase
     * @param port
     * @param host
     * @param user
     */
    public SshConfiguration(String host, int port, String user, String pkeyPath, String pkeyPassphrase) {
        super();
        this.host = host;
        this.port = port;
        this.user = user;
        this.pkeyPath = pkeyPath;
        this.pkeyPassphrase = pkeyPassphrase;
    }

    /**
     * ssh server address
     * 
     */
    @JsonProperty("host")
    public String getHost() {
        return host;
    }

    /**
     * ssh server address
     * 
     */
    @JsonProperty("host")
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * ssh port
     * 
     */
    @JsonProperty("port")
    public int getPort() {
        return port;
    }

    /**
     * ssh port
     * 
     */
    @JsonProperty("port")
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * ssh username
     * 
     */
    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    /**
     * ssh username
     * 
     */
    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * path to private key
     * 
     */
    @JsonProperty("pkeyPath")
    public String getPkeyPath() {
        return pkeyPath;
    }

    /**
     * path to private key
     * 
     */
    @JsonProperty("pkeyPath")
    public void setPkeyPath(String pkeyPath) {
        this.pkeyPath = pkeyPath;
    }

    /**
     * private key passphrase
     * 
     */
    @JsonProperty("pkeyPassphrase")
    public String getPkeyPassphrase() {
        return pkeyPassphrase;
    }

    /**
     * private key passphrase
     * 
     */
    @JsonProperty("pkeyPassphrase")
    public void setPkeyPassphrase(String pkeyPassphrase) {
        this.pkeyPassphrase = pkeyPassphrase;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SshConfiguration.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("host");
        sb.append('=');
        sb.append(((this.host == null)?"<null>":this.host));
        sb.append(',');
        sb.append("port");
        sb.append('=');
        sb.append(this.port);
        sb.append(',');
        sb.append("user");
        sb.append('=');
        sb.append(((this.user == null)?"<null>":this.user));
        sb.append(',');
        sb.append("pkeyPath");
        sb.append('=');
        sb.append(((this.pkeyPath == null)?"<null>":this.pkeyPath));
        sb.append(',');
        sb.append("pkeyPassphrase");
        sb.append('=');
        sb.append(((this.pkeyPassphrase == null)?"<null>":this.pkeyPassphrase));
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
        result = ((result* 31)+((this.host == null)? 0 :this.host.hashCode()));
        result = ((result* 31)+((this.pkeyPath == null)? 0 :this.pkeyPath.hashCode()));
        result = ((result* 31)+((this.pkeyPassphrase == null)? 0 :this.pkeyPassphrase.hashCode()));
        result = ((result* 31)+ this.port);
        result = ((result* 31)+((this.user == null)? 0 :this.user.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SshConfiguration) == false) {
            return false;
        }
        SshConfiguration rhs = ((SshConfiguration) other);
        return ((((((this.host == rhs.host)||((this.host!= null)&&this.host.equals(rhs.host)))&&((this.pkeyPath == rhs.pkeyPath)||((this.pkeyPath!= null)&&this.pkeyPath.equals(rhs.pkeyPath))))&&((this.pkeyPassphrase == rhs.pkeyPassphrase)||((this.pkeyPassphrase!= null)&&this.pkeyPassphrase.equals(rhs.pkeyPassphrase))))&&(this.port == rhs.port))&&((this.user == rhs.user)||((this.user!= null)&&this.user.equals(rhs.user))));
    }

}
