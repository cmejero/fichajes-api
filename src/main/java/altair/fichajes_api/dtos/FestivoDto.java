package altair.fichajes_api.dtos;

import java.time.LocalDate;

/**
 * Clase que se encarga de los campos de festivo
 */
public class FestivoDto {

	
	private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	private LocalDate fecha;
	
	
	
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	
	
	
	
}
