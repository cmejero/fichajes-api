package altair.fichajes_api.controladores;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import altair.fichajes_api.dtos.AlumnoConMatriculacionDto;
import altair.fichajes_api.dtos.AlumnoDto;
import altair.fichajes_api.dtos.AsistenciaDto;
import altair.fichajes_api.dtos.CursoDto;
import altair.fichajes_api.dtos.GrupoDto;
import altair.fichajes_api.dtos.MatriculacionDto;
import altair.fichajes_api.entidad.AsistenciaEntidad;
import altair.fichajes_api.entidad.MatriculacionEntidad;
import altair.fichajes_api.logs.Logs;
import altair.fichajes_api.repositorios.AsistenciaInterfaz;
import altair.fichajes_api.servicios.AlumnoServicio;
import altair.fichajes_api.servicios.AsistenciaServicio;
import altair.fichajes_api.servicios.CursoServicio;
import altair.fichajes_api.servicios.GrupoServicio;
import altair.fichajes_api.servicios.MatriculacionServicio;

/*
 * Clase que se encarga de los metodos CRUD de la API
 */
@RestController
@RequestMapping("/api")
public class Controlador {

	@Autowired
	private AlumnoServicio alumnoServicio;
	@Autowired
	private CursoServicio cursoServicio;
	@Autowired
	private GrupoServicio grupoServicio;
	@Autowired
	private MatriculacionServicio matriculacionServicio;
	@Autowired
	private AsistenciaServicio asistenciaServicio;
	@Autowired
	AsistenciaInterfaz asistenciaInterfaz;


	/* METODOS CRUD DE LA TABLA ALUMNO */

	/**
	 * Obtiene los datos de un alumno por su ID.
	 *
	 * @param id ID del alumno a consultar.
	 * @return ResponseEntity con AlumnoDto si se encuentra, 404 si no, 500 en caso de error.
	 */
	@GetMapping("/alumno/{idAlumno}")
	public ResponseEntity<AlumnoDto> obtenerAlumno(@PathVariable("idAlumno") Long id) {
	    Logs.ficheroLog("➡️ Solicitud para obtener alumno con ID: " + id);
	    try {
	        AlumnoDto alumno = alumnoServicio.obtenerAlumnoPorId(id);
	        if (alumno != null) {
	            Logs.ficheroLog("✅ Alumno encontrado con ID: " + id);
	            return ResponseEntity.ok(alumno);
	        } else {
	            Logs.ficheroLog("⚠️ No se encontró alumno con ID: " + id);
	            return ResponseEntity.notFound().build();
	        }
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al obtener alumno con ID " + id + ": " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	/**
	 * Lista todos los alumnos registrados.
	 *
	 * @return ArrayList de AlumnoDto.
	 */
	@GetMapping("/alumnos")
	public ArrayList<AlumnoDto> listaAlumnos() {
	    Logs.ficheroLog("➡️ Solicitud para listar todos los alumnos");
	    return alumnoServicio.obtenerTodosAlumnos();
	}

	/**
	 * Guarda un alumno con su matriculación asociada.
	 *
	 * @param dto Objeto AlumnoConMatriculacionDto con los datos a guardar.
	 * @return ResponseEntity con el alumno guardado o mensaje de error.
	 */
	@PostMapping("/guardarAlumno")
	public ResponseEntity<?> guardarAlumno(@RequestBody AlumnoConMatriculacionDto dto) {
	    Logs.ficheroLog("➡️ Solicitud para guardar alumno: " + dto.toString());
	    try {
	        AlumnoConMatriculacionDto alumnoGuardado = alumnoServicio.guardarAlumnoConMatriculacion(dto);
	        Logs.ficheroLog("✅ Alumno guardado exitosamente con ID: " + alumnoGuardado.getIdAlumno());
	        return ResponseEntity.ok(alumnoGuardado);
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al guardar alumno: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al guardar alumno: " + e.getMessage());
	    }
	}

	/**
	 * Modifica los datos de un alumno existente.
	 *
	 * @param idAlumno ID del alumno a modificar.
	 * @param dto      Objeto AlumnoDto con los datos actualizados.
	 * @return ResponseEntity con resultado de la operación.
	 */
	@PutMapping("/modificarAlumno/{idAlumno}")
	public ResponseEntity<String> modificarAlumno(@PathVariable Long idAlumno, @RequestBody AlumnoDto dto) {
	    Logs.ficheroLog("➡️ Solicitud para modificar alumno con ID: " + idAlumno);
	    try {
	        boolean modificado = alumnoServicio.modificarAlumno(idAlumno, dto);
	        if (modificado) {
	            Logs.ficheroLog("✅ Alumno modificado correctamente con ID: " + idAlumno);
	            return ResponseEntity.ok("Alumno modificado correctamente");
	        } else {
	            Logs.ficheroLog("⚠️ Alumno no encontrado con ID: " + idAlumno);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alumno no encontrado");
	        }
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al modificar alumno con ID " + idAlumno + ": " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar alumno");
	    }
	}

	/**
	 * Elimina un alumno por su ID.
	 *
	 * @param idAlumno ID del alumno a eliminar.
	 * @return ResponseEntity con resultado de la operación.
	 */
	@DeleteMapping("/eliminarAlumno/{idAlumno}")
	public ResponseEntity<String> eliminarAlumno(@PathVariable Long idAlumno) {
	    Logs.ficheroLog("➡️ Solicitud para eliminar alumno con ID: " + idAlumno);
	    try {
	        boolean eliminado = alumnoServicio.borrarAlumno(idAlumno);
	        if (eliminado) {
	            Logs.ficheroLog("✅ Alumno eliminado correctamente con ID: " + idAlumno);
	            return ResponseEntity.ok("Alumno eliminado correctamente");
	        } else {
	            Logs.ficheroLog("⚠️ Alumno no encontrado con ID: " + idAlumno);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alumno no encontrado");
	        }
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al eliminar alumno con ID " + idAlumno + ": " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar alumno");
	    }
	}

