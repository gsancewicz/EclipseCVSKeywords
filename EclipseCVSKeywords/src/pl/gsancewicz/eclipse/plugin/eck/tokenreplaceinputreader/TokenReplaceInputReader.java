/**
 * 
 */
package pl.gsancewicz.eclipse.plugin.eck.tokenreplaceinputreader;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.EnumSet;

import org.eclipse.core.runtime.IPath;

/**
 * Input reader replacing CVS-like tokens in source string.
 * 
 * Example:
 * <pre>
 * For input string:
 * File id: $Id$ 
 * 
 * Reader will return something like:
 * File id: $Id: 13.02.2016 17:42:10$
 * </pre>
 * 
 * TokenReplaceInputReader has possibility to register additional strategies.
 * 
 * @author Grzegorz Sancewicz
 * @created 13 lut 2016 10:49:57
 *
 */
public class TokenReplaceInputReader extends Reader {
	private final static char TOKEN_CHARACTER = '$';
	private final static char TOKEN_SPLIT_CHARACTER = ':';
	
	private enum State {
		NORMAL_READ,
		TOKEN_NAME_READ,
		TOKEN_OLD_VALUE_SKIP,
		TOKEN_VALUE_REPLACE;
	}
	private State state = State.NORMAL_READ;
	
	final PushbackReader pushBackReader;
	final StringBuilder skippedValueBuilder = new StringBuilder();
	final StringBuilder tokenNameBuilder = new StringBuilder();
	String tokenReplacementValue = null;
	private final IPath path;
	
	public TokenReplaceInputReader(final String inputString, final IPath path) {
		this.path = path;
		this.pushBackReader = new PushbackReader(new StringReader(inputString), 256);
	}

	/* (non-Javadoc)
	 * @see java.io.Reader#read(char[], int, int)
	 */
	@Override
	public int read(final char[] cbuf, final int off, final int len) throws IOException {
		final char[] readArray = new char[1];
		final int readCount = pushBackReader.read(readArray);
		final char read = readArray[0];
		if(EnumSet.of(State.NORMAL_READ, 
			State.TOKEN_VALUE_REPLACE).contains(state)) {
			if(read == TOKEN_CHARACTER)
			{
				if(state == State.NORMAL_READ) {
					state = State.TOKEN_NAME_READ;
				}
				else {
					state = State.NORMAL_READ;
				}
			}
			cbuf[0] = read;
			skippedValueBuilder.delete(0, skippedValueBuilder.length());
			return readCount;
		}
		else {
			if(readCount == -1)	{
				pushBackSkippedValue();
				return read(cbuf, off, len);
			}
			skippedValueBuilder.append(read);
			
			if(state == State.TOKEN_NAME_READ) {
				if(read == TOKEN_CHARACTER) {
					insertResolvedValue();
				}
				else if(read == TOKEN_SPLIT_CHARACTER) {
					state = State.TOKEN_OLD_VALUE_SKIP;
				}
				else {
					tokenNameBuilder.append(read);
				}
				return read(cbuf, off, len);
			}
			else if(state == State.TOKEN_OLD_VALUE_SKIP)	{
				if(read == TOKEN_CHARACTER) {
					insertResolvedValue();
				}
				return read(cbuf, off, len);
			}
		}
		throw new IllegalStateException();
	}

	/**
	 * Method pushes back value previously skipped.
	 * 
	 * @throws IOException
	 */
	private void pushBackSkippedValue() throws IOException {
		final char[] bytesToPushBack = skippedValueBuilder.toString().toCharArray();
		skippedValueBuilder.delete(0, skippedValueBuilder.length());
		pushBackReader.unread(bytesToPushBack);
		state = State.NORMAL_READ;
	}

	/**
	 * Method pushes back resolved token value if exists.
	 * 
	 * @throws IOException
	 */
	private void insertResolvedValue() throws IOException {
		tokenReplacementValue = getTokenReplacementValue(tokenNameBuilder.toString());
		tokenNameBuilder.delete(0, tokenNameBuilder.length());
		if(tokenReplacementValue == null)
		{
			pushBackSkippedValue();
			return;
		}
		pushBackReader.unread(tokenReplacementValue.toCharArray());
		tokenReplacementValue = null;
		state = State.TOKEN_VALUE_REPLACE;
	}

	/**
	 * @param token
	 * @return
	 */
	private String getTokenReplacementValue(final String token) {
		final String tokenValueFromStrategy = getTokenValueFromStrategy(token);
		if(tokenValueFromStrategy == null)
		{
			return null;
		}
		return token + TOKEN_SPLIT_CHARACTER + " " + tokenValueFromStrategy + TOKEN_CHARACTER;
	}

	/**
	 * @param token
	 * @return
	 */
	private String getTokenValueFromStrategy(final String token) {
		final TokenReplaceStrategy strategy = TokenReplaceUtil.getStrategy(token);
		return strategy.getTokenReplacement(path);
	}

	/* (non-Javadoc)
	 * @see java.io.Reader#close()
	 */
	@Override
	public void close() throws IOException {
		this.pushBackReader.close();
	}


}
