package com.llr.chihuo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.mvc.servlet.FlowController;

import com.llr.chihuo.service.CartService;

@Controller
@RequestMapping("/cart")
public class CartController extends FlowController {

	@Autowired
	public void setFlowExecutor(FlowExecutor flowExecutor) {
		super.setFlowExecutor(flowExecutor);
	}

	@Autowired
	private CartService cartService;

	@RequestMapping
	public String index() {
		return "/cart";
	}

	@RequestMapping("/payment-methods")
	public String paymentMethods() {
		return "/payment-methods";
	}
	
	@RequestMapping("/new-details")
	public String newDetails() {
		return "/new-details";
	}

	@RequestMapping("/summary")
	public String summary() {
		return "/summary";
	}
	
	@RequestMapping("/end")
	@ResponseBody
	public String end() {
		return "付款完成！";
	}
}
