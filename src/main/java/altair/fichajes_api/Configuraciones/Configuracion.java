package altair.fichajes_api.Configuraciones;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

/**
 * Clase de configuración de Spring para la aplicación de fichajes.
 * Habilita la planificación de tareas y define beans necesarios como RestTemplate.
 */
@Configuration
@EnableScheduling
public class Configuracion {

    /**
     * Crea y devuelve un bean de RestTemplate para realizar llamadas HTTP.
     *
     * @return instancia de RestTemplate
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