	/**
	 * Obtiene un alumno junto con su matriculación asociada.
	 *
	 * @param idAlumno ID del alumno a consultar.
	 * @return ResponseEntity con AlumnoConMatriculacionDto si existe, 404 si no.
	 */
	@GetMapping("/alumno/{idAlumno}/conMatriculacion")
	public ResponseEntity<?> obtenerAlumnoConMatriculacion(@PathVariable Long idAlumno) {
	    Logs.ficheroLog("➡️ Solicitud para obtener alumno con matriculación ID: " + idAlumno);
	    AlumnoConMatriculacionDto dto = alumnoServicio.obtenerAlumnoConMatriculacion(idAlumno);

	    if (dto == null) {
	        Logs.ficheroLog("⚠️ No se encontró el alumno o no tiene matriculación asociada ID: " + idAlumno);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("No se encontró el alumno o no tiene matriculación asociada");
	    }

	    Logs.ficheroLog("✅ Alumno con matriculación encontrado ID: " + idAlumno);
	    return ResponseEntity.ok(dto);
	}

	/* METODOS CRUD DE LA TABLA CURSOS */

	/**
	 * Lista todos los cursos registrados.
	 *
	 * @return ArrayList de CursoDto. Devuelve lista vacía si ocurre un error.
	 */
	@GetMapping("/cursos")
	public ArrayList<CursoDto> listaCursos() {
	    Logs.ficheroLog("➡️ Solicitud para listar todos los cursos");
	    try {
	        return cursoServicio.obtenerTodosCursos();
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al listar cursos: " + e.getMessage());
	        return new ArrayList<>();
	    }
	}

