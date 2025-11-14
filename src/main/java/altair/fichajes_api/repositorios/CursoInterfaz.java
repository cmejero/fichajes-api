package altair.fichajes_api.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import altair.fichajes_api.entidad.CursoEntidad;

public interface CursoInterfaz  extends JpaRepository<CursoEntidad, Long> {
	
	 CursoEntidad findByNombreCurso(String nombre);

}
