package altair.fichajes_api.repositorios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import altair.fichajes_api.entidad.AsistenciaEntidad;

/**
 * Repositorio encargado de gestionar operaciones CRUD sobre la entidad AsistenciaEntidad.
 * Extiende {@link JpaRepository} para proporcionar acceso a la base de datos.
 */
public interface AsistenciaInterfaz extends JpaRepository<AsistenciaEntidad, Long> {

	
	/**
	 * Busca una asistencia por el ID de matrícula y una fecha específica.
	 * @param matriculacionId ID de la matrícula
	 * @param fecha Fecha de la asistencia
	 * @return Optional de AsistenciaEntidad si existe, vacío si no
	 */
	Optional<AsistenciaEntidad> findByMatriculacion_IdMatriculacionAndFecha(Long matriculacionId, LocalDate fecha);

	/**
	 * Obtiene todas las asistencias asociadas a una matrícula.
	 * @param matriculacionId ID de la matrícula
	 * @return Lista de AsistenciaEntidad
	 */
	List<AsistenciaEntidad> findByMatriculacion_IdMatriculacion(Long matriculacionId);

	/**
	 * Obtiene todas las asistencias de un alumno dentro de un rango de fechas.
	 * @param alumnoId ID del alumno
	 * @param desde Fecha inicial del rango
	 * @param hasta Fecha final del rango
	 * @return Lista de AsistenciaEntidad
	 */
	List<AsistenciaEntidad> findByMatriculacion_Alumno_IdAlumnoAndFechaBetween(
	        Long alumnoId, LocalDate desde, LocalDate hasta);

	/**
	 * Cuenta el número total de asistencias de una matrícula.
	 * @param matriculacionId ID de la matrícula
	 * @return Cantidad de asistencias
	 */
	Long countByMatriculacion_IdMatriculacion(Long matriculacionId);

	/**
	 * Obtiene todas las asistencias de una fecha específica.
	 * @param fecha Fecha de búsqueda
	 * @return Lista de AsistenciaEntidad
	 */
	List<AsistenciaEntidad> findByFecha(LocalDate fecha);

	/**
	 * Obtiene las asistencias de un alumno filtradas por estado dentro de un rango de fechas.
	 * @param alumnoId ID del alumno
	 * @param estado Estado de la asistencia
	 * @param desde Fecha inicial del rango
	 * @param hasta Fecha final del rango
	 * @return Lista de AsistenciaEntidad
	 */
	List<AsistenciaEntidad> findByMatriculacion_Alumno_IdAlumnoAndEstadoAndFechaBetween(
	        Long alumnoId, String estado, LocalDate desde, LocalDate hasta);

	/**
	 * Obtiene todas las asistencias que tienen hora de entrada registrada pero sin hora de salida.
	 * @return Lista de AsistenciaEntidad
	 */
	List<AsistenciaEntidad> findByHoraEntradaIsNotNullAndHoraSalidaIsNull();

	/**
	 * Obtiene todas las asistencias de una fecha específica que tienen hora de entrada registrada pero sin hora de salida.
	 * @param fecha Fecha de búsqueda
	 * @return Lista de AsistenciaEntidad
	 */
	List<AsistenciaEntidad> findByFechaAndHoraEntradaIsNotNullAndHoraSalidaIsNull(LocalDate fecha);



}
