<%@ Page Title="" Language="C#" MasterPageFile="~/Admin/header.Master" AutoEventWireup="true" CodeBehind="orders.aspx.cs" Inherits="OMSAdmin.Admin.orders" %>
<asp:Content ID="Content1" ContentPlaceHolderID="contentplaceholder1" runat="server">
    <h3>Orders:-</h3>
    <br />
    
    Search:
    <asp:DropDownList ID="category" runat="server">
        <asp:ListItem Text="New Orders" Value="placed"></asp:ListItem>
        <asp:ListItem Text="In process" Value="in process"></asp:ListItem>
        <asp:ListItem Text="Delivered" Value="delivered"></asp:ListItem>
        <asp:ListItem Text="Cancelled" Value="cancelled"></asp:ListItem>
        <asp:ListItem Text="All" Value=""></asp:ListItem>
    </asp:DropDownList>
    <asp:TextBox ID="searchKey" runat="server" Columns="60"></asp:TextBox>
    <asp:Button ID="search" runat="server" Text="Search" OnClick="search_Click" /><br />
    <asp:Label ID="msg" runat="server" Text=""></asp:Label>
    <br />
    <asp:GridView ID="movieList" runat="server" AutoGenerateColumns="false">
        <Columns>
            <asp:BoundField DataField="order_id" HeaderText="ID" />
            <asp:BoundField DataField="type" HeaderText="Type" />
            <asp:BoundField DataField="fname" HeaderText="FName" />
            <asp:BoundField DataField="lname" HeaderText="LName" />
            <asp:BoundField DataField="movie_name" HeaderText="Movie Name" />
            <asp:BoundField DataField="email" HeaderText="Email" />
            <asp:BoundField DataField="phone_no" HeaderText="Phone No" />
            <asp:BoundField DataField="pincode" HeaderText="Pincode" />
            <asp:BoundField DataField="city" HeaderText="City" />
            <asp:BoundField DataField="order_date" DataFormatString="{0:dd-MM-yyyy}" HeaderText="Order Date" />
            <asp:BoundField DataField="del_date" DataFormatString="{0:dd-MM-yyyy}" HeaderText="Delivery Date" />
            <asp:BoundField DataField="ret_date" DataFormatString="{0:dd-MM-yyyy}" HeaderText="Return Date" />
            <asp:BoundField DataField="quantity" HeaderText="Quantity" />
            <asp:BoundField DataField="status" HeaderText="Status" />
            <asp:TemplateField>
                <ItemTemplate>
                    <asp:Button ID="confirmButton" CommandArgument='<%# Bind("order_id") %>' OnClick="confirmButton_Click" runat="server" Text="Confirm" />
                </ItemTemplate>
            </asp:TemplateField>
            <asp:TemplateField>
                    <ItemTemplate>
                        <asp:Button ID="cancelButton" CommandArgument='<%# Bind("order_id") %>' OnClick="cancelButton_Click" runat="server" Text="Cancel" />
                    </ItemTemplate>
            </asp:TemplateField>
            <asp:TemplateField>
                <ItemTemplate>
                    <asp:Button ID="deliveryButton" CommandArgument='<%# Bind("order_id") %>' OnClick="deliveryButton_Click" runat="server" Text="Delivery" />
                </ItemTemplate>
            </asp:TemplateField>
            <asp:TemplateField>
                <ItemTemplate>
                    <asp:Button ID="returnButton" CommandArgument='<%# Bind("order_id") %>' CommandName='<%# Bind("movie_id") %>' OnClick="returnButton_Click" runat="server" Text="Returned" />
                </ItemTemplate>
            </asp:TemplateField>
        </Columns>
    </asp:GridView>
</asp:Content>
