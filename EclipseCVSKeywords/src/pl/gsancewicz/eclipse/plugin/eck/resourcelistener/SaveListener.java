/**
 * 
 */
package pl.gsancewicz.eclipse.plugin.eck.resourcelistener;

import java.io.BufferedReader;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.jface.text.BlockTextSelection;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * 
 * @author Grzegorz Sancewicz
 * @created 10 lut 2016 18:11:32
 *
 */
public class SaveListener implements IExecutionListener {
	

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#notHandled(java.lang.String, org.eclipse.core.commands.NotHandledException)
	 */
	@Override
	public void notHandled(final String arg0, final NotHandledException arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#postExecuteFailure(java.lang.String, org.eclipse.core.commands.ExecutionException)
	 */
	@Override
	public void postExecuteFailure(final String arg0, final ExecutionException arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#postExecuteSuccess(java.lang.String, java.lang.Object)
	 */
	@Override
	public void postExecuteSuccess(final String arg0, final Object arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#preExecute(java.lang.String, org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public void preExecute(final String commandId, final ExecutionEvent arg1) {
		if (commandId.equals("org.eclipse.ui.file.save")) {
			final IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
			        .getActiveEditor();
			    if (!(editor instanceof ITextEditor)) {
					return;
				}
			    final ITextEditor ite = (ITextEditor)editor;
			    final ITextSelection selection = (ITextSelection)ite.getSelectionProvider().getSelection();
			    final int startLine = selection.getStartLine();
				
			    final IDocument doc = ite.getDocumentProvider().getDocument(ite.getEditorInput());
			    int column = 0;
			    try
			    {
			    	column = selection.getOffset() - doc.getLineOffset(selection.getStartLine());
			    }
			    catch(final Exception e)
			    {
			    	//fallthru
			    }
			    final String currentText = doc.get();
			    final TokenReplaceInputReader cvsTokenReplaceInputStream = new TokenReplaceInputReader(currentText);
			    try(final BufferedReader reader = new BufferedReader(cvsTokenReplaceInputStream);)
			    {
			    	final StringBuilder out = new StringBuilder();
				    final String newLine = System.getProperty("line.separator");
				    String line;
				    while ((line = reader.readLine()) != null) {
				        out.append(line);
				        out.append(newLine);
				    }
				    doc.set(out.toString());
			    }
			    catch(final Exception e)
			    {
			    	e.printStackTrace();
			    }
			    final BlockTextSelection blockTextSelection = new BlockTextSelection(doc, startLine, column, startLine, column, 4);
			    ite.getSelectionProvider().setSelection(blockTextSelection);
            // log file save event
//            final IEditorInput input = window.getPartService().getActivePart().getSite().getPage().getActiveEditor().getEditorInput();
//            (IReusableEditor)input;
//            final input.
//            final IFile file = ResourceUtil.getFile(input);
//
//            try{
//				final InputStream contents = file.getContents();
//				final CVSTokenReplaceInputStream cvsTokenReplaceInputStream = 
//						new CVSTokenReplaceInputStream(contents);
//				file.setContents(cvsTokenReplaceInputStream, 1, null);
//            }
//			catch (final Exception e) {
//				e.printStackTrace();
//			}
//            System.out.println();
        }
	}

}
