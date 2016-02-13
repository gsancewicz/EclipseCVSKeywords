/**
 * 
 */
package pl.gsancewicz.eclipse.plugin.eck.resourcelistener;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

/**
 * 
 * 
 * @author Grzegorz Sancewicz
 * @created 13 lut 2016 10:49:57
 *
 */
public class TokenReplaceInputStream extends Reader {
	private final static char TOKEN_CHARACTER = '$';
	private final static char TOKEN_SPLIT_CHARACTER = ':';
	
	enum TokenReplaceInputStreamState {
		NORMAL_READ,
		TOKEN_NAME_READ,
		TOKEN_OLD_VALUE_SKIP,
		TOKEN_VALUE_REPLACE;
	}
	
	
	private TokenReplaceInputStreamState state = TokenReplaceInputStreamState.NORMAL_READ;
	
	final PushbackReader pushBackReader;
	final StringBuilder skippedValueBuilder = new StringBuilder();
	final StringBuilder tokenNameBuilder = new StringBuilder();
	String tokenReplacementValue = null;
	
	public TokenReplaceInputStream(final String inputString) {
		this.pushBackReader = new PushbackReader(new StringReader(inputString), 256);
	}

	/**
	 * @param string
	 * @return
	 */
	private String getTokenReplacementValue(final String string) {
		return string+TOKEN_SPLIT_CHARACTER+" " +"wartość testowa"+TOKEN_CHARACTER;
	}

	/* (non-Javadoc)
	 * @see java.io.Reader#close()
	 */
	@Override
	public void close() throws IOException {
		this.pushBackReader.close();
	}

	/* (non-Javadoc)
	 * @see java.io.Reader#read(char[], int, int)
	 */
	@Override
	public int read(final char[] cbuf, final int off, final int len) throws IOException {
		final char[] readArray = new char[1];
		final int readCount = pushBackReader.read(readArray);
		final char read = readArray[0];
		if(state == TokenReplaceInputStreamState.NORMAL_READ || 
			state == TokenReplaceInputStreamState.TOKEN_VALUE_REPLACE)
		{
			if(read == TOKEN_CHARACTER)
			{
				if(state == TokenReplaceInputStreamState.NORMAL_READ)
				{
					state = TokenReplaceInputStreamState.TOKEN_NAME_READ;
				}
				else 
				{
					state = TokenReplaceInputStreamState.NORMAL_READ;
				}
			}
			cbuf[0] = read;
			skippedValueBuilder.delete(0, skippedValueBuilder.length());
			return readCount;
		}
		else 
		{
			if(readCount == -1)
			{
				final char[] bytesToPushBack = skippedValueBuilder.toString().toCharArray();
				skippedValueBuilder.delete(0, skippedValueBuilder.length());
				pushBackReader.unread(bytesToPushBack);
				state = TokenReplaceInputStreamState.NORMAL_READ;
				return read(cbuf, off, len);
			}
			skippedValueBuilder.append(read);
			
			if(state == TokenReplaceInputStreamState.TOKEN_NAME_READ)
			{
				if(read == TOKEN_CHARACTER) {
					unreadSkippedValues();
				}
				else if(read == TOKEN_SPLIT_CHARACTER)
				{
					state = TokenReplaceInputStreamState.TOKEN_OLD_VALUE_SKIP;
				}
				else {
					tokenNameBuilder.append(read);
				}
				return read(cbuf, off, len);
			}
			else if(state == TokenReplaceInputStreamState.TOKEN_OLD_VALUE_SKIP)
			{
				if(read == TOKEN_CHARACTER) {
					unreadSkippedValues();
				}
				return read(cbuf, off, len);
			}
			else 
			{
				throw new RuntimeException();
			}
		}
	}

	/**
	 * @throws IOException
	 */
	private void unreadSkippedValues() throws IOException {
		tokenReplacementValue = getTokenReplacementValue(tokenNameBuilder.toString());
		tokenNameBuilder.delete(0, tokenNameBuilder.length());
		pushBackReader.unread(tokenReplacementValue.toCharArray());
		tokenReplacementValue = null;
		state = TokenReplaceInputStreamState.TOKEN_VALUE_REPLACE;
	}


}
