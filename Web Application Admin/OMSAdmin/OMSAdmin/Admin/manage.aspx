<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="manage.aspx.cs" Inherits="OMSAdmin.Admin.manage" %>

<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title></title>
</head>
<body>
    <center>
        <h1>Online Movie Store Admin Panel</h1>
        <hr />
    </center>
            
    <form id="login_form" runat="server">
    <center>
          <asp:Label ID="errorMsg" runat="server" Text=""></asp:Label>
    <br />   
    <table>
        <tr>
            <td>Username:</td><td><asp:TextBox ID="username" runat="server" placeholder="Username"></asp:TextBox> </td>
        </tr>
        <tr>
            <td>Password:</td><td><asp:TextBox ID="password" TextMode="Password" runat="server" placeholder="Password"></asp:TextBox></td>
        </tr>
        <tr>
            <td><asp:Button ID="login_button" runat="server" Text="Login" style="height: 26px" OnClick="login_button_click" /></td>
        </tr>
    </table>
        </center>
    </form>
</body>
</html>
