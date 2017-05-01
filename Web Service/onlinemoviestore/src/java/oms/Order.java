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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
@Path("/orders")
public class Order {

    Connection conn;
    ResultSet rs;

    /**
     *
     * @param shippingDetail
     * @param orderDetail
     * @return
     */
    @GET
    @Path("/placeorder")
    @Produces(MediaType.APPLICATION_JSON)
    public String placeOrder(@QueryParam("shippingDetail") String shippingDetail, @QueryParam("orderDetail") String orderDetail) {
        JSONObject response = new JSONObject();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);

            JSONObject shippingDetailObject = new JSONObject(shippingDetail);
            if (shippingDetailObject.has("user_id") == false || shippingDetailObject.has("fname") == false || shippingDetailObject.has("lname") == false || shippingDetailObject.has("email") == false || shippingDetailObject.has("address") == false || shippingDetailObject.has("city") == false || shippingDetailObject.has("state") == false || shippingDetailObject.has("pincode") == false || shippingDetailObject.has("phone_no") == false) {
                response.put("flag", "error");
                response.put("errorMsg", "Invalid shipping details format");
                return response.toString();
            }
            String user_id = shippingDetailObject.getString("user_id");
            String fname = shippingDetailObject.getString("fname");
            String lname = shippingDetailObject.getString("lname");
            String email = shippingDetailObject.getString("email");
            String address = shippingDetailObject.getString("address");
            String city = shippingDetailObject.getString("city");
            String state = shippingDetailObject.getString("state");
            String pincode = shippingDetailObject.getString("pincode");
            String phone_no = shippingDetailObject.getString("phone_no");

            if (user_id.equals("") || fname.equals("") || lname.equals("") || email.equals("") || address.equals("") || city.equals("") || state.equals("") || pincode.equals("") || phone_no.equals("")) {
                response.put("flag", "error");
                response.put("errorMsg", "Invalid shipping details format");
                return response.toString();
            }

            PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement("select * from users where user_id=?");
            preparedStatement.setString(1, user_id);
            rs = preparedStatement.executeQuery();
            if (!rs.next()) {
                response.put("flag", "error");
                response.put("errorMsg", "Authentication failure");
                return response.toString();
            }

            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            String phonePattern = "\\d+";

            if (email.matches(emailPattern) == false) {
                response.put("flag", "error");
                response.put("errorMsg", "Invalid email");
                return response.toString();
            }

            if (pincode.matches(phonePattern) == false || pincode.length() != 6) {
                response.put("flag", "error");
                response.put("errorMsg", "Invalid pincode");
                return response.toString();
            }

            if (phone_no.matches(phonePattern) == false || phone_no.length() != 10) {
                response.put("flag", "error");
                response.put("errorMsg", "Invalid phone number");
                return response.toString();
            }

