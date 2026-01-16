package altair.fichajes_api.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import altair.fichajes_api.entidad.CursoEntidad;
import altair.fichajes_api.entidad.GrupoEntidad;
import altair.fichajes_api.entidad.MatriculacionEntidad;

/**
 * Repositorio encargado de gestionar operaciones CRUD sobre la entidad
 * {@link MatriculacionEntidad}. Extiende {@link JpaRepository} para
 * proporcionar acceso a la base de datos.
 */
public interface MatriculacionInterfaz extends JpaRepository<MatriculacionEntidad, Long> {

	/**
	 * Obtiene todas las matriculaciones asociadas a un curso y grupo específicos.
	 * 
	 * @param curso Entidad del curso
	 * @param grupo Entidad del grupo
	 * @return Lista de entidades de matriculación correspondientes al curso y grupo
	 */
	List<MatriculacionEntidad> findByCursoAndGrupo(CursoEntidad curso, GrupoEntidad grupo);

	Optional<MatriculacionEntidad> findByUidLlave(String uidTarjeta);

	/**
	 * Busca todas las matriculaciones de un alumno por su ID.
	 *
	 * @param idAlumno ID del alumno.
	 * @return Lista de entidades de matriculación.
	 */
	List<MatriculacionEntidad> findByAlumno_IdAlumno(Long idAlumno);

	Optional<MatriculacionEntidad> findByUidLlaveAndAnioEscolar(String uidLlave, String anioEscolar);

	List<MatriculacionEntidad> findByCursoAndGrupoAndAnioEscolar(CursoEntidad curso, GrupoEntidad grupo,
			String anioEscolar);
	
	
	@Query("SELECT m FROM MatriculacionEntidad m " +
		       "JOIN FETCH m.alumno " +
		       "JOIN FETCH m.curso " +
		       "JOIN FETCH m.grupo " +
		       "WHERE m.uidLlave = :uid AND m.anioEscolar = :anio")
		Optional<MatriculacionEntidad> findConAlumnoCursoGrupoByUidLlaveAndAnioEscolar(
		        @Param("uid") String uid,
		        @Param("anio") String anio
		);


}
