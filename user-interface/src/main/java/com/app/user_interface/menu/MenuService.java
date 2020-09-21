package com.app.user_interface.menu;

import com.app.service.service.OrdersService;

import java.time.LocalDate;

import static com.app.user_interface.menu.UserDataService.getDate;
import static com.app.user_interface.menu.UserDataService.getInt;

public class MenuService {

    private final OrdersService ordersService;

    public MenuService(String jsonFilename) {
        this.ordersService = new OrdersService(jsonFilename);
    }

    private int printMenu() {
        System.out.println("1. Show average price of products bought in given date range.");
        System.out.println("2. Show the most expensive product for every category.");
        System.out.println("3. Show date with most and date with least quantity of orders.");
        System.out.println("4. Show a customer who paid the most.");
        System.out.println("5. Show summary price for all products having a discount.");
        System.out.println("6. Show quantity of customers who bought at least giving quantity of products and save than customers to file.");
        System.out.println("7. Show Category which products was bought the most often.");
        System.out.println("8. Show quantity of orders ordered in every month.");
        System.out.println("9. Show most popular category in every month.");
        System.out.println("10. Send email with products ist to customers");
        System.out.println("0. Exit");
        return getInt("Choose an option");
    }

    public void showMainMenu() {
        int choice;
        do {
            choice = printMenu();
            switch (choice) {
                case 1 -> showAvrPriceOfProductsBoughtInDateRange();
                case 2 -> showMostExpensiveProductInEveryCategory();
                case 3 -> showDateWithMostAndLeastOrders();
                case 4 -> showCustomerWhoPaidTheMost();
                case 5 -> showSummaryPriceWithDiscount();
                case 6 -> saveToFileCustomersWhoBoughtAtLeast();
                case 7 -> showTheMostPopularCategory();
                case 8 -> showMonthWithOrdersQuantity();
                case 9 -> showTheMostPopularCategoryInMonth();
                case 10 -> sendEmailWithProductList();
                case 0 -> {
                    System.out.println("Have a nice day!");
                    return;
                }
                default -> System.out.println("Incorrect choice!");
            }

        } while (true);
    }

    private void sendEmailWithProductList(){
        ordersService.sendProductsListToCustomer();
        System.out.println("Email with products list has been send.");
    }

    private void showAvrPriceOfProductsBoughtInDateRange() {
        LocalDate date1 = getDate("Insert first date of range. ");
        LocalDate date2 = getDate("Insert second date of range. ");

        System.out.println("Average price of products bought in date range is: " + ordersService.averagePriceOfAllProductsInDateRange(date1, date2));
    }

    private void showMostExpensiveProductInEveryCategory() {
        ordersService.mostExpensiveProductInCategory().forEach(
                (k, v) -> System.out.println("In " + k + " category " + v + " is the most expensive product")
        );
    }

    private void showDateWithMostAndLeastOrders() {
        System.out.println(
                "Date " +
                        ordersService.dateWithLeastOrdersCount() +
                        " was date with least quantity of orders.\n" +
                        "Date " +
                        ordersService.dateWithMostOrdersCount() +
                        " was date with most quantity of orders.");
    }

    private void showCustomerWhoPaidTheMost() {
        System.out.println("Customer who paid the most for his orders: " + ordersService.customerWhoPaidTheMost());
    }

    private void showSummaryPriceWithDiscount() {
        System.out.println("Summary price for all orders having discount is: " + ordersService.summaryPriceWithDiscount());
    }

    private void saveToFileCustomersWhoBoughtAtLeast() {
        int quantity = getInt("Insert minimal quantity of products in every order.");
        System.out.println(ordersService.howManyCustomersWithGivenMinimalProductsQuantity(quantity));
    }

    private void showTheMostPopularCategory() {
        System.out.println("Category " + ordersService.mostPopularCategory() + " was the most popular.");
    }

    private void showMonthWithOrdersQuantity() {
        ordersService.quantityOfOrdersInMonth().forEach((k, v) -> System.out.println("In " + k + " was " + v + " orders."));
    }

    private void showTheMostPopularCategoryInMonth() {
        ordersService.mostPopularCategoryInMonth().forEach((k, v) -> System.out.println("In " + k + ",  " + v + " was the most popular."));
    }
}
