using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace OMSAdmin.Admin
{
    public partial class header : System.Web.UI.MasterPage
    {
        string username;
        protected void Page_Load(object sender, EventArgs e)
        {
            /*        if (!IsPostBack)
                    {
                        if (Session["username"] == null)
                        {
                            Response.Redirect("manage.aspx?Msg=Session expired please login again");
                            return;
                        }
                        username = Session["username"].ToString();
                        usernameLabel.Text = username;
                    }*/
        }

        protected void logout_button_Click(object sender, EventArgs e)
        {
            Session["admin_id"] = null;
            Response.Redirect("manage.aspx?Msg=Logged out successfully");
            return;
        }
    }
}