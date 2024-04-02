package com.example.fashionfrenzy.controller;

import com.example.fashionfrenzy.FetchProductsFromExcelBasedOnCategory;
import com.example.fashionfrenzy.SendDeals;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SendDealsController {

    @Autowired
    private SendDeals sendDeals;

    @GetMapping("/send-deals")
    public String showSendDealsForm(Model model) {
        model.addAttribute("to", "");
        return "send-deals";
    }

    @PostMapping("/send-deals")
    public String sendDeals(@RequestParam("to") String to, Model model) {
//        boolean isSent = sendDeals.sendEmail(to, "frenzyfashionacc@gmail.com", "Fashion-Frenzy Top Deals", getFashionDealText());
//        if (isSent) {
//            model.addAttribute("message", "Email sent successfully!");
//        } else {
//            model.addAttribute("message", "There was a problem sending the email.");
//        }
        return "send-deals";

    }

    private String getFashionDealText() {
        StringBuilder textBuilder = new StringBuilder();
        textBuilder.append("Hello.. Sale is going on the fashion frenzy....!\n\n");
        textBuilder.append("Here are the top deals:\n");
        textBuilder.append(FetchProductsFromExcelBasedOnCategory.readData(Arrays.asList("product_info.xlsx")));
        return textBuilder.toString();
    }
}
