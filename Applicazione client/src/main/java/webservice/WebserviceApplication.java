package webservice;
import webservice.controller.ApulianFaunaController;
import webservice.controller.PagesController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackageClasses= ApulianFaunaController.class)
@ComponentScan(basePackageClasses= PagesController.class)
@SpringBootApplication
public class WebserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebserviceApplication.class, args);
    }

}
