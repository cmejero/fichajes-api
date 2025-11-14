package altair.fichajes_api.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import altair.fichajes_api.entidad.GrupoEntidad;

public interface GrupoInterfaz extends JpaRepository<GrupoEntidad, Long> {

	 GrupoEntidad findByNombreGrupo(String nombre);
	 
	 List<GrupoEntidad> findByCurso_IdCurso(Long idCurso);
}
