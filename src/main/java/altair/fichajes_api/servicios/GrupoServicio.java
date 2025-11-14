package altair.fichajes_api.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import altair.fichajes_api.dtos.GrupoDto;
import altair.fichajes_api.entidad.CursoEntidad;
import altair.fichajes_api.entidad.GrupoEntidad;
import altair.fichajes_api.repositorios.CursoInterfaz;
import altair.fichajes_api.repositorios.GrupoInterfaz;

@Service
public class GrupoServicio {


    @Autowired
    private GrupoInterfaz grupoInterfaz;

    @Autowired
    private CursoInterfaz cursoInterfaz;

    // Mapear entidad -> DTO
    public GrupoDto mapearAGrupoDTO(GrupoEntidad grupo) {
        GrupoDto dto = new GrupoDto();
        dto.setIdGrupo(grupo.getIdGrupo());
        dto.setNombreGrupo(grupo.getNombreGrupo());
        dto.setCursoId(grupo.getCurso() != null ? grupo.getCurso().getIdCurso() : null);
        return dto;
    }

    // Mapear DTO -> Entidad
    private GrupoEntidad mapearADtoAEntidad(GrupoDto dto) {
        GrupoEntidad grupo = new GrupoEntidad();
        grupo.setIdGrupo(dto.getIdGrupo());
        grupo.setNombreGrupo(dto.getNombreGrupo());
        if (dto.getCursoId() != null) {
            CursoEntidad curso = cursoInterfaz.findById(dto.getCursoId()).orElse(null);
            grupo.setCurso(curso);
        }
        return grupo;
    }

    // Obtener todos los grupos
    public ArrayList<GrupoDto> obtenerTodosGrupos() {
        List<GrupoEntidad> grupos = grupoInterfaz.findAll();
        ArrayList<GrupoDto> dtos = new ArrayList<>();
        for (GrupoEntidad g : grupos) {
            dtos.add(mapearAGrupoDTO(g));
        }
        return dtos;
    }

    // Obtener grupo por ID
    public GrupoDto obtenerGrupoPorId(Long id) {
        Optional<GrupoEntidad> grupoOpt = grupoInterfaz.findById(id);
        return grupoOpt.map(this::mapearAGrupoDTO).orElse(null);
    }
    
    public List<GrupoDto> obtenerGruposPorCurso(Long idCurso) {
        List<GrupoEntidad> grupos = grupoInterfaz.findByCurso_IdCurso(idCurso);
        List<GrupoDto> dtos = new ArrayList<>();
        for (GrupoEntidad g : grupos) {
            GrupoDto dto = new GrupoDto();
            dto.setIdGrupo(g.getIdGrupo());
            dto.setNombreGrupo(g.getNombreGrupo());
            dto.setCursoId(g.getCurso().getIdCurso());
            dtos.add(dto);
        }
        return dtos;
    }

    // Guardar grupo
    public GrupoEntidad guardarGrupo(GrupoDto dto) {
        GrupoEntidad grupo = mapearADtoAEntidad(dto);
        return grupoInterfaz.save(grupo);
    }

    // Borrar grupo
    public boolean borrarGrupo(Long id) {
        Optional<GrupoEntidad> grupoOpt = grupoInterfaz.findById(id);
        if (grupoOpt.isPresent()) {
            grupoInterfaz.delete(grupoOpt.get());
            return true;
        }
        return false;
    }
}
