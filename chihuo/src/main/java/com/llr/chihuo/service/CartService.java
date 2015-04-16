package com.llr.chihuo.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CartService {

	public List<String> getPaymentMethods(){
		return Arrays.<String> asList("信用卡","银联卡","快捷支付");
	}
	
	public List<String> getDeliveryAddress(){
		return Arrays.<String> asList("家里","工作单位","其他");
	}
}
