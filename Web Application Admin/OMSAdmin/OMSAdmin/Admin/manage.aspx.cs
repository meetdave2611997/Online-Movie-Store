using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Net;
using System.IO;
using Newtonsoft.Json;
using Newtonsoft;
using Newtonsoft.Json.Linq;

namespace OMSAdmin.Admin
{
    public partial class manage : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            if (!IsPostBack)
            {
                string errorMessage = Request.QueryString["Msg"];
                if (errorMessage != "")
                {
                    errorMsg.Text = errorMessage;
                }

            }
        }

        protected void login_button_click(object sender, EventArgs e)
        {
            try
            {
                String uri = "http://" + Connection.host + ":" + Connection.port + "/onlinemoviestore/users/authenticateadmin?username="+username.Text+"&password="+password.Text;
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(uri);
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                StreamReader reader = new StreamReader(response.GetResponseStream());
                string responseText = reader.ReadToEnd();
                JObject loginJson= JObject.Parse(responseText);
                string flag = (string)loginJson["flag"];
                if (flag.Equals("success")){
                    Session["admin_id"] = loginJson["admin_id"].ToString();
                    Response.Redirect("orders.aspx");
                }
                else
                {
                    errorMsg.Text = loginJson["errorMsg"].ToString();
                }


            }
            catch (Exception e1)
            {
            }
         
        }
    }
}