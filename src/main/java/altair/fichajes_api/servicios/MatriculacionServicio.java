package altair.fichajes_api.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import altair.fichajes_api.dtos.AlumnoConMatriculacionDto;
import altair.fichajes_api.dtos.MatriculacionDto;
import altair.fichajes_api.entidad.CursoEntidad;
import altair.fichajes_api.entidad.GrupoEntidad;
import altair.fichajes_api.entidad.MatriculacionEntidad;
import altair.fichajes_api.repositorios.AlumnoInterfaz;
import altair.fichajes_api.repositorios.CursoInterfaz;
import altair.fichajes_api.repositorios.GrupoInterfaz;
import altair.fichajes_api.repositorios.MatriculacionInterfaz;

/**
 * Servicio encargado de la gestión de matriculaciones. Proporciona métodos para
 * crear, obtener y eliminar matriculaciones, así como para convertir entidades
 * a DTOs.
 */
@Service
public class MatriculacionServicio {

	@Autowired
	private MatriculacionInterfaz matriculacionInterfaz;

	@Autowired
	private AlumnoInterfaz alumnoInterfaz;

	@Autowired
	private CursoInterfaz cursoInterfaz;

	@Autowired
	private GrupoInterfaz grupoInterfaz;
	
	
	  public static AlumnoConMatriculacionDto mapearADto(MatriculacionEntidad matricula) {
	        AlumnoConMatriculacionDto dto = new AlumnoConMatriculacionDto();

	        dto.setIdAlumno(matricula.getAlumno().getIdAlumno());
	        dto.setIdMatriculacion(matricula.getIdMatriculacion());
	        dto.setNombreAlumno(matricula.getAlumno().getNombreAlumno());
	        dto.setApellidoAlumno(matricula.getAlumno().getApellidoAlumno());
	        dto.setCursoId(matricula.getCurso().getIdCurso());
	        dto.setGrupoId(matricula.getGrupo().getIdGrupo());
	        dto.setAnioEscolar(matricula.getAnioEscolar());
	        dto.setUidLlave(matricula.getUidLlave());

	        return dto;
	    }

	/**
	 * Crea una nueva matriculación a partir de un DTO.
	 * 
	 * @param dto DTO con los datos de la matriculación
	 * @return Entidad de matriculación guardada
	 */
	public MatriculacionEntidad crearMatriculacion(MatriculacionDto dto) {
		MatriculacionEntidad matricula = new MatriculacionEntidad();

		alumnoInterfaz.findById(dto.getAlumnoId()).ifPresent(matricula::setAlumno);
		cursoInterfaz.findById(dto.getCursoId()).ifPresent(matricula::setCurso);
		grupoInterfaz.findById(dto.getGrupoId()).ifPresent(matricula::setGrupo);

		matricula.setAnioEscolar(dto.getAnioEscolar());
		matricula.setUidLlave(dto.getUidLlave());

		return matriculacionInterfaz.save(matricula);
	}

	/**
	 * Modifica los datos de una matrícula existente.
	 *
	 * @param id  ID de la matrícula
	 * @param dto DTO con los nuevos datos
	 * @return true si se modificó correctamente, false si no existe
	 */
	public boolean modificarMatriculacion(Long id, MatriculacionDto dto) {
	    Optional<MatriculacionEntidad> matriculaOpt = matriculacionInterfaz.findById(id);
	    if (matriculaOpt.isPresent()) {
	        MatriculacionEntidad matricula = matriculaOpt.get();

	        // Buscar las entidades relacionadas
	        CursoEntidad curso = cursoInterfaz.findById(dto.getCursoId()).orElse(null);
	        GrupoEntidad grupo = grupoInterfaz.findById(dto.getGrupoId()).orElse(null);

	        if (curso == null || grupo == null) {
	            return false; 
	        }

	        matricula.setCurso(curso);
	        matricula.setGrupo(grupo);
	        matricula.setAnioEscolar(dto.getAnioEscolar());
	        matricula.setUidLlave(dto.getUidLlave());

	        matriculacionInterfaz.save(matricula);
	        return true;
	    }
	    return false;
	}

	/**
	 * Obtiene todas las matriculaciones y las convierte a DTOs.
	 * 
	 * @return Lista de DTOs de matriculaciones
	 */
	public List<MatriculacionDto> obtenerTodasDto() {
		List<MatriculacionEntidad> matriculas = matriculacionInterfaz.findAll();

		return matriculas.stream().map(m -> {
			MatriculacionDto dto = new MatriculacionDto();
			dto.setIdMatriculacion(m.getIdMatriculacion());
			dto.setAlumnoId(m.getAlumno().getIdAlumno());
			dto.setCursoId(m.getCurso().getIdCurso());
			dto.setGrupoId(m.getGrupo().getIdGrupo());
			dto.setAnioEscolar(m.getAnioEscolar());
			dto.setUidLlave(m.getUidLlave());
			return dto;
		}).toList();
	}

	/**
	 * Obtiene todas las matriculaciones de un alumno específico.
	 *
	 * @param idAlumno ID del alumno.
	 * @return Lista de DTOs de matriculaciones asociadas al alumno.
	 */
	public List<MatriculacionDto> obtenerPorAlumno(Long idAlumno) {
		List<MatriculacionEntidad> matriculas = matriculacionInterfaz.findByAlumno_IdAlumno(idAlumno);

		return matriculas.stream().map(m -> {
			MatriculacionDto dto = new MatriculacionDto();
			dto.setIdMatriculacion(m.getIdMatriculacion());
			dto.setAlumnoId(m.getAlumno().getIdAlumno());
			dto.setCursoId(m.getCurso().getIdCurso());
			dto.setNombreCurso(m.getCurso().getNombreCurso());
			dto.setGrupoId(m.getGrupo().getIdGrupo());
			dto.setNombreGrupo(m.getGrupo().getNombreGrupo());
			dto.setAnioEscolar(m.getAnioEscolar());
			dto.setUidLlave(m.getUidLlave());
			return dto;
		}).toList();
	}


	/**
	 * Obtiene una matriculación por su ID.
	 * 
	 * @param id ID de la matriculación
	 * @return Optional con la entidad de matriculación si existe
	 */
	public Optional<MatriculacionEntidad> obtenerPorId(Long id) {
		return matriculacionInterfaz.findById(id);
	}

	/**
	 * Elimina una matriculación por su ID.
	 * 
	 * @param id ID de la matriculación a eliminar
	 * @return true si se eliminó, false si no existía
	 */
	public boolean eliminarMatriculacion(Long id) {
		if (matriculacionInterfaz.existsById(id)) {
			matriculacionInterfaz.deleteById(id);
			return true;
		}
		return false;
	}
}
