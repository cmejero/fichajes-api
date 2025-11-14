package altair.fichajes_api.Configuraciones;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AplicacionConfiguracion {

	 @Bean
	    public WebMvcConfigurer corsConfigurer() {
	        return new WebMvcConfigurer() {
	            @Override
	            public void addCorsMappings(CorsRegistry registry) {
	                registry.addMapping("/api/**")
	                        .allowedOrigins("http://localhost:8080")  // tu app JSP
	                        .allowedMethods("GET", "POST", "PUT", "DELETE")
	                        .allowedHeaders("*")
	                        .allowCredentials(false);
	            }
	        };
	    }
}
