package com.llr.ipayment;

import java.util.HashMap;
import java.util.Map;

import com.pingplusplus.Pingpp;
import com.pingplusplus.exception.PingppException;
import com.pingplusplus.model.Channel;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Refund;

public class PaymentHelper {
	private String chargeID;

    public static void main(String[] args) {
        Pingpp.apiKey = "sk_test_fDyP0SGKWXzTTKyTKKm5eD8C";
        PaymentHelper example = new PaymentHelper();
        // 支付
        example.charge();
        // 退款
 //       example.refund();
    }

    public void charge() {
        Map<String, Object> chargeMap = new HashMap<String, Object>();
        chargeMap.put("amount", 100);
        chargeMap.put("currency", "cny");
        chargeMap.put("subject",  "苹果");
        chargeMap.put("body",  "一个又大又红的红富士苹果");
        chargeMap.put("order_no", (int) Math.floor(Math.random() * 100000000) + "");
        chargeMap.put("channel",  Channel.WECHAT);
        chargeMap.put("client_ip",  "127.0.0.1");
        Map<String, String> app = new HashMap<String, String>();
        app.put("id", "app_Tyf5GCuvPyjDSO4y");
        chargeMap.put("app", app);
        try {
            Charge charge = Charge.create(chargeMap);
            chargeID = charge.getId();
            System.out.println(chargeID);
            System.out.println(charge);
            String credential = charge.getCredential();
            System.out.println(credential);
        } catch (PingppException e) {
            e.printStackTrace();
        }
    }

    public void refund() {
        try {
            Charge charge = Charge.retrieve(chargeID);
            Map<String, Object> refundMap = new HashMap<String, Object>();
            refundMap.put("amount", 100);
            refundMap.put("description", "小苹果");
            Refund re = charge.getRefunds().create(refundMap);
            System.out.println(re);
        } catch (PingppException e) {
            e.printStackTrace();
        }
    }
}
