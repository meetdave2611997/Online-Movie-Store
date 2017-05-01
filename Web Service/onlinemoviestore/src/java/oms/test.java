/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package oms;

import java.io.File;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
/**
 *
 * @author Meet Dave
 */

@Path("/test")
public class test {
    @GET
    @Path("/msg")
    @Produces("image/jpg")
    public Response  getMsg(){
        File f=new File("1.jpg");
        ResponseBuilder response = Response.ok((Object) f);
        response.header("Content-Disposition","attachment; filename=image_from_server.png");
	return response.build();
        
    }
}
