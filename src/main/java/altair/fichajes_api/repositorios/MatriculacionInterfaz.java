package altair.fichajes_api.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import altair.fichajes_api.entidad.CursoEntidad;
import altair.fichajes_api.entidad.GrupoEntidad;
import altair.fichajes_api.entidad.MatriculacionEntidad;

public interface MatriculacionInterfaz extends JpaRepository<MatriculacionEntidad, Long>{

	
	List<MatriculacionEntidad> findByCursoAndGrupo(CursoEntidad curso, GrupoEntidad grupo);

}
