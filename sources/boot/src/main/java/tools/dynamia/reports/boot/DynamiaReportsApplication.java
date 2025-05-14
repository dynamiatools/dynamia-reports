package tools.dynamia.reports.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import tools.dynamia.app.EnableDynamiaTools;
import tools.dynamia.navigation.ModuleProvider;
import tools.dynamia.reports.ui.DynamiaReportsModule;
import tools.dynamia.reports.ui.DynamiaReportsUserModule;

@SpringBootApplication
@EnableDynamiaTools
@EntityScan("tools.dynamia")
public class DynamiaReportsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamiaReportsApplication.class, args);
    }

    @Bean
    public ModuleProvider dynamiaReportsModule() {
        return () -> new DynamiaReportsModule("reports", "Design Reports", "", 1);
    }

    @Bean
    public ModuleProvider dynamiaReportsUserModule() {
        return () -> new DynamiaReportsUserModule("view", "View Reports", 0);
    }


}
