package com.pwllc.app;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/*
 *  configuration read from dropwizard yaml configuration file
 */
public class AppConfig extends Configuration {
	
	@NotEmpty
	private String response;
	
	@JsonProperty
	public String getResponse() {
		return response;
	}
	
	@JsonProperty
	public void setResponse(String response) {
		this.response = response;
	}

	/**
	 * This can disable Cache-Control 304s and is useful durring debugging
	 * I cannot claim credit for this btw
	 */

	@JsonProperty
	private String enableCacheControl;

	public String getEnableCacheControl() {
		return enableCacheControl;
	}

	public void setEnableCacheControl(String enableCacheControl) {
		this.enableCacheControl = enableCacheControl;
	}

    /**
     * allows us to develop on the front-end without having the background automations going on
     */
	@JsonProperty
	private String enableAutomation;

    public String getEnableAutomation() {
        return enableAutomation;
    }

    public Boolean isEnableAutomation() {
        return Boolean.valueOf(enableAutomation);
    }

    public void setEnableAutomation(String enableAutomation) {
		this.enableAutomation = enableAutomation;
	}
}