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

	/**
	 * This can disable Cache-Control 304s and is useful durring debugging
	 * I cannot claim credit for this btw
	 */

	@JsonProperty
	private String enableCacheControl;

	public String getEnableCacheControl() {
		return enableCacheControl;
	}

    /**
     * allows us to develop on the front-end without having the background automations going on
     */
	@JsonProperty
	private String automationEnabled;

	public String getAutomationEnabled() {
		return automationEnabled;
	}
    public String getEnableAutomation() {
        return automationEnabled;
    }

    public Boolean isEnableAutomation() {
        return Boolean.valueOf(automationEnabled);
    }

	@JsonProperty
	private Integer automationIntervalInSeconds;

	public Integer getAutomationIntervalInSeconds() {
		return automationIntervalInSeconds;
	}

	@JsonProperty
	private Integer automationStaggeredDelayInSeconds;

	public Integer getAutomationStaggeredDelayInSeconds() {
		return automationStaggeredDelayInSeconds;
	}

	@JsonProperty
	private Integer automationThreadCount;

	public Integer getAutomationThreadCount() {
		return automationThreadCount;
	}


    /**
     * Driver type selects the driver class meaning either UT or UARK currently supported.
     */
    @NotEmpty
    private String automationDriverType;

    @JsonProperty
    public String getAutomationDriverType() {
        return automationDriverType;
    }

}