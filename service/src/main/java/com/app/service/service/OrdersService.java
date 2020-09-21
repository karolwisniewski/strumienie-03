package com.app.service.service;

import com.app.converter.model.*;
import com.app.service.exception.OrdersServiceException;
import org.eclipse.collections.impl.collector.Collectors2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class OrdersService {

    private List<Order> orders;

    public OrdersService(String jsonFilename) {
        this.orders = new DataGenerator(jsonFilename).fromJson()
                .orElseThrow(() -> new OrdersServiceException("Json to orders list conversion failed!"));
    }

    /*
    1. Obliczenie średniej ceny wszystkich produktów, które
    zamówiono w przedziale czasowym <d1, d2>, gdzie d1 oraz d2
    to podane np. jako argument metody obiekty typu LocalDate.
     */
    public BigDecimal averagePriceOfAllProductsInDateRange(LocalDate d1, LocalDate d2) {
        if (Objects.isNull(d1) || Objects.isNull(d2)) {
            throw new OrdersServiceException("Date range is null");
        }
        if (d1.compareTo(d2) > 0) {
            LocalDate tmp = d1;
            d1 = d2;
            d2 = tmp;
        }

        LocalDate finalD2 = d2;
        LocalDate finalD1 = d1;

        return
                orders
                        .stream()
                        .filter(order -> order.getOrderDate().compareTo(finalD1) >= 0 && order.getOrderDate().compareTo(finalD2) <= 0)
                        .collect(Collectors2.summarizingBigDecimal(order -> order.getProduct().getPrice()))
                        .getAverage();
    }

    /*
    2. Wyznaczenie dla każdej kategorii produktu o największej cenie.
     */
    public Map<Category, Product> mostExpensiveProductInCategory() {
        return
                orders
                        .stream()
                        .collect(Collectors.groupingBy(
                                order -> order.getProduct().getCategory(),
                                Collectors.collectingAndThen(
                                        Collectors.mapping(Order::getProduct, Collectors.toList()),
                                        product -> product.stream().max(Comparator.comparing(Product::getPrice)).orElseThrow()
                                )
                        ));
    }

    /*
    3. Przygotowanie dla każdego klienta zestawienia wszystkich
    produktów, które zamówił i wysłanie na jego adres e-mail tak
    otrzymanego zestawienia (zastosuj wybraną przez Ciebie bibliotekę
    do wysyłania wiadomości email).
     */
    public void sendProductsListToCustomer() {
        EmailService emailService = new EmailService();
        customerWithProductsList().forEach(
                (k, v) -> emailService.sendAsHtml(k.getEmail(), "Products List", "<h1>" + v + "</h1>"));
    }

    public Map<Customer, String> customerWithProductsList() {
        return
                orders
                        .stream()
                        .collect(Collectors.groupingBy(
                                Order::getCustomer,
                                Collectors.collectingAndThen(
                                        Collectors.mapping(x -> x.getProduct(), Collectors.toList()),
                                        x -> x.stream().map(product -> product.toString()).collect(Collectors.joining("\n"))
                                )));
    }

    /*
    4. Wyznaczenie daty, dla której złożono najwięcej zamówień
    oraz daty dla której złożono najmniej zamówień.
     */
    private Map<LocalDate, Long> dateWithOrdersCount() {
        return
                orders
                        .stream()
                        .collect(Collectors.groupingBy(
                                Order::getOrderDate,
                                Collectors.counting()
                        ));
    }

    public LocalDate dateWithMostOrdersCount() {
        return
                Collections.max(dateWithOrdersCount().entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public LocalDate dateWithLeastOrdersCount() {
        return
                Collections.min(dateWithOrdersCount().entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    /*
    5. Wyznacz informację o kliencie, który zapłacił najwięcej za złożone zamówienia.
     */
    public Customer customerWhoPaidTheMost() {
        return
                orders
                        .stream()
                        .collect(Collectors.groupingBy(
                                Order::getCustomer,
                                Collectors.collectingAndThen(
                                        Collectors.mapping(order -> order.getProduct().getPrice().multiply(BigDecimal.valueOf(order.getQuantity())), Collectors.toList()),
                                        price -> price.stream().collect(Collectors2.summingBigDecimal(p -> p)))
                                )
                        )
                        .entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .orElseThrow()
                        .getKey();
    }

    /*
    6. Zakładamy, że wszystkie zamówienia, których data realizacji jest nie później
    niż 2 dni od daty aktualnej dostają 2% rabat. Zamówienie dla klientów przed
    25 rokiem życia dostają rabat 3%. Uwaga rabaty nie sumuję się, tylko wybierany
    jest korzystniejszy wariant. Uwzględniając te informacje wyznacz całkowitą
    cenę wszystkich zamówień.
     */
    public BigDecimal summaryPriceWithDiscount() {
        final BigDecimal DISCOUNT_2 = new BigDecimal("0.98");
        final BigDecimal DISCOUNT_3 = new BigDecimal("0.97");
        return
                orders
                        .stream()
                        .map(order -> {
                            if (order.getCustomer().getAge() < 25) {
                                return order.getProduct().getPrice()
                                        .multiply(BigDecimal.valueOf(order.getQuantity()))
                                        .multiply(DISCOUNT_3);
                            } else if (order.getOrderDate().minusDays(2).isAfter(LocalDate.now())
                                    || order.getOrderDate().minusDays(2).isEqual(LocalDate.now())) {
                                return order.getProduct().getPrice()
                                        .multiply(BigDecimal.valueOf(order.getQuantity()))
                                        .multiply(DISCOUNT_2);
                            }
                            return order.getProduct().getPrice()
                                    .multiply(BigDecimal.valueOf(order.getQuantity()));
                        })
                        .collect(Collectors2.summingBigDecimal(p -> p));
    }

    /*
    7. Wyznacz liczbę klientów, którzy za każdym razem zamówili co najmniej x sztuk
    zamawianego produktu. Wartość zmiennej x przekazywana jest przykładowo
    jako argument metody. Informacje o takich klientach zapisz do pliku JSON.
     */
    private List<Customer> howManyCustomersWithProductsQuantity(int quantity) {
        if (quantity <= 0) {
            throw new OrdersServiceException("Value can not be less or equal than zero");
        }
        return
                orders
                        .stream()
                        .collect(Collectors.groupingBy(
                                Order::getCustomer,
                                Collectors.mapping(Order::getQuantity, Collectors.toList())
                        ))
                        .entrySet()
                        .stream()
                        .filter(cwq -> cwq.getValue().stream().allMatch(q -> q >= quantity))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
    }

    public String howManyCustomersWithGivenMinimalProductsQuantity(int quantity) {
        List<Customer> customersWithProductsQuantity = howManyCustomersWithProductsQuantity(quantity);
        CustomerConverter converter = new CustomerConverter("customers_who_bought_at_least_" + quantity + " _quantity_of_products.json");
        converter.toJson(customersWithProductsQuantity);
        return
                "Number of customers who bought at least given quantity of products in every order is: "
                        + customersWithProductsQuantity.size() + ". Information about them was saved to file.";
    }

    /*
    8. Wyznacz kategorię, której produkty kupowano najczęściej.
     */
    public Category mostPopularCategory() {
        return
                orders
                        .stream()
                        .collect(Collectors.groupingBy(
                                order -> order.getProduct().getCategory(),
                                Collectors.counting()
                        ))
                        .entrySet()
                        .stream()
                        .max(Map.Entry.comparingByValue())
                        .orElseThrow()
                        .getKey();

    }

    /*
    9. Wykonaj zestawienie, w którym podasz nazwę miesiąca oraz ilość
    produktów zamówionych w tym miesiącu. Zestawienie posortuj malejąco
    według ilości zamówionych produktów.
     */
    public Map<Month, Long> quantityOfOrdersInMonth() {
        return
                orders
                        .stream()
                        .collect(Collectors.groupingBy(
                                order -> order.getOrderDate().getMonth(),
                                Collectors.counting()
                        ))
                        .entrySet()
                        .stream()
                        .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                Long::sum,
                                LinkedHashMap::new
                        ));
    }

    /*
    10. Wykonaj zestawienie, w którym podasz nazwę miesiąca oraz
    kategorię produktu, której produkty najchętniej w tym miesiącu zamawiano.
     */
    public Map<Month, Category> mostPopularCategoryInMonth() {
        return
                orders
                        .stream()
                        .collect(Collectors.groupingBy(
                                order -> order
                                        .getOrderDate()
                                        .getMonth(),
                                Collectors.collectingAndThen(
                                        Collectors.groupingBy(
                                                order -> order
                                                        .getProduct()
                                                        .getCategory(),
                                                Collectors.counting()),
                                        qic -> qic.entrySet()
                                                .stream()
                                                .max(Map.Entry.comparingByValue()).orElseThrow()
                                                .getKey())));
    }
}
