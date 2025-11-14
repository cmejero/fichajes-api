package altair.fichajes_api.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;

import altair.fichajes_api.entidad.AlumnoEntidad;

public interface AlumnoInterfaz extends JpaRepository<AlumnoEntidad, Long> {


}
