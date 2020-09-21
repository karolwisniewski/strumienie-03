package com.app.user_interface;

import com.app.service.service.EmailService;
import com.app.user_interface.menu.MenuService;

public class App {
    public static void main(String[] args) {

        MenuService menuService = new MenuService("orders.json");
       // menuService.showMainMenu();
        EmailService em = new EmailService();
        em.sendAsHtml("karolus1511@wp.pl", "TOROTORO", "<h1>" + "Asdas dad a a " + "</h1>");

    }
}
