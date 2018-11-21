
package fr.groom.configuration;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * required authentication options
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "performAuthentication",
    "authSourceDatabaseName",
    "username",
    "password"
})
public class AuthenticationConfiguration implements Serializable
{

    /**
     * whether the program should perform authentication or not
     * (Required)
     * 
     */
    @JsonProperty("performAuthentication")
    @JsonPropertyDescription("whether the program should perform authentication or not")
    private boolean performAuthentication = false;
    /**
     * name of the authentication database
     * 
     */
    @JsonProperty("authSourceDatabaseName")
    @JsonPropertyDescription("name of the authentication database")
    private String authSourceDatabaseName = "admin";
    /**
     * username to authenticate to the database
     * (Required)
     * 
     */
    @JsonProperty("username")
    @JsonPropertyDescription("username to authenticate to the database")
    private String username = "lgitzing";
    /**
     * user password for authentication
     * (Required)
     * 
     */
    @JsonProperty("password")
    @JsonPropertyDescription("user password for authentication")
    private String password = "tout_petit_poney";
    private final static long serialVersionUID = -5524459326108765333L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public AuthenticationConfiguration() {
    }

    /**
     * 
     * @param authSourceDatabaseName
     * @param performAuthentication
     * @param password
     * @param username
     */
    public AuthenticationConfiguration(boolean performAuthentication, String authSourceDatabaseName, String username, String password) {
        super();
        this.performAuthentication = performAuthentication;
        this.authSourceDatabaseName = authSourceDatabaseName;
        this.username = username;
        this.password = password;
    }

    /**
     * whether the program should perform authentication or not
     * (Required)
     * 
     */
    @JsonProperty("performAuthentication")
    public boolean isPerformAuthentication() {
        return performAuthentication;
    }

    /**
     * whether the program should perform authentication or not
     * (Required)
     * 
     */
    @JsonProperty("performAuthentication")
    public void setPerformAuthentication(boolean performAuthentication) {
        this.performAuthentication = performAuthentication;
    }

    /**
     * name of the authentication database
     * 
     */
    @JsonProperty("authSourceDatabaseName")
    public String getAuthSourceDatabaseName() {
        return authSourceDatabaseName;
    }

    /**
     * name of the authentication database
     * 
     */
    @JsonProperty("authSourceDatabaseName")
    public void setAuthSourceDatabaseName(String authSourceDatabaseName) {
        this.authSourceDatabaseName = authSourceDatabaseName;
    }

    /**
     * username to authenticate to the database
     * (Required)
     * 
     */
    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    /**
     * username to authenticate to the database
     * (Required)
     * 
     */
    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * user password for authentication
     * (Required)
     * 
     */
    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    /**
     * user password for authentication
     * (Required)
     * 
     */
    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(AuthenticationConfiguration.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("performAuthentication");
        sb.append('=');
        sb.append(this.performAuthentication);
        sb.append(',');
        sb.append("authSourceDatabaseName");
        sb.append('=');
        sb.append(((this.authSourceDatabaseName == null)?"<null>":this.authSourceDatabaseName));
        sb.append(',');
        sb.append("username");
        sb.append('=');
        sb.append(((this.username == null)?"<null>":this.username));
        sb.append(',');
        sb.append("password");
        sb.append('=');
        sb.append(((this.password == null)?"<null>":this.password));
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
        result = ((result* 31)+((this.authSourceDatabaseName == null)? 0 :this.authSourceDatabaseName.hashCode()));
        result = ((result* 31)+(this.performAuthentication? 1 : 0));
        result = ((result* 31)+((this.password == null)? 0 :this.password.hashCode()));
        result = ((result* 31)+((this.username == null)? 0 :this.username.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AuthenticationConfiguration) == false) {
            return false;
        }
        AuthenticationConfiguration rhs = ((AuthenticationConfiguration) other);
        return (((((this.authSourceDatabaseName == rhs.authSourceDatabaseName)||((this.authSourceDatabaseName!= null)&&this.authSourceDatabaseName.equals(rhs.authSourceDatabaseName)))&&(this.performAuthentication == rhs.performAuthentication))&&((this.password == rhs.password)||((this.password!= null)&&this.password.equals(rhs.password))))&&((this.username == rhs.username)||((this.username!= null)&&this.username.equals(rhs.username))));
    }

}
