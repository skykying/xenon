package com.abubusoft.xenon;

import java.lang.reflect.Field;

import org.abubu.elio.logger.ElioLogger;

import android.content.Context;

/**
 * <p>
 * Utilità per la creazione di componenti argon.
 * 
 * @author Francesco Benincasa
 * 
 */
public abstract class ArgonBeanContext {

	/**
	 * <p>
	 * Crea un'instanza di un determinato oggetto. E' richiesto che tale oggetto abbia il costruttore di default. Tutti i field con annotazione ArgonBeanInject vengono risolti con gli oggetti già instanziati.
	 * </p>
	 * 
	 * @param clazz
	 * 		classe da istanziare
	 * @return
	 * 		crea una nuova istanza
	 */
	public static <E> E createInstance(Class<E> clazz) {
		E object = null;
		try {
			// creiamo istanza oggetto
			ElioLogger.debug("BeanInject - createInstance of allocation %s", clazz.getSimpleName());
			object = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			ElioLogger.error(e.getMessage());
			throw (new RuntimeException(e));
		}

		fillBean(object);

		return object;
	}

	/**
	 * <p>
	 * Effettua nuovamente il binding delle proprietà se eventualmente ci sono.
	 * </p>
	 * 
	 * @param bean
	 */
	public static void fillBean(Object bean) {
		// verifichiamo se è un argonComponent
		// if (clazz.getAnnotation(ArgonBean.class) != null) {
		Field[] fields = bean.getClass().getFields();
		ArgonBeanInject inject;
		int n = fields.length;
		for (int i = 0; i < n; i++) {
			inject = fields[i].getAnnotation(ArgonBeanInject.class);

			if (inject != null) {
				try {
					fields[i].set(bean, inject.value().value);
					ElioLogger.debug("BeanInject - Injected field %s with bean %s", fields[i].getName(), inject.value().toString());
				} catch (IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
					ElioLogger.error(e.getMessage());
				}

			}
		}
		// }
	}

	/**
	 * <p>
	 * Imposta un bean predefinito.
	 * </p>
	 * @param type
	 * 		tipo da impostare
	 * @param value
	 * 		istanza
	 * 		
	 */
	public static void setBean(ArgonBeanType type, Object value) {
		if (value != null)
			ElioLogger.debug("BeanInject - setBean %s of allocation %s", type.toString(), value.getClass().getName());
		else {
			ElioLogger.debug("BeanInject - setBean %s as null", type.toString());
		}
		type.value = value;
	}

	/**
	 * <p>
	 * Recupera un bean predefinito.
	 * </p>
	 * 
	 * @param type
	 * @return bean predefinito
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(ArgonBeanType type) {
		return (T) type.value;
	}

	/**
	 * <p>
	 * Permette di recuperare da qualunque punto il context
	 * </p>
	 * 
	 * @return
	 * 		contesto android
	 */
	public static Context getContext() {
		return getBean(ArgonBeanType.CONTEXT);
	}
}
