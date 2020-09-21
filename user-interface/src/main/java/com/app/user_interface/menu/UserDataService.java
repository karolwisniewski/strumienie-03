package com.app.user_interface.menu;

import com.app.user_interface.exception.UserDataServiceException;

import java.time.LocalDate;
import java.util.Scanner;

public final class UserDataService {

    private static Scanner sc = new Scanner(System.in);

    public static String getString(String message){
        System.out.println(message);
        return sc.nextLine();
    }

    public static int getInt(String message){
        String value = getString(message);
        if(!value.matches("\\d+")){
            throw new UserDataServiceException("Value is not a number");
        }
        return Integer.parseInt(value);
    }

    public static LocalDate getDate(String message){
        String dateString = getString(message + "Insert date in format: dd.mm.yyy");
        if(!dateString.matches("\\d{2}\\.\\d{2}\\.\\d{4}")){
            throw new UserDataServiceException("Incorrect date format");
        }
        String [] dateArr = dateString.split("\\.");
        LocalDate date;
        try {
            date = LocalDate.of(
                    Integer.parseInt(dateArr[2]),
                    Integer.parseInt(dateArr[1]),
                    Integer.parseInt(dateArr[0]));
        }catch (Exception e ){
            throw new UserDataServiceException(e.getMessage());
        }
        return date;
    }
}
