/**
 * 
 */
package pl.gsancewicz.eclipse.plugin.eck.resourcelistener;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * 
 * @author Grzegorz Sancewicz
 * @created 13 lut 2016 21:02:50
 *
 */
public class SaveAllListener extends AbstractSaveListener {

	@Override
	public void preExecute(final String commandId, final ExecutionEvent event) {
		if (!commandId.equals(ActionFactory.SAVE_ALL.getCommandId())) {
			return;
		}
		final List<ITextEditor> editorList = getEditorList(commandId, event);
		
		for(final ITextEditor textEditor : editorList)
		{
			doDocumentContentReplacement(textEditor);
		}
	}
	

	/**
	 * @param commandId
	 * @param event
	 * @return
	 */
	private List<ITextEditor> getEditorList(final String commandId, final ExecutionEvent event) {
		final List<ITextEditor> result = new ArrayList<>();
		final IEditorReference[] editorReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
	    	.getActivePage().getEditorReferences();
		for(final IEditorReference iEditorReference : editorReferences) {
			final IEditorPart editor = iEditorReference.getEditor(false);
			if(editor == null) {
				continue;
			}
			if(editor.isDirty() && editor instanceof ITextEditor) {
				result.add((ITextEditor)editor);
			}
		}
		return result;
	}
}
