package cn.com.aeon.web.admin.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mchange.v2.beans.BeansUtils;

public class BeanToMapUtils {

	private static Log log = LogFactory.getLog(BeanToMapUtils.class);

	/**
	 * 将bean转换成map
	 * 
	 * @param bean
	 * @param removeNullValue
	 *            是否移除value=null的值
	 * @return map对象
	 */
	public static Map<String, Object> beanToMap(Object bean,
			boolean removeNullValue) {

		if (null == bean) {
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BeansUtils.extractAccessiblePropertiesToMap(map, bean);
			if (removeNullValue) {
				removeNullValue(map);
			}
		} catch (Exception e) {
			log.error(e);
		}

		return map;
	}

	/**
	 * 去除map中value=null的对象
	 * 
	 * @param map
	 */
	private static void removeNullValue(Map<String, Object> map) {

		Iterator<String> it = map.keySet().iterator();

		while (it.hasNext()) {

			String key = it.next();
			Object value = map.get(key);

			log.warn("removeNullValue key:" + key + ",value:" + value);

			if (null == value) {
				it.remove();
			}
		}
	}

}
