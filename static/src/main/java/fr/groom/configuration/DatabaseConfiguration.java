
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
    "connectToDatabase",
    "storeOutputToDatabase",
    "outputDatabaseName",
    "fetchDatabaseName",
    "applicationCollectionName",
    "staticCollectionName",
    "url",
    "port",
    "authenticationConfiguration"
})
public class DatabaseConfiguration implements Serializable
{

    /**
     * connect to database or use printer
     * 
     */
    @JsonProperty("connectToDatabase")
    @JsonPropertyDescription("connect to database or use printer")
    private boolean connectToDatabase = false;
    /**
     * store results to database
     * 
     */
    @JsonProperty("storeOutputToDatabase")
    @JsonPropertyDescription("store results to database")
    private boolean storeOutputToDatabase = false;
    /**
     * name of the database where to output results
     * 
     */
    @JsonProperty("outputDatabaseName")
    @JsonPropertyDescription("name of the database where to output results")
    private String outputDatabaseName = "groom2";
    /**
     * name of the database where to fetch apks
     * 
     */
    @JsonProperty("fetchDatabaseName")
    @JsonPropertyDescription("name of the database where to fetch apks")
    private String fetchDatabaseName = "TEST";
    /**
     * app collection name
     * 
     */
    @JsonProperty("applicationCollectionName")
    @JsonPropertyDescription("app collection name")
    private String applicationCollectionName = "application_test";
    /**
     * static collection name
     * 
     */
    @JsonProperty("staticCollectionName")
    @JsonPropertyDescription("static collection name")
    private String staticCollectionName = "static_test";
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
    private final static long serialVersionUID = 6146694764424641647L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public DatabaseConfiguration() {
    }

    /**
     * 
     * @param connectToDatabase
     * @param outputDatabaseName
     * @param fetchDatabaseName
     * @param port
     * @param storeOutputToDatabase
     * @param staticCollectionName
     * @param applicationCollectionName
     * @param url
     * @param authenticationConfiguration
     */
    public DatabaseConfiguration(boolean connectToDatabase, boolean storeOutputToDatabase, String outputDatabaseName, String fetchDatabaseName, String applicationCollectionName, String staticCollectionName, String url, int port, AuthenticationConfiguration authenticationConfiguration) {
        super();
        this.connectToDatabase = connectToDatabase;
        this.storeOutputToDatabase = storeOutputToDatabase;
        this.outputDatabaseName = outputDatabaseName;
        this.fetchDatabaseName = fetchDatabaseName;
        this.applicationCollectionName = applicationCollectionName;
        this.staticCollectionName = staticCollectionName;
        this.url = url;
        this.port = port;
        this.authenticationConfiguration = authenticationConfiguration;
    }

    /**
     * connect to database or use printer
     * 
     */
    @JsonProperty("connectToDatabase")
    public boolean isConnectToDatabase() {
        return connectToDatabase;
    }

    /**
     * connect to database or use printer
     * 
     */
    @JsonProperty("connectToDatabase")
    public void setConnectToDatabase(boolean connectToDatabase) {
        this.connectToDatabase = connectToDatabase;
    }

    /**
     * store results to database
     * 
     */
    @JsonProperty("storeOutputToDatabase")
    public boolean isStoreOutputToDatabase() {
        return storeOutputToDatabase;
    }

    /**
     * store results to database
     * 
     */
    @JsonProperty("storeOutputToDatabase")
    public void setStoreOutputToDatabase(boolean storeOutputToDatabase) {
        this.storeOutputToDatabase = storeOutputToDatabase;
    }

    /**
     * name of the database where to output results
     * 
     */
    @JsonProperty("outputDatabaseName")
    public String getOutputDatabaseName() {
        return outputDatabaseName;
    }

    /**
     * name of the database where to output results
     * 
     */
    @JsonProperty("outputDatabaseName")
    public void setOutputDatabaseName(String outputDatabaseName) {
        this.outputDatabaseName = outputDatabaseName;
    }

