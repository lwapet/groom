
package fr.groom.configuration;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * database configuration for mongodb instance
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "url",
    "port",
    "authenticationConfiguration"
})
public class DatabaseConfiguration implements Serializable
{

    /**
     * name of the database
     * (Required)
     * 
     */
    @JsonProperty("name")
    @JsonPropertyDescription("name of the database")
    private String name = "dynamic";
    /**
     * url of the database, e.g localhost
     * (Required)
     * 
     */
    @JsonProperty("url")
    @JsonPropertyDescription("url of the database, e.g localhost")
    private String url = "localhost";
    /**
     * port on which the mongodb instance runs
     * (Required)
     * 
     */
    @JsonProperty("port")
    @JsonPropertyDescription("port on which the mongodb instance runs")
    private int port = 27017;
    /**
     * required authentication options
     * 
     */
    @JsonProperty("authenticationConfiguration")
    @JsonPropertyDescription("required authentication options")
    private AuthenticationConfiguration authenticationConfiguration;
    private final static long serialVersionUID = -90560545942467868L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public DatabaseConfiguration() {
    }

    /**
     * 
     * @param port
     * @param name
     * @param url
     * @param authenticationConfiguration
     */
    public DatabaseConfiguration(String name, String url, int port, AuthenticationConfiguration authenticationConfiguration) {
        super();
        this.name = name;
        this.url = url;
        this.port = port;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    /**
     * name of the database
     * (Required)
     * 
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * name of the database
     * (Required)
     * 
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     * url of the database, e.g localhost
     * (Required)
     * 
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     * url of the database, e.g localhost
     * (Required)
     * 
     */
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * port on which the mongodb instance runs
     * (Required)
     * 
     */
    @JsonProperty("port")
    public int getPort() {
        return port;
    }

    /**
     * port on which the mongodb instance runs
     * (Required)
     * 
     */
    @JsonProperty("port")
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * required authentication options
     * 
     */
    @JsonProperty("authenticationConfiguration")
    public AuthenticationConfiguration getAuthenticationConfiguration() {
        return authenticationConfiguration;
    }

    /**
     * required authentication options
     * 
     */
    @JsonProperty("authenticationConfiguration")
    public void setAuthenticationConfiguration(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(DatabaseConfiguration.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("name");
        sb.append('=');
        sb.append(((this.name == null)?"<null>":this.name));
        sb.append(',');
        sb.append("url");
        sb.append('=');
        sb.append(((this.url == null)?"<null>":this.url));
        sb.append(',');
        sb.append("port");
        sb.append('=');
        sb.append(this.port);
        sb.append(',');
        sb.append("authenticationConfiguration");
        sb.append('=');
        sb.append(((this.authenticationConfiguration == null)?"<null>":this.authenticationConfiguration));
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
        result = ((result* 31)+((this.name == null)? 0 :this.name.hashCode()));
        result = ((result* 31)+ this.port);
        result = ((result* 31)+((this.url == null)? 0 :this.url.hashCode()));
        result = ((result* 31)+((this.authenticationConfiguration == null)? 0 :this.authenticationConfiguration.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof DatabaseConfiguration) == false) {
            return false;
        }
        DatabaseConfiguration rhs = ((DatabaseConfiguration) other);
        return (((((this.name == rhs.name)||((this.name!= null)&&this.name.equals(rhs.name)))&&(this.port == rhs.port))&&((this.url == rhs.url)||((this.url!= null)&&this.url.equals(rhs.url))))&&((this.authenticationConfiguration == rhs.authenticationConfiguration)||((this.authenticationConfiguration!= null)&&this.authenticationConfiguration.equals(rhs.authenticationConfiguration))));
    }

}
