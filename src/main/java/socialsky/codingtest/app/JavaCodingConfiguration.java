/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socialsky.codingtest.app;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import java.io.UnsupportedEncodingException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

public class JavaCodingConfiguration extends Configuration {

    @NotEmpty
    private String jwtsecrettoken;

    @JsonProperty
    public void setJwtsecrettoken(String jwtsecrettoken) {
        this.jwtsecrettoken = jwtsecrettoken;
    }
    
    @JsonProperty
    public String getJwtsecrettoken() {
        return jwtsecrettoken;
    }
    
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }

    public byte[] getjwtsecrettoken() throws UnsupportedEncodingException {
        return jwtsecrettoken.getBytes("UTF-8");
    }

}
