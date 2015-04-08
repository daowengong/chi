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
 * 支付帮助类
 * @author gongdaowen
 * @create 2015年4月7日 下午5:49:07
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
	 * 付款
	 * @author gongdaowen
	 * @create 2015年4月7日 下午5:48:01
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
	 * 退款
	 * @author gongdaowen
	 * @create 2015年4月7日 下午5:48:01
	 */
    public synchronized String refund(String chargeId, BigDecimal amount, String desc) throws PingppException {
    	return refund(chargeId, PMap.put0("amount"		, 100)
            				 		.put_("description"	, desc));
    }
    /**
	 * 付款
	 * @author gongdaowen
	 * @create 2015年4月7日 下午5:48:01
	 */
    public synchronized String refund(String chargeId, PMap data) throws PingppException {
    	 Charge charge = Charge.retrieve(chargeId, null, API_KEY);
    	 Refund re = charge.getRefunds().create(data.get());
    	 return re.getInstanceURL();
    }
    /**
	 * 退款
	 * @author gongdaowen
	 * @create 2015年4月7日 下午5:48:01
	 */
    public synchronized String charge(PMap data) throws PingppException {
    	Charge charge = Charge.create(data.get(), API_KEY);
		return charge.getId();
    }
    
    /**
     * 数据封装类
     */
    public static class PMap {
    	private Map<String, Object> _map = new HashMap<String, Object>();
    	/**
    	 * 静态实例化方法
    	 * @author gongdaowen
    	 * @create 2015年4月7日 下午4:49:44
    	 */
    	public static PMap put0(String key, Object value){
    		return new PMap().put_(key, value);
    	}
    	/**
    	 * put方法
    	 * @author gongdaowen
    	 * @create 2015年4月7日 下午4:49:44
    	 */
	    public PMap put_(String key, Object value){
	    	_map.put(key, value);
	    	return this;
	    }
	    /**
	     * 获取最终的Map
	     * @author gongdaowen
	     * @create 2015年4月7日 下午4:50:39
	     */
	    public Map<String, Object> get(){
	    	return _map;
	    }
    }
    /**
     * 数据封装工具类
     */
    static class PMapUtil {
    	// 不转换的class
    	static List<Class<?>> filterClass = Arrays.asList(new Class<?>[] {String.class, Integer.class, Float.class, Double.class, Character.class, Short.class, Long.class, Byte.class, Boolean.class, BigDecimal.class});
	    /**
	     * 转换对象为PMap
	     */
    	public static PMap convert(Object obj) throws Exception {
	    	if(!isConvert(obj)) {
	    		throw new RuntimeException("参数不能为：" + PMapUtil.filterClass.toString().replace("class ", ""));
	    	}
	    	return (PMap) _convert(obj);
	    }
    	/**
	     * 转换对象为PMap（如果是不需要转换的class[filterClass]，直接返回；否则返回转换好的PMap）
	     */
    	private static Object _convert(Object obj) throws Exception {
    		if(isConvert(obj)) {
	    		PMap _map = new PMap();
	    		BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
	    		PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
	    		for(PropertyDescriptor prop : properties){
	    			Object val = prop.getReadMethod().invoke(obj);
	    			// put到Map中
	    			_map.put_(prop.getName(), _convert(val));
	    		}
	    		return _map.get();
	    	}
	    	return obj;
	    }
    	/**
    	 * 是否需要转换（是否存在filterClass中）
    	 */
    	public static boolean isConvert(Object obj){
    		return obj != null && !filterClass.contains(obj.getClass());
    	}
    }
}
