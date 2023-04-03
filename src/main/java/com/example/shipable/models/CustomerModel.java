package com.example.shipable.models;

import com.example.shipable.dao.PaymentService;
import com.example.shipable.entities.Customers;
import com.example.shipable.entities.Payments;
import com.example.shipable.entities.Users;
import com.example.shipable.helpers.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class CustomerModel {
    private static final Connection connection = DbConnection.getConnection();

    public void insert(Customers customer) throws SQLException {
        String insertQuery = "INSERT INTO customers(first_name, middle_name, last_name, phone, gander, shift, address, image, weight, who_added)\n" + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        insertOrUpdateStatement(customer, insertQuery, true);
    }

    public void update(Customers customer) throws SQLException {
        String updateQuery = "UPDATE customers SET first_name=?,middle_name=?,last_name=?,phone=?,gander=?,shift=?, " + "address=?,image=?,weight=? WHERE customer_id=" + customer.getCustomerId();
        insertOrUpdateStatement(customer, updateQuery, false);
    }

    public void delete(Customers customer) throws SQLException {
        connection.setAutoCommit(false);

        try {
            String deleteCustomerQuery = "DELETE FROM customers WHERE phone=" + customer.getPhone();
            String deletePaymentsQuery = "DELETE FROM payments WHERE customer_phone_fk=" + customer.getPhone();

            Statement statement = connection.createStatement();

            statement.addBatch(deleteCustomerQuery);
            if (customer.getPayments() != null)
                statement.addBatch(deletePaymentsQuery);

            statement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
        }
        System.out.println("Customer and it's payments were deleted");
    }

    public ObservableList<Customers> fetchOfflineCustomers(Users activeUser) throws SQLException {
        System.out.println("Called offline customers");
        ObservableList<Customers> customers = FXCollections.observableArrayList();

        String fetchCustomers = fetchByRoleAndGander(activeUser.getGender(), activeUser.getRole());

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(fetchCustomers);

        while (rs.next()) {
            String customerPhone = rs.getString("phone");
            ObservableList<Payments> payments = PaymentService.fetchCustomersOfflinePayment(customerPhone);
            if (payments == null || payments.isEmpty()) {
                continue;
            }
            getCustomers(customers, rs, payments);
        }
        rs.close();
        statement.close();
        return customers;
    }

    public ObservableList<Customers> fetchOnlineCustomers(Users activeUser) throws SQLException {
        System.out.println("Called offline customers");
        ObservableList<Customers> customers = FXCollections.observableArrayList();

        String fetchCustomers = fetchByRoleAndGander(activeUser.getGender(), activeUser.getRole());

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(fetchCustomers);

        while (rs.next()) {
            String customerPhone = rs.getString("phone");
            ObservableList<Payments> payments = PaymentService.fetchCustomersOnlinePayment(customerPhone);
            if (payments == null || payments.isEmpty()) {
                continue;
            }
            getCustomers(customers, rs, payments);
        }
        rs.close();
        statement.close();
        return customers;
    }

    public ObservableList<Customers> fetchAllCustomers(Users activeUser) throws SQLException {
        System.out.println("Called customers");

        ObservableList<Customers> customers = FXCollections.observableArrayList();

        String fetchCustomers = fetchByRoleAndGander(activeUser.getGender(), activeUser.getRole());

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(fetchCustomers);

        while (rs.next()) {
            getCustomers(customers, rs, null);
        }

        rs.close();
        statement.close();
        return customers;
    }

    public ObservableList<Customers> fetchQualifiedOfflineCustomers(String customerQuery, String fromDate, String toDate) throws SQLException {

        ObservableList<Customers> customers = FXCollections.observableArrayList();

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(customerQuery);

        while (rs.next()) {
            String customerPhone = rs.getString("phone");
            ObservableList<Payments> payments = PaymentService.fetchQualifiedOfflinePayment(customerPhone, fromDate, toDate);

            if (payments == null || payments.isEmpty()) {
                continue;
            }
            Customers customer = new Customers(rs.getInt("customer_id"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("middle_name"), rs.getString("phone"), rs.getString("gander"), rs.getString("shift"), rs.getString("address"), rs.getBytes("image"), rs.getDouble("weight"), rs.getString("who_added"));

            customer.setPayments(payments);
            customers.add(customer);

        }

        return customers;

    }

    public int nextID() throws SQLException {
        String query = "SELECT * FROM SQLITE_SEQUENCE WHERE name='customers'";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(query);

        if (rs.next()) {
            return rs.getInt("seq");
        }

        return 0;
    }

    //---------------––Helpers---------------------
    private void getCustomers(ObservableList<Customers> customers, ResultSet rs, ObservableList<Payments> payment) throws SQLException {
        Customers customer = new Customers(rs.getInt("customer_id"), rs.getString("first_name"), rs.getString("last_name"), rs.getString("middle_name"), rs.getString("phone"), rs.getString("gander"), rs.getString("shift"), rs.getString("address"), rs.getBytes("image"),
                rs.getDouble("weight"), rs.getString("who_added"));

        if (payment != null) {
            customer.setPayments(payment);
        }
        customers.add(customer);
    }

    private void insertOrUpdateStatement(Customers customer, String query, boolean insert) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, customer.getFirstName());
        ps.setString(2, customer.getMiddleName());
        ps.setString(3, customer.getLastName());
        ps.setString(4, customer.getPhone());
        ps.setString(5, customer.getGander());
        ps.setString(6, customer.getShift());
        ps.setString(7, customer.getAddress());
        ps.setBytes(8, customer.getImage());
        ps.setDouble(9, customer.getWeight());
        if (insert) {
            ps.setString(10, customer.getWhoAdded());
            System.out.println("Customer added");
        } else {
            System.out.println("Customer Updated..");
        }
        ps.executeUpdate();
        ps.close();
    }

    private String fetchByRoleAndGander(String gander, String role) {

        String fetchQuery = "SELECT * FROM customers WHERE gander='" + gander + "' ORDER BY customer_id ";

        if (role.equals("super_admin")) {
            System.out.println("Active customer is " + role);
            fetchQuery = "SELECT * FROM customers ORDER BY customer_id ";
        }
        System.out.println(fetchQuery);
        return fetchQuery;
    }


}
