package altair.fichajes_api.Configuraciones;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Clase de configuración de la aplicación.
 * Define la configuración de CORS para permitir solicitudes desde el frontend.
 */
@Configuration
public class AplicacionConfiguracion {

    /**
     * Configura los permisos de CORS para la API.
     *
     * @return instancia de WebMvcConfigurer con CORS configurado
     */
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