    /**
     * name of the database where to fetch apks
     * 
     */
    @JsonProperty("fetchDatabaseName")
    public String getFetchDatabaseName() {
        return fetchDatabaseName;
    }

    /**
     * name of the database where to fetch apks
     * 
     */
    @JsonProperty("fetchDatabaseName")
    public void setFetchDatabaseName(String fetchDatabaseName) {
        this.fetchDatabaseName = fetchDatabaseName;
    }

    /**
     * app collection name
     * 
     */
    @JsonProperty("applicationCollectionName")
    public String getApplicationCollectionName() {
        return applicationCollectionName;
    }

    /**
     * app collection name
     * 
     */
    @JsonProperty("applicationCollectionName")
    public void setApplicationCollectionName(String applicationCollectionName) {
        this.applicationCollectionName = applicationCollectionName;
    }

    /**
     * static collection name
     * 
     */
    @JsonProperty("staticCollectionName")
    public String getStaticCollectionName() {
        return staticCollectionName;
    }

    /**
     * static collection name
     * 
     */
    @JsonProperty("staticCollectionName")
    public void setStaticCollectionName(String staticCollectionName) {
        this.staticCollectionName = staticCollectionName;
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
        sb.append("connectToDatabase");
        sb.append('=');
        sb.append(this.connectToDatabase);
        sb.append(',');
        sb.append("storeOutputToDatabase");
        sb.append('=');
        sb.append(this.storeOutputToDatabase);
        sb.append(',');
        sb.append("outputDatabaseName");
        sb.append('=');
        sb.append(((this.outputDatabaseName == null)?"<null>":this.outputDatabaseName));
        sb.append(',');
        sb.append("fetchDatabaseName");
        sb.append('=');
        sb.append(((this.fetchDatabaseName == null)?"<null>":this.fetchDatabaseName));
        sb.append(',');
        sb.append("applicationCollectionName");
        sb.append('=');
        sb.append(((this.applicationCollectionName == null)?"<null>":this.applicationCollectionName));
        sb.append(',');
        sb.append("staticCollectionName");
        sb.append('=');
        sb.append(((this.staticCollectionName == null)?"<null>":this.staticCollectionName));
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
        result = ((result* 31)+(this.connectToDatabase? 1 : 0));
        result = ((result* 31)+((this.outputDatabaseName == null)? 0 :this.outputDatabaseName.hashCode()));
        result = ((result* 31)+((this.fetchDatabaseName == null)? 0 :this.fetchDatabaseName.hashCode()));
        result = ((result* 31)+ this.port);
        result = ((result* 31)+(this.storeOutputToDatabase? 1 : 0));
        result = ((result* 31)+((this.staticCollectionName == null)? 0 :this.staticCollectionName.hashCode()));
        result = ((result* 31)+((this.applicationCollectionName == null)? 0 :this.applicationCollectionName.hashCode()));
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
        return (((((((((this.connectToDatabase == rhs.connectToDatabase)&&((this.outputDatabaseName == rhs.outputDatabaseName)||((this.outputDatabaseName!= null)&&this.outputDatabaseName.equals(rhs.outputDatabaseName))))&&((this.fetchDatabaseName == rhs.fetchDatabaseName)||((this.fetchDatabaseName!= null)&&this.fetchDatabaseName.equals(rhs.fetchDatabaseName))))&&(this.port == rhs.port))&&(this.storeOutputToDatabase == rhs.storeOutputToDatabase))&&((this.staticCollectionName == rhs.staticCollectionName)||((this.staticCollectionName!= null)&&this.staticCollectionName.equals(rhs.staticCollectionName))))&&((this.applicationCollectionName == rhs.applicationCollectionName)||((this.applicationCollectionName!= null)&&this.applicationCollectionName.equals(rhs.applicationCollectionName))))&&((this.url == rhs.url)||((this.url!= null)&&this.url.equals(rhs.url))))&&((this.authenticationConfiguration == rhs.authenticationConfiguration)||((this.authenticationConfiguration!= null)&&this.authenticationConfiguration.equals(rhs.authenticationConfiguration))));
    }

}
