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

	@GetMapping("/alumno/{idAlumno}")
	public ResponseEntity<AlumnoDto> obtenerAlumno(@PathVariable Long id) {
		Logs.ficheroLog("Solicitud para obtener alumno con ID: " + id);
		try {
			AlumnoDto alumno = alumnoServicio.obtenerAlumnoPorId(id);
			if (alumno != null) {
				Logs.ficheroLog("Alumno encontrado con ID: " + id);
				return ResponseEntity.ok(alumno);
			} else {
				Logs.ficheroLog("No se encontró alumno con ID: " + id);
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			Logs.ficheroLog("Error al obtener alumno con ID " + id + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/alumnos")
	public ArrayList<AlumnoDto> listaAlumnos() {
		Logs.ficheroLog("Solicitud para listar todos los alumnos");
		return alumnoServicio.obtenerTodosAlumnos();
	}

	@PostMapping("/guardarAlumno")
	public ResponseEntity<?> guardarAlumno(@RequestBody AlumnoConMatriculacionDto dto) {
		Logs.ficheroLog("Solicitud para guardar alumno: " + dto.toString());
		try {
			AlumnoConMatriculacionDto alumnoGuardado = alumnoServicio.guardarAlumnoConMatriculacion(dto);
			Logs.ficheroLog("Alumno guardado exitosamente con ID: " + alumnoGuardado.getIdAlumno());
			return ResponseEntity.ok(alumnoGuardado);
		} catch (Exception e) {
			Logs.ficheroLog("❌ Error al guardar alumno: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al guardar alumno: " + e.getMessage());
		}
	}

	@PutMapping("/modificarAlumno/{idAlumno}")
	public ResponseEntity<String> modificarAlumno(@PathVariable Long idAlumno, @RequestBody AlumnoDto dto) {
		Logs.ficheroLog("Solicitud para modificar alumno con ID: " + idAlumno);
		try {
			boolean modificado = alumnoServicio.modificarAlumno(idAlumno, dto);
			if (modificado) {
				Logs.ficheroLog("Alumno modificado correctamente con ID: " + idAlumno);
				return ResponseEntity.ok("Alumno modificado correctamente");
			} else {
				Logs.ficheroLog("Alumno no encontrado con ID: " + idAlumno);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alumno no encontrado");
			}
		} catch (Exception e) {
			Logs.ficheroLog("Error al modificar alumno con ID " + idAlumno + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar alumno");
		}
	}

	@DeleteMapping("/eliminarAlumno/{idAlumno}")
	public ResponseEntity<String> eliminarAlumno(@PathVariable Long idAlumno) {
		Logs.ficheroLog("Solicitud para eliminar alumno con ID: " + idAlumno);
		try {
			boolean eliminado = alumnoServicio.borrarAlumno(idAlumno);
			if (eliminado) {
				Logs.ficheroLog("Alumno eliminado correctamente con ID: " + idAlumno);
				return ResponseEntity.ok("Alumno eliminado correctamente");
			} else {
				Logs.ficheroLog("Alumno no encontrado con ID: " + idAlumno);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alumno no encontrado");
			}
		} catch (Exception e) {
			Logs.ficheroLog("Error al eliminar alumno con ID " + idAlumno + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar alumno");
		}
	}

	@GetMapping("/alumno/{idAlumno}/conMatriculacion")
	public ResponseEntity<?> obtenerAlumnoConMatriculacion(@PathVariable Long idAlumno) {
		AlumnoConMatriculacionDto dto = alumnoServicio.obtenerAlumnoConMatriculacion(idAlumno);

		if (dto == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body("No se encontró el alumno o no tiene matriculación asociada");
		}

		return ResponseEntity.ok(dto);
	}

	/* METODOS CRUD DE LA TABLA CURSOS */

	// GET todos los cursos
	@GetMapping("/cursos")
	public ArrayList<CursoDto> listaCursos() {
		Logs.ficheroLog("Solicitud para listar todos los cursos");
		try {
			return cursoServicio.obtenerTodosCursos();
		} catch (Exception e) {
			Logs.ficheroLog("Error al listar cursos: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	// GET curso por ID
	@GetMapping("/curso/{idCurso}")
	public ResponseEntity<CursoDto> obtenerCurso(@PathVariable Long idCurso) {
		Logs.ficheroLog("Solicitud para obtener curso con ID: " + idCurso);
		try {
			CursoDto curso = cursoServicio.obtenerCursoPorId(idCurso);
			if (curso != null) {
				Logs.ficheroLog("Curso encontrado con ID: " + idCurso);
				return ResponseEntity.ok(curso);
			} else {
				Logs.ficheroLog("No se encontró curso con ID: " + idCurso);
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			Logs.ficheroLog("Error al obtener curso con ID " + idCurso + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// POST nuevo curso
	@PostMapping("/guardarCurso")
	public ResponseEntity<?> guardarCurso(@RequestBody CursoDto dto) {
		Logs.ficheroLog("Solicitud para guardar curso: " + dto.getNombreCurso());
		try {
			CursoDto cursoGuardado = cursoServicio.mapearACursoDTO(cursoServicio.guardarCurso(dto));
			Logs.ficheroLog("Curso guardado exitosamente con ID: " + cursoGuardado.getIdCurso());
			return ResponseEntity.ok(cursoGuardado);
		} catch (Exception e) {
			Logs.ficheroLog("Error al guardar curso: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar curso");
		}
	}

	// DELETE curso
	@DeleteMapping("/eliminarCurso/{idCurso}")
	public ResponseEntity<String> eliminarCurso(@PathVariable Long idCurso) {
		Logs.ficheroLog("Solicitud para eliminar curso con ID: " + idCurso);
		try {
			boolean eliminado = cursoServicio.borrarCurso(idCurso);
			if (eliminado) {
				Logs.ficheroLog("Curso eliminado correctamente con ID: " + idCurso);
				return ResponseEntity.ok("Curso eliminado correctamente");
			} else {
				Logs.ficheroLog("Curso no encontrado con ID: " + idCurso);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Curso no encontrado");
			}
		} catch (Exception e) {
			Logs.ficheroLog("Error al eliminar curso con ID " + idCurso + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar curso");
		}

	}

	/* METODOS CRUD DE LA TABLA GRUPO */

	// GET todos los grupos
	@GetMapping("/grupos")
	public ArrayList<GrupoDto> listaGrupos() {
		Logs.ficheroLog("Solicitud para listar todos los grupos");
		try {
			return grupoServicio.obtenerTodosGrupos();
		} catch (Exception e) {
			Logs.ficheroLog("Error al listar grupos: " + e.getMessage());
			return new ArrayList<>();
		}
	}

	// GET grupo por ID
	@GetMapping("/grupo/{idGrupo}")
	public ResponseEntity<GrupoDto> obtenerGrupo(@PathVariable Long idGrupo) {
		Logs.ficheroLog("Solicitud para obtener grupo con ID: " + idGrupo);
		try {
			GrupoDto grupo = grupoServicio.obtenerGrupoPorId(idGrupo);
			if (grupo != null) {
				Logs.ficheroLog("Grupo encontrado con ID: " + idGrupo);
				return ResponseEntity.ok(grupo);
			} else {
				Logs.ficheroLog("No se encontró grupo con ID: " + idGrupo);
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			Logs.ficheroLog("Error al obtener grupo con ID " + idGrupo + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	// ✅ NUEVO: Obtener los grupos de un curso por su ID
	@GetMapping("/grupos/curso/{idCurso}")
	public ResponseEntity<List<GrupoDto>> obtenerGruposPorCurso(@PathVariable Long idCurso) {
	    Logs.ficheroLog("Solicitud para obtener grupos del curso con ID: " + idCurso);
	    try {
	        List<GrupoDto> grupos = grupoServicio.obtenerGruposPorCurso(idCurso);
	        if (grupos.isEmpty()) {
	            Logs.ficheroLog("No se encontraron grupos para el curso ID: " + idCurso);
	            return ResponseEntity.notFound().build();
	        }
	        return ResponseEntity.ok(grupos);
	    } catch (Exception e) {
	        Logs.ficheroLog("Error al obtener grupos por curso: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	 

	// POST nuevo grupo
	@PostMapping("/guardarGrupo")
	public ResponseEntity<?> guardarGrupo(@RequestBody GrupoDto dto) {
		Logs.ficheroLog("Solicitud para guardar grupo: " + dto.getNombreGrupo());
		try {
			GrupoDto grupoGuardado = grupoServicio.mapearAGrupoDTO(grupoServicio.guardarGrupo(dto));
			Logs.ficheroLog("Grupo guardado exitosamente con ID: " + grupoGuardado.getIdGrupo());
			return ResponseEntity.ok(grupoGuardado);
		} catch (Exception e) {
			Logs.ficheroLog("Error al guardar grupo: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar grupo");
		}
	}

	// DELETE grupo
	@DeleteMapping("/eliminarGrupo/{idGrupo}")
	public ResponseEntity<String> eliminarGrupo(@PathVariable Long idGrupo) {
		Logs.ficheroLog("Solicitud para eliminar grupo con ID: " + idGrupo);
		try {
			boolean eliminado = grupoServicio.borrarGrupo(idGrupo);
			if (eliminado) {
				Logs.ficheroLog("Grupo eliminado correctamente con ID: " + idGrupo);
				return ResponseEntity.ok("Grupo eliminado correctamente");
			} else {
				Logs.ficheroLog("Grupo no encontrado con ID: " + idGrupo);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Grupo no encontrado");
			}
		} catch (Exception e) {
			Logs.ficheroLog("Error al eliminar grupo con ID " + idGrupo + ": " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar grupo");
		}
	}

	/* METODOS CRUD DE LA TABLA MATRICULACION */

	@PostMapping("/guardarMatriculacion")
	public ResponseEntity<?> crearMatriculacion(@RequestBody MatriculacionDto dto) {
		try {
			MatriculacionEntidad nueva = matriculacionServicio.crearMatriculacion(dto);
			return ResponseEntity.ok(nueva);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body("Error al crear la matriculación: " + e.getMessage());
		}
	}

	@GetMapping("/matriculaciones")
	public List<MatriculacionDto> listarMatriculaciones() {
	    return matriculacionServicio.obtenerTodasDto();
	}


	@GetMapping("/matriculacion/{idMatriculacion}")
	public ResponseEntity<?> obtenerPorId(@PathVariable Long idMatriculacion) {
		return matriculacionServicio.obtenerPorId(idMatriculacion).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/eliminarMatriculacion/{idMatriculacion}")
	public ResponseEntity<?> eliminarMatriculacion(@PathVariable Long idMatriculacion) {
		if (matriculacionServicio.eliminarMatriculacion(idMatriculacion)) {
			return ResponseEntity.ok("Matriculación eliminada correctamente");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Matriculación no encontrada");
		}
	}

	/* METODOS CRUD DE LA TABLA ASISTENCIA */

	// Fichar entrada
	@PostMapping("/asistencia/entrada/{matriculacionId}")
	public ResponseEntity<?> ficharEntrada(@PathVariable Long matriculacionId) {
		try {
			AsistenciaDto asistencia = asistenciaServicio.ficharEntrada(matriculacionId);
			return ResponseEntity.ok(asistencia);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// Fichar salida
	@PutMapping("/asistencia/salida/{matriculacionId}")
	public ResponseEntity<?> ficharSalida(@PathVariable Long matriculacionId) {
		try {
			AsistenciaDto asistencia = asistenciaServicio.ficharSalida(matriculacionId);
			return ResponseEntity.ok(asistencia);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("/modificarAsistencia/{idAsistencia}")
	public ResponseEntity<?> modificarAsistencia(
	        @PathVariable("idAsistencia") Long idAsistencia,
	        @RequestBody AsistenciaDto asistenciaDto) {

	    Logs.ficheroLog("Solicitud para modificar asistencia con ID: " + idAsistencia);

	    try {
	        boolean resultado = asistenciaServicio.modificarAsistencia(idAsistencia, asistenciaDto);

	        Logs.ficheroLog("Resultado de la modificación de la asistencia con ID " + idAsistencia + ": " + resultado);

	        if (resultado) {
	            // Recuperamos la asistencia modificada para devolverla como JSON
	            AsistenciaEntidad asistenciaActualizada = asistenciaInterfaz.findById(idAsistencia).orElse(null);
	            if (asistenciaActualizada == null) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Asistencia no encontrada");
	            }

	            // Convertimos a DTO si es necesario
	            AsistenciaDto dto = new AsistenciaDto();
	            dto.setIdAsistencia(asistenciaActualizada.getIdAsistencia());
	            dto.setEstado(asistenciaActualizada.getEstado());
	            dto.setHoraEntrada(asistenciaActualizada.getHoraEntrada());
	            dto.setHoraSalida(asistenciaActualizada.getHoraSalida());
	            dto.setJustificarModificacion(asistenciaActualizada.getJustificar_modificacion());
	            dto.setFecha(asistenciaActualizada.getFecha());

	            return ResponseEntity.ok(dto);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Asistencia no encontrada");
	        }
	    } catch (Exception e) {
	        Logs.ficheroLog("Error al modificar asistencia con ID " + idAsistencia + ": " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al modificar asistencia");
	    }
	}



	
	@GetMapping("/asistencia/{curso}/{grupo}")
	public ResponseEntity<List<AsistenciaDto>> obtenerAsistenciaDeHoy(
	        @PathVariable String curso,
	        @PathVariable String grupo) {
	    LocalDate hoy = LocalDate.now();
	    return ResponseEntity.ok(asistenciaServicio.obtenerAsistenciaPorCursoYGrupoEnFecha(curso, grupo, hoy));
	}

	@GetMapping("/asistencia/curso-grupo")
	public ResponseEntity<List<AsistenciaDto>> obtenerAsistenciasPorCursoGrupoYFechaUsuario(
	        @RequestParam String curso,
	        @RequestParam String grupo,
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

	    List<AsistenciaDto> asistencias = asistenciaServicio
	            .obtenerAsistenciasPorCursoGrupoYFecha(curso, grupo, fecha);
	    return ResponseEntity.ok(asistencias);
	}

	
	
	@GetMapping("/asistencias")
	public ResponseEntity<List<AsistenciaDto>> verTodasAsistencias() {
	    return ResponseEntity.ok(asistenciaServicio.obtenerTodasAsistencias());
	}



	@GetMapping("/asistencia/fecha/{fecha}")
	public ResponseEntity<?> listarPorFecha(
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
		return ResponseEntity.ok(asistenciaServicio.obtenerPorFecha(fecha));
	}
	
	@GetMapping("/asistencia/alumno-estado")
	public ResponseEntity<List<AsistenciaDto>> obtenerPorAlumnoEstadoYAnio(
	        @RequestParam Long alumnoId,
	        @RequestParam String estado,
	        @RequestParam String anioEscolar) {

	    List<AsistenciaDto> lista = asistenciaServicio.obtenerPorAlumnoEstadoYAnio(alumnoId, estado, anioEscolar);
	    return ResponseEntity.ok(lista);
	}
	
	


	
	@GetMapping("/asistencia/rango/{alumnoId}")
	public ResponseEntity<List<AsistenciaDto>> obtenerPorRango(
	        @PathVariable Long alumnoId,
	        @RequestParam("desde") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
	        @RequestParam("hasta") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
	    
	    List<AsistenciaDto> asistencias = asistenciaServicio.obtenerPorRango(alumnoId, desde, hasta);
	    return ResponseEntity.ok(asistencias);
	}

	@GetMapping("/asistencia/conteoEstados/{alumnoId}")
	public ResponseEntity<Map<String, Integer>> obtenerConteoEstados(
	        @PathVariable Long alumnoId,
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
	        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

	    Map<String, Integer> conteo = asistenciaServicio.obtenerConteoEstados(alumnoId, desde, hasta);
	    return ResponseEntity.ok(conteo);
	}

	



}
