package altair.fichajes_api.servicios;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import altair.fichajes_api.repositorios.FestivoInterfaz;
import altair.fichajes_api.repositorios.GrupoInterfaz;
import altair.fichajes_api.repositorios.MatriculacionInterfaz;
import jakarta.annotation.PostConstruct;

/**
 * 
 * Servicio encargado de gestionar las asistencias de los alumnos.
 * 
 * Permite registrar entradas y salidas, obtener asistencias por curso, grupo,
 * fecha,
 * 
 * y cerrar asistencias del día con generación de faltas.
 */
@Service
public class AsistenciaServicio {

	@Autowired
	private AsistenciaInterfaz asistenciaInterfaz;
	@Autowired
	private MatriculacionInterfaz matriculacionInterfaz;
	@Autowired
	CursoInterfaz cursoInterfaz;
	@Autowired
	GrupoInterfaz grupoInterfaz;
	@Autowired
	private FestivoInterfaz festivoInterfaz;

	private Set<LocalDate> vacaciones = new HashSet<>();
	private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	/**
	 * 
	 * Registra la entrada de un alumno en la fecha actual.
	 * 
	 * @param matriculacionId ID de la matrícula del alumno
	 * 
	 * @return DTO de la asistencia registrada
	 * 
	 * @throws RuntimeException si ya existe la asistencia de hoy
	 */
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

	/**
	 * 
	 * Registra la salida de un alumno en la fecha actual.
	 * 
	 * @param matriculacionId ID de la matrícula del alumno
	 * 
	 * @return DTO de la asistencia actualizada
	 * 
	 * @throws RuntimeException si no existe la asistencia de entrada de hoy
	 */
	public AsistenciaDto ficharSalida(Long matriculacionId) {
		LocalDate hoy = LocalDate.now();

		AsistenciaEntidad asistencia = asistenciaInterfaz
				.findByMatriculacion_IdMatriculacionAndFecha(matriculacionId, hoy)
				.orElseThrow(() -> new RuntimeException("No se encontró asistencia de entrada para hoy"));

		asistencia.setHoraSalida(LocalDateTime.now());
		asistencia.setEstado("COMPLETA");

		return mapearADto(asistenciaInterfaz.save(asistencia));
	}

	/**
	 * Obtiene la asistencia de un curso y grupo en una fecha concreta.
	 * Si la asistencia no existe y la fecha es HOY, crea una falta automática.
	 * Si la fecha es pasada, NO crea faltas nuevas.
	 *
	 * @param curso Nombre del curso
	 * @param grupo Nombre del grupo
	 * @param fecha Fecha a consultar
	 * @return DTO de la asistencia o null si no existe registro en fechas pasadas
	 */
	public List<AsistenciaDto> obtenerAsistenciaPorCursoYGrupoEnFecha(String curso, String grupo, LocalDate fecha) {

	    CursoEntidad cursoEntidad = cursoInterfaz.findByNombreCurso(curso);
	    GrupoEntidad grupoEntidad = grupoInterfaz.findByNombreGrupo(grupo);

	    List<MatriculacionEntidad> matriculas = 
	            matriculacionInterfaz.findByCursoAndGrupo(cursoEntidad, grupoEntidad);

	    List<AsistenciaDto> resultado = new ArrayList<>();

	    for (MatriculacionEntidad m : matriculas) {

	        Optional<AsistenciaEntidad> asistenciaOpt =
	                asistenciaInterfaz.findByMatriculacion_IdMatriculacionAndFecha(
	                        m.getIdMatriculacion(), fecha
	                );

	        if (asistenciaOpt.isPresent()) {
	            resultado.add(mapearADto(asistenciaOpt.get()));
	            continue;
	        }

	        if (!fecha.equals(LocalDate.now())) {
	            continue;
	        }

	        AsistenciaEntidad nueva = new AsistenciaEntidad();
	        nueva.setMatriculacion(m);
	        nueva.setFecha(fecha);
	        nueva.setEstado("FALTA");
	        nueva.setFechaModificacion(LocalDateTime.now());

	        resultado.add(mapearADto(asistenciaInterfaz.save(nueva)));
	    }

	    return resultado;
	}


