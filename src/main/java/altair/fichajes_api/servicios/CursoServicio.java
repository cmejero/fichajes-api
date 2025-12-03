package altair.fichajes_api.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import altair.fichajes_api.dtos.CursoDto;
import altair.fichajes_api.entidad.CursoEntidad;
import altair.fichajes_api.repositorios.CursoInterfaz;

/**
 * Servicio encargado de la lógica de negocio relacionada con los cursos.
 * Proporciona métodos para mapear entre entidades y DTOs,
 * obtener, guardar y eliminar cursos.
 */
@Service
public class CursoServicio {

    @Autowired
    private CursoInterfaz cursoInterfaz;

    /**
     * Mapea una entidad {@link CursoEntidad} a su DTO {@link CursoDto}.
     * @param curso Entidad de curso
     * @return DTO con la información del curso
     */
    public CursoDto mapearACursoDTO(CursoEntidad curso) {
        CursoDto dto = new CursoDto();
        dto.setIdCurso(curso.getIdCurso());
        dto.setNombreCurso(curso.getNombreCurso());
        return dto;
    }

    /**
     * Mapea un DTO {@link CursoDto} a su entidad {@link CursoEntidad}.
     * @param dto DTO de curso
     * @return Entidad de curso
     */
    private CursoEntidad mapearADtoAEntidad(CursoDto dto) {
        CursoEntidad curso = new CursoEntidad();
        curso.setIdCurso(dto.getIdCurso());
        curso.setNombreCurso(dto.getNombreCurso());
        return curso;
    }

    /**
     * Obtiene todos los cursos existentes.
     * @return Lista de DTOs de cursos
     */
    public ArrayList<CursoDto> obtenerTodosCursos() {
        List<CursoEntidad> cursos = cursoInterfaz.findAll();
        ArrayList<CursoDto> dtos = new ArrayList<>();
        for (CursoEntidad c : cursos) {
            dtos.add(mapearACursoDTO(c));
        }
        return dtos;
    }

    /**
     * Obtiene un curso por su ID.
     * @param id ID del curso
     * @return DTO del curso, o null si no existe
     */
    public CursoDto obtenerCursoPorId(Long id) {
        Optional<CursoEntidad> cursoOpt = cursoInterfaz.findById(id);
        return cursoOpt.map(this::mapearACursoDTO).orElse(null);
    }

    /**
     * Guarda un curso.
     * @param dto DTO con los datos del curso
     * @return Entidad del curso guardada
     */
    public CursoEntidad guardarCurso(CursoDto dto) {
        CursoEntidad curso = mapearADtoAEntidad(dto);
        return cursoInterfaz.save(curso);
    }

    /**
     * Elimina un curso existente por su ID.
     * @param id ID del curso
     * @return true si se eliminó correctamente, false si no existe
     */
    public boolean borrarCurso(Long id) {
        Optional<CursoEntidad> cursoOpt = cursoInterfaz.findById(id);
        if (cursoOpt.isPresent()) {
            cursoInterfaz.delete(cursoOpt.get());
            return true;
        }
        return false;
    }
}
