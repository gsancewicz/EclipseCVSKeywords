/**
 * 
 */
package pl.gsancewicz.eclipse.plugin.eck.resourcelistener;

import java.io.BufferedReader;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BlockTextSelection;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import pl.gsancewicz.eclipse.plugin.eck.tokenreplaceinputreader.TokenReplaceInputReader;

/**
 * Execution listener that hooks into file save command (
 * 
 * @author Grzegorz Sancewicz
 * @created 10 lut 2016 18:11:32
 *
 */
public class SaveListener implements IExecutionListener {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#preExecute(java.lang.String, org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public void preExecute(final String commandId, final ExecutionEvent event) {
		if (!commandId.equals("org.eclipse.ui.file.save")) {
			return;
		}

		final IEditorPart editor;
		try	{
			editor = HandlerUtil.getActiveEditorChecked(event);
			if(!(editor instanceof ITextEditor))
			{
				return;
			}
		}
		catch (final ExecutionException e) {
			return;
		}
		
	    final ITextEditor textEditor = (ITextEditor)editor;
	    
	    final FileEditorInput editorInput = (FileEditorInput)textEditor.getEditorInput();
	    final IPath path = editorInput.getPath();
//	    editorInput.getFile().setContents(arg0, arg1, arg2, arg3);
	    
	    
	    final IDocument doc = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
	    
	    final ITextSelection selection = (ITextSelection)textEditor.getSelectionProvider().getSelection();
	    final int columnNumber = getCurrentColumnNumber(selection, doc);
	    final int lineNumber = selection.getStartLine();
	    
	    final String documentContent = doc.get();
	    final String replacedString = replaceTokens(documentContent, path);
	    doc.set(replacedString);
	    final BlockTextSelection blockTextSelection = new BlockTextSelection(doc, lineNumber, columnNumber, lineNumber, columnNumber, 4);
	    textEditor.getSelectionProvider().setSelection(blockTextSelection);
	}

	/**
	 * @param currentText
	 * @param path 
	 * @return
	 */
	private String replaceTokens(final String currentText, final IPath path) {
		String replacedString = currentText;
	    final TokenReplaceInputReader cvsTokenReplaceInputStream = new TokenReplaceInputReader(currentText, path);
	    try(final BufferedReader reader = new BufferedReader(cvsTokenReplaceInputStream);)
	    {
	    	final StringBuilder out = new StringBuilder();
		    final String newLine = System.getProperty("line.separator");
		    String line;
		    boolean first = true;
		    while ((line = reader.readLine()) != null) {
		    	if(!first) {
		    		out.append(newLine);
		    		first = false;
		    	}
		        out.append(line);
		    }
		    replacedString = out.toString();
	    }
	    catch(final Exception e)
	    {
	    	throw new RuntimeException(e);
	    }
		return replacedString;
	}

	private int getCurrentColumnNumber(final ITextSelection selection, final IDocument doc) {
		int column = 0;
	    try
	    {
	    	column = selection.getOffset() - doc.getLineOffset(selection.getStartLine());
	    }
	    catch(final Exception e)
	    {
	    	//ignore
	    }
		return column;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#notHandled(java.lang.String, org.eclipse.core.commands.NotHandledException)
	 */
	@Override
	public void notHandled(final String arg0, final NotHandledException arg1) {
		//no implementation needed
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#postExecuteFailure(java.lang.String, org.eclipse.core.commands.ExecutionException)
	 */
	@Override
	public void postExecuteFailure(final String arg0, final ExecutionException arg1) {
		//no implementation needed

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#postExecuteSuccess(java.lang.String, java.lang.Object)
	 */
	@Override
	public void postExecuteSuccess(final String arg0, final Object arg1) {
		//no implementation needed
		System.out.println();
	}

}
