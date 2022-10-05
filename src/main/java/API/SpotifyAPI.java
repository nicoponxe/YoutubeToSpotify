package API;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SpotifyAPI {

    private String authentication; // Generar Token OAuth 2.0
    private String id; //ID de usuario en spotify

    public SpotifyAPI() {  //Constructor que almaneca en variable el ID del usuario en spotify
        try {
            this.id = UsuarioID();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public  String UsuarioID() throws IOException {  //Recupero JsonObject con info del usuario, devuelvo ID de usuario

        JSONObject ID = Get("https://api.spotify.com/v1/me");
        return ID.getString("id");

    }


    public JSONArray Playlists() throws IOException { //Recupero JsonObject con info de playlist y devuelvo JsonArray solo con playlist

        JSONObject playlistInfo = Get("https://api.spotify.com/v1/me/playlists");
        return playlistInfo.getJSONArray("items");

    }

    public String Cancion(String titulo) throws IOException {  //Devuelvo URI (id) de canción

        String tituloFormateado = titulo.replace(" ","%20");

        JSONObject canciones = Get("https://api.spotify.com/v1/search?q="+tituloFormateado+"&type=track&limit=1");
        String cancion = canciones.getJSONObject("tracks").getJSONArray("items").getJSONObject(0).getString("uri");  //siempre devuelve el primer resultado de la busqueda
        return cancion;

    }

    public String CrearPlaylist() throws IOException {   // Agrego cancion a playlist y devuelvo su ID

        Boolean creado = Post("https://api.spotify.com/v1/users/"+this.id+"/playlists");

        if (creado){
            JSONArray playlists = Playlists(); // Recorro playlist para encontrar el ID de la nueva playlist
            String id = playlists.getJSONObject(0).getString("id"); //Devuelvo el ID de la primer playlist ya que es por orden de creación
        }
        return id;
    }

    public boolean AgregarCancionAPlaylist(String cancion, String playlist) throws IOException {   //sobrecarga de metodo para agregar canciones

        return Post("https://api.spotify.com/v1/playlists/"+playlist+"/tracks?uris="+cancion+"");

    }


    //Solicitudes (requests)

    private JSONObject Get(String uri) throws IOException {

        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", authentication);
        conn.connect();

        if (conn.getResponseCode() == 200) {
            StringBuffer response = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            conn.disconnect();
            JSONObject Json = new JSONObject(response.toString());
            return Json;

        } else {
            conn.disconnect();
            return null;
        }
    }

    private boolean Post(String uri) throws IOException {

        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", authentication);

        if (uri.contains("id")){  //Solo se envia un body en caso que se quiera crear una playlist. Sino, solo se agregan canciones.

            String data = "{\"name\":\"Songs from Youtube\",\"description\":\"Canciones de videos de youtube likeados\",\"public\":false}";
            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = conn.getOutputStream();
            stream.write(out);
        }

        if (conn.getResponseCode() == 201){ //201 = Creada/agregada
            return true;
        }else {
            return false;
        }
    }

}




