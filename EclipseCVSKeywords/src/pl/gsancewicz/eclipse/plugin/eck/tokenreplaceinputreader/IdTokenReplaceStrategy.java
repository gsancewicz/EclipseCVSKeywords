/**
 * 
 */
package pl.gsancewicz.eclipse.plugin.eck.tokenreplaceinputreader;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IPath;

/**
 * Strategy used to replace $Id$ token.
 * 
 * Eg: $Id: keyword.html,v 1.3 1999/12/23 21:59:22 markd Exp $
 * 
 * @author Grzegorz Sancewicz
 * @created 13 lut 2016 18:49:01
 *
 */
public class IdTokenReplaceStrategy implements TokenReplaceStrategy {

	/* (non-Javadoc)
	 * @see pl.gsancewicz.eclipse.plugin.eck.tokenreplaceinputreader.TokenReplaceStrategy#getTokenReplacement(org.eclipse.core.runtime.IPath)
	 */
	@Override
	public String getTokenReplacement(final IPath path) {
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		final StringBuilder sb = new StringBuilder();
		sb.append(path.segment(path.segmentCount()-1));
		sb.append(", ");
		sb.append(sdf.format(new Date()));
		sb.append(" ");
		sb.append(System.getProperty("user.name"));
		return sb.toString();
	}


}
