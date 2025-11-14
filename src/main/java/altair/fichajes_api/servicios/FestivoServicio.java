package altair.fichajes_api.servicios;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import altair.fichajes_api.entidad.FestivoEntidad;
import altair.fichajes_api.repositorios.FestivoInterfaz;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class FestivoServicio {

    @Autowired
    private FestivoInterfaz festivoInterfaz;

    @Autowired
    private RestTemplate restTemplate;

    // URL limpia sin saltos de línea
    private final String URL_FESTIVOS_CSV = "https://www.juntadeandalucia.es/datosabiertos/portal/dataset/2e273ede-2187-4a7e-abef-e6aa8375c64f/resource/c9e2c68e-1c4d-4948-8458-143f617352cd/download/calendario_2025.csv";

    private final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyyMMdd");

    public void importarFestivos() {
        System.out.println("Iniciando importación de festivos...");

        try {
            // Descarga el CSV
            String csv = restTemplate.getForObject(URL_FESTIVOS_CSV, String.class);

            if (csv == null || csv.isBlank()) {
                System.out.println("No se ha recibido contenido CSV.");
                return;
            }

            // Separar por líneas
            String[] lines = csv.split("\\r?\\n");

            // Iterar cada línea
            Arrays.stream(lines).forEach(line -> {
                String[] campos = line.split(";");
                if (campos.length >= 5) {
                    String fechaStr = campos[1]; // día en formato YYYYMMDD
                    String descripcion = campos[2];
                    String municipio = campos[3].trim(); // eliminar espacios

                    // Solo Sevilla
                    if ("Sevilla".equalsIgnoreCase(municipio)) {
                        try {
                            LocalDate fecha = LocalDate.parse(fechaStr, FORMATO_FECHA);

                            // Evitar duplicados
                            if (!festivoInterfaz.existsByFecha(fecha)) {
                                FestivoEntidad festivo = new FestivoEntidad();
                                festivo.setFecha(fecha);
                                festivo.setNombre(descripcion);
                                festivoInterfaz.save(festivo);
                                System.out.println("Guardado festivo: " + fecha + " - " + descripcion);
                            }
                        } catch (Exception e) {
                            System.out.println("Error parseando fecha: " + fechaStr);
                        }
                    }
                }
            });

            System.out.println("Importación completada.");

        } catch (Exception e) {
            System.out.println("Error descargando CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 0 0 * * *") // Ejecuta a medianoche todos los días
    public void actualizarFestivosDiarios() {
        importarFestivos();
    }
}
