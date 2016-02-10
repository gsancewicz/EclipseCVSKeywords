/**
 * 
 */
package pl.gsancewicz.eclipse.plugin.eck.resourcelistener;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Input stream used to replace CSV tokens.
 * 
 * Proof of concept.
 * 
 * TODO: Rewrite this buggy implementation using standard ByteArrayInputStream with mark method.
 * 
 * @author Grzegorz Sancewicz
 * @created 7 lut 2016 20:36:40
 *
 */
public class CVSTokenReplaceInputStream extends InputStream {
	private final StringBuilder tokenBuilder = new StringBuilder();
	private String tokenValue;
	private int tokenValueIndex;
	private boolean isInsideLastTokenReplacement = false;
	
	private final char DELIMITER = '$';
	
	private final PushbackInputStream pushbackInputStream;
	
	public CVSTokenReplaceInputStream(final String text) {
		this.pushbackInputStream = new PushbackInputStream(new ByteArrayInputStream(text.getBytes()), 2);
	}
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		if(this.tokenValue != null)
		{
			if(this.tokenValueIndex < tokenValue.length())
			{
				tokenValueIndex++;
				return tokenValue.charAt(this.tokenValueIndex-1);
			}
			else 
			{
				tokenValueIndex = 0;
				tokenValue = null;
			}
		}
		final int read = pushbackInputStream.read();
		if(read == DELIMITER)
		{
			int nextRead = pushbackInputStream.read();
			if(isTerminateCharacter(nextRead))
			{
				return read;
			}
			else if(isNewLine(nextRead))
			{
				pushbackInputStream.unread(nextRead);
				return read;
			}
			if(isSpace(nextRead))
			{
				this.pushbackInputStream.unread(nextRead);
				return DELIMITER;
			}
			tokenBuilder.append((char)nextRead);
			while((nextRead = pushbackInputStream.read()) != DELIMITER) {
				if(isTerminateCharacter(nextRead) || isNewLine(nextRead))
				{
					//2 = first delimiter + (eof)
					pushbackInputStream.unread(nextRead);
					pushbackInputStream.unread(tokenBuilder.toString().getBytes());
					return read;
				}
				else if(isSpace(nextRead) || this.isInsideLastTokenReplacement)
				{
					//token end - start of last token replacement
					this.isInsideLastTokenReplacement = true;
				}
				else 
				{
					tokenBuilder.append((char)nextRead);
				}
			}
			this.isInsideLastTokenReplacement = false;
			
			//start reading tokenValue
		}

		if(tokenBuilder.length() != 0)
		{
			this.tokenValue = getTokenValue(tokenBuilder.toString());
			tokenBuilder.delete(0, tokenBuilder.length());
			this.tokenValueIndex = 1;
			return tokenValue.charAt(0);
		}
		else
		{
			return read;
		}
		
	}

	/**
	 * @param nextRead
	 * @return
	 */
	private boolean isNewLine(final int nextRead) {
		final String property = System.getProperty("line.separator");
		return nextRead == property.charAt(0);
	}

	/**
	 * @param string
	 * @return
	 */
	private String getTokenValue(final String string) {
		//prepare strategies for token replacing
		if(string.equals("Id"))
		{
			final Date date = new Date();
			final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//$Id: keyword.html,v 1.3 1999/12/23 21:59:22 markd Exp $
			return "$Id "+ sdf.format(date) + " " + System.getProperty("user.name") + " Exp $";
		}
		return "$zmiana Grzegorz Sancewicz aa sda asd $";
	}

	/**
	 * @param nextRead
	 * @return
	 */
	private boolean isSpace(final int nextRead) {
		return nextRead == ' ';
	}

	/**
	 * @param nextRead
	 * @return
	 */
	private boolean isTerminateCharacter(final int nextRead) {
		return nextRead == -1;
	}
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#close()
	 */
	@Override
	public void close() throws IOException {
		super.close();
		this.pushbackInputStream.close();
	}
	
	public static int copy(final InputStream input, final OutputStream output) throws IOException{
	     final byte[] buffer = new byte[1024];
	     int count = 0;
	     int n = 0;
	     while (-1 != (n = input.read(buffer))) {
	         output.write(buffer, 0, n);
	         count += n;
	     }
	     return count;
	 }

}
