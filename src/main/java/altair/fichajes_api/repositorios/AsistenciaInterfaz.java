package altair.fichajes_api.repositorios;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import altair.fichajes_api.entidad.AsistenciaEntidad;

public interface AsistenciaInterfaz extends JpaRepository<AsistenciaEntidad, Long> {


	  // Buscar por matrícula y fecha exacta
    Optional<AsistenciaEntidad> findByMatriculacion_IdMatriculacionAndFecha(Long matriculacionId, LocalDate fecha);

    // Buscar todas las asistencias de una matrícula
    List<AsistenciaEntidad> findByMatriculacion_IdMatriculacion(Long matriculacionId);

    // Buscar todas las asistencias entre un rango de fechas
    List<AsistenciaEntidad> findByMatriculacion_Alumno_IdAlumnoAndFechaBetween(
            Long alumnoId, LocalDate desde, LocalDate hasta);


    // Contar asistencias de una matrícula
    Long countByMatriculacion_IdMatriculacion(Long matriculacionId);

    // Buscar todas las asistencias por fecha
    List<AsistenciaEntidad> findByFecha(LocalDate fecha);
    
    List<AsistenciaEntidad> findByMatriculacion_Alumno_IdAlumnoAndEstadoAndFechaBetween(
            Long alumnoId, String estado, LocalDate desde, LocalDate hasta);

    // Traer todas las asistencias con horaEntrada != null y horaSalida == null
    List<AsistenciaEntidad> findByHoraEntradaIsNotNullAndHoraSalidaIsNull();

    // Opcional: filtrar por fecha específica
    List<AsistenciaEntidad> findByFechaAndHoraEntradaIsNotNullAndHoraSalidaIsNull(LocalDate fecha);

}
