package io.dmcapps.dshopping.product;

import io.dmcapps.dshopping.product.init.DatabaseInitialization;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.configuration.ProfileManager;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
class ProductApplicationLifeCycle {

    private static final Logger LOGGER = Logger.getLogger(ProductApplicationLifeCycle.class);

    void onStart(@Observes StartupEvent ev) {
        
        LOGGER.info("'########::'########:::'#######::'########::'##::::'##::'######::'########:");
        LOGGER.info(" ##.... ##: ##.... ##:'##.... ##: ##.... ##: ##:::: ##:'##... ##:... ##..::");
        LOGGER.info(" ##:::: ##: ##:::: ##: ##:::: ##: ##:::: ##: ##:::: ##: ##:::..::::: ##::::");
        LOGGER.info(" ########:: ########:: ##:::: ##: ##:::: ##: ##:::: ##: ##:::::::::: ##::::");
        LOGGER.info(" ##.....::: ##.. ##::: ##:::: ##: ##:::: ##: ##:::: ##: ##:::::::::: ##::::");
        LOGGER.info(" ##:::::::: ##::. ##:: ##:::: ##: ##:::: ##: ##:::: ##: ##::: ##:::: ##::::");
        LOGGER.info(" ##:::::::: ##:::. ##:. #######:: ########::. #######::. ######::::: ##::::");
        LOGGER.info("..:::::::::..:::::..:::.......:::........::::.......::::......::::::..:::::");
        LOGGER.info(":'######:::::'###::::'########::::'###::::'##::::::::'#######:::'######::: ");
        LOGGER.info("'##... ##:::'## ##:::... ##..::::'## ##::: ##:::::::'##.... ##:'##... ##:: ");
        LOGGER.info(" ##:::..:::'##:. ##::::: ##:::::'##:. ##:: ##::::::: ##:::: ##: ##:::..::: ");
        LOGGER.info(" ##:::::::'##:::. ##:::: ##::::'##:::. ##: ##::::::: ##:::: ##: ##::'####: ");
        LOGGER.info(" ##::::::: #########:::: ##:::: #########: ##::::::: ##:::: ##: ##::: ##:: ");
        LOGGER.info(" ##::: ##: ##.... ##:::: ##:::: ##.... ##: ##::::::: ##:::: ##: ##::: ##:: ");
        LOGGER.info(". ######:: ##:::: ##:::: ##:::: ##:::: ##: ########:. #######::. ######::: ");
        LOGGER.info(":......:::..:::::..:::::..:::::..:::::..::........:::.......::::......:::: ");
        LOGGER.info("                                                         Powered by Quarkus");
        
        LOGGER.infof("The application PRODUCT is starting with profile `%s`", ProfileManager.getActiveProfile());
        
        if(ProfileManager.getActiveProfile() == "dev" || ProfileManager.getActiveProfile() == "test"){
            DatabaseInitialization.initialize(ProfileManager.getActiveProfile());
        }
    }             

    void onStop(@Observes ShutdownEvent ev) {
        LOGGER.info("The application PRODUCT is stopping...");
    }
}