package altair.fichajes_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FichajesApiApplication  {


    public static void main(String[] args) {
        SpringApplication.run(FichajesApiApplication.class, args);
    }

 

}
