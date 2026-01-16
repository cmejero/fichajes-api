package altair.fichajes_api.lector;

import org.springframework.stereotype.Service;

@Service
public class LectorNfcFuncionalidad {

    private volatile String ultimoUid;

    
    
    // Se llama desde el listener cuando detecta un nuevo UID
    public synchronized void nuevoUid(String uid) {
        this.ultimoUid = uid;
        System.out.println("UID detectado: " + uid); // log para consola
    }

    // Consumir UID una sola vez desde tus endpoints
    public synchronized String consumirUid() {
        String uid = ultimoUid;
        ultimoUid = null;
        return uid;
    }
}