	/**
	 * Devuelve una lista de asistencias para un curso+grupo+fecha.
	 * Si un alumno no tiene registro:
	 *   - SOLO si la fecha es HOY se crea una falta automática.
	 *   - Si la fecha es pasada, simplemente no aparece.
	 */
	public List<AsistenciaDto> obtenerAsistenciasPorCursoGrupoYFecha(
	        String curso, String grupo, LocalDate fecha) {

	    CursoEntidad cursoEntidad = cursoInterfaz.findByNombreCurso(curso);
	    GrupoEntidad grupoEntidad = grupoInterfaz.findByNombreGrupo(grupo);

	    if (cursoEntidad == null || grupoEntidad == null) {
	        return List.of();
	    }

	    List<MatriculacionEntidad> matriculas =
	            matriculacionInterfaz.findByCursoAndGrupo(cursoEntidad, grupoEntidad);

	    List<AsistenciaDto> resultado = new ArrayList<>();

	    for (MatriculacionEntidad m : matriculas) {

	        Optional<AsistenciaEntidad> asistenciaOpt =
	                asistenciaInterfaz.findByMatriculacion_IdMatriculacionAndFecha(
	                        m.getIdMatriculacion(), fecha);


	        if (asistenciaOpt.isPresent()) {
	            resultado.add(mapearADto(asistenciaOpt.get()));
	            continue;
	        }

	        if (!fecha.equals(LocalDate.now())) {
	            continue;
	        }

	        AsistenciaEntidad nueva = new AsistenciaEntidad();
	        nueva.setMatriculacion(m);
	        nueva.setFecha(fecha);
	        nueva.setEstado("FALTA");
	        nueva.setFechaModificacion(LocalDateTime.now());

	        resultado.add(mapearADto(asistenciaInterfaz.save(nueva)));
	    }

	    return resultado;
	}



	/**
	 * 
	 * Cierra todas las asistencias de un día determinado, asignando hora de salida
	 * y estado "SIN SALIDA"
	 * 
	 * a las asistencias que no tengan salida, y genera faltas.
	 * 
	 * @param fecha Fecha a cerrar
	 */
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

	/**
	 * 
	 * Obtiene todas las asistencias registradas.
	 * 
	 * @return Lista de DTOs de asistencia
	 */
	public List<AsistenciaDto> obtenerTodasAsistencias() {
		return asistenciaInterfaz.findAll().stream().map(this::mapearADto).toList();
	}

	/**
	 * 
	 * Obtiene las asistencias de un alumno dentro de un rango de fechas.
	 * 
	 * @param alumnoId ID del alumno
	 * 
	 * @param desde    Fecha de inicio
	 * 
	 * @param hasta    Fecha de fin
	 * 
	 * @return Lista de DTOs de asistencia
	 */
	public List<AsistenciaDto> obtenerPorRango(Long alumnoId, LocalDate desde, LocalDate hasta) {
		return asistenciaInterfaz.findByMatriculacion_Alumno_IdAlumnoAndFechaBetween(alumnoId, desde, hasta).stream()
				.map(this::mapearADto).toList();
	}

	/**
	 * 
	 * Obtiene las asistencias de un alumno filtrando por estado y año escolar.
	 * 
	 * @param alumnoId    ID del alumno
	 * 
	 * @param estado      Estado de la asistencia (PRESENTE, FALTA, FESTIVO, etc.)
	 * 
	 * @param anioEscolar Año escolar en formato "AAAA/AAAA"
	 * 
	 * @return Lista de DTOs de asistencia
	 */
	public List<AsistenciaDto> obtenerPorAlumnoEstadoYAnio(Long alumnoId, String estado, String anioEscolar) {
		String[] partes = anioEscolar.split("/");
		int anioInicio = Integer.parseInt(partes[0]);
		int anioFin = Integer.parseInt(partes[1]);

		LocalDate desde = LocalDate.of(anioInicio, 9, 1);
		LocalDate hasta = LocalDate.of(anioFin, 6, 30);

		List<AsistenciaEntidad> lista = asistenciaInterfaz
				.findByMatriculacion_Alumno_IdAlumnoAndEstadoAndFechaBetween(alumnoId, estado, desde, hasta);

		return lista.stream().map(this::mapearADto).toList();
	}

