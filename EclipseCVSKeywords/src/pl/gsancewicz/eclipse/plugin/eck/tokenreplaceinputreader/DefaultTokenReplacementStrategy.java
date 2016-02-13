/**
 * 
 */
package pl.gsancewicz.eclipse.plugin.eck.tokenreplaceinputreader;

import org.eclipse.core.runtime.IPath;

/**
 * Default token replacement strategy.
 * 
 * @author Grzegorz Sancewicz
 * @created 13 lut 2016 19:22:28
 *
 */
public class DefaultTokenReplacementStrategy implements TokenReplaceStrategy {

	/* (non-Javadoc)
	 * @see pl.gsancewicz.eclipse.plugin.eck.tokenreplaceinputreader.TokenReplaceStrategy#getTokenReplacement(org.eclipse.core.runtime.IPath)
	 */
	@Override
	public String getTokenReplacement(final IPath path) {
		return null;
	}

}
