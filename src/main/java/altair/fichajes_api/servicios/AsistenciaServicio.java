package altair.fichajes_api.servicios;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import altair.fichajes_api.dtos.AsistenciaDto;
import altair.fichajes_api.entidad.AsistenciaEntidad;
import altair.fichajes_api.entidad.CursoEntidad;
import altair.fichajes_api.entidad.GrupoEntidad;
import altair.fichajes_api.entidad.MatriculacionEntidad;
import altair.fichajes_api.repositorios.AsistenciaInterfaz;
import altair.fichajes_api.repositorios.CursoInterfaz;
import altair.fichajes_api.repositorios.GrupoInterfaz;
import altair.fichajes_api.repositorios.MatriculacionInterfaz;

@Service
public class AsistenciaServicio {

	@Autowired
	private AsistenciaInterfaz asistenciaInterfaz;
	@Autowired
	private GrupoInterfaz grupoInterfaz;
	@Autowired
	private CursoInterfaz cursoInterfaz;
	@Autowired
	private MatriculacionInterfaz matriculacionInterfaz;

	// Registrar entrada
	public AsistenciaDto ficharEntrada(Long matriculacionId) {
		LocalDate hoy = LocalDate.now();

		Optional<AsistenciaEntidad> asistenciaExistente = asistenciaInterfaz
				.findByMatriculacion_IdMatriculacionAndFecha(matriculacionId, hoy);

		if (asistenciaExistente.isPresent()) {
			throw new RuntimeException("El alumno ya tiene registrada la asistencia de hoy");
		}

		MatriculacionEntidad matricula = matriculacionInterfaz.findById(matriculacionId)
				.orElseThrow(() -> new RuntimeException("No se encontró la matrícula"));

		AsistenciaEntidad asistencia = new AsistenciaEntidad();
		asistencia.setMatriculacion(matricula);
		asistencia.setFecha(hoy);
		asistencia.setHoraEntrada(LocalDateTime.now());
		asistencia.setEstado("PRESENTE");

		AsistenciaEntidad guardada = asistenciaInterfaz.save(asistencia);
		return mapearADto(guardada);
	}

	// Registrar salida
	public AsistenciaDto ficharSalida(Long matriculacionId) {
		LocalDate hoy = LocalDate.now();

		AsistenciaEntidad asistencia = asistenciaInterfaz
				.findByMatriculacion_IdMatriculacionAndFecha(matriculacionId, hoy)
				.orElseThrow(() -> new RuntimeException("No se encontró asistencia de entrada para hoy"));

		asistencia.setHoraSalida(LocalDateTime.now());
		asistencia.setEstado("COMPLETA");

		return mapearADto(asistenciaInterfaz.save(asistencia));
	}

	public List<AsistenciaDto> obtenerAsistenciaPorCursoYGrupoEnFecha(String curso, String grupo, LocalDate fecha) {
	    CursoEntidad cursoEntidad = cursoInterfaz.findByNombreCurso(curso);
	    GrupoEntidad grupoEntidad = grupoInterfaz.findByNombreGrupo(grupo);

	    List<MatriculacionEntidad> matriculas = matriculacionInterfaz.findByCursoAndGrupo(cursoEntidad, grupoEntidad);

	    return matriculas.stream()
	        .map(m -> {
	            Optional<AsistenciaEntidad> asistenciaOpt = asistenciaInterfaz
	                    .findByMatriculacion_IdMatriculacionAndFecha(m.getIdMatriculacion(), fecha);

	            // Si existe, devolvemos el DTO normal
	            if (asistenciaOpt.isPresent()) {
	                return mapearADto(asistenciaOpt.get());
	            }

	            // Si NO existe, creamos una asistencia "FALTA" en la base de datos
	            AsistenciaEntidad nuevaAsistencia = new AsistenciaEntidad();
	            nuevaAsistencia.setMatriculacion(m);
	            nuevaAsistencia.setFecha(fecha);
	            nuevaAsistencia.setEstado("FALTA");
	            nuevaAsistencia.setFechaModificacion(LocalDateTime.now());

	            AsistenciaEntidad guardada = asistenciaInterfaz.save(nuevaAsistencia);
	            return mapearADto(guardada);
	        })
	        .collect(Collectors.toList());
	}


	public void cerrarAsistenciasDelDia(LocalDate fecha) {
		List<AsistenciaEntidad> asistencias = asistenciaInterfaz.findByFecha(fecha);
		LocalDateTime horaCierre = LocalDateTime.of(fecha, LocalTime.of(23, 0));

		// Marcar sin salida
		asistencias.stream().filter(a -> a.getHoraEntrada() != null && a.getHoraSalida() == null).forEach(a -> {
			a.setHoraSalida(horaCierre);
			a.setEstado("SIN SALIDA");
			a.setFechaModificacion(LocalDateTime.now());
			asistenciaInterfaz.save(a);
		});

		// Crear faltas
		generarFaltas(fecha);
	}

