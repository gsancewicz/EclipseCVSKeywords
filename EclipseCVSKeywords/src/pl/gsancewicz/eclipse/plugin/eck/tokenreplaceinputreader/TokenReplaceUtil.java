/**
 * 
 */
package pl.gsancewicz.eclipse.plugin.eck.tokenreplaceinputreader;

import java.util.HashMap;
import java.util.Map;

/**
 * Util used for token replacing.
 * 
 * @author Grzegorz Sancewicz
 * @created 13 lut 2016 18:46:45
 *
 */
class TokenReplaceUtil {
	
	private final static TokenReplaceStrategy DEFAULT_STRATEGY = new DefaultTokenReplacementStrategy();
	private final static Map<String, TokenReplaceStrategy> TOKEN_REPLACE_STRATEGY_MAP =
		new HashMap<>();
	static {
		TOKEN_REPLACE_STRATEGY_MAP.put("Id", new IdTokenReplaceStrategy());
	}
	/**
	 * @param token
	 * @return
	 */
	public static TokenReplaceStrategy getStrategy(final String token) {
		if(TOKEN_REPLACE_STRATEGY_MAP.containsKey(token))
		{
			return TOKEN_REPLACE_STRATEGY_MAP.get(token);
		}
		return DEFAULT_STRATEGY;
	}

}
