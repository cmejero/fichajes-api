package altair.fichajes_api.dtos;

import java.time.LocalDate;

public class FestivoDto {

	private LocalDate fecha;
	private String descripcion;
	private String municipio;
	public LocalDate getFecha() {
		return fecha;
	}
	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getMunicipio() {
		return municipio;
	}
	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	} 
	
	
	
}
