package com.llr.ipayment;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pingplusplus.exception.PingppException;
import com.pingplusplus.model.Channel;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Refund;

/**
 * ֧��������
 * @author gongdaowen
 * @create 2015��4��7�� ����5:49:07
 */
public class PaymentHelper {
	private final static PaymentHelper paymentHelper = new PaymentHelper();
	
	private static final String 				API_KEY = "sk_test_fDyP0SGKWXzTTKyTKKm5eD8C";
	private static final Map<String, Object> 	APP 	= PMap.put0("id", "app_Tyf5GCuvPyjDSO4y").get();
	
	private PaymentHelper(){}
	
	public static PaymentHelper newInstance(){
		return paymentHelper;
	}
	/**
	 * ����
	 * @author gongdaowen
	 * @create 2015��4��7�� ����5:48:01
	 */
    public synchronized String charge(String orderNo, BigDecimal amount, String subject, String body) throws PingppException {
        return charge(PMap.put0("amount"	, amount)
            			  .put_("currency"	, "cny")
            			  .put_("subject"	, subject)
            			  .put_("body"		, body)
            			  .put_("order_no"	, orderNo)
            			  .put_("channel"	, Channel.WECHAT)
            			  .put_("client_ip"	, "127.0.0.1")
            			  .put_("app"		, APP));
    }
    /**
	 * �˿�
	 * @author gongdaowen
	 * @create 2015��4��7�� ����5:48:01
	 */
    public synchronized String refund(String chargeId, BigDecimal amount, String desc) throws PingppException {
    	return refund(chargeId, PMap.put0("amount"		, 100)
            				 		.put_("description"	, desc));
    }
    /**
	 * ����
	 * @author gongdaowen
	 * @create 2015��4��7�� ����5:48:01
	 */
    public synchronized String refund(String chargeId, PMap data) throws PingppException {
    	 Charge charge = Charge.retrieve(chargeId, null, API_KEY);
    	 Refund re = charge.getRefunds().create(data.get());
    	 return re.getInstanceURL();
    }
    /**
	 * �˿�
	 * @author gongdaowen
	 * @create 2015��4��7�� ����5:48:01
	 */
    public synchronized String charge(PMap data) throws PingppException {
    	Charge charge = Charge.create(data.get(), API_KEY);
		return charge.getId();
    }
    
    /**
     * ���ݷ�װ��
     */
    public static class PMap {
    	private Map<String, Object> _map = new HashMap<String, Object>();
    	/**
    	 * ��̬ʵ��������
    	 * @author gongdaowen
    	 * @create 2015��4��7�� ����4:49:44
    	 */
    	public static PMap put0(String key, Object value){
    		return new PMap().put_(key, value);
    	}
    	/**
    	 * put����
    	 * @author gongdaowen
    	 * @create 2015��4��7�� ����4:49:44
    	 */
	    public PMap put_(String key, Object value){
	    	_map.put(key, value);
	    	return this;
	    }
	    /**
	     * ��ȡ���յ�Map
	     * @author gongdaowen
	     * @create 2015��4��7�� ����4:50:39
	     */
	    public Map<String, Object> get(){
	    	return _map;
	    }
    }
    /**
     * ���ݷ�װ������
     */
    static class PMapUtil {
    	// ��ת����class
    	static List<Class<?>> filterClass = Arrays.asList(new Class<?>[] {String.class, Integer.class, Float.class, Double.class, Character.class, Short.class, Long.class, Byte.class, Boolean.class, BigDecimal.class});
	    /**
	     * ת������ΪPMap
	     */
    	public static PMap convert(Object obj) throws Exception {
	    	if(!isConvert(obj)) {
	    		throw new RuntimeException("��������Ϊ��" + PMapUtil.filterClass.toString().replace("class ", ""));
	    	}
	    	return (PMap) _convert(obj);
	    }
    	/**
	     * ת������ΪPMap������ǲ���Ҫת����class[filterClass]��ֱ�ӷ��أ����򷵻�ת���õ�PMap��
	     */
    	private static Object _convert(Object obj) throws Exception {
    		if(isConvert(obj)) {
	    		PMap _map = new PMap();
	    		BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
	    		PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
	    		for(PropertyDescriptor prop : properties){
	    			Object val = prop.getReadMethod().invoke(obj);
	    			// put��Map��
	    			_map.put_(prop.getName(), _convert(val));
	    		}
	    		return _map.get();
	    	}
	    	return obj;
	    }
    	/**
    	 * �Ƿ���Ҫת�����Ƿ����filterClass�У�
    	 */
    	public static boolean isConvert(Object obj){
    		return obj != null && !filterClass.contains(obj.getClass());
    	}
    }
}