	/**
	 * Obtiene los datos de un curso por su ID.
	 *
	 * @param idCurso ID del curso a consultar.
	 * @return ResponseEntity con CursoDto si se encuentra, 404 si no, 500 en caso de error.
	 */
	@GetMapping("/curso/{idCurso}")
	public ResponseEntity<CursoDto> obtenerCurso(@PathVariable Long idCurso) {
	    Logs.ficheroLog("➡️ Solicitud para obtener curso con ID: " + idCurso);
	    try {
	        CursoDto curso = cursoServicio.obtenerCursoPorId(idCurso);
	        if (curso != null) {
	            Logs.ficheroLog("✅ Curso encontrado con ID: " + idCurso);
	            return ResponseEntity.ok(curso);
	        } else {
	            Logs.ficheroLog("⚠️ No se encontró curso con ID: " + idCurso);
	            return ResponseEntity.notFound().build();
	        }
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al obtener curso con ID " + idCurso + ": " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	/**
	 * Guarda un nuevo curso en el sistema.
	 *
	 * @param dto Objeto CursoDto con los datos del curso a guardar.
	 * @return ResponseEntity con el curso guardado o mensaje de error.
	 */
	@PostMapping("/guardarCurso")
	public ResponseEntity<?> guardarCurso(@RequestBody CursoDto dto) {
	    Logs.ficheroLog("➡️ Solicitud para guardar curso: " + dto.getNombreCurso());
	    try {
	        CursoDto cursoGuardado = cursoServicio.mapearACursoDTO(cursoServicio.guardarCurso(dto));
	        Logs.ficheroLog("✅ Curso guardado exitosamente con ID: " + cursoGuardado.getIdCurso());
	        return ResponseEntity.ok(cursoGuardado);
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al guardar curso: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar curso");
	    }
	}

	/**
	 * Elimina un curso por su ID.
	 *
	 * @param idCurso ID del curso a eliminar.
	 * @return ResponseEntity con resultado de la operación.
	 */
	@DeleteMapping("/eliminarCurso/{idCurso}")
	public ResponseEntity<String> eliminarCurso(@PathVariable Long idCurso) {
	    Logs.ficheroLog("➡️ Solicitud para eliminar curso con ID: " + idCurso);
	    try {
	        boolean eliminado = cursoServicio.borrarCurso(idCurso);
	        if (eliminado) {
	            Logs.ficheroLog("✅ Curso eliminado correctamente con ID: " + idCurso);
	            return ResponseEntity.ok("Curso eliminado correctamente");
	        } else {
	            Logs.ficheroLog("⚠️ Curso no encontrado con ID: " + idCurso);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso no encontrado");
	        }
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al eliminar curso con ID " + idCurso + ": " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar curso");
	    }
	}

	/* METODOS CRUD DE LA TABLA GRUPO */

	/**
	 * Lista todos los grupos registrados.
	 *
	 * @return ArrayList de GrupoDto. Devuelve lista vacía si ocurre un error.
	 */
	@GetMapping("/grupos")
	public ArrayList<GrupoDto> listaGrupos() {
	    Logs.ficheroLog("➡️ Solicitud para listar todos los grupos");
	    try {
	        return grupoServicio.obtenerTodosGrupos();
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al listar grupos: " + e.getMessage());
	        return new ArrayList<>();
	    }
	}

	/**
	 * Obtiene los datos de un grupo por su ID.
	 *
	 * @param idGrupo ID del grupo a consultar.
	 * @return ResponseEntity con GrupoDto si se encuentra, 404 si no, 500 en caso de error.
	 */
	@GetMapping("/grupo/{idGrupo}")
	public ResponseEntity<GrupoDto> obtenerGrupo(@PathVariable Long idGrupo) {
	    Logs.ficheroLog("➡️ Solicitud para obtener grupo con ID: " + idGrupo);
	    try {
	        GrupoDto grupo = grupoServicio.obtenerGrupoPorId(idGrupo);
	        if (grupo != null) {
	            Logs.ficheroLog("✅ Grupo encontrado con ID: " + idGrupo);
	            return ResponseEntity.ok(grupo);
	        } else {
	            Logs.ficheroLog("⚠️ No se encontró grupo con ID: " + idGrupo);
	            return ResponseEntity.notFound().build();
	        }
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al obtener grupo con ID " + idGrupo + ": " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	/**
	 * Obtiene todos los grupos de un curso específico por su ID.
	 *
	 * @param idCurso ID del curso.
	 * @return ResponseEntity con lista de GrupoDto o 404 si no hay grupos.
	 */
	@GetMapping("/grupos/curso/{idCurso}")
	public ResponseEntity<List<GrupoDto>> obtenerGruposPorCurso(@PathVariable Long idCurso) {
	    Logs.ficheroLog("➡️ Solicitud para obtener grupos del curso con ID: " + idCurso);
	    try {
	        List<GrupoDto> grupos = grupoServicio.obtenerGruposPorCurso(idCurso);
	        if (grupos.isEmpty()) {
	            Logs.ficheroLog("⚠️ No se encontraron grupos para el curso ID: " + idCurso);
	            return ResponseEntity.notFound().build();
	        }
	        Logs.ficheroLog("✅ Se encontraron " + grupos.size() + " grupos para el curso ID: " + idCurso);
	        return ResponseEntity.ok(grupos);
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al obtener grupos por curso: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	/**
	 * Guarda un nuevo grupo en el sistema.
	 *
	 * @param dto Objeto GrupoDto con los datos del grupo a guardar.
	 * @return ResponseEntity con el grupo guardado o mensaje de error.
	 */
	@PostMapping("/guardarGrupo")
	public ResponseEntity<?> guardarGrupo(@RequestBody GrupoDto dto) {
	    Logs.ficheroLog("➡️ Solicitud para guardar grupo: " + dto.getNombreGrupo());
	    try {
	        GrupoDto grupoGuardado = grupoServicio.mapearAGrupoDTO(grupoServicio.guardarGrupo(dto));
	        Logs.ficheroLog("✅ Grupo guardado exitosamente con ID: " + grupoGuardado.getIdGrupo());
	        return ResponseEntity.ok(grupoGuardado);
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al guardar grupo: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar grupo");
	    }
	}

	/**
	 * Elimina un grupo por su ID.
	 *
	 * @param idGrupo ID del grupo a eliminar.
	 * @return ResponseEntity con resultado de la operación.
	 */
	@DeleteMapping("/eliminarGrupo/{idGrupo}")
	public ResponseEntity<String> eliminarGrupo(@PathVariable Long idGrupo) {
	    Logs.ficheroLog("➡️ Solicitud para eliminar grupo con ID: " + idGrupo);
	    try {
	        boolean eliminado = grupoServicio.borrarGrupo(idGrupo);
	        if (eliminado) {
	            Logs.ficheroLog("✅ Grupo eliminado correctamente con ID: " + idGrupo);
	            return ResponseEntity.ok("Grupo eliminado correctamente");
	        } else {
	            Logs.ficheroLog("⚠️ Grupo no encontrado con ID: " + idGrupo);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Grupo no encontrado");
	        }
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al eliminar grupo con ID " + idGrupo + ": " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar grupo");
	    }
	}

	/* METODOS CRUD DE LA TABLA MATRICULACION */

	/**
	 * Crea una nueva matriculación.
	 *
	 * @param dto Objeto MatriculacionDto con los datos de la matriculación a crear.
	 * @return ResponseEntity con la entidad creada o mensaje de error.
	 */
	@PostMapping("/guardarMatriculacion")
	public ResponseEntity<?> crearMatriculacion(@RequestBody MatriculacionDto dto) {
	    Logs.ficheroLog("➡️ Solicitud para crear matriculación: " + dto.toString());
	    try {
	        MatriculacionEntidad nueva = matriculacionServicio.crearMatriculacion(dto);
	        Logs.ficheroLog("✅ Matriculación creada con ID: " + nueva.getIdMatriculacion());
	        return ResponseEntity.ok(nueva);
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al crear matriculación: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body("Error al crear la matriculación: " + e.getMessage());
	    }
	}

	/**
	 * Obtiene la lista de todas las matriculaciones.
	 *
	 * @return Lista de MatriculacionDto.
	 */
	@GetMapping("/matriculaciones")
	public List<MatriculacionDto> listarMatriculaciones() {
	    Logs.ficheroLog("➡️ Solicitud para listar todas las matriculaciones");
	    try {
	        return matriculacionServicio.obtenerTodasDto();
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al listar matriculaciones: " + e.getMessage());
	        return new ArrayList<>();
	    }
	}

	/**
	 * Obtiene una matriculación por su ID.
	 *
	 * @param idMatriculacion ID de la matriculación.
	 * @return ResponseEntity con la matriculación si se encuentra, 404 si no, 500 en caso de error.
	 */
	@GetMapping("/matriculacion/{idMatriculacion}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long idMatriculacion) {
	    Logs.ficheroLog("➡️ Solicitud para obtener matriculación con ID: " + idMatriculacion);
	    try {
	        return matriculacionServicio.obtenerPorId(idMatriculacion)
	                .map(m -> {
	                    Logs.ficheroLog("✅ Matriculación encontrada con ID: " + idMatriculacion);
	                    return ResponseEntity.ok(m);
	                })
	                .orElseGet(() -> {
	                    Logs.ficheroLog("⚠️ Matriculación no encontrada con ID: " + idMatriculacion);
	                    return ResponseEntity.notFound().build();
	                });
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al obtener matriculación con ID " + idMatriculacion + ": " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	
	
	/**
	 * Lista todas las matriculaciones de un alumno específico.
	 *
	 * @param idAlumno ID del alumno.
	 * @return Lista de DTOs de matriculaciones asociadas al alumno.
	 */
	@GetMapping("/matriculaciones/alumno/{idAlumno}")
	public List<MatriculacionDto> listarMatriculacionesPorAlumno(@PathVariable("idAlumno") Long idAlumno) {
	    Logs.ficheroLog("➡️ Solicitud para listar matriculaciones del alumno ID: " + idAlumno);
	    try {
	        return matriculacionServicio.obtenerPorAlumno(idAlumno);
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al listar matriculaciones por alumno: " + e.getMessage());
	        return new ArrayList<>();
	    }
	}


	/**
	 * Elimina una matriculación por su ID.
	 *
	 * @param idMatriculacion ID de la matriculación a eliminar.
	 * @return ResponseEntity con resultado de la operación.
	 */
	@DeleteMapping("/eliminarMatriculacion/{idMatriculacion}")
	public ResponseEntity<?> eliminarMatriculacion(@PathVariable Long idMatriculacion) {
	    Logs.ficheroLog("➡️ Solicitud para eliminar matriculación con ID: " + idMatriculacion);
	    try {
	        boolean eliminado = matriculacionServicio.eliminarMatriculacion(idMatriculacion);
	        if (eliminado) {
	            Logs.ficheroLog("✅ Matriculación eliminada correctamente con ID: " + idMatriculacion);
	            return ResponseEntity.ok("Matriculación eliminada correctamente");
	        } else {
	            Logs.ficheroLog("⚠️ Matriculación no encontrada con ID: " + idMatriculacion);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Matriculación no encontrada");
	        }
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al eliminar matriculación con ID " + idMatriculacion + ": " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar matriculación");
	    }
	}


	/* METODOS CRUD DE LA TABLA ASISTENCIA */
	/**
	 * Ficha la entrada de un alumno para la matriculación indicada.
	 *
	 * @param matriculacionId ID de la matriculación.
	 * @return ResponseEntity con la asistencia registrada o mensaje de error.
	 */
	@PostMapping("/asistencia/entrada/{matriculacionId}")
	public ResponseEntity<?> ficharEntrada(@PathVariable Long matriculacionId) {
	    Logs.ficheroLog("➡️ Solicitud para fichar entrada matriculación ID: " + matriculacionId);
	    try {
	        AsistenciaDto asistencia = asistenciaServicio.ficharEntrada(matriculacionId);
	        Logs.ficheroLog("✅ Entrada fichada para matriculación ID: " + matriculacionId);
	        return ResponseEntity.ok(asistencia);
	    } catch (RuntimeException e) {
	        Logs.ficheroLog("❌ Error al fichar entrada: " + e.getMessage());
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}

	/**
	 * Ficha la salida de un alumno para la matriculación indicada.
	 *
	 * @param matriculacionId ID de la matriculación.
	 * @return ResponseEntity con la asistencia actualizada o mensaje de error.
	 */
	@PutMapping("/asistencia/salida/{matriculacionId}")
	public ResponseEntity<?> ficharSalida(@PathVariable Long matriculacionId) {
	    Logs.ficheroLog("➡️ Solicitud para fichar salida matriculación ID: " + matriculacionId);
	    try {
	        AsistenciaDto asistencia = asistenciaServicio.ficharSalida(matriculacionId);
	        Logs.ficheroLog("✅ Salida fichada para matriculación ID: " + matriculacionId);
	        return ResponseEntity.ok(asistencia);
	    } catch (RuntimeException e) {
	        Logs.ficheroLog("❌ Error al fichar salida: " + e.getMessage());
	        return ResponseEntity.badRequest().body(e.getMessage());
	    }
	}

	/**
	 * Modifica una asistencia existente.
	 *
	 * @param idAsistencia ID de la asistencia a modificar.
	 * @param asistenciaDto DTO con los nuevos datos de la asistencia.
	 * @return ResponseEntity con la asistencia modificada o mensaje de error.
	 */
	@PutMapping("/modificarAsistencia/{idAsistencia}")
	public ResponseEntity<?> modificarAsistencia(
	        @PathVariable Long idAsistencia,
	        @RequestBody AsistenciaDto asistenciaDto) {

	    Logs.ficheroLog("➡️ Solicitud para modificar asistencia con ID: " + idAsistencia);

	    try {
	        boolean resultado = asistenciaServicio.modificarAsistencia(idAsistencia, asistenciaDto);
	        Logs.ficheroLog("Resultado de modificación asistencia ID " + idAsistencia + ": " + resultado);

	        if (resultado) {
	            AsistenciaEntidad asistenciaActualizada = asistenciaInterfaz.findById(idAsistencia).orElse(null);
	            if (asistenciaActualizada == null) {
	                Logs.ficheroLog("⚠️ Asistencia no encontrada ID: " + idAsistencia);
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Asistencia no encontrada");
	            }

	            AsistenciaDto dto = new AsistenciaDto();
	            dto.setIdAsistencia(asistenciaActualizada.getIdAsistencia());
	            dto.setEstado(asistenciaActualizada.getEstado());
	            dto.setHoraEntrada(asistenciaActualizada.getHoraEntrada());
	            dto.setHoraSalida(asistenciaActualizada.getHoraSalida());
	            dto.setJustificarModificacion(asistenciaActualizada.getJustificar_modificacion());
	            dto.setFecha(asistenciaActualizada.getFecha());

	            Logs.ficheroLog("✅ Asistencia modificada correctamente ID: " + idAsistencia);
	            return ResponseEntity.ok(dto);
	        } else {
	            Logs.ficheroLog("⚠️ Asistencia no encontrada ID: " + idAsistencia);
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Asistencia no encontrada");
	        }
	    } catch (Exception e) {
	        Logs.ficheroLog("❌ Error al modificar asistencia ID " + idAsistencia + ": " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar asistencia");
	    }
	}

	/**
	 * Obtiene las asistencias de hoy para un curso y grupo.
	 *
	 * @param curso Nombre del curso.
	 * @param grupo Nombre del grupo.
	 * @return Lista de asistencias del día actual.
	 */
	@GetMapping("/asistencia/{curso}/{grupo}")
	public ResponseEntity<List<AsistenciaDto>> obtenerAsistenciaDeHoy(
	        @PathVariable String curso,
	        @PathVariable String grupo) {

	    Logs.ficheroLog("➡️ Solicitud para obtener asistencias de hoy Curso: " + curso + ", Grupo: " + grupo);
	    LocalDate hoy = LocalDate.now();
	    List<AsistenciaDto> lista = asistenciaServicio.obtenerAsistenciaPorCursoYGrupoEnFecha(curso, grupo, hoy);
	    Logs.ficheroLog("✅ Asistencias obtenidas: " + lista.size());
	    return ResponseEntity.ok(lista);
	}

	/**
	 * Obtiene asistencias por curso, grupo y fecha específica.
	 *
	 * @param curso Nombre del curso.
	 * @param grupo Nombre del grupo.
	 * @param fecha Fecha para filtrar las asistencias.
	 * @return ResponseEntity con la lista de asistencias encontradas.
	 */
	@GetMapping("/asistencia/curso-grupo")
	public ResponseEntity<List<AsistenciaDto>> obtenerAsistenciasPorCursoGrupoYFechaUsuario(
	        @RequestParam String curso,
	        @RequestParam String grupo,
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

	    Logs.ficheroLog("➡️ Solicitud para obtener asistencias Curso: " + curso + ", Grupo: " + grupo + ", Fecha: " + fecha);
	    List<AsistenciaDto> asistencias = asistenciaServicio.obtenerAsistenciasPorCursoGrupoYFecha(curso, grupo, fecha);
	    Logs.ficheroLog("✅ Asistencias obtenidas: " + asistencias.size());
	    return ResponseEntity.ok(asistencias);
	}

	/**
	 * Obtiene todas las asistencias registradas.
	 *
	 * @return ResponseEntity con la lista completa de asistencias.
	 */
	@GetMapping("/asistencias")
	public ResponseEntity<List<AsistenciaDto>> verTodasAsistencias() {
	    Logs.ficheroLog("➡️ Solicitud para obtener todas las asistencias");
	    List<AsistenciaDto> lista = asistenciaServicio.obtenerTodasAsistencias();
	    Logs.ficheroLog("✅ Total asistencias obtenidas: " + lista.size());
	    return ResponseEntity.ok(lista);
	}

	/**
	 * Lista asistencias por fecha específica.
	 *
	 * @param fecha Fecha para filtrar las asistencias.
	 * @return ResponseEntity con la lista de asistencias en la fecha indicada.
	 */
	@GetMapping("/asistencia/fecha/{fecha}")
	public ResponseEntity<?> listarPorFecha(
	        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

	    Logs.ficheroLog("➡️ Solicitud para obtener asistencias por fecha: " + fecha);
	    List<AsistenciaDto> lista = asistenciaServicio.obtenerPorFecha(fecha);
	    Logs.ficheroLog("✅ Asistencias obtenidas: " + lista.size());
	    return ResponseEntity.ok(lista);
	}

	/**
	 * Obtiene asistencias de un alumno por estado y año escolar.
	 *
	 * @param alumnoId ID del alumno.
	 * @param estado Estado de la asistencia a filtrar (ej. PRESENTE, FALTA).
	 * @param anioEscolar Año escolar para filtrar.
	 * @return ResponseEntity con la lista de asistencias encontradas.
	 */
	@GetMapping("/asistencia/alumno-estado")
	public ResponseEntity<List<AsistenciaDto>> obtenerPorAlumnoEstadoYAnio(
	        @RequestParam Long alumnoId,
	        @RequestParam String estado,
	        @RequestParam String anioEscolar) {

	    Logs.ficheroLog("➡️ Solicitud para obtener asistencias AlumnoID: " + alumnoId + ", Estado: " + estado + ", Año escolar: " + anioEscolar);
	    List<AsistenciaDto> lista = asistenciaServicio.obtenerPorAlumnoEstadoYAnio(alumnoId, estado, anioEscolar);
	    Logs.ficheroLog("✅ Asistencias obtenidas: " + lista.size());
	    return ResponseEntity.ok(lista);
	}

	/**
	 * Obtiene asistencias de un alumno en un rango de fechas.
	 *
	 * @param alumnoId ID del alumno.
	 * @param desde Fecha de inicio del rango.
	 * @param hasta Fecha final del rango.
	 * @return ResponseEntity con la lista de asistencias encontradas dentro del rango.
	 */
	@GetMapping("/asistencia/rango/{alumnoId}")
	public ResponseEntity<List<AsistenciaDto>> obtenerPorRango(
	        @PathVariable Long alumnoId,
	        @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
	        @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

	    Logs.ficheroLog("➡️ Solicitud para obtener asistencias AlumnoID: " + alumnoId + " desde " + desde + " hasta " + hasta);
	    List<AsistenciaDto> asistencias = asistenciaServicio.obtenerPorRango(alumnoId, desde, hasta);
	    Logs.ficheroLog("✅ Asistencias obtenidas: " + asistencias.size());
	    return ResponseEntity.ok(asistencias);
	}

	/**
	 * Obtiene el conteo de estados de asistencias para un alumno en un rango de fechas.
	 *
	 * @param alumnoId ID del alumno.
	 * @param desde Fecha de inicio del rango.
	 * @param hasta Fecha final del rango.
	 * @return ResponseEntity con un mapa que contiene la cantidad de cada estado de asistencia.
	 */
	@GetMapping("/asistencia/conteoEstados/{alumnoId}")
	public ResponseEntity<Map<String, Integer>> obtenerConteoEstados(
	        @PathVariable Long alumnoId,
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

	    Logs.ficheroLog("➡️ Solicitud para obtener conteo de estados AlumnoID: " + alumnoId + " desde " + desde + " hasta " + hasta);
	    Map<String, Integer> conteo = asistenciaServicio.obtenerConteoEstados(alumnoId, desde, hasta);
	    Logs.ficheroLog("✅ Conteo obtenido: " + conteo.toString());
	    return ResponseEntity.ok(conteo);
	}


}