	// Consultar por matrícula
	public List<AsistenciaDto> obtenerPorMatricula(Long matriculacionId) {
		return asistenciaInterfaz.findByMatriculacion_IdMatriculacion(matriculacionId).stream().map(this::mapearADto)
				.collect(Collectors.toList());
	}

	public List<AsistenciaDto> obtenerTodasAsistencias() {
		return asistenciaInterfaz.findAll().stream().map(this::mapearADto).toList();
	}

	public Optional<AsistenciaDto> obtenerPorMatriculaYFecha(Long matriculacionId, LocalDate fecha) {
		return asistenciaInterfaz.findByMatriculacion_IdMatriculacionAndFecha(matriculacionId, fecha)
				.map(this::mapearADto);
	}

	public List<AsistenciaDto> obtenerPorRango(Long matriculacionId, LocalDate desde, LocalDate hasta) {
		return asistenciaInterfaz.findByMatriculacion_IdMatriculacionAndFechaBetween(matriculacionId, desde, hasta)
				.stream().map(this::mapearADto).toList();
	}

	public Long contarAsistencias(Long matriculacionId) {
		return asistenciaInterfaz.countByMatriculacion_IdMatriculacion(matriculacionId);
	}

	// Consultar por fecha
	public List<AsistenciaDto> obtenerPorFecha(LocalDate fecha) {
		return asistenciaInterfaz.findByFecha(fecha).stream().map(this::mapearADto).collect(Collectors.toList());
	}

	// Mapeo a DTO
	private AsistenciaDto mapearADto(AsistenciaEntidad entidad) {
	    AsistenciaDto dto = new AsistenciaDto();
	    dto.setIdAsistencia(entidad.getIdAsistencia());
	    dto.setMatriculacionId(entidad.getMatriculacion().getIdMatriculacion());
	    dto.setNombreAlumno(entidad.getMatriculacion().getAlumno().getNombreAlumno());
	    dto.setApellidoAlumno(entidad.getMatriculacion().getAlumno().getApellidoAlumno());
	    dto.setNombreCurso(entidad.getMatriculacion().getCurso().getNombreCurso());
	    dto.setNombreGrupo(entidad.getMatriculacion().getGrupo().getNombreGrupo());
	    dto.setFecha(entidad.getFecha());
	    dto.setFechaModificacion(entidad.getFechaModificacion());
	    dto.setHoraEntrada(entidad.getHoraEntrada());
	    dto.setHoraSalida(entidad.getHoraSalida());
	    dto.setJustificarModificacion(entidad.getJustificar_modificacion());
	    dto.setEstado(entidad.getEstado());

	    return dto;
	}

	


	public boolean modificarAsistencia(Long idAsistencia, AsistenciaDto asistenciaDto) {
	    boolean esModificado = false;

	    try {
	        // Buscar la asistencia por ID
	        AsistenciaEntidad asistencia = asistenciaInterfaz.findById(idAsistencia)
	                .orElse(null);

	        if (asistencia == null) {
	            return false; // No existe
	        }

	        // Sobrescribir todos los campos del DTO, incluso null
	        asistencia.setHoraEntrada(asistenciaDto.getHoraEntrada());
	        asistencia.setHoraSalida(asistenciaDto.getHoraSalida());
	        asistencia.setEstado(asistenciaDto.getEstado());
	        asistencia.setJustificar_modificacion(asistenciaDto.getJustificarModificacion());
	        asistencia.setFechaModificacion(LocalDateTime.now());

	        // Fecha de modificación siempre se actualiza
	        asistencia.setFechaModificacion(LocalDateTime.now());

	        // Guardar cambios
	        asistenciaInterfaz.save(asistencia);
	        esModificado = true;

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return esModificado;
	}


	public void generarFaltas(LocalDate fecha) {
		DayOfWeek dia = fecha.getDayOfWeek();

		// Solo de lunes a viernes
		if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
			return;
		}

		List<MatriculacionEntidad> matriculas = matriculacionInterfaz.findAll();

		for (MatriculacionEntidad matricula : matriculas) {
			boolean asistenciaExiste = asistenciaInterfaz
					.findByMatriculacion_IdMatriculacionAndFecha(matricula.getIdMatriculacion(), fecha).isPresent();

			if (!asistenciaExiste) {
				AsistenciaEntidad falta = new AsistenciaEntidad();
				falta.setMatriculacion(matricula);
				falta.setFecha(fecha);
				falta.setEstado("FALTA");
				falta.setFechaModificacion(LocalDateTime.now());
				asistenciaInterfaz.save(falta);
			}
		}
	}

}