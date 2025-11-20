package altair.fichajes_api.servicios;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import jakarta.annotation.PostConstruct;

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
	            .filter(m -> {
	                // Convertir anioEscolar a fechas
	                if (m.getAnioEscolar() == null || !m.getAnioEscolar().contains("-")) return false;
	                String[] partes = m.getAnioEscolar().split("-");
	                int anioInicio = Integer.parseInt(partes[0]);
	                int anioFin = Integer.parseInt(partes[1]);

	                LocalDate inicio = LocalDate.of(anioInicio, 9, 1);
	                LocalDate fin = LocalDate.of(anioFin, 6, 30);

	                // Solo matriculaciones cuyo rango contenga la fecha
	                return !fecha.isBefore(inicio) && !fecha.isAfter(fin);
	            })
	            .map(m -> {
	                Optional<AsistenciaEntidad> asistenciaOpt = asistenciaInterfaz
	                        .findByMatriculacion_IdMatriculacionAndFecha(m.getIdMatriculacion(), fecha);

	                if (asistenciaOpt.isPresent()) {
	                    return mapearADto(asistenciaOpt.get());
	                }

	                // Si no existe, crear asistencia "FALTA"
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



	
	public List<AsistenciaDto> obtenerAsistenciasPorCursoGrupoYFecha(String curso, String grupo, LocalDate fecha) {
	    CursoEntidad cursoEntidad = cursoInterfaz.findByNombreCurso(curso);
	    GrupoEntidad grupoEntidad = grupoInterfaz.findByNombreGrupo(grupo);

	    if (cursoEntidad == null || grupoEntidad == null) {
	        throw new RuntimeException("Curso o grupo no encontrados");
	    }

	    List<MatriculacionEntidad> matriculas = matriculacionInterfaz.findByCursoAndGrupo(cursoEntidad, grupoEntidad);

	    return matriculas.stream()
	            .map(m -> {
	                Optional<AsistenciaEntidad> asistenciaOpt = asistenciaInterfaz
	                        .findByMatriculacion_IdMatriculacionAndFecha(m.getIdMatriculacion(), fecha);

	                if (asistenciaOpt.isPresent()) {
	                    return mapearADto(asistenciaOpt.get());
	                }

	                // Crear "FALTA" si no existe
	                AsistenciaEntidad nuevaAsistencia = new AsistenciaEntidad();
	                nuevaAsistencia.setMatriculacion(m);
	                nuevaAsistencia.setFecha(fecha);
	                nuevaAsistencia.setEstado("FALTA");
	                nuevaAsistencia.setFechaModificacion(LocalDateTime.now());

	                return mapearADto(asistenciaInterfaz.save(nuevaAsistencia));
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


	public List<AsistenciaDto> obtenerTodasAsistencias() {
		return asistenciaInterfaz.findAll().stream().map(this::mapearADto).toList();
	}

	public List<AsistenciaDto> obtenerPorRango(Long alumnoId, LocalDate desde, LocalDate hasta) {
	    return asistenciaInterfaz
	            .findByMatriculacion_Alumno_IdAlumnoAndFechaBetween(alumnoId, desde, hasta)
	            .stream()
	            .map(this::mapearADto)
	            .toList();
	}

	public List<AsistenciaDto> obtenerPorAlumnoEstadoYAnio(Long alumnoId, String estado, String anioEscolar) {

	    String[] partes = anioEscolar.split("/");
	    int anioInicio = Integer.parseInt(partes[0]);
	    int anioFin = Integer.parseInt(partes[1]);

	    LocalDate desde = LocalDate.of(anioInicio, 9, 1);
	    LocalDate hasta = LocalDate.of(anioFin, 6, 30);

	    List<AsistenciaEntidad> lista = asistenciaInterfaz
	            .findByMatriculacion_Alumno_IdAlumnoAndEstadoAndFechaBetween(
	                    alumnoId, estado, desde, hasta);

	    return lista.stream()
	            .map(this::mapearADto)
	            .toList();
	}


	// Consultar por fecha
	public List<AsistenciaDto> obtenerPorFecha(LocalDate fecha) {
		return asistenciaInterfaz.findByFecha(fecha).stream().map(this::mapearADto).collect(Collectors.toList());
	}

	
	
	public Map<String, Integer> obtenerConteoEstados(Long alumnoId, LocalDate desde, LocalDate hasta) {
	    List<AsistenciaEntidad> asistencias = asistenciaInterfaz
	            .findByMatriculacion_Alumno_IdAlumnoAndFechaBetween(alumnoId, desde, hasta);

	    Map<String, Integer> conteo = new HashMap<>();
	    conteo.put("PRESENTE", 0);
	    conteo.put("COMPLETA", 0);
	    conteo.put("SIN SALIDA", 0);
	    conteo.put("FALTA", 0);

	    for (AsistenciaEntidad a : asistencias) {
	        String estado = a.getEstado();
	        conteo.put(estado, conteo.getOrDefault(estado, 0) + 1);
	    }

	    return conteo;
	}

	// Mapeo a DTO
	private AsistenciaDto mapearADto(AsistenciaEntidad entidad) {
	    AsistenciaDto dto = new AsistenciaDto();

	    dto.setIdAsistencia(entidad.getIdAsistencia());
	    dto.setMatriculacionId(entidad.getMatriculacion().getIdMatriculacion());
	    dto.setAlumnoId(entidad.getMatriculacion().getAlumno().getIdAlumno());
	    String nombre = entidad.getMatriculacion().getAlumno().getNombreAlumno();
	    String apellido = entidad.getMatriculacion().getAlumno().getApellidoAlumno();
	    dto.setNombreCompletoAlumno(nombre + " " + apellido);
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
	        AsistenciaEntidad asistencia = asistenciaInterfaz.findById(idAsistencia).orElse(null);
	        if (asistencia == null) return false;

	        asistencia.setHoraEntrada(asistenciaDto.getHoraEntrada());
	        asistencia.setHoraSalida(asistenciaDto.getHoraSalida());

	        // Calcula automáticamente el estado según la lógica
	        asistencia.setEstado(calcularEstado(asistencia.getHoraEntrada(), asistencia.getHoraSalida()));

	        asistencia.setJustificar_modificacion(asistenciaDto.getJustificarModificacion());
	        asistencia.setFechaModificacion(LocalDateTime.now());

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
	
	private String calcularEstado(LocalDateTime horaEntrada, LocalDateTime horaSalida) {
	    if (horaEntrada == null) {
	        return "FALTA";
	    } else if (horaEntrada != null && horaSalida == null) {
	        LocalDateTime ahora = LocalDateTime.now();
	        LocalDateTime cierre = horaEntrada.toLocalDate().atTime(23, 0);

	        if (ahora.isBefore(cierre)) {
	            return "PRESENTE";
	        } else {
	            return "SIN SALIDA";
	        }
	    } else {
	        return "COMPLETA";
	    }
	}


	@PostConstruct
	public void inicializarAsistencias() {
	    LocalDate hoy = LocalDate.now();

	    // Traer solo las asistencias existentes con horaEntrada y sin horaSalida
	    List<AsistenciaEntidad> pendientes = asistenciaInterfaz.findByHoraEntradaIsNotNullAndHoraSalidaIsNull();

	    for (AsistenciaEntidad a : pendientes) {
	        LocalDate fechaAsistencia = a.getFecha();
	        LocalDateTime horaCierre = LocalDateTime.of(fechaAsistencia, LocalTime.of(23, 0));

	        // Solo actualizar si la fecha ya pasó o es hoy después de las 23:00
	        if (fechaAsistencia.isBefore(hoy) || 
	            (fechaAsistencia.isEqual(hoy) && LocalDateTime.now().isAfter(horaCierre))) {
	            a.setHoraSalida(horaCierre);
	            a.setEstado("SIN SALIDA");
	            a.setFechaModificacion(LocalDateTime.now());
	            asistenciaInterfaz.save(a);
	        }
	    }
	}





}