package altair.fichajes_api.repositorios;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import altair.fichajes_api.entidad.FestivoEntidad;
/**
 * Repositorio encargado de gestionar operaciones CRUD sobre la entidad {@link FestivoEntidad}.
 * Extiende {@link JpaRepository} para proporcionar acceso a la base de datos.
 */
public interface FestivoInterfaz extends JpaRepository<FestivoEntidad, Long> {

    /**
     * Verifica si existe un festivo en una fecha específica.
     * @param fecha Fecha a consultar
     * @return true si existe un festivo en la fecha indicada, false en caso contrario
     */
    boolean existsByFecha(LocalDate fecha);

}
