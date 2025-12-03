package altair.fichajes_api.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import altair.fichajes_api.entidad.AlumnoEntidad;

/**
 * Repositorio encargado de gestionar operaciones CRUD sobre la entidad AlumnoEntidad.
 * Extiende {@link JpaRepository} para proporcionar acceso a la base de datos.
 */
public interface AlumnoInterfaz extends JpaRepository<AlumnoEntidad, Long> {


}
