/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oms;

import com.mysql.jdbc.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Meet Dave
 */
@Path("/movies")
public class Movies {

    Connection conn;
    ResultSet rs;

    @GET
    @Path("newlyarrived")
    @Produces(MediaType.APPLICATION_JSON)
    public String getNewlyArrived() {
        JsonArrayBuilder array = Json.createArrayBuilder();
        try {
            String path;

            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from movies where stock != 0 order by add_date desc limit 10");
            rs = ps.executeQuery();

            while (rs.next()) {
                JsonObjectBuilder obj = Json.createObjectBuilder();
                obj.add("movie_id", rs.getInt("movie_id"));
                obj.add("movie_name", rs.getString("name"));
                obj.add("movie_price", rs.getString("price"));
                path = ConnectionToDB.movieImagePath + rs.getString("image");
                obj.add("movie_image_url", path);
                array.add(obj.build());
            }
        } catch (Exception ex) {
            JSONObject temp = new JSONObject();
            temp.put("flag", "error");
            temp.put("errorMsg", ex.getMessage());
            return temp.toString();
        }
        return array.build().toString();
     
    }

    @GET
    @Path("search")
    @Produces(MediaType.APPLICATION_JSON)
    public String search(@QueryParam("query") String query) {
        JsonArrayBuilder array = Json.createArrayBuilder();
        try {
            String path;

            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from movies where name like ? || director like ? || category like ? || language like ? || release_year like ? || star_cast like ?");
            ps.setString(1, "%" + query + "%");
            ps.setString(2, "%" + query + "%");
            ps.setString(3, "%" + query + "%");
            ps.setString(4, "%" + query + "%");
            ps.setString(5, "%" + query + "%");
            ps.setString(6, "%" + query + "%");

            rs = ps.executeQuery();

            while (rs.next()) {
                JsonObjectBuilder obj = Json.createObjectBuilder();
                obj.add("movie_id", rs.getInt("movie_id"));
                obj.add("movie_name", rs.getString("name"));
                obj.add("movie_price", rs.getString("price"));
                path = ConnectionToDB.movieImagePath + rs.getString("image");
                obj.add("movie_image_url", path);
                array.add(obj.build());
            }
        } catch(Exception e){
            JSONObject object=new JSONObject();
            object.put("flag", "error");
            object.put("errorMsg", e.getMessage());
            return object.toString();
        }
        return array.build().toString();

    }

    @GET
    @Path("moviedetails")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMovieDetails(@QueryParam("movie_id") String movie_id) {
        JsonObjectBuilder obj = Json.createObjectBuilder();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from movies where movie_id=?");
            ps.setString(1, movie_id);
            rs = ps.executeQuery();
            if (rs.next()) {
                obj.add("movie_id", rs.getString("movie_id"));
                obj.add("movie_stock", rs.getString("stock"));
                obj.add("movie_name", rs.getString("name"));
                obj.add("movie_director", rs.getString("director"));
                obj.add("movie_description", rs.getString("description"));
                obj.add("movie_category", rs.getString("category"));
                obj.add("movie_language", rs.getString("language"));
                obj.add("movie_releaseyear", rs.getString("release_year"));
                obj.add("movie_format", rs.getString("format"));
                obj.add("movie_starcast", rs.getString("star_cast"));
                obj.add("movie_price", rs.getString("price"));
                String path = ConnectionToDB.movieImagePath + rs.getString("image");
                obj.add("movie_image", path);
            }
        } catch (Exception e) {

            
            JSONObject object=new JSONObject();
            object.put("flag", "error");
            object.put("errorMsg", e.getMessage());
            return object.toString();
        }
        return obj.build().toString();
    }

    @GET
    @Path("/updatecart")
    @Produces(MediaType.APPLICATION_JSON)
    public String updateCart(@QueryParam("movies") String movies) throws ClassNotFoundException, SQLException {
        JSONArray array = new JSONArray(movies);
        JSONArray response = new JSONArray();
        Class.forName("com.mysql.jdbc.Driver");
        conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
        PreparedStatement ps;
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject temp = array.getJSONObject(i);
                JSONObject movieObject = new JSONObject();
                String movie_id = temp.getString("movie_id");
                ps = (PreparedStatement) conn.prepareStatement("select * from movies where movie_id=?");
                ps.setString(1, movie_id);
                rs = ps.executeQuery();
                if (rs.next()) {
                    movieObject.put("movie_id", rs.getString("movie_id"));
                    movieObject.put("movie_stock", rs.getString("stock"));
                    movieObject.put("movie_name", rs.getString("name"));
                    movieObject.put("movie_director", rs.getString("director"));
                    movieObject.put("movie_description", rs.getString("description"));
                    movieObject.put("movie_category", rs.getString("category"));
                    movieObject.put("movie_language", rs.getString("language"));
                    movieObject.put("movie_releaseyear", rs.getString("release_year"));
                    movieObject.put("movie_format", rs.getString("format"));
                    movieObject.put("movie_starcast", rs.getString("star_cast"));
                    movieObject.put("movie_price", rs.getString("price"));
                    String path = ConnectionToDB.movieImagePath + rs.getString("image");
                    movieObject.put("movie_image", path);
                    response.put(movieObject);
                }
            }
        } catch (Exception e) {
            JSONObject errorresponse = new JSONObject();
            errorresponse.put("errorMsg", e.getMessage());
            return errorresponse.toString();
        }

        return response.toString();
    }
}
