package altair.fichajes_api.dtos;

/**
 * Clase que se encarga de los campos de la informacion del alumno y uid de la llave UID
 */
public class EventoLectorDto {

    private boolean hayUid;
    private boolean registrado;
    private String uid;
    private String alumno;
    private String curso;
    private String grupo;


    // Getters y setters

    public boolean isHayUid() {
        return hayUid;
    }

    public void setHayUid(boolean hayUid) {
        this.hayUid = hayUid;
    }

    public boolean isRegistrado() {
        return registrado;
    }

    public void setRegistrado(boolean registrado) {
        this.registrado = registrado;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAlumno() {
        return alumno;
    }

    public void setAlumno(String alumno) {
        this.alumno = alumno;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

  
}
