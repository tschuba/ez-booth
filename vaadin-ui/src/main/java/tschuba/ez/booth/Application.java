package tschuba.ez.booth;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

import static lombok.AccessLevel.PRIVATE;

@SpringBootApplication
@Theme("booth")
@ComponentScan(basePackages = {"tschuba.ez.booth"})
@ConfigurationPropertiesScan(basePackages = {"tschuba.ez.booth"})
@NoArgsConstructor(access = PRIVATE)
public class Application implements AppShellConfigurator {

    static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
