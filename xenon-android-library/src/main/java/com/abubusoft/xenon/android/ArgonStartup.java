package com.abubusoft.xenon.android;

import com.abubusoft.xenon.Argon;
import com.abubusoft.xenon.Argon4App;
import com.abubusoft.xenon.Argon4OpenGL;
import com.abubusoft.xenon.ArgonBeanContext;
import com.abubusoft.xenon.ArgonBeanType;
import com.abubusoft.xenon.R;
import com.abubusoft.xenon.settings.ArgonSettings;
import com.abubusoft.xenon.settings.ArgonSettingsFactory;
import com.abubusoft.xenon.settings.ArgonSettingsReader;
import com.abubusoft.xenon.settings.LoggerAppenderSettings;
import org.abubu.elio.ElioRuntimeException;
import org.abubu.elio.application.ApplicationInfo;
import org.abubu.elio.application.ApplicationManager;
import org.abubu.elio.application.ApplicationUpgradePolicy;
import org.abubu.elio.logger.ElioLogger;

import android.app.Application;

/**
 * <p>
 * Rappresenta l'application di base di tutte le applicazioni che si basano su argon.
 * </p>
 * 
 * @author Francesco Benincasa
 * 
 */
public class ArgonStartup extends Application implements ArgonSettingsFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.abubu.argon.ArgonApplicationFactory#buildSettings()
	 */
	@Override
	public ArgonSettings buildSettings() {
		return ArgonSettingsReader.readFromRawXml(this, R.raw.argon_settings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		// 1 - carichiamo settings da file xml
		ArgonSettings settings = buildSettings();

		// 2 - definiamo il log
		applyElioLoggerSettings(settings);

		// 3 - sistemiamo applicationManager
		ApplicationManager am = ApplicationManager.instance();
		applyStartupSettings(am, settings);

		// 4 - startup dell'application manager
		ApplicationInfo info = am.startup(this);

		ElioLogger.info("ArgonStartup - onCreate, mode %s", settings.application.mode);
		

		// avvio applicazione
		ElioLogger.info("Application %s ver. %s stopped, execution counter %s ", info.name, info.version, info.executionNumber);

		// 5 - cancelliamo la parte relativa ai file xml che rendono persistente
		// la configurazione
		if (settings.application.resetConfig) {
			// avvio applicazione
			ElioLogger.info("Both system and application preferences are reset by configuration");
			// pulisce la configurazione di sistema
			am.resetSystemPreferences();
			am.resetApplicationPreferences();
		}

		// 6 - avvia la parte mode e l'application
		Argon argon = null;

		switch (settings.application.mode) {
		case APP:
			argon = new Argon4App();
			break;
		case OPENGL:
			argon = new Argon4OpenGL();
			if (settings.application.activityClazz == null) {
				settings.application.activityClazz = ArgonActivity4OpenGL.class.getName();
			}
			break;
		default:
			throw (new ElioRuntimeException("No valid application type was defined with settings.application.mode parameter"));
		}

		// impostiamo il bean context
		ArgonBeanContext.setBean(ArgonBeanType.CONTEXT, this);
		// impostiamo il bean argon
		ArgonBeanContext.setBean(ArgonBeanType.ARGON, argon);
		argon.onStartup(this, settings, am.getApplicationPreferences());

		// imposta il config storage
		am.configStorage = argon.getConfigStorage();

		//
		// <p>lo dobbiamo mettere qua perchè l'application deve essere
		// inizializzata.</p>
		if (am.isConfigReset()) {
			argon.onConfigReset();
			am.setConfigReset(false);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onLowMemory()
	 */
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		
		ElioLogger.info("ArgonStartup - onLowMemory");
	}

	/**
	 * <p>
	 * Effettua la configurazione di avvio di argon relativa all'upgrade policy
	 * </p>
	 * 
	 */
	protected void applyStartupSettings(ApplicationManager am, ArgonSettings settings) {
		// impostiamo i settings tra gli attributi
		ArgonBeanContext.setBean(ArgonBeanType.ARGON_SETTINGS, settings);
		// am.attributes.put(ApplicationManagerAttributeKeys.SETTINGS, settings);
		try {
			am.upgradePolicy = (ApplicationUpgradePolicy) Class.forName(settings.application.upgradePolicyClazz.trim()).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * Configurazione del logger
	 * </p>
	 */
	protected void applyElioLoggerSettings(ArgonSettings settings) {
		// impostiamo il logger
		ElioLogger.config.level = settings.logger.level;

		for (LoggerAppenderSettings item : settings.logger.appenders) {
			ElioLogger.config.createAppender(item.tag, item.level);
		}

	}

}
