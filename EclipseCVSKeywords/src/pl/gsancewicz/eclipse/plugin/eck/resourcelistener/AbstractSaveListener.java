/**
 * 
 */
package pl.gsancewicz.eclipse.plugin.eck.resourcelistener;

import java.io.BufferedReader;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.BlockTextSelection;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import pl.gsancewicz.eclipse.plugin.eck.tokenreplaceinputreader.TokenReplaceInputReader;

/**
 * Abstract class for save listeners.
 * 
 * @author Grzegorz Sancewicz
 * @created 13 lut 2016 21:07:04
 *
 */
public abstract class AbstractSaveListener implements IExecutionListener {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#notHandled(java.lang.String, org.eclipse.core.commands.NotHandledException)
	 */
	@Override
	public void notHandled(final String arg0, final NotHandledException arg1) {
		//nothing to do

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#postExecuteFailure(java.lang.String, org.eclipse.core.commands.ExecutionException)
	 */
	@Override
	public void postExecuteFailure(final String arg0, final ExecutionException arg1) {
		//nothing to do
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#postExecuteSuccess(java.lang.String, java.lang.Object)
	 */
	@Override
	public void postExecuteSuccess(final String arg0, final Object arg1) {
		//nothing to do
	}

	/**
	 * @param textEditor
	 */
	protected void doDocumentContentReplacement(final ITextEditor textEditor) {
		final ITextViewer textViewer = getTextViewer(textEditor);
		
		final int topIndex = textViewer.getTopIndex();
		final IPath path = getFilePath(textEditor);
		
		final IDocument doc = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		
		final ITextSelection selection = (ITextSelection)textEditor.getSelectionProvider().getSelection();
		final int columnNumber = getCurrentColumnNumber(selection, doc);
		final int lineNumber = selection.getStartLine();
		
		replaceTokens(doc, path);
		
		final BlockTextSelection blockTextSelection = new BlockTextSelection(doc, lineNumber, columnNumber, lineNumber, columnNumber, 4);
		textEditor.getSelectionProvider().setSelection(new TextSelection(doc, blockTextSelection.getOffset(), blockTextSelection.getLength()));
		
		textViewer.setTopIndex(topIndex);
	}


	private IPath getFilePath(final ITextEditor textEditor) {
		final FileEditorInput editorInput = (FileEditorInput)textEditor.getEditorInput();
	    final IPath path = editorInput.getPath();
		return path;
	}

	/**
	 * @param textEditor
	 * @return
	 */
	private ITextViewer getTextViewer(final ITextEditor textEditor) {
	    final ITextOperationTarget target =
	    		textEditor.getAdapter(ITextOperationTarget.class);
	    if (target instanceof ITextViewer) {
	        return (ITextViewer)target;
	    } 
	    return null;
	}
	
	/**
	 * @param doc
	 * @param path
	 */
	private void replaceTokens(final IDocument doc, final IPath path) {
		final String documentContent = doc.get();
	    final String replacedString = replaceTokens(documentContent, path);
	    doc.set(replacedString);
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
		    	}
	    		first = false;
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
	

}
