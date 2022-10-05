package API;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.Normalizer;

public class Playlist {

    private SpotifyAPI spotify = new SpotifyAPI();
    private YoutubeAPI youtube = new YoutubeAPI();
    private String playlistID; //ID que representa la nueva playlist en Spotify

    public void CrearPlaylist() throws GeneralSecurityException, IOException {

        //Youtube
        JSONArray videos = youtube.VideosMeGusta(); //Recupero videos de Youtube que el usuario dio Me Gusta

        //Spotify
        JSONArray playlists = spotify.Playlists();  //Recupero todas las playlists del usuario en Spotify


        playlistID =  ExistePlaylistID(playlists);  //Verifico que la playlist a crear no exista, caso contrario, recupero su ID.

        if (playlistID != null){  //si existe, solo agrega canciones.
            AgregoCancionesAPlaylist(videos, playlistID);
        }else{ // si no existe, se crea y agrego canciones.
            AgregoCancionesAPlaylist(videos, spotify.CrearPlaylist());
        }
    }

    private void AgregoCancionesAPlaylist(JSONArray videos, String playlist) throws IOException {  /** Funcion que recibe los video con Me Gusta que se quieren transformar y agregar
                                                                                                             como canciones a playlist de Spotify.
                                                                                                        */
        for (Object video : videos) {   //Recorro cada video leyendo su titulo, busco cancion en spotify y de existir, se agrega a playlist

            if (video != null && video instanceof JSONObject vid){

                String titulo = NormalizoTexto(vid.getJSONObject("snippet").getString("title"));  //Normalizo el titulo del video
                String cancionURI = spotify.Cancion(titulo); //Busco titulo del video en Spotify, devuelve codigo URI de la primer cancion encontrada,
                if (cancionURI != null){                    // si existe, la agrega.
                    spotify.AgregarCancionAPlaylist(cancionURI, playlist);
                }
            }
        }
    }

    private String NormalizoTexto(String titulo){   //Funcion que saca puntuacion

        String texto = Normalizer.normalize(titulo, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
    }

    private String ExistePlaylistID(JSONArray playlists){  //funcion que recorre y verifica si existe la playlist en spotify que quiero crear.
                                                                // Si existe, devuelve su ID.
        String id = null;

        for ( Object playlist : playlists ) {
            if (playlist != null && playlist instanceof JSONObject p){
                if (p.getString("name").equals("Songs from Youtube")){
                    id = p.getString("id");
                    break;
                }
            } id = null;
        }
        return id;
    }

}
