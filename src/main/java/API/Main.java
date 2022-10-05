package API;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class Main {

    /**
     * 0. Acceder a Youtube  listo
     * 1. ver los videos likeados   listo
     * 2. Realizar lista de los videos likeados   listo
     * 3. Acceder a cuenta Spotify   listo
     * 4. Chequear que la playlist a crear no exista  listo
     * 5. Crear Playlist "Songs from youtube"  listo
     * 6. Buscar cancion del video en spotify listo VER FORMATO DE TITULO PARA QUE NO DE ERROR LA BUSQUEDA
     * 7. Chequear que la canción no se encuentre en la lista
     * 8. Agregar la cancion a la playlist
     * 9. Cerrar conexiones
     */



    public static void main(String[] args) throws IOException, GeneralSecurityException {

        Playlist playlist = new Playlist();
        playlist.CrearPlaylist();

    }

}

