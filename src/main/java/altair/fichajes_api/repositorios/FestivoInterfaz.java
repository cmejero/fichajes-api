package altair.fichajes_api.repositorios;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import altair.fichajes_api.entidad.FestivoEntidad;

public interface FestivoInterfaz extends JpaRepository<FestivoEntidad, Long> {
    boolean existsByFecha(LocalDate fecha);
}