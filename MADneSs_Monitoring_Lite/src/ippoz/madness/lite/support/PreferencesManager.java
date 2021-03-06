/**
 * 
 */
package ippoz.madness.lite.support;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Tommy
 *
 */
public class PreferencesManager {
	
	private HashMap<String, String> preferences;
	
	public PreferencesManager(File prefFile){
		try {
			preferences = AppUtility.loadPreferences(prefFile, null);
		} catch (IOException ex) {
			AppLogger.logException(getClass(), ex, "Unable to load main preferences file");
		}
	}
	
	public String getPreference(String tag){
		return preferences.get(tag);
	}

	public Set<String> listTags() {
		return preferences.keySet();
	}

}
