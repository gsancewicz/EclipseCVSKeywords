/**
 * 
 */
package pl.gsancewicz.eclipse.plugin.eck.tokenreplaceinputreader;

import org.eclipse.core.runtime.IPath;

/**
 * Interface for strategies used for token replacing.
 * 
 * @author Grzegorz Sancewicz
 * @created 13 lut 2016 18:48:06
 */
public interface TokenReplaceStrategy {
	/**
	 * Replace token with calculated value.
	 * @param path
	 * @return
	 */
	public String getTokenReplacement(final IPath path);
}