            JSONArray array = new JSONArray(orderDetail);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                if (object.has("movie_id") == false || object.has("movie_qty") == false) {
                    response.put("flag", "error");
                    response.put("errorMsg", "Invalid order details format");
                    return response.toString();
                }
                String movie_id = object.getString("movie_id");
                String movie_qty = object.getString("movie_qty");
                if (movie_id.equals("") || movie_qty.equals("")) {
                    response.put("flag", "error");
                    response.put("errorMsg", "Invalid order details format");
                    return response.toString();
                }
                preparedStatement = (PreparedStatement) conn.prepareStatement("select * from movies where movie_id=?");
                preparedStatement.setString(1, movie_id);
                rs = preparedStatement.executeQuery();
                if (rs.next() == false) {
                    response.put("flag", "error");
                    response.put("errorMsg", "Invalid movie ID");
                    return response.toString();
                } else if (Integer.parseInt(rs.getString("stock")) == 0) {
                    response.put("flag", "error");
                    response.put("errorMsg", "Movie " + rs.getString("name") + " is out of stock");
                    return response.toString();
                } else if (Integer.parseInt(object.getString("movie_qty")) > Integer.parseInt(rs.getString("stock"))) {
                    response.put("flag", "error");
                    response.put("errorMsg", "That many items are not available for movie " + rs.getString("name"));
                    return response.toString();
                }
            }

            preparedStatement = (PreparedStatement) conn.prepareStatement("insert into orders(user_id,fname,lname,email,phone_no,address,pincode,status,city,state,order_date) VALUES(?,?,?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user_id);
            preparedStatement.setString(2, fname);
            preparedStatement.setString(3, lname);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, phone_no);
            preparedStatement.setString(6, address);
            preparedStatement.setString(7, pincode);
            preparedStatement.setString(8, "placed");
            preparedStatement.setString(9, city);
            preparedStatement.setString(10, state);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date dateobj = new Date();
            preparedStatement.setString(11, df.format(dateobj));
            int f = preparedStatement.executeUpdate();
            rs = preparedStatement.getGeneratedKeys();
            rs.next();
            int order_id = rs.getInt(1);

            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String movie_id = object.getString("movie_id");
                String movie_qty = object.getString("movie_qty");
                preparedStatement = (PreparedStatement) conn.prepareStatement("select price,stock from movies where movie_id=?");
                preparedStatement.setString(1, movie_id);
                rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    int price = rs.getInt("price");
                    int stock = Integer.parseInt(rs.getString("stock"));
                    preparedStatement = (PreparedStatement) conn.prepareStatement("insert into movie_orders(order_id,movie_id,quantity,price,deposit,type) values(?,?,?,?,?,?)");
                    preparedStatement.setString(1, String.valueOf(order_id));
                    preparedStatement.setString(2, movie_id);
                    preparedStatement.setString(3, movie_qty);
                    preparedStatement.setString(4, String.valueOf(price));
                    preparedStatement.setString(5, String.valueOf(0));
                    preparedStatement.setString(6, "buy");
                    int flag = preparedStatement.executeUpdate();
                    stock = stock - Integer.parseInt(movie_qty);
                    PreparedStatement ps = (PreparedStatement) conn.prepareStatement("update movies set stock=? where movie_id=?");
                    ps.setString(1, String.valueOf(stock));
                    ps.setString(2, String.valueOf(movie_id));
                    flag = ps.executeUpdate();
                }
            }

        } catch (Exception e) {
            JSONObject errorresponse = new JSONObject();
            errorresponse.put("flag", "error");
            errorresponse.put("errorMsg", e.getStackTrace());
            return errorresponse.toString();
        }

        response.put("flag", "success");
        return response.toString();

    }

    @GET
    @Path("/myorders")
    @Produces(MediaType.APPLICATION_JSON)
    public String myOrders(@QueryParam("user_id") String user_id) {
        JSONObject finalObject = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from users where user_id=?");
            ps.setString(1, user_id);
            rs = ps.executeQuery();
            if (rs.next() == false) {
                JSONObject obj = new JSONObject();
                obj.put("flag", "error");
                obj.put("errorMsg", "Authentication failure");
                return obj.toString();
            }

            ps = (PreparedStatement) conn.prepareStatement("select od.order_id as order_id,od.fname as fname,od.lname as lname,od.phone_no as phone_no,od.address as address,md.name as movie_name,md.image as movie_image,md.format as format,od.order_date as order_date,od.del_date as del_date,od.status as status,mo.quantity as quantity,mo.deposit as deposit,mo.type as type,mo.price as price,mo.due_date as due_date,mo.ret_date as ret_date from orders as od join movie_orders as mo on od.order_id=mo.order_id join movies as md on mo.movie_id=md.movie_id where user_id=? order by od.order_date desc");
            ps.setString(1, user_id);
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject object = new JSONObject();
                object.put("order_id", rs.getString("order_id"));
                object.put("movie_name", rs.getString("movie_name"));
                int movie_price = Integer.parseInt(rs.getString("price")) * Integer.parseInt(rs.getString("quantity"));
                object.put("movie_price", String.valueOf(movie_price));
                object.put("movie_qty", rs.getString("quantity"));
                object.put("order_status", rs.getString("status"));
                object.put("order_date", rs.getString("order_date"));
                String movie_image = ConnectionToDB.movieImagePath + rs.getString("movie_image");
                object.put("movie_image", movie_image);
                array.put(object);
            }
        } catch (Exception e) {

            JSONObject object = new JSONObject();
            object.put("flag", "error");
            object.put("errorMsg", e.getMessage());
            return object.toString();
        }

        finalObject.put("flag", "success");
        finalObject.put("movies", array);
        return finalObject.toString();
    }

    @GET
    @Path("/cancelorder")
    @Produces(MediaType.APPLICATION_JSON)
    public String cancelOrder(@QueryParam("user_id") String user_id, @QueryParam("order_id") String order_id) {
        JSONObject responseObject = new JSONObject();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from users where user_id=?");
            ps.setString(1, user_id);
            rs = ps.executeQuery();
            if (rs.next() == false) {
                JSONObject obj = new JSONObject();
                obj.put("flag", "error");
                obj.put("errorMsg", "Authentication failure");
                return obj.toString();
            }
            rs.close();
            PreparedStatement ps1 = (PreparedStatement) conn.prepareStatement("update orders set status=? where order_id=?");
            ps1.setString(1, "cancelled");
            ps1.setString(2, order_id);
            int flag = ps1.executeUpdate();

            if (flag == 0) {
                responseObject.put("flag", "error");
                responseObject.put("errorMsg", "An error occured!");
                return responseObject.toString();
            }

            PreparedStatement ps2 = (PreparedStatement) conn.prepareStatement("select movie_id,quantity from movie_orders where order_id=?");
            ps2.setString(1, order_id);
            rs = ps2.executeQuery();
            while (rs.next()) {
                String movie_id = rs.getString("movie_id");
                String movie_qty = rs.getString("quantity");
                PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement("select stock from movies where movie_id=?");
                preparedStatement.setString(1, movie_id);
                ResultSet resultSet = preparedStatement.executeQuery();
                int stock = Integer.parseInt(movie_qty);
                if (resultSet.next()) {
                    int s = Integer.parseInt(resultSet.getString("stock"));
                    stock = stock + s;
                }
                resultSet.close();
                PreparedStatement preparedStatement1 = (PreparedStatement) conn.prepareStatement("update movies set stock=? where movie_id=?");
                preparedStatement1.setString(1, String.valueOf(stock));
                preparedStatement1.setString(2, String.valueOf(movie_id));
                flag = preparedStatement1.executeUpdate();

                if (flag == 0) {
                    responseObject.put("flag", "error");
                    responseObject.put("errorMsg", "An error occured!!!!");
                    return responseObject.toString();
                }
            }

        } catch (Exception e) {
            responseObject.put("flag", "error");
            responseObject.put("errorMsg", e.getMessage());
            return responseObject.toString();
        }

        responseObject.put("flag", "success");
        return responseObject.toString();
    }

    @GET
    @Path("/neworders")
    @Produces(MediaType.APPLICATION_JSON)
    public String getNewOrders(@QueryParam("admin_id") String admin_id, @QueryParam("searchKey") String searchKey) {
        JSONObject response = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from admin where id=?");
            ps.setString(1, admin_id);
            rs = ps.executeQuery();
            if (rs.next() == false) {
                response.put("flag", "error");
                response.put("errorMsg", "Authentication failure");
                return response.toString();
            }

            ps = (PreparedStatement) conn.prepareStatement("select orders.order_id as order_id,users.fname as fname,users.lname as lname,movies.movie_id as movie_id,movies.name as movie_name,orders.email as email,orders.phone_no as phone_no,orders.address as address,orders.pincode as pincode,orders.city as city,orders.order_date as order_date,orders.del_date as del_date,movie_orders.quantity as quantity,movie_orders.type as type,movie_orders.ret_date as ret_date,movie_orders.price as price,orders.status as status from users inner join orders on users.user_id=orders.user_id inner join movie_orders on orders.order_id=movie_orders.order_id inner join movies on movie_orders.movie_id=movies.movie_id where (orders.status='placed' and movie_orders.type='buy') and (orders.order_id like ? || orders.fname like ? || orders.lname like ? || movies.name like ? || orders.email like ? || orders.phone_no like ? || orders.pincode like ? || orders.city like ?) order by orders.order_date desc");
            ps.setString(1, "%" + searchKey + "%");
            ps.setString(2, "%" + searchKey + "%");
            ps.setString(3, "%" + searchKey + "%");
            ps.setString(4, "%" + searchKey + "%");
            ps.setString(5, "%" + searchKey + "%");
            ps.setString(6, "%" + searchKey + "%");
            ps.setString(7, "%" + searchKey + "%");
            ps.setString(8, "%" + searchKey + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                JSONObject object = new JSONObject();
                object.put("order_id", rs.getString("order_id"));
                object.put("fname", rs.getString("fname"));
                object.put("lname", rs.getString("lname"));
                object.put("movie_id", rs.getString("movie_id"));
                object.put("movie_name", rs.getString("movie_name"));
                object.put("email", rs.getString("email"));
                object.put("phone_no", rs.getString("phone_no"));
                object.put("address", rs.getString("address"));
                object.put("pincode", rs.getString("pincode"));
                object.put("city", rs.getString("city"));
                object.put("order_date", rs.getString("order_date"));
                object.put("del_date", rs.getString("del_date"));
                object.put("quantity", rs.getString("quantity"));
                object.put("type", rs.getString("type"));
                object.put("ret_date", rs.getString("ret_date"));
                object.put("price", rs.getString("price"));
                object.put("status", rs.getString("status"));
                array.put(object);
            }
        } catch (Exception e) {
            JSONObject object = new JSONObject();
            object.put("flag", "error");
            object.put("errorMsg", e.getMessage());
            return object.toString();
        }

        response.put("flag", "success");
        response.put("movies", array);
        return response.toString();
    }

    @GET
    @Path("/inprocessorders")
    @Produces(MediaType.APPLICATION_JSON)
    public String getInProcessOrders(@QueryParam("admin_id") String admin_id, @QueryParam("searchKey") String searchKey) {
        JSONObject response = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from admin where id=?");
            ps.setString(1, admin_id);
            rs = ps.executeQuery();
            if (rs.next() == false) {
                response.put("flag", "error");
                response.put("errorMsg", "Authentication failure");
                return response.toString();
            }

            ps = (PreparedStatement) conn.prepareStatement("select orders.order_id as order_id,users.fname as fname,users.lname as lname,movies.movie_id as movie_id,movies.name as movie_name,orders.email as email,orders.phone_no as phone_no,orders.address as address,orders.pincode as pincode,orders.city as city,orders.order_date as order_date,orders.del_date as del_date,movie_orders.quantity as quantity,movie_orders.type as type,movie_orders.ret_date as ret_date,movie_orders.price as price,orders.status as status from users inner join orders on users.user_id=orders.user_id inner join movie_orders on orders.order_id=movie_orders.order_id inner join movies on movie_orders.movie_id=movies.movie_id where (orders.status='In process' and movie_orders.type='buy') and (orders.order_id like ? || orders.fname like ? || orders.lname like ? || movies.name like ? || orders.email like ? || orders.phone_no like ? || orders.pincode like ? || orders.city like ?) order by orders.order_date desc");
            ps.setString(1, "%" + searchKey + "%");
            ps.setString(2, "%" + searchKey + "%");
            ps.setString(3, "%" + searchKey + "%");
            ps.setString(4, "%" + searchKey + "%");
            ps.setString(5, "%" + searchKey + "%");
            ps.setString(6, "%" + searchKey + "%");
            ps.setString(7, "%" + searchKey + "%");
            ps.setString(8, "%" + searchKey + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                JSONObject object = new JSONObject();
                object.put("order_id", rs.getString("order_id"));
                object.put("fname", rs.getString("fname"));
                object.put("lname", rs.getString("lname"));
                object.put("movie_id", rs.getString("movie_id"));
                object.put("movie_name", rs.getString("movie_name"));
                object.put("email", rs.getString("email"));
                object.put("phone_no", rs.getString("phone_no"));
                object.put("address", rs.getString("address"));
                object.put("pincode", rs.getString("pincode"));
                object.put("city", rs.getString("city"));
                object.put("order_date", rs.getString("order_date"));
                object.put("del_date", rs.getString("del_date"));
                object.put("quantity", rs.getString("quantity"));
                object.put("type", rs.getString("type"));
                object.put("ret_date", rs.getString("ret_date"));
                object.put("price", rs.getString("price"));
                object.put("status", rs.getString("status"));
                array.put(object);
            }
        } catch (Exception e) {
            JSONObject object = new JSONObject();
            object.put("flag", "error");
            object.put("errorMsg", e.getMessage());
            return object.toString();
        }

        response.put("flag", "success");
        response.put("movies", array);
        return response.toString();
    }

    @GET
    @Path("/deliveredorders")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDeliveredOrders(@QueryParam("admin_id") String admin_id, @QueryParam("searchKey") String searchKey) {

        JSONObject response = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from admin where id=?");
            ps.setString(1, admin_id);
            rs = ps.executeQuery();
            if (rs.next() == false) {
                response.put("flag", "error");
                response.put("errorMsg", "Authentication failure");
                return response.toString();
            }

            ps = (PreparedStatement) conn.prepareStatement("select orders.order_id as order_id,users.fname as fname,users.lname as lname,movies.movie_id as movie_id,movies.name as movie_name,orders.email as email,orders.phone_no as phone_no,orders.address as address,orders.pincode as pincode,orders.city as city,orders.order_date as order_date,orders.del_date as del_date,movie_orders.quantity as quantity,movie_orders.type as type,movie_orders.ret_date as ret_date,movie_orders.price as price,orders.status as status from users inner join orders on users.user_id=orders.user_id inner join movie_orders on orders.order_id=movie_orders.order_id inner join movies on movie_orders.movie_id=movies.movie_id where (orders.status='delivered' and movie_orders.type='buy') and (orders.order_id like ? || orders.fname like ? || orders.lname like ? || movies.name like ? || orders.email like ? || orders.phone_no like ? || orders.pincode like ? || orders.city like ?) order by orders.order_date desc");
            ps.setString(1, "%" + searchKey + "%");
            ps.setString(2, "%" + searchKey + "%");
            ps.setString(3, "%" + searchKey + "%");
            ps.setString(4, "%" + searchKey + "%");
            ps.setString(5, "%" + searchKey + "%");
            ps.setString(6, "%" + searchKey + "%");
            ps.setString(7, "%" + searchKey + "%");
            ps.setString(8, "%" + searchKey + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                JSONObject object = new JSONObject();
                object.put("order_id", rs.getString("order_id"));
                object.put("fname", rs.getString("fname"));
                object.put("lname", rs.getString("lname"));
                object.put("movie_id", rs.getString("movie_id"));
                object.put("movie_name", rs.getString("movie_name"));
                object.put("email", rs.getString("email"));
                object.put("phone_no", rs.getString("phone_no"));
                object.put("address", rs.getString("address"));
                object.put("pincode", rs.getString("pincode"));
                object.put("city", rs.getString("city"));
                object.put("order_date", rs.getString("order_date"));
                object.put("del_date", rs.getString("del_date"));
                object.put("quantity", rs.getString("quantity"));
                object.put("type", rs.getString("type"));
                object.put("ret_date", rs.getString("ret_date"));
                object.put("price", rs.getString("price"));
                object.put("status", rs.getString("status"));
                array.put(object);
            }
        } catch (Exception e) {
            JSONObject object = new JSONObject();
            object.put("flag", "error");
            object.put("errorMsg", e.getMessage());
            return object.toString();
        }

        response.put("flag", "success");
        response.put("movies", array);
        return response.toString();
    }

    @GET
    @Path("/cancelledorders")
    @Produces(MediaType.APPLICATION_JSON)
    public String getCancelledOrders(@QueryParam("admin_id") String admin_id, @QueryParam("searchKey") String searchKey) {

        JSONObject response = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from admin where id=?");
            ps.setString(1, admin_id);
            rs = ps.executeQuery();
            if (rs.next() == false) {
                response.put("flag", "error");
                response.put("errorMsg", "Authentication failure");
                return response.toString();
            }

            ps = (PreparedStatement) conn.prepareStatement("select orders.order_id as order_id,users.fname as fname,users.lname as lname,movies.movie_id as movie_id,movies.name as movie_name,orders.email as email,orders.phone_no as phone_no,orders.address as address,orders.pincode as pincode,orders.city as city,orders.order_date as order_date,orders.del_date as del_date,movie_orders.quantity as quantity,movie_orders.type as type,movie_orders.ret_date as ret_date,movie_orders.price as price,orders.status as status from users inner join orders on users.user_id=orders.user_id inner join movie_orders on orders.order_id=movie_orders.order_id inner join movies on movie_orders.movie_id=movies.movie_id where (orders.status='cancelled' and movie_orders.type='buy') and (orders.order_id like ? || orders.fname like ? || orders.lname like ? || movies.name like ? || orders.email like ? || orders.phone_no like ? || orders.pincode like ? || orders.city like ?) order by orders.order_date desc");
            ps.setString(1, "%" + searchKey + "%");
            ps.setString(2, "%" + searchKey + "%");
            ps.setString(3, "%" + searchKey + "%");
            ps.setString(4, "%" + searchKey + "%");
            ps.setString(5, "%" + searchKey + "%");
            ps.setString(6, "%" + searchKey + "%");
            ps.setString(7, "%" + searchKey + "%");
            ps.setString(8, "%" + searchKey + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                JSONObject object = new JSONObject();
                object.put("order_id", rs.getString("order_id"));
                object.put("fname", rs.getString("fname"));
                object.put("lname", rs.getString("lname"));
                object.put("movie_id", rs.getString("movie_id"));
                object.put("movie_name", rs.getString("movie_name"));
                object.put("email", rs.getString("email"));
                object.put("phone_no", rs.getString("phone_no"));
                object.put("address", rs.getString("address"));
                object.put("pincode", rs.getString("pincode"));
                object.put("city", rs.getString("city"));
                object.put("order_date", rs.getString("order_date"));
                object.put("del_date", rs.getString("del_date"));
                object.put("quantity", rs.getString("quantity"));
                object.put("type", rs.getString("type"));
                object.put("ret_date", rs.getString("ret_date"));
                object.put("price", rs.getString("price"));
                object.put("status", rs.getString("status"));
                array.put(object);
            }
        } catch (Exception e) {
            JSONObject object = new JSONObject();
            object.put("flag", "error");
            object.put("errorMsg", e.getMessage());
            return object.toString();
        }

        response.put("flag", "success");
        response.put("movies", array);
        return response.toString();
    }

    @GET
    @Path("/allorders")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllOrders(@QueryParam("admin_id") String admin_id, @QueryParam("searchKey") String searchKey) {

        JSONObject response = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from admin where id=?");
            ps.setString(1, admin_id);
            rs = ps.executeQuery();
            if (rs.next() == false) {
                response.put("flag", "error");
                response.put("errorMsg", "Authentication failure");
                return response.toString();
            }

            ps = (PreparedStatement) conn.prepareStatement("select orders.order_id as order_id,users.fname as fname,users.lname as lname,movies.movie_id as movie_id,movies.name as movie_name,orders.email as email,orders.phone_no as phone_no,orders.address as address,orders.pincode as pincode,orders.city as city,orders.order_date as order_date,orders.del_date as del_date,movie_orders.quantity as quantity,movie_orders.type as type,movie_orders.ret_date as ret_date,movie_orders.price as price,orders.status as status from users inner join orders on users.user_id=orders.user_id inner join movie_orders on orders.order_id=movie_orders.order_id inner join movies on movie_orders.movie_id=movies.movie_id where (movie_orders.type='buy') and (orders.order_id like ? || orders.fname like ? || orders.lname like ? || movies.name like ? || orders.email like ? || orders.phone_no like ? || orders.pincode like ? || orders.city like ?) order by orders.order_date desc");
            ps.setString(1, "%" + searchKey + "%");
            ps.setString(2, "%" + searchKey + "%");
            ps.setString(3, "%" + searchKey + "%");
            ps.setString(4, "%" + searchKey + "%");
            ps.setString(5, "%" + searchKey + "%");
            ps.setString(6, "%" + searchKey + "%");
            ps.setString(7, "%" + searchKey + "%");
            ps.setString(8, "%" + searchKey + "%");
            rs = ps.executeQuery();

            while (rs.next()) {
                JSONObject object = new JSONObject();
                object.put("order_id", rs.getString("order_id"));
                object.put("fname", rs.getString("fname"));
                object.put("lname", rs.getString("lname"));
                object.put("movie_id", rs.getString("movie_id"));
                object.put("movie_name", rs.getString("movie_name"));
                object.put("email", rs.getString("email"));
                object.put("phone_no", rs.getString("phone_no"));
                object.put("address", rs.getString("address"));
                object.put("pincode", rs.getString("pincode"));
                object.put("city", rs.getString("city"));
                object.put("order_date", rs.getString("order_date"));
                object.put("del_date", rs.getString("del_date"));
                object.put("quantity", rs.getString("quantity"));
                object.put("type", rs.getString("type"));
                object.put("ret_date", rs.getString("ret_date"));
                object.put("price", rs.getString("price"));
                object.put("status", rs.getString("status"));
                array.put(object);
            }
        } catch (Exception e) {
            JSONObject object = new JSONObject();
            object.put("flag", "error");
            object.put("errorMsg", e.getMessage());
            return object.toString();
        }

        response.put("flag", "success");
        response.put("movies", array);
        return response.toString();
    }

    @GET
    @Path("/confirmorder")
    @Produces(MediaType.APPLICATION_JSON)
    public String confirmOrder(@QueryParam("admin_id") String admin_id, @QueryParam("order_id") String order_id) {
        JSONObject response = new JSONObject();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from admin where id=?");
            ps.setString(1, admin_id);
            rs = ps.executeQuery();
            if (rs.next() == false) {
                response.put("flag", "error");
                response.put("errorMsg", "Authentication failure");
                return response.toString();
            }

            ps = (PreparedStatement) conn.prepareStatement("update orders set status=? where order_id=?");
            ps.setString(1, "In process");
            ps.setString(2, order_id);
            int flag = ps.executeUpdate();
            if (flag == 0) {
                response.put("flag", "error");
                response.put("errorMsg", "ERROR");
                return response.toString();
            }

            response.put("flag", "success");
            response.put("msg", "Order is in process");
            return response.toString();
        } catch (Exception e) {
            response.put("flag", "error");
            response.put("errorMsg", e.getMessage());
            return response.toString();
        }
    }

    @GET
    @Path("/cancelorderadmin")
    @Produces(MediaType.APPLICATION_JSON)
    public String cancelOrderAdmin(@QueryParam("admin_id") String admin_id, @QueryParam("order_id") String order_id) {
        JSONObject responseObject = new JSONObject();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from admin where id=?");
            ps.setString(1, admin_id);
            rs = ps.executeQuery();
            if (rs.next() == false) {
                JSONObject obj = new JSONObject();
                obj.put("flag", "error");
                obj.put("errorMsg", "Authentication failure");
                return obj.toString();
            }
            rs.close();
            PreparedStatement ps1 = (PreparedStatement) conn.prepareStatement("update orders set status=? where order_id=?");
            ps1.setString(1, "cancelled");
            ps1.setString(2, order_id);
            int flag = ps1.executeUpdate();

            if (flag == 0) {
                responseObject.put("flag", "error");
                responseObject.put("errorMsg", "An error occured!");
                return responseObject.toString();
            }

            PreparedStatement ps2 = (PreparedStatement) conn.prepareStatement("select movie_id,quantity from movie_orders where order_id=?");
            ps2.setString(1, order_id);
            rs = ps2.executeQuery();
            while (rs.next()) {
                String movie_id = rs.getString("movie_id");
                String movie_qty = rs.getString("quantity");
                PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement("select stock from movies where movie_id=?");
                preparedStatement.setString(1, movie_id);
                ResultSet resultSet = preparedStatement.executeQuery();
                int stock = Integer.parseInt(movie_qty);
                if (resultSet.next()) {
                    int s = Integer.parseInt(resultSet.getString("stock"));
                    stock = stock + s;
                }
                resultSet.close();
                PreparedStatement preparedStatement1 = (PreparedStatement) conn.prepareStatement("update movies set stock=? where movie_id=?");
                preparedStatement1.setString(1, String.valueOf(stock));
                preparedStatement1.setString(2, String.valueOf(movie_id));
                flag = preparedStatement1.executeUpdate();

                if (flag == 0) {
                    responseObject.put("flag", "error");
                    responseObject.put("errorMsg", "An error occured!!!!");
                    return responseObject.toString();
                }
            }

        } catch (Exception e) {
            responseObject.put("flag", "error");
            responseObject.put("errorMsg", e.getMessage());
            return responseObject.toString();
        }

        responseObject.put("flag", "success");
        return responseObject.toString();
    }

    @GET
    @Path("/deliverorder")
    @Produces(MediaType.APPLICATION_JSON)
    public String deliverOrder(@QueryParam("admin_id") String admin_id, @QueryParam("order_id") String order_id) {
        JSONObject responseObject = new JSONObject();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = (Connection) DriverManager.getConnection("jdbc:mysql://" + ConnectionToDB.host + ":" + ConnectionToDB.port + "/oms", ConnectionToDB.uname, ConnectionToDB.pass);
            PreparedStatement ps = (PreparedStatement) conn.prepareStatement("select * from admin where id=?");
            ps.setString(1, admin_id);
            rs = ps.executeQuery();
            if (rs.next() == false) {
                JSONObject obj = new JSONObject();
                obj.put("flag", "error");
                obj.put("errorMsg", "Authentication failure");
                return obj.toString();
            }
            rs.close();

            ps = (PreparedStatement) conn.prepareStatement("select movie_id,quantity from movie_orders where order_id=? and type=?");
            ps.setString(1, order_id);
            ps.setString(2, "buy");
            rs = ps.executeQuery();
            while (rs.next()) {
                String movie_id = rs.getString("movie_id");
                String quantity = rs.getString("quantity");
                PreparedStatement preparedStatement = (PreparedStatement) conn.prepareStatement("select sold from movies where movie_id=?");
                preparedStatement.setString(1, movie_id);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int sold = Integer.parseInt(resultSet.getString("sold"));
                    sold = sold + Integer.parseInt(quantity);
                    PreparedStatement preparedStatement1 = (PreparedStatement) conn.prepareStatement("update movies set sold=? where movie_id=?");
                    preparedStatement1.setString(1, String.valueOf(sold));
                    preparedStatement1.setString(2, movie_id);
                    int flag = preparedStatement1.executeUpdate();
                    if (flag == 0) {
                        responseObject.put("flag", "error");
                        responseObject.put("errorMsg", "An error occured!");
                        return responseObject.toString();
                    }
                } else {
                    responseObject.put("flag", "error");
                    responseObject.put("errorMsg", "An error occured!");
                    return responseObject.toString();
                }
            }

            ps = (PreparedStatement) conn.prepareStatement("update orders set status=?,del_date=? where order_id=?");
            ps.setString(1, "delivered");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date dateobj = new Date();
            ps.setString(2, df.format(dateobj));
            ps.setString(3, order_id);
            int flag = ps.executeUpdate();
            if (flag == 0) {
                responseObject.put("flag", "error");
                responseObject.put("errorMsg", "An error occured!");
                return responseObject.toString();

            }
        } catch (Exception e) {
            responseObject.put("flag", "error");
            responseObject.put("errorMsg", e.getMessage());
            return responseObject.toString();
        }

        responseObject.put("flag", "success");
        responseObject.put("msg", "Order status changed to delivered");
        return responseObject.toString();
    }
}