	/**
	 * Obtiene todas las asistencias de una fecha específica.
	 * @param fecha Fecha a consultar
	 * @return Lista de DTOs de asistencia
	 */
	public List<AsistenciaDto> obtenerPorFecha(LocalDate fecha) {
	    return asistenciaInterfaz.findByFecha(fecha)
	            .stream()
	            .map(this::mapearADto)
	            .collect(Collectors.toList());
	}

	/**
	 * Obtiene el conteo de asistencias por estado de un alumno en un rango de fechas.
	 * @param alumnoId ID del alumno
	 * @param desde Fecha de inicio
	 * @param hasta Fecha de fin
	 * @return Mapa con clave = estado y valor = cantidad de asistencias
	 */
	public Map<String, Integer> obtenerConteoEstados(Long alumnoId, LocalDate desde, LocalDate hasta) {
	    List<AsistenciaEntidad> asistencias = asistenciaInterfaz
	            .findByMatriculacion_Alumno_IdAlumnoAndFechaBetween(alumnoId, desde, hasta);

	    Map<String, Integer> conteo = new HashMap<>();
	    conteo.put("PRESENTE", 0);
	    conteo.put("COMPLETA", 0);
	    conteo.put("SIN SALIDA", 0);
	    conteo.put("FALTA", 0);
	    conteo.put("FESTIVO", 0);

	    for (AsistenciaEntidad a : asistencias) {
	        String estado = a.getEstado();
	        conteo.put(estado, conteo.getOrDefault(estado, 0) + 1);
	    }

	    return conteo;
	}

	/**
	 * Convierte una entidad Asistencia a su DTO correspondiente.
	 * @param entidad Entidad de asistencia
	 * @return DTO de asistencia
	 */
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

