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

/**
 * Servicio encargado de la lógica de negocio relacionada con alumnos y sus matriculaciones.
 * Proporciona métodos para crear, obtener, modificar y eliminar alumnos,
 * así como mapear entre entidades y DTOs.
 */
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

    /**
     * Mapea una entidad {@link AlumnoEntidad} a su DTO {@link AlumnoDto}.
     * @param alumno Entidad de alumno
     * @return DTO con información básica del alumno
     */
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

    /**
     * Guarda un alumno y su matriculación asociada.
     * @param dto DTO con los datos del alumno y matriculación
     * @return DTO con la información combinada del alumno y la matriculación
     */
    public AlumnoConMatriculacionDto guardarAlumnoConMatriculacion(AlumnoConMatriculacionDto dto) {

        AlumnoEntidad alumno = new AlumnoEntidad();
        alumno.setNombreAlumno(dto.getNombreAlumno());
        alumno.setApellidoAlumno(dto.getApellidoAlumno());
        alumno = alumnoInterfaz.save(alumno);

        MatriculacionEntidad matriculacion = new MatriculacionEntidad();
        matriculacion.setAlumno(alumno);
        matriculacion.setAnioEscolar(dto.getAnioEscolar());
        matriculacion.setUidLlave(dto.getUidLlave());

        CursoEntidad curso = cursoInterfaz.findById(dto.getCursoId())
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + dto.getCursoId()));

        GrupoEntidad grupo = grupoInterfaz.findById(dto.getGrupoId())
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado con ID: " + dto.getGrupoId()));

        if (grupo.getCurso() == null || !grupo.getCurso().getIdCurso().equals(curso.getIdCurso())) {
            throw new RuntimeException("El grupo no pertenece al curso indicado.");
        }

        matriculacion.setCurso(curso);
        matriculacion.setGrupo(grupo);

        matriculacion = matriculacionInterfaz.save(matriculacion);

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

    /**
     * Obtiene todos los alumnos existentes.
     * @return Lista de DTOs de alumnos
     */
    public ArrayList<AlumnoDto> obtenerTodosAlumnos() {
        List<AlumnoEntidad> alumnos = alumnoInterfaz.findAll();
        ArrayList<AlumnoDto> dtos = new ArrayList<>();
        for (AlumnoEntidad a : alumnos) {
            dtos.add(mapearAAlumnoDTO(a));
        }
        return dtos;
    }

    /**
     * Obtiene un alumno por su ID.
     * @param id ID del alumno
     * @return DTO del alumno, o null si no existe
     */
    public AlumnoDto obtenerAlumnoPorId(Long id) {
        return alumnoInterfaz.findById(id).map(this::mapearAAlumnoDTO).orElse(null);
    }

    /**
     * Obtiene un alumno y su última matriculación.
     * @param idAlumno ID del alumno
     * @return DTO combinado de alumno y matriculación, o null si no existe
     */
    public AlumnoConMatriculacionDto obtenerAlumnoConMatriculacion(Long idAlumno) {
        Optional<AlumnoEntidad> alumnoOpt = alumnoInterfaz.findById(idAlumno);
        if (alumnoOpt.isEmpty()) {
            return null;
        }

        AlumnoEntidad alumno = alumnoOpt.get();
        MatriculacionEntidad matriculacion = alumno.getMatriculaciones()
                .stream()
                .reduce((first, second) -> second)
                .orElse(null);

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

    /**
     * Modifica los datos de un alumno existente.
     * @param id ID del alumno
     * @param dto DTO con los nuevos datos
     * @return true si se modificó correctamente, false si no existe
     */
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

    /**
     * Elimina un alumno existente.
     * @param id ID del alumno
     * @return true si se eliminó correctamente, false si no existe
     */
    public boolean borrarAlumno(Long id) {
        Optional<AlumnoEntidad> alumnoOpt = alumnoInterfaz.findById(id);
        if (alumnoOpt.isPresent()) {
            alumnoInterfaz.delete(alumnoOpt.get());
            return true;
        }
        return false;
    }
}
