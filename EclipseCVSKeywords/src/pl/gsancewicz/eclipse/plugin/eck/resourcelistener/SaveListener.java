/**
 * 
 */
package pl.gsancewicz.eclipse.plugin.eck.resourcelistener;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Execution listener that hooks into file save command (
 * 
 * @author Grzegorz Sancewicz
 * @created 10 lut 2016 18:11:32
 *
 */
public class SaveListener extends AbstractSaveListener {
	private boolean undoDone = false;
	private boolean saveActionDone = false;
	private final IDocumentListener DOCUMENT_LISTENER = new IDocumentListener() {
		@Override
		public void documentChanged(final DocumentEvent arg0) {
			undoDone = false;
			arg0.getDocument().removeDocumentListener(this);
			
		}
		
		@Override
		public void documentAboutToBeChanged(final DocumentEvent arg0) {
			//nothing to do
			
		}
	};
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IExecutionListener#preExecute(java.lang.String, org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public void preExecute(final String commandId, final ExecutionEvent event) {
		if(commandId.equals(ActionFactory.UNDO.getCommandId())) {
			if(saveActionDone) {
				undoDone = true;
				saveActionDone = false;
			}
			
		}
		if (!commandId.equals(ActionFactory.SAVE.getCommandId())) {
			return;
		}

		if(undoDone) {
			undoDone = false;
			return;
		}
		
		final ITextEditor textEditor = getTextEditor(event);
		final IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		document.addDocumentListener(DOCUMENT_LISTENER);
		doDocumentContentReplacement(textEditor);
		saveActionDone = true;
	}
	
	/**
	 * @return ItextEditor from event
	 */
	private ITextEditor getTextEditor(final ExecutionEvent event) {
		final IEditorPart editor;
		try	{
			editor = HandlerUtil.getActiveEditorChecked(event);
			if(editor instanceof ITextEditor)
			{
				return (ITextEditor)editor;
			}
			return null;
		}
		catch (final ExecutionException e) {
			return null;
		}
	}

}