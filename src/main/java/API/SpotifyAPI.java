package API;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class SpotifyAPI {

    private String authentication = "SPOTIFY OAuth Token";
    private String id; //ID de usuario en spotify

    public SpotifyAPI() {  
        try {
            this.id = UsuarioID(); //recupero ID de usuario
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public  String UsuarioID() throws IOException {  //Recupero JsonObject con info del usuario, devuelvo ID de usuario

        JSONObject ID = Get("https://api.spotify.com/v1/me");
        return ID.getString("id");

    }


    public JSONArray Playlists() throws IOException { //Recupero JsonObject con info de playlist y devuelvo JsonArray solo con playlist

        JSONObject playlistInfo = Get("https://api.spotify.com/v1/me/playlists?limit=49");
        return playlistInfo.getJSONArray("items");

    }

    public String Cancion(String titulo) throws IOException {  //Devuelvo URI (id) de canción

        String tituloFormateado = URLEncoder.encode(titulo, "UTF-8");  //formateo el titulo en url encoding
        String cancion = null;
        JSONObject canciones = Get("https://api.spotify.com/v1/search?q="+tituloFormateado+"&type=track&limit=1");
        if (canciones != null){
             cancion = canciones.getJSONObject("tracks").getJSONArray("items").getJSONObject(0).getString("uri");  //siempre devuelve el primer resultado de la busqueda
        }
        return cancion;

    }

    public String CrearPlaylist() throws IOException {   // Agrego cancion a playlist y devuelvo su ID

        String playlistID = null;

        String json = new JSONObject()
                .put("name", "Songs from Youtube")
                .put("description", "Canciones de videos de youtube likeados")
                .put("public", "false")
                .toString();

        Boolean creado = Post("https://api.spotify.com/v1/users/" + this.id + "/playlists", json);

        if (creado) {
            JSONArray playlists = Playlists(); // Recorro playlist para encontrar el ID de la nueva playlist
            playlistID = playlists.getJSONObject(0).getString("id");
        }
        return playlistID;

    }


    public boolean AgregarCancionAPlaylist(String cancion, String playlist) throws IOException {   //sobrecarga de metodo para agregar canciones

        String json = "{\"uris\":[\""+cancion+"\"],\"position\":0}";
        return Post("https://api.spotify.com/v1/playlists/"+playlist+"/tracks", json);

    }

    //Solicitudes (requests)


    private boolean Post(String uri, String data) throws IOException {
        URL url = new URL(uri);
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        http.setRequestMethod("POST");
        http.setDoOutput(true);
        http.setRequestProperty("Accept", "application/json");
        http.setRequestProperty("Content-Type", "application/json");
        http.setRequestProperty("Authorization", authentication);

        byte[] out = data.getBytes(StandardCharsets.UTF_8);
        OutputStream stream = http.getOutputStream();
        stream.write(out);

        if (http.getResponseCode() == 201){ //201 = Creada/agregada
            http.disconnect();
            return true;
        }else {
            return false;
        }
    }

    private JSONObject Get(String uri) throws IOException {

        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", authentication);
        conn.connect();

        String respuesta = conn.getResponseMessage();

        if (conn.getResponseCode() == 200) {
            StringBuffer response = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));  //El bufferreader se cierra solo porque adentro tiene un reader, LEER igual.
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
                response.append(System.lineSeparator()) ; // agrega separador
            }
            conn.disconnect();
            JSONObject Json = new JSONObject(response.toString());
            return Json;

        } else {
            conn.disconnect();
            return null;
        }
    }


}
