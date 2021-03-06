/**
 * 
 */
package ippoz.madness.lite.probes;

import ippoz.madness.lite.support.MADneSsLiteSupport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 * @author root
 *
 */
public class UnixNetworkProbe extends IteratingCommandProbe {

	private TreeMap<String, String> paramNames;
	
	public UnixNetworkProbe(LinkedList<Indicator> indicators) {
		super("dstat", " -n --tcp -d", ProbeType.UNIX_NETWORK, "UnixNetwork", indicators);
	}
	
	@Override
	public void setupParameters() {
		setupParamNames();	
	}

	private void setupParamNames() {
		paramNames = new TreeMap<String, String>();
		paramNames.put("recv", "Net_Received");
		paramNames.put("send", "Net_Sent");
		paramNames.put("lis", "Tcp_Listen");
		paramNames.put("act", "Tcp_Established");
		paramNames.put("syn", "Tcp_Syn");
		paramNames.put("tim", "Tcp_TimeWait");
		paramNames.put("clo", "Tcp_Close");
	}

	@Override
	protected boolean isHeader(String line) {
		return line.trim().endsWith("--");
	}

	@Override
	protected HashMap<Indicator, String> readParams() {
		StringTokenizer lt;
		LinkedList<String> activeTokens;
		String tokenName, next;
		HashMap<Indicator, String> params = new HashMap<Indicator, String>();
		try {
			synchronized(lastReaded){
				activeTokens = new LinkedList<String>();
				lt = new StringTokenizer(lastReaded, " |m\t\n\r\f");
				while(lt.hasMoreTokens()){
				
					next = null;
					while(lt.hasMoreTokens() && (next == null || (!MADneSsLiteSupport.isInteger(next) && !MADneSsLiteSupport.isInteger(next.substring(0, next.length()-1))))){
						next = lt.nextToken();
					}
					activeTokens.add(next);

				}
				for(int i=0;i<listDefaultIndicatorNames().length;i++){
					tokenName = listDefaultIndicatorNames()[i];
					if(hasIndicator(tokenName))
						params.put(getIndicator(tokenName), parseQuantity(activeTokens.get(i)));
				}
			}
		} catch(Exception ex){}
		return params;
	}

	private String parseQuantity(String splitted) {
		String cleared = splitted.trim().toUpperCase();
		if(cleared.endsWith("B"))
			return splitted.substring(0, splitted.length()-1);
		else if(cleared.endsWith("K"))
			return String.valueOf(1000*Integer.parseInt(splitted.substring(0, splitted.length()-1)));
		else if(cleared.endsWith("M"))
			return String.valueOf(1000000*Integer.parseInt(splitted.substring(0, splitted.length()-1)));
		else return splitted;
	}

	@Override
	public LinkedList<Indicator> setDefaultIndicators() {
		LinkedList<Indicator> defaultList = new LinkedList<Indicator>();
		for(String indName : listDefaultIndicatorNames()){
			defaultList.add(Indicator.buildIndicator(indName, ProbeType.UNIX_NETWORK));
		}
		return defaultList;
	}

	public static String[] listDefaultIndicatorNames() {
		return new String[]{"Net_Received", "Net_Sent", "Tcp_Listen", "Tcp_Established",
				"Tcp_Syn", "Tcp_TimeWait", "Tcp_Close"};
	}	

}
