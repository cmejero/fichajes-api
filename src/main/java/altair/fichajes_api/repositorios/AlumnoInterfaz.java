package altair.fichajes_api.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import altair.fichajes_api.entidad.AlumnoEntidad;

/**
 * Repositorio encargado de gestionar operaciones CRUD sobre la entidad AlumnoEntidad.
 * Extiende {@link JpaRepository} para proporcionar acceso a la base de datos.
 */
public interface AlumnoInterfaz extends JpaRepository<AlumnoEntidad, Long> {

	 /**
     * Obtiene un alumno por su ID junto con todas sus matriculaciones,
     * incluyendo los cursos y grupos asociados. 
     * Utiliza JOIN FETCH para evitar múltiples consultas por cada relación (problema N+1).
     *
     * @param id ID del alumno a buscar
     * @return Optional con el AlumnoEntidad completo si existe
     */
    @Query("SELECT a FROM AlumnoEntidad a " +
           "LEFT JOIN FETCH a.matriculaciones m " +
           "LEFT JOIN FETCH m.curso " +
           "LEFT JOIN FETCH m.grupo " +
           "WHERE a.idAlumno = :id")
    Optional<AlumnoEntidad> findAlumnoConMatriculaciones(@Param("id") Long id);

    /**
     * Obtiene todos los alumnos de la base de datos junto con sus matriculaciones,
     * incluyendo cursos y grupos asociados. 
     * La cláusula DISTINCT evita duplicados por los JOINs.
     *
     * Esto permite cargar toda la información necesaria en una sola consulta
     * y evita el problema N+1 al iterar sobre los alumnos y sus matriculaciones.
     *
     * @return Lista de todos los alumnos con sus matriculaciones completas
     */
    @Query("SELECT DISTINCT a FROM AlumnoEntidad a " +
           "LEFT JOIN FETCH a.matriculaciones m " +
           "LEFT JOIN FETCH m.curso " +
           "LEFT JOIN FETCH m.grupo")
    List<AlumnoEntidad> findAllConMatriculaciones();
}
