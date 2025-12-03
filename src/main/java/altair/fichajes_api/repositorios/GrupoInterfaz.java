package altair.fichajes_api.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import altair.fichajes_api.entidad.GrupoEntidad;

/**
 * Repositorio encargado de gestionar operaciones CRUD sobre la entidad {@link GrupoEntidad}.
 * Extiende {@link JpaRepository} para proporcionar acceso a la base de datos.
 */
public interface GrupoInterfaz extends JpaRepository<GrupoEntidad, Long> {

    /**
     * Busca un grupo por su nombre.
     * @param nombre Nombre del grupo a buscar
     * @return Entidad del grupo correspondiente al nombre
     */
    GrupoEntidad findByNombreGrupo(String nombre);
    
    /**
     * Obtiene todos los grupos asociados a un curso específico.
     * @param idCurso ID del curso
     * @return Lista de entidades de grupo correspondientes al curso
     */
    List<GrupoEntidad> findByCurso_IdCurso(Long idCurso);
}
