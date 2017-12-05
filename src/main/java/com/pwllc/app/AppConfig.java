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
     * determines if the program will run background scans.
	 * The "Run Now!" will work regardless of this setting
     */
	@JsonProperty
	private String automationPeriodicScansEnabled;

    public Boolean isAutomationPeriodicScansEnabled() {
        return Boolean.valueOf(automationPeriodicScansEnabled);
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
	@JsonProperty
	private String automationDriverType;

    public String getAutomationDriverType() {
        return automationDriverType;
    }

	/**
	 * throttles the number of notifications to 1 every N minutes
	 */
	@JsonProperty
	private Integer automationNotificationWindowInMinutes;

	public Integer getAutomationNotificationWindowInMinutes() {
		return automationNotificationWindowInMinutes;
	}
}