package com.es.phoneshop.web;

import com.es.phoneshop.domain.cart.model.Cart;
import com.es.phoneshop.domain.cart.model.DisplayCartItem;
import com.es.phoneshop.domain.cart.service.CartService;
import com.es.phoneshop.domain.common.model.PaymentMethod;
import com.es.phoneshop.domain.common.model.Price;
import com.es.phoneshop.domain.order.model.ContactDetails;
import com.es.phoneshop.domain.order.model.DeliveryDetails;
import com.es.phoneshop.domain.order.service.CartEmptyException;
import com.es.phoneshop.domain.order.service.OrderService;
import com.es.phoneshop.domain.product.model.ProductPrice;
import com.es.phoneshop.domain.product.persistence.ProductDao;
import com.es.phoneshop.infra.config.Configuration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.es.phoneshop.web.MessagesHandler.MessageType.ERROR;
import static com.es.phoneshop.web.MessagesHandler.MessageType.SUCCESS;

public class CheckoutPageServlet extends HttpServlet {

    private CartService cartService;

    private MessagesHandler messagesHandler;

    private ProductDao productDao;

    private OrderService orderService;

    public CheckoutPageServlet(Configuration configuration, MessagesHandler messagesHandler) {
        this.cartService = configuration.getCartService();
        this.productDao = configuration.getProductDao();
        this.orderService = configuration.getOrderService();
        this.messagesHandler = messagesHandler;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<DisplayCartItem> productsInCart = cartService.get(request.getSession()).getItems().stream()
                .filter(it -> isPresentInDao(it.getProductId(), id ->
                        messagesHandler.add(
                                request,
                                response,
                                ERROR,
                                "Item wih id " + it.getProductId() + "is not present in catalog and hence deleted from cart.")
                ))
                .map(it -> new DisplayCartItem(productDao.getById(it.getProductId()).get(), it.getQuantity()))
                .collect(Collectors.toList());
        request.setAttribute("productsInCart", productsInCart);
        request.setAttribute("deliveryPrice", new Price(new BigDecimal(100), Currency.getInstance("USD")));
        request.setAttribute("subtotal", productsInCart.stream()
                .map(it -> {
                    ProductPrice productPrice = it.getProduct().getActualPrice();
                    return new Price(productPrice.getValue(), productPrice.getCurrency());
                })
                .reduce(new Price(new BigDecimal(0), Currency.getInstance("USD")),
                        (acc, it) -> new Price(acc.getValue().add(it.getValue()), acc.getCurrency())));
        response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
        request.getRequestDispatcher("/WEB-INF/pages/checkout.jsp").forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String firstName = request.getParameter("firstName");
        if (!verifyFirstName(firstName)) {
            showError(request, response, "First name must contain only letters.");
            return;
        }
        String lastName = request.getParameter("lastName");
        if (!verifyLastName(lastName)) {
            showError(request, response, "Last name must contain only letters.");
            return;
        }
        String phoneNumber = request.getParameter("phoneNumber");
        if (!verifyPhoneNumber(phoneNumber)) {
            showError(request, response, "Phone number must contain digits spaces or dashes.");
            return;
        }
        String deliveryDateStr = request.getParameter("deliveryDate");
        if (!verifyDeliveryDate(deliveryDateStr)) {
            showError(request, response, "Delivery date must be after now.");
            return;
        }
        String deliveryAddress = request.getParameter("deliveryAddress");
        if (!verifyAddress(deliveryAddress)) {
            showError(request, response, "Address must not be empty.");
            return;
        }

        String paymentMethodStr = request.getParameter("paymentMethod");
        if (!verifyPaymentMethod(paymentMethodStr)) {
            showError(request, response, "Payment method must be one of the suggested.");
            return;
        }

        Cart cart = cartService.get(request.getSession());
        UUID orderSecureId;
        try {
            orderSecureId = orderService.order(cart,
                    new DeliveryDetails(deliveryAddress,
                            LocalDate.parse(deliveryDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                            new Price(new BigDecimal(100), Currency.getInstance("USD"))),
                    new ContactDetails(firstName, lastName, phoneNumber),
                    PaymentMethod.fromString(paymentMethodStr));
        } catch (CartEmptyException e) {
            showError(request, response, "Cart cannot be empty.");
            return;
        }
        cartService.clear(request.getSession());
        messagesHandler.add(request, response, SUCCESS, "Products are successfully ordered!");
        response.sendRedirect(request.getContextPath() + "/order/overview/" + orderSecureId);
    }

    private boolean verifyPaymentMethod(String paymentMethod) {
        return paymentMethod != null && PaymentMethod.fromString(paymentMethod) != null;
    }

    private boolean verifyDeliveryDate(String deliveryDateStr) {
        try {
            return deliveryDateStr != null
                    && LocalDate.parse(deliveryDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .isAfter(LocalDate.now());
        } catch (DateTimeException e) {
            return false;
        }
    }

    private boolean verifyLastName(String lastName) {
        return verifyWord(lastName);
    }

    private boolean verifyAddress(String address) {
        return verifyNotEmpty(address);
    }

    private boolean verifyNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private boolean verifyFirstName(String firstName) {
        return verifyWord(firstName);
    }

    private boolean verifyPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("[0-9 -]+");
    }

    private boolean verifyWord(String word) {
        return word != null && word.matches("[A-Za-z]+");
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws IOException, ServletException {
        messagesHandler.add(request, response, ERROR, errorMessage);
        doGet(request, response);
    }

    private boolean isPresentInDao(Long productId, Consumer<Long> negativeAction) {
        if (productDao.getById(productId).isPresent()) {
            return true;
        } else {
            negativeAction.accept(productId);
            return false;
        }
    }
}
