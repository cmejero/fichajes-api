package altair.fichajes_api.servicios;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import altair.fichajes_api.dtos.FestivoDto;
import altair.fichajes_api.entidad.FestivoEntidad;
import altair.fichajes_api.repositorios.FestivoInterfaz;
import jakarta.annotation.PostConstruct;

/**
 * Servicio encargado de la gestión de festivos. Proporciona métodos para
 * importar festivos desde archivo, obtenerlos, guardarlos, eliminarlos y
 * mantenerlos actualizados.
 */
@Service
public class FestivoServicio {

	private static final String RUTA_ARCHIVO = "src/main/resources/festivos.txt";
	private static final String FORMATO_FECHA = "dd-MM-yyyy";

	@Autowired
	private FestivoInterfaz festivoInterfaz;

	/**
	 * Método ejecutado al iniciar la aplicación para importar los festivos.
	 */
	@PostConstruct
	public void importarFestivosAlIniciar() {
		importarFestivosDesdeArchivo();
	}

	/**
	 * Importa festivos desde un archivo de texto, limpiando previamente la tabla.
	 */
	public void importarFestivosDesdeArchivo() {
		try (BufferedReader br = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
			// 1️⃣ Leer todas las fechas del archivo
			Set<LocalDate> fechasArchivo = new HashSet<>();
			String line;
			while ((line = br.readLine()) != null) {
				String[] campos = line.split(";");
				for (String fechaStr : campos) {
					try {
						LocalDate fecha = LocalDate.parse(fechaStr.trim(), DateTimeFormatter.ofPattern(FORMATO_FECHA));
						fechasArchivo.add(fecha);
					} catch (DateTimeParseException e) {
						System.out.println("Error al parsear la fecha: " + fechaStr);
					}
				}
			}

			// 2️⃣ Obtener todas las fechas existentes en la BD
			List<FestivoEntidad> festivosBD = festivoInterfaz.findAll();
			Set<LocalDate> fechasBD = festivosBD.stream().map(FestivoEntidad::getFecha).collect(Collectors.toSet());

			// 3️⃣ Eliminar de la BD las fechas que ya no están en el archivo
			for (FestivoEntidad festivo : festivosBD) {
				if (!fechasArchivo.contains(festivo.getFecha())) {
					festivoInterfaz.delete(festivo);
				}
			}

			// 4️⃣ Insertar en la BD las fechas nuevas que están en el archivo
			for (LocalDate fecha : fechasArchivo) {
				if (!fechasBD.contains(fecha)) {
					FestivoEntidad festivo = new FestivoEntidad();
					festivo.setFecha(fecha);
					festivoInterfaz.save(festivo);
				}
			}

			System.out.println("Festivos sincronizados correctamente. Total en BD: " + festivoInterfaz.count());

		} catch (IOException e) {
			System.out.println("Error al leer el archivo de festivos: " + e.getMessage());
		}

	}

	/**
	 * Obtiene todos los festivos existentes.
	 * 
	 * @return Lista de DTOs de festivos
	 */
	public List<FestivoDto> obtenerTodosFestivos() {
		return festivoInterfaz.findAll().stream().map(this::convertirA_dto).collect(Collectors.toList());
	}

	/**
	 * Guarda un festivo en la base de datos.
	 * 
	 * @param dto DTO del festivo a guardar
	 * @return DTO del festivo guardado
	 */
	public FestivoDto guardarFestivo(FestivoDto dto) {
		FestivoEntidad entidad = new FestivoEntidad();
		entidad.setFecha(dto.getFecha());
		entidad = festivoInterfaz.save(entidad);
		return convertirA_dto(entidad);
	}

	/**
	 * Elimina un festivo por su ID.
	 * 
	 * @param id ID del festivo a eliminar
	 * @return true si se eliminó, false si no existía
	 */
	public boolean eliminarFestivo(Long id) {
		Optional<FestivoEntidad> festivo = festivoInterfaz.findById(id);
		if (festivo.isPresent()) {
			festivoInterfaz.delete(festivo.get());
			return true;
		}
		return false;
	}

	/**
	 * Convierte una entidad de festivo a su DTO correspondiente.
	 * 
	 * @param entidad Entidad de festivo
	 * @return DTO de festivo
	 */
	private FestivoDto convertirA_dto(FestivoEntidad entidad) {
		FestivoDto dto = new FestivoDto();
		dto.setId(entidad.getId());
		dto.setFecha(entidad.getFecha());
		return dto;
	}

	/**
	 * Método para actualizar los festivos diarios, re-importando desde archivo.
	 */
	public void actualizarFestivosDiarios() {
		importarFestivosDesdeArchivo();
	}

	/**
	 * Guarda una lista de festivos en la base de datos, evitando duplicados.
	 * 
	 * @param festivos Lista de entidades de festivo
	 */
	public void guardarFestivos(List<FestivoEntidad> festivos) {
		for (FestivoEntidad festivo : festivos) {
			LocalDate fecha = festivo.getFecha();
			if (!festivoInterfaz.existsByFecha(fecha)) {
				festivoInterfaz.save(festivo);
			} else {
			}
		}
	}
}
