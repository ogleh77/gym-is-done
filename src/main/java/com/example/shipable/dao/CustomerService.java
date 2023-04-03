package com.example.shipable.dao;

import com.example.shipable.entities.Customers;
import com.example.shipable.entities.Payments;
import com.example.shipable.entities.Users;
import com.example.shipable.helpers.CustomException;
import com.example.shipable.models.CustomerModel;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;

public class CustomerService {

    private static final CustomerModel customerModel = new CustomerModel();
    private static ObservableList<Customers> allCustomersList;
    private static ObservableList<Customers> offlineCustomers;
    private static ObservableList<Customers> onlineCustomers;

    public static void insertOrUpdateCustomer(Customers customer, boolean newCustomer) throws SQLException {
        try {
            if (newCustomer) {
                insertCustomer(customer);
            } else {
                updateCustomer(customer);
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("(UNIQUE constraint failed: customers.phone)")) {
                throw new CustomException("Lanbarka " + customer.getPhone() + " hore ayaa loo diwaan geshay fadlan dooro lanbarkale");
            } else {
                throw new CustomException("Khalad ayaaa ka dhacay " + e.getMessage());
            }
        }
    }

    private static void insertCustomer(Customers customer) throws SQLException {
        customerModel.insert(customer);
    }

    private static void updateCustomer(Customers customer) throws SQLException {
        customerModel.update(customer);
    }

    public static void deleteCustomer(Customers customer) throws SQLException {
        try {
            if (customer == null) {
                throw new CustomException("Fadlan ka dooro table ka macmiilka aad donayso inad masaxdo");
            } else {

                ObservableList<Payments> payments = PaymentService.fetchAllCustomersPayments(customer.getPhone());

                String pendPaymentMessage = "Macmiilkan waxa uu xidhay payment sasoo ay tahay ma delete garayn kartid" +
                        "Marka hore dib u fur paymentkisa marka wakhtigu u dhamadana wad masaxi kartaa insha Allah.";
                String onlinePaymentMessage = "Macmiilkan waxa uu u socda payment sasoo ay tahay ma delete garayn kartid" +
                        " ilaa wakhtigiisa uu dhamaysanyo insha Allah.";
                if (customer.getPayments() != null) {
                    for (Payments payment : payments) {
                        if (payment.isOnline() || payment.isPending()) {
                            throw new CustomException(payment.isOnline() ? onlinePaymentMessage : pendPaymentMessage);
                        }
                        //        customerModel.delete(customer);
                    }
                }
                allCustomersList.remove(customer);
                customerModel.delete(customer);

            }
        } catch (SQLException e) {
            throw new CustomException(e.getMessage());
        }

    }

    public static ObservableList<Customers> fetchOfflineCustomer(Users activeUser) throws SQLException {
        if (offlineCustomers == null) {
            offlineCustomers = customerModel.fetchOfflineCustomers(activeUser);
        }
        return offlineCustomers;
    }

    public static ObservableList<Customers> fetchOnlineCustomer(Users activeUser) throws SQLException {
        System.out.println("Online customers Called");

        onlineCustomers = customerModel.fetchOnlineCustomers(activeUser);
        Collections.sort(onlineCustomers);

        return onlineCustomers;
    }

    public static ObservableList<Customers> fetchAllCustomer(Users activeUser) throws SQLException {

        allCustomersList = customerModel.fetchAllCustomers(activeUser);

        System.out.println("Some one called me and I returned " + allCustomersList.hashCode());
        return allCustomersList;
    }

    public static ObservableList<Customers> fetchQualifiedOfflineCustomers(String customerQuery, LocalDate fromDate, LocalDate toDate) throws SQLException {
        String from = fromDate.toString();
        String to = toDate.toString();
        ObservableList<Customers> offlineCustomers = customerModel.fetchQualifiedOfflineCustomers(customerQuery, from, to);
        Collections.sort(offlineCustomers);
        // TODO: 26/03/2023 sort insha Allah
        System.out.println("I Service \n");
        System.out.println(offlineCustomers);
        return offlineCustomers;
    }

    public static int predictNextId() throws CustomException {
        try {
            return (1 + customerModel.nextID());
        } catch (SQLException e) {
            throw new CustomException("Khalad " + e.getMessage());
        }
    }
}
