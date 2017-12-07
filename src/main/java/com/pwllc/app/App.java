package com.pwllc.app;

import com.pwllc.course.CourseAutomationMgr;
import com.pwllc.course.CourseDAO;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.servlets.CacheBustingFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.pwllc.web.MainResource;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * The main Dropwizard Application class.
 * TODO: Move credentials to config
 * TODO: Use HL for simple storage of class jobs
 * TODO: UI for list of target classes
 * TODO: UI to add a new class monitor
 * TODO: UI to remove a class monitor
 * TODO: UI to toggle class monitor
 *
 */
public class App extends Application<AppConfig> {
	
	
    public static void main(String[] args) throws Exception {
    	
    	// instantiate and run this main Application class
        new App().run(args);
    }

    @Override
    public String getName() {
        return "ClassBot";
    }

    @Override
    public void initialize(Bootstrap<AppConfig> bootstrap) {

        // create a Servlet to serve static resources from the assets folder, as root, defaulting to index.html
        bootstrap.addBundle(new AssetsBundle("/assets", "/","index.html"));
    }

    @Override
    public void run(AppConfig configuration, Environment environment) {

        // enable or disable static asset caching (helps during development)?
        setCachePolicy(configuration, environment);

        // configuration has a 'server:rootPath' declaration which prepends a rootPath for all Resources
        CourseDAO dao = new CourseDAO(configuration);
        AppPreferences pref = new AppPreferences();

        // start background automation
        CourseAutomationMgr mgr = new CourseAutomationMgr(dao, pref, configuration);

        // create and register REST resource endpoints
    	MainResource resource = new MainResource(dao, configuration, pref, mgr);
    	environment.jersey().register(resource);

    }

    private void setCachePolicy(AppConfig configuration, Environment environment) {

        // get the cache control settings from the YAML - configuration
        String enableCacheControl = configuration.getEnableCacheControl();
        boolean enableCacheBustingFilter = Boolean.parseBoolean(enableCacheControl);

        if(enableCacheBustingFilter){

            // caching was enabled in YAML - was set to true - enabling the cacheBustingFilter
            // this will ALWAYS return  "must-revalidate,no-cache,no-store" in the Cache-Control response header
            environment.servlets().addFilter("CacheBustingFilter", new CacheBustingFilter())
                    .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
        }
    }

}