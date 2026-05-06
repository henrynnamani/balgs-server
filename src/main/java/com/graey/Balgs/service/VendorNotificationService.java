package com.graey.Balgs.service;

import com.graey.Balgs.model.Order;
import com.graey.Balgs.model.Vendor;
import com.graey.Balgs.service.TermiiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VendorNotificationService {

    private final TermiiService termiiService;

    public void notifyNewOrder(Vendor vendor, Order order) {
        String message = String.format(
                "Hi %s, you have a new order!\n" +
                        "Order: #%s\n" +
                        "Customer: %s\n" +
                        "Item: %s\n" +
                        "Amount: NGN %,.0f\n" +
                        "Login to Balgs to confirm.",
                vendor.getBusinessName(),
                order.getId().toString().substring(0, 8).toUpperCase(),
                order.getUser().getFullName(),
                order.getItem().getProduct().getModel(),
                order.getTotalPrice()
        );

        System.out.println("Working!!!");

        termiiService.sendSms(vendor.getPhoneNumber(), message);
    }

    public void notifyOrderStatusUpdate(Vendor vendor, Order order) {
        String message = String.format(
                "Order #%s has been updated to: %s.\n" +
                        "Login to Balgs for details.",
                order.getId().toString().substring(0, 8).toUpperCase(),
                order.getStatus()
        );

        termiiService.sendSms(vendor.getPhoneNumber(), message);
    }

//    public void notifyOrderCancelled(Vendor vendor, Order order) {
//        String message = String.format(
//                "Order #%s has been cancelled by the customer.",
//                order.getId().substring(0, 8).toUpperCase()
//        );
//
//        termiiService.sendSms(vendor.getPhoneNumber(), message);
//    }
}