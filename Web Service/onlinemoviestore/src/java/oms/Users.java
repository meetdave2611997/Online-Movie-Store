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
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.json.JSONObject;

/**
 *
 * @author Meet Dave
 */
@Path("/users")
public class Users {

    Connection conn;
    ResultSet rs;

    @GET
    @Path("/authenticateuser")
    @Produces(MediaType.APPLICATION_JSON)
    public String authenticateUser(@QueryParam("email") String email, @QueryParam("password") String password) {
        JsonObjectBuilder obj = Json.createObjectBuilder();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from users where email=? and password=?");
            ps.setString(1, email);
            ps.setString(2, password);
            rs = ps.executeQuery();
            if (rs.next()) {
                obj.add("flag", "success");
                obj.add("user_id", rs.getString("user_id"));
                obj.add("fname", rs.getString("fname"));
                obj.add("lname", rs.getString("lname"));
                obj.add("email", rs.getString("email"));
                obj.add("phone_no", rs.getString("phone_no"));
                return obj.build().toString();
            }

        } catch (Exception e) {
            obj.add("flag", "error");
            obj.add("errorMsg", e.getMessage());
            return obj.build().toString();
        }

        obj.add("flag", "error");
        obj.add("errorMsg", "Invalid email or password");
        return obj.build().toString();
    }

    @GET
    @Path("signup")
    @Produces(MediaType.APPLICATION_JSON)
    public String signup(@QueryParam("fname") String fname, @QueryParam("lname") String lname, @QueryParam("email") String email, @QueryParam("password") String password, @QueryParam("phone_no") String phone_no) {
        JsonObjectBuilder obj = Json.createObjectBuilder();
        int user_id = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from users where email=?");
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) {
                obj.add("flag", "error");
                obj.add("errorMsg", "Email already exists try another one!");
                return obj.build().toString();
            }

            ps = (PreparedStatement) conn.prepareStatement("insert into users(fname,lname,email,password,phone_no) values(?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, fname);
            ps.setString(2, lname);
            ps.setString(3, email);
            ps.setString(4, password);
            ps.setString(5, phone_no);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                user_id = rs.getInt(1);
            }
        } catch (Exception e) {

            JSONObject object = new JSONObject();
            object.put("flag", "error");
            object.put("errorMsg", e.getMessage());
            return object.toString();
        }
        obj.add("flag", "success");
        obj.add("user_id", String.valueOf(user_id));
        obj.add("fname", fname);
        obj.add("lname", lname);
        obj.add("email", email);
        obj.add("phone_no", phone_no);
        return obj.build().toString();
    }

    @GET
    @Path("/authenticateadmin")
    @Produces(MediaType.APPLICATION_JSON)
    public String authenticateAdmin(@QueryParam("username") String username, @QueryParam("password") String password) {
        JSONObject response = new JSONObject();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement preparedStatement=(PreparedStatement)conn.prepareStatement("select * from  admin where username=? and password=?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            rs=preparedStatement.executeQuery();
            if(rs.next()){
                response.put("flag", "success");
                response.put("admin_id", rs.getString("id"));
                return response.toString();
            }
        } catch (Exception e) {
                response.put("flag", "error");
                response.put("errorMsg", e.getMessage());
                return response.toString();
        }
        
        response.put("flag", "error");
        response.put("errorMsg", "authentication failure");
        return response.toString();
    }
}
