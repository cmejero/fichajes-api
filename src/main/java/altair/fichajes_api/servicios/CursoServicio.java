package altair.fichajes_api.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import altair.fichajes_api.dtos.CursoDto;
import altair.fichajes_api.entidad.CursoEntidad;
import altair.fichajes_api.repositorios.CursoInterfaz;

@Service
public class CursoServicio {

    @Autowired
    private CursoInterfaz cursoInterfaz;

    // Mapear entidad -> DTO
    public CursoDto mapearACursoDTO(CursoEntidad curso) {
        CursoDto dto = new CursoDto();
        dto.setIdCurso(curso.getIdCurso());
        dto.setNombreCurso(curso.getNombreCurso());
        return dto;
    }

    // Mapear DTO -> Entidad
    private CursoEntidad mapearADtoAEntidad(CursoDto dto) {
        CursoEntidad curso = new CursoEntidad();
        curso.setIdCurso(dto.getIdCurso());
        curso.setNombreCurso(dto.getNombreCurso());
        return curso;
    }

    // Obtener todos los cursos
    public ArrayList<CursoDto> obtenerTodosCursos() {
        List<CursoEntidad> cursos = cursoInterfaz.findAll();
        ArrayList<CursoDto> dtos = new ArrayList<>();
        for (CursoEntidad c : cursos) {
            dtos.add(mapearACursoDTO(c));
        }
        return dtos;
    }

    // Obtener curso por ID
    public CursoDto obtenerCursoPorId(Long id) {
        Optional<CursoEntidad> cursoOpt = cursoInterfaz.findById(id);
        return cursoOpt.map(this::mapearACursoDTO).orElse(null);
    }

    // Guardar curso
    public CursoEntidad guardarCurso(CursoDto dto) {
        CursoEntidad curso = mapearADtoAEntidad(dto);
        return cursoInterfaz.save(curso);
    }

    // Borrar curso
    public boolean borrarCurso(Long id) {
        Optional<CursoEntidad> cursoOpt = cursoInterfaz.findById(id);
        if (cursoOpt.isPresent()) {
            cursoInterfaz.delete(cursoOpt.get());
            return true;
        }
        return false;
    }
}
