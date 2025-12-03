package altair.fichajes_api.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import altair.fichajes_api.entidad.CursoEntidad;

/**
 * Repositorio encargado de gestionar operaciones CRUD sobre la entidad {@link CursoEntidad}.
 * Extiende {@link JpaRepository} para proporcionar acceso a la base de datos.
 */
public interface CursoInterfaz extends JpaRepository<CursoEntidad, Long> {

    /**
     * Busca un curso por su nombre.
     * @param nombre Nombre del curso
     * @return CursoEntidad correspondiente al nombre, o null si no existe
     */
    CursoEntidad findByNombreCurso(String nombre);

}