	/**
	 * Modifica una asistencia existente con los datos del DTO proporcionado.
	 * Calcula automáticamente el estado según hora de entrada y salida.
	 * @param idAsistencia ID de la asistencia a modificar
	 * @param asistenciaDto DTO con los nuevos datos
	 * @return true si la asistencia fue modificada correctamente, false si no se encontró
	 */
	public boolean modificarAsistencia(Long idAsistencia, AsistenciaDto asistenciaDto) {
	    boolean esModificado = false;
	    try {
	        AsistenciaEntidad asistencia = asistenciaInterfaz.findById(idAsistencia).orElse(null);
	        if (asistencia == null) return false;

	        asistencia.setHoraEntrada(asistenciaDto.getHoraEntrada());
	        asistencia.setHoraSalida(asistenciaDto.getHoraSalida());
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

	/**
	 * Verifica si una fecha es un día festivo.
	 * @param fecha Fecha a verificar
	 * @return true si es festivo, false si no
	 */
	private boolean esDiaFestivo(LocalDate fecha) {
	    return festivoInterfaz.existsByFecha(fecha);
	}

	/**
	 * Genera faltas para todos los alumnos en una fecha específica,
	 * excluyendo fines de semana y días festivos.
	 * @param fecha Fecha para generar faltas
	 */
	public void generarFaltas(LocalDate fecha) {
	    DayOfWeek dia = fecha.getDayOfWeek();
	    if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) return;
	    if (esDiaFestivo(fecha)) return;

	    List<MatriculacionEntidad> matriculas = matriculacionInterfaz.findAll();
	    for (MatriculacionEntidad matricula : matriculas) {
	        boolean asistenciaExiste = asistenciaInterfaz
	                .findByMatriculacion_IdMatriculacionAndFecha(matricula.getIdMatriculacion(), fecha)
	                .isPresent();
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

	/**
	 * Calcula el estado de la asistencia según hora de entrada y salida.
	 * @param horaEntrada Hora de entrada
	 * @param horaSalida Hora de salida
	 * @return Estado calculado: "FALTA", "PRESENTE", "SIN SALIDA" o "COMPLETA"
	 */
	private String calcularEstado(LocalDateTime horaEntrada, LocalDateTime horaSalida) {
	    if (horaEntrada == null) return "FALTA";
	    else if (horaEntrada != null && horaSalida == null) {
	        LocalDateTime ahora = LocalDateTime.now();
	        LocalDateTime cierre = horaEntrada.toLocalDate().atTime(23, 0);
	        return ahora.isBefore(cierre) ? "PRESENTE" : "SIN SALIDA";
	    } else {
	        return "COMPLETA";
	    }
	}

	/**
	 * Inicializa el sistema al arrancar, cargando vacaciones y creando asistencias del día.
	 */
	@PostConstruct
	public void inicializarSistema() {
	    vacaciones = new HashSet<>(cargarVacaciones("src/main/resources/vacaciones.txt"));

	    LocalDate hoy = LocalDate.now();
	    crearAsistenciasDelDia(hoy);

	    List<AsistenciaEntidad> pendientes = asistenciaInterfaz
	            .findByHoraEntradaIsNotNullAndHoraSalidaIsNull();
	    for (AsistenciaEntidad a : pendientes) {
	        LocalDate fechaAsistencia = a.getFecha();
	        LocalDateTime horaCierre = LocalDateTime.of(fechaAsistencia, LocalTime.of(23, 0));
	        if (fechaAsistencia.isBefore(hoy)
	                || (fechaAsistencia.isEqual(hoy) && LocalDateTime.now().isAfter(horaCierre))) {
	            a.setHoraSalida(horaCierre);
	            a.setEstado("SIN SALIDA");
	            a.setFechaModificacion(LocalDateTime.now());
	            asistenciaInterfaz.save(a);
	        }
	    }
	}

	/**
	 * Carga vacaciones desde un archivo de texto.
	 * @param rutaArchivo Ruta del archivo de vacaciones
	 * @return Lista de fechas de vacaciones
	 */
	public List<LocalDate> cargarVacaciones(String rutaArchivo) {
	    List<LocalDate> vacaciones = new ArrayList<>();
	    try {
	        String contenido = Files.readString(Paths.get(rutaArchivo));
	        String[] fechas = contenido.split(";");
	        for (String fechaStr : fechas) {
	            LocalDate fecha = LocalDate.parse(fechaStr.trim(), FORMATO_FECHA);
	            vacaciones.add(fecha);
	        }
	    } catch (IOException e) {
	        System.out.println("Error al leer el archivo: " + e.getMessage());
	    } catch (Exception e) {
	        System.out.println("Error al parsear fechas: " + e.getMessage());
	    }
	    return vacaciones;
	}

	/**
	 * Verifica si una fecha está dentro del periodo de vacaciones cargado.
	 * @param fecha Fecha a verificar
	 * @return true si es vacaciones
	 */
	private boolean esVacaciones(LocalDate fecha) {
	    return vacaciones.contains(fecha);
	}

	/**
	 * Crea las asistencias del día, evitando fines de semana y vacaciones,
	 * y asignando estado "FESTIVO" o "FALTA" según corresponda.
	 * @param fecha Fecha para crear asistencias
	 */
	private void crearAsistenciasDelDia(LocalDate fecha) {
	    DayOfWeek dia = fecha.getDayOfWeek();
	    if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) return;
	    if (esVacaciones(fecha)) return;

	    boolean esFestivo = esDiaFestivo(fecha);
	    List<MatriculacionEntidad> matriculas = matriculacionInterfaz.findAll();

	    for (MatriculacionEntidad m : matriculas) {
	        String anioEscolar = m.getAnioEscolar();
	        if (anioEscolar == null || !anioEscolar.contains("-")) continue;

	        String[] partes = anioEscolar.split("-");
	        int anioInicio = Integer.parseInt(partes[0].trim());
	        int anioFin = Integer.parseInt(partes[1].trim());

	        LocalDate inicioCurso = LocalDate.of(anioInicio, 9, 1);
	        LocalDate finCurso = LocalDate.of(anioFin, 6, 30);
	        if (fecha.isBefore(inicioCurso) || fecha.isAfter(finCurso)) continue;

	        boolean existe = asistenciaInterfaz
	                .findByMatriculacion_IdMatriculacionAndFecha(m.getIdMatriculacion(), fecha).isPresent();
	        if (existe) continue;

	        AsistenciaEntidad nueva = new AsistenciaEntidad();
	        nueva.setMatriculacion(m);
	        nueva.setFecha(fecha);
	        nueva.setFechaModificacion(LocalDateTime.now());
	        nueva.setEstado(esFestivo ? "FESTIVO" : "FALTA");

	        asistenciaInterfaz.save(nueva);
	    }
	}


}
