package shoppingcart.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import shoppingcart.service.PaymentService;

@Controller
@RequestMapping("/ipayment")
public class PaymentController {
	
	@Autowired
	private PaymentService paymentService;
	
	@RequestMapping
	public String index(){
		return "/index";
	}
	
	@RequestMapping(value = "/charge", method = RequestMethod.POST)
	public String charge(@RequestParam(required = true) BigDecimal amount, @RequestParam(required = true) String subject, String body){
		paymentService.charge(amount, subject, body);
		return "/index";
	}
	
	@RequestMapping(value = "/refund", method = RequestMethod.POST)
	public String refund(@RequestParam(required = true) String chargeId, @RequestParam(required = true) BigDecimal amount, @RequestParam(required = true) String desc){
		paymentService.refund(chargeId, amount, desc);
		return "/index";
	}
}