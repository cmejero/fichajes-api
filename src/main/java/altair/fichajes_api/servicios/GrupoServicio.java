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

/**
 * Servicio encargado de la gestión de grupos.
 * Proporciona métodos para obtener, guardar, borrar y mapear grupos entre DTOs y entidades.
 */
@Service
public class GrupoServicio {

    @Autowired
    private GrupoInterfaz grupoInterfaz;

    @Autowired
    private CursoInterfaz cursoInterfaz;

    /**
     * Convierte una entidad de grupo a su DTO correspondiente.
     * @param grupo Entidad de grupo
     * @return DTO de grupo
     */
    public GrupoDto mapearAGrupoDTO(GrupoEntidad grupo) {
        GrupoDto dto = new GrupoDto();
        dto.setIdGrupo(grupo.getIdGrupo());
        dto.setNombreGrupo(grupo.getNombreGrupo());
        dto.setCursoId(grupo.getCurso() != null ? grupo.getCurso().getIdCurso() : null);
        dto.setNombreCurso(grupo.getCurso().getNombreCurso());
        return dto;
    }

    /**
     * Convierte un DTO de grupo a su entidad correspondiente.
     * @param dto DTO de grupo
     * @return Entidad de grupo
     */
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

    /**
     * Obtiene todos los grupos existentes.
     * @return Lista de DTOs de grupos
     */
    public ArrayList<GrupoDto> obtenerTodosGrupos() {
        List<GrupoEntidad> grupos = grupoInterfaz.findAll();
        ArrayList<GrupoDto> dtos = new ArrayList<>();
        for (GrupoEntidad g : grupos) {
            dtos.add(mapearAGrupoDTO(g));
        }
        return dtos;
    }

    /**
     * Obtiene un grupo por su ID.
     * @param id ID del grupo
     * @return DTO de grupo, o null si no existe
     */
    public GrupoDto obtenerGrupoPorId(Long id) {
        Optional<GrupoEntidad> grupoOpt = grupoInterfaz.findById(id);
        return grupoOpt.map(this::mapearAGrupoDTO).orElse(null);
    }

    /**
     * Obtiene todos los grupos pertenecientes a un curso específico.
     * @param idCurso ID del curso
     * @return Lista de DTOs de grupos asociados al curso
     */
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

    /**
     * Guarda un grupo en la base de datos.
     * @param dto DTO del grupo a guardar
     * @return Entidad de grupo guardada
     */
    public GrupoEntidad guardarGrupo(GrupoDto dto) {
        GrupoEntidad grupo = mapearADtoAEntidad(dto);
        return grupoInterfaz.save(grupo);
    }
    
    

    /**
     * Modifica los datos de un grupo existente.
     *
     * @param idGrupo ID del grupo a modificar
     * @param dto     DTO con los nuevos datos del grupo
     * @return true si se modificó correctamente, false si no existe
     */
    public boolean modificarGrupo(Long idGrupo, GrupoDto dto) {

        Optional<GrupoEntidad> grupoOpt = grupoInterfaz.findById(idGrupo);

        if (grupoOpt.isPresent()) {
            GrupoEntidad grupo = grupoOpt.get();

            grupo.setNombreGrupo(dto.getNombreGrupo());

            if (dto.getCursoId() != null) {
                cursoInterfaz.findById(dto.getCursoId())
                    .ifPresent(grupo::setCurso);
            }

            grupoInterfaz.save(grupo);
            return true;
        }

        return false;
    }


    /**
     * Elimina un grupo por su ID.
     * @param id ID del grupo a eliminar
     * @return true si se eliminó, false si no existía
     */
    public boolean borrarGrupo(Long id) {
        Optional<GrupoEntidad> grupoOpt = grupoInterfaz.findById(id);
        if (grupoOpt.isPresent()) {
            grupoInterfaz.delete(grupoOpt.get());
            return true;
        }
        return false;
    }
}
