package altair.fichajes_api.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import altair.fichajes_api.dtos.AlumnoConMatriculacionDto;
import altair.fichajes_api.dtos.AlumnoDto;
import altair.fichajes_api.entidad.AlumnoEntidad;
import altair.fichajes_api.entidad.CursoEntidad;
import altair.fichajes_api.entidad.GrupoEntidad;
import altair.fichajes_api.entidad.MatriculacionEntidad;
import altair.fichajes_api.repositorios.AlumnoInterfaz;
import altair.fichajes_api.repositorios.CursoInterfaz;
import altair.fichajes_api.repositorios.GrupoInterfaz;
import altair.fichajes_api.repositorios.MatriculacionInterfaz;

@Service
public class AlumnoServicio {

	@Autowired
	private AlumnoInterfaz alumnoInterfaz;

	@Autowired
	private CursoInterfaz cursoInterfaz;

	@Autowired
	private GrupoInterfaz grupoInterfaz;

	@Autowired
	private MatriculacionInterfaz matriculacionInterfaz;

	// --- Mapeo entidad → DTO simple ---
	public AlumnoDto mapearAAlumnoDTO(AlumnoEntidad alumno) {
		AlumnoDto dto = new AlumnoDto();
		dto.setIdAlumno(alumno.getIdAlumno());
		dto.setNombreAlumno(alumno.getNombreAlumno());
		dto.setApellidoAlumno(alumno.getApellidoAlumno());

		if (alumno.getMatriculaciones() != null) {
			List<Long> matriculacionIds = alumno.getMatriculaciones().stream()
					.map(MatriculacionEntidad::getIdMatriculacion).collect(Collectors.toList());
			dto.setMatriculacionIds(matriculacionIds);
		}

		return dto;
	}

	// --- Guardar alumno con matriculación ---
	public AlumnoConMatriculacionDto guardarAlumnoConMatriculacion(AlumnoConMatriculacionDto dto) {

		// 1️⃣ Crear y guardar el alumno
		AlumnoEntidad alumno = new AlumnoEntidad();
		alumno.setNombreAlumno(dto.getNombreAlumno());
		alumno.setApellidoAlumno(dto.getApellidoAlumno());
		alumno = alumnoInterfaz.save(alumno);

		// 2️⃣ Crear la matrícula asociada
		MatriculacionEntidad matriculacion = new MatriculacionEntidad();
		matriculacion.setAlumno(alumno);
		matriculacion.setAnioEscolar(dto.getAnioEscolar());
		matriculacion.setUidLlave(dto.getUidLlave());

		// 3️⃣ Asignar curso y grupo (obligatorios)
		CursoEntidad curso = cursoInterfaz.findById(dto.getCursoId())
				.orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + dto.getCursoId()));

		GrupoEntidad grupo = grupoInterfaz.findById(dto.getGrupoId())
				.orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + dto.getGrupoId()));

		// Validar que el grupo pertenezca al curso
		if (grupo.getCurso() == null || !grupo.getCurso().getIdCurso().equals(curso.getIdCurso())) {
			throw new RuntimeException("El grupo no pertenece al curso indicado.");
		}

		matriculacion.setCurso(curso);
		matriculacion.setGrupo(grupo);

		matriculacion = matriculacionInterfaz.save(matriculacion);

		// 4️⃣ Mapear todo al DTO de respuesta
		AlumnoConMatriculacionDto respuesta = new AlumnoConMatriculacionDto();
		respuesta.setIdAlumno(alumno.getIdAlumno());
		respuesta.setIdMatriculacion(matriculacion.getIdMatriculacion());
		respuesta.setNombreAlumno(alumno.getNombreAlumno());
		respuesta.setApellidoAlumno(alumno.getApellidoAlumno());
		respuesta.setCursoId(curso.getIdCurso());
		respuesta.setGrupoId(grupo.getIdGrupo());
		respuesta.setAnioEscolar(matriculacion.getAnioEscolar());
		respuesta.setUidLlave(matriculacion.getUidLlave());

		return respuesta;
	}

	// --- Obtener todos los alumnos ---
	public ArrayList<AlumnoDto> obtenerTodosAlumnos() {
		List<AlumnoEntidad> alumnos = alumnoInterfaz.findAll();
		ArrayList<AlumnoDto> dtos = new ArrayList<>();
		for (AlumnoEntidad a : alumnos) {
			dtos.add(mapearAAlumnoDTO(a));
		}
		return dtos;
	}

	// --- Obtener alumno por ID ---
	public AlumnoDto obtenerAlumnoPorId(Long id) {
		return alumnoInterfaz.findById(id).map(this::mapearAAlumnoDTO).orElse(null);
	}
	
	public AlumnoConMatriculacionDto obtenerAlumnoConMatriculacion(Long idAlumno) {
	    // Buscar el alumno
	    Optional<AlumnoEntidad> alumnoOpt = alumnoInterfaz.findById(idAlumno);

	    if (alumnoOpt.isEmpty()) {
	        return null;
	    }

	    AlumnoEntidad alumno = alumnoOpt.get();

	    // Tomamos su última matriculación (por ejemplo, la más reciente)
	    MatriculacionEntidad matriculacion = alumno.getMatriculaciones()
	            .stream()
	            .reduce((first, second) -> second) // obtiene la última
	            .orElse(null);

	    // Mapear a DTO combinado
	    AlumnoConMatriculacionDto dto = new AlumnoConMatriculacionDto();
	    dto.setIdAlumno(alumno.getIdAlumno());
	    dto.setNombreAlumno(alumno.getNombreAlumno());
	    dto.setApellidoAlumno(alumno.getApellidoAlumno());

	    if (matriculacion != null) {
	        dto.setIdMatriculacion(matriculacion.getIdMatriculacion());
	        dto.setCursoId(matriculacion.getCurso().getIdCurso());
	        dto.setGrupoId(matriculacion.getGrupo().getIdGrupo());
	        dto.setAnioEscolar(matriculacion.getAnioEscolar());
	        dto.setUidLlave(matriculacion.getUidLlave());
	    }

	    return dto;
	}


	// --- Modificar alumno ---
	public boolean modificarAlumno(Long id, AlumnoDto dto) {
		Optional<AlumnoEntidad> alumnoOpt = alumnoInterfaz.findById(id);
		if (alumnoOpt.isPresent()) {
			AlumnoEntidad alumno = alumnoOpt.get();
			alumno.setNombreAlumno(dto.getNombreAlumno());
			alumno.setApellidoAlumno(dto.getApellidoAlumno());
			alumnoInterfaz.save(alumno);
			return true;
		}
		return false;
	}

	// --- Eliminar alumno ---
	public boolean borrarAlumno(Long id) {
		Optional<AlumnoEntidad> alumnoOpt = alumnoInterfaz.findById(id);
		if (alumnoOpt.isPresent()) {
			alumnoInterfaz.delete(alumnoOpt.get());
			return true;
		}
		return false;
	}
}
