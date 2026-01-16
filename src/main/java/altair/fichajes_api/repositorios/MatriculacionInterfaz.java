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

	

	/**
	 * Busca todas las matriculaciones de un alumno por su ID.
	 *
	 * @param idAlumno ID del alumno.
	 * @return Lista de entidades de matriculación.
	 */
	List<MatriculacionEntidad> findByAlumno_IdAlumno(Long idAlumno);
	

	/**
	 * Busca una matrícula por la UID de la tarjeta.
	 *
	 * @param uidTarjeta UID de la tarjeta NFC.
	 * @return Optional con la matrícula si existe.
	 */
	Optional<MatriculacionEntidad> findByUidLlave(String uidTarjeta);


/**
 * Busca una matrícula por la UID de la tarjeta y año escolar.
 *
 * @param uidLlave UID de la tarjeta NFC.
 * @param anioEscolar Año escolar en formato "AAAA-AAAA".
 * @return Optional con la matrícula si existe.
 */
	Optional<MatriculacionEntidad> findByUidLlaveAndAnioEscolar(String uidLlave, String anioEscolar);

	
	/**
	 * Obtiene todas las matrículas de un curso y grupo en un año escolar específico.
	 *
	 * @param curso Curso al que pertenece la matrícula.
	 * @param grupo Grupo al que pertenece la matrícula.
	 * @param anioEscolar Año escolar en formato "AAAA-AAAA".
	 * @return Lista de matrículas que cumplen los criterios.
	 */
	List<MatriculacionEntidad> findByCursoAndGrupoAndAnioEscolar(CursoEntidad curso, GrupoEntidad grupo,
			String anioEscolar);
	
	/**
	 * Busca una matrícula junto con su alumno, curso y grupo asociados
	 * filtrando por UID de la tarjeta y año escolar.
	 *
	 * @param uid UID de la tarjeta NFC.
	 * @param anio Año escolar en formato "AAAA-AAAA".
	 * @return Optional con la matrícula completa si existe.
	 */
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
