using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.IO;
using Newtonsoft.Json.Linq;

namespace OMSAdmin.Admin
{
    public partial class orders : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {

                if (Session["admin_id"] == null)
                {
                    Response.Redirect("manage.aspx?Msg=Session expired please login again");
                    return;
                }
                fetch();

            }
        }

        protected void fetch()
        {
            String uri;
            if (category.SelectedValue.ToString().Equals("placed"))
            {
                uri = "http://" + Connection.host + ":" + Connection.port + "/onlinemoviestore/orders/neworders?admin_id=" + Session["admin_id"] + "&searchKey=" + searchKey.Text;
            }
            else if(category.SelectedValue.ToString().Equals("in process"))
            {
                uri = "http://" + Connection.host + ":" + Connection.port + "/onlinemoviestore/orders/inprocessorders?admin_id=" + Session["admin_id"] + "&searchKey=" + searchKey.Text;
            }
            else if (category.SelectedValue.ToString().Equals("delivered"))
            {
                uri = "http://" + Connection.host + ":" + Connection.port + "/onlinemoviestore/orders/deliveredorders?admin_id=" + Session["admin_id"] + "&searchKey=" + searchKey.Text;
            }
            else if (category.SelectedValue.ToString().Equals("cancelled"))
            {
                uri = "http://" + Connection.host + ":" + Connection.port + "/onlinemoviestore/orders/cancelledorders?admin_id=" + Session["admin_id"] + "&searchKey=" + searchKey.Text;
            }
            else
            {
                uri = "http://" + Connection.host + ":" + Connection.port + "/onlinemoviestore/orders/allorders?admin_id=" + Session["admin_id"] + "&searchKey=" + searchKey.Text;
            }

            HttpWebRequest request = (HttpWebRequest)WebRequest.CreateHttp(uri);
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            StreamReader reader = new StreamReader(response.GetResponseStream());
            String responseText = reader.ReadToEnd();
            JObject jsonObject = JObject.Parse(responseText);
            if (jsonObject["flag"].ToString().Equals("success"))
            {
                DataTable dt = generateTable(jsonObject["movies"].ToString());
                movieList.DataSource = dt;
                movieList.DataBind();
            }
            else
            {
                msg.Text = jsonObject["errorMsg"].ToString();
            }
            generateButtons();

        }

        protected DataTable generateTable(String jsonarray)
        {
            DataTable dt = new DataTable();
            dt.Clear();
            dt.Columns.Add("order_id");
            dt.Columns.Add("fname");
            dt.Columns.Add("lname");
            dt.Columns.Add("movie_id");
            dt.Columns.Add("movie_name");
            dt.Columns.Add("email");
            dt.Columns.Add("phone_no");
            dt.Columns.Add("address");
            dt.Columns.Add("pincode");
            dt.Columns.Add("city");
            dt.Columns.Add("order_date");
            dt.Columns.Add("del_date");
            dt.Columns.Add("quantity");
            dt.Columns.Add("type");
            dt.Columns.Add("ret_date");
            dt.Columns.Add("price");
            dt.Columns.Add("status");
            JArray array = JArray.Parse(jsonarray);
            for(int i = 0; i < array.Count; i++)
            {
                JObject temp = JObject.Parse(array[i].ToString());
                DataRow row = dt.NewRow();
                row["order_id"] = temp["order_id"];
                row["fname"] = temp["fname"];
                row["lname"] = temp["lname"];
                row["movie_id"] = temp["movie_id"];
                row["movie_name"] = temp["movie_name"];
                row["email"] = temp["email"];
                row["phone_no"] = temp["phone_no"];
                row["address"] = temp["address"];
                row["pincode"] = temp["pincode"];
                row["city"] = temp["city"];
                row["order_date"] = temp["order_date"];
                row["del_date"] = temp["del_date"];
                row["quantity"] = temp["quantity"];
                row["type"] = temp["type"];
                row["ret_date"] = temp["ret_date"];
                row["price"] = temp["price"];
                row["status"] = temp["status"];
                dt.Rows.Add(row);
            }
            return dt;
        }

        protected void search_Click(object sender, EventArgs e)
        {
            fetch();

        }

        protected void generateButtons()
        {
            foreach (GridViewRow row in movieList.Rows)
            {
                string status = row.Cells[13].Text.ToString();
                if (status == "placed")
                {
                    Button b = (Button)row.Cells[14].FindControl("confirmButton");
                    b.Enabled = true;
                    b.Visible = true;
                    b = (Button)row.Cells[15].FindControl("cancelButton");
                    b.Enabled = true;
                    b.Visible = true;
                    b = (Button)row.Cells[16].FindControl("deliveryButton");
                    b.Enabled = false;
                    b.Visible = false;
                    b = (Button)row.Cells[17].FindControl("returnButton");
                    b.Enabled = false;
                    b.Visible = false;
                }
                else if (status == "In process")
                {
                    Button b = (Button)row.Cells[14].FindControl("confirmButton");
                    b.Enabled = false;
                    b.Visible = false;
                    b = (Button)row.Cells[15].FindControl("cancelButton");
                    b.Enabled = true;
                    b.Visible = true;
                    b = (Button)row.Cells[16].FindControl("deliveryButton");
                    b.Enabled = true;
                    b.Visible = true;
                    b = (Button)row.Cells[17].FindControl("returnButton");
                    b.Enabled = false;
                    b.Visible = false;
                }
                else if (status == "cancelled")
                {
                    Button b = (Button)row.Cells[14].FindControl("confirmButton");
                    b.Enabled = false;
                    b.Visible = false;
                    b = (Button)row.Cells[15].FindControl("cancelButton");
                    b.Enabled = false;
                    b.Visible = false;
                    b = (Button)row.Cells[16].FindControl("deliveryButton");
                    b.Enabled = false;
                    b.Visible = false;
                    b = (Button)row.Cells[17].FindControl("returnButton");
                    b.Enabled = false;
                    b.Visible = false;
                }
                else
                {
                    Button b = (Button)row.Cells[14].FindControl("confirmButton");
                    b.Enabled = false;
                    b.Visible = false;
                    b = (Button)row.Cells[15].FindControl("cancelButton");
                    b.Enabled = false;
                    b.Visible = false;
                    b = (Button)row.Cells[16].FindControl("deliveryButton");
                    b.Enabled = false;
                    b.Visible = false;
                    if (row.Cells[1].Text.ToString() == "rent")
                    {

                        if (row.Cells[11].Text.ToString().Length == 6)
                        {
                            b = (Button)row.Cells[17].FindControl("returnButton");
                            b.Enabled = true;
                            b.Visible = true;
                        }
                        else
                        {
                            b = (Button)row.Cells[17].FindControl("returnButton");
                            b.Enabled = false;
                            b.Visible = false;
                        }

                    }
                    else
                    {
                        b = (Button)row.Cells[17].FindControl("returnButton");
                        b.Enabled = false;
                        b.Visible = false;
                    }

                }
            }
        }


        protected void confirmButton_Click(object sender, EventArgs e)
        {
            Button b = (Button)sender;
            int order_id = Int32.Parse(b.CommandArgument);
            String uri = "http://" + Connection.host + ":" + Connection.port + "/onlinemoviestore/orders/confirmorder?admin_id="+Session["admin_id"]+"&order_id="+order_id.ToString();
            
            HttpWebRequest request = (HttpWebRequest)WebRequest.CreateHttp(uri);
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            StreamReader reader = new StreamReader(response.GetResponseStream());
            String jsonresponse = reader.ReadToEnd();
            JObject temp= JObject.Parse(jsonresponse);
            if (temp["flag"].ToString().Equals("success"))
            {
                msg.Text = temp["msg"].ToString();
            }
            else
            {
                msg.Text = temp["errorMsg"].ToString();
            }
                fetch();

            
        }

        protected void cancelButton_Click(object sender, EventArgs e)
        {
            Button b = (Button)sender;
            int order_id = Int32.Parse(b.CommandArgument);
            string uri;
            uri= "http://" + Connection.host + ":" + Connection.port + "/onlinemoviestore/orders/cancelorderadmin?admin_id=" + Session["admin_id"] + "&order_id=" + order_id.ToString();
            HttpWebRequest request = (HttpWebRequest)WebRequest.CreateHttp(uri);
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            StreamReader reader = new StreamReader(response.GetResponseStream());
            String jsonresponse = reader.ReadToEnd();
            JObject temp = JObject.Parse(jsonresponse);
            if (temp["flag"].ToString().Equals("success"))
            {
                msg.Text = "Order has been cancelled";
            }
            else
            {
                msg.Text = temp["errorMsg"].ToString();
            }
            fetch();
        }

        protected void deliveryButton_Click(object sender, EventArgs e)
        {
            Button b = (Button)sender;
            int order_id = Int32.Parse(b.CommandArgument);
            string uri;
            uri = "http://" + Connection.host + ":" + Connection.port + "/onlinemoviestore/orders/deliverorder?admin_id=" + Session["admin_id"] + "&order_id=" + order_id.ToString();
            HttpWebRequest request = (HttpWebRequest)WebRequest.CreateHttp(uri);
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            StreamReader reader = new StreamReader(response.GetResponseStream());
            String jsonresponse = reader.ReadToEnd();
            JObject temp = JObject.Parse(jsonresponse);
            if (temp["flag"].ToString().Equals("success"))
            {
                msg.Text = "Order has been delivered";
            }
            else
            {
                msg.Text = temp["errorMsg"].ToString();
            }
            fetch();

        }

        protected void returnButton_Click(object sender, EventArgs e)
        {
            Button b = (Button)sender;
            string movie_id = b.CommandName;
            string order_id = b.CommandArgument;

            fetch();

        }
    }
}