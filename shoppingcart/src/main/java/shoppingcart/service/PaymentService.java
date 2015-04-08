package shoppingcart.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.llr.ipayment.PaymentHelper;
import com.pingplusplus.exception.PingppException;

@Service
public class PaymentService {
	
	public String charge(BigDecimal amount, String subject, String body){
		try {
			String orderNo = System.currentTimeMillis() + "";
			return PaymentHelper.newInstance().charge(orderNo, amount, subject, body);
		} catch (PingppException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String refund(String chargeId, BigDecimal amount, String desc){
		try {
			return PaymentHelper.newInstance().refund(chargeId, amount, desc);
		} catch (PingppException e) {
			e.printStackTrace();
		}
		return null;
	}
}
