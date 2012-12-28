package domosaics.ui.workspace.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import domosaics.model.workspace.WorkspaceElement;
import domosaics.ui.WorkspaceManager;
import domosaics.ui.util.MessageUtil;




/**
 * Workspace action which triggers a delete event on a workspace element.
 * 
 * @author Andreas Held
 *
 */
public class DeleteElementAction extends AbstractAction{
	private static final long serialVersionUID = 1L;
	
	public DeleteElementAction (){
		super();
		putValue(Action.NAME, "Delete");
		putValue(Action.SHORT_DESCRIPTION, "Deletes the selected component and all its childs");
	}

	public void actionPerformed(ActionEvent e) {
		// get selected Workspace element
		WorkspaceElement selected = WorkspaceManager.getInstance().getSelectionManager().getSelectedElement();
		
		if (selected == null){
			MessageUtil.showWarning("Please select a workspace element first");
			return;
		}
		
//		// check if selected workspace element is present on hard drive as well
//		if (selected.isView()) {
//			WorkspaceElement cat = selected.getParent();
//			WorkspaceElement project = selected.getProject();
//			
//			File viewFile = new File(Configuration.getInstance().getWorkspaceDir()+"/"+project.getTitle()+"/"+cat.getTitle()+"/"+selected.getTitle());
//			if (viewFile.exists())
//				if (MessageUtil.showDialog(""+ selected.getTypeName()+" \""+selected.getTitle()+"\"+ from hard drive as well?")) {
//					viewFile.delete();
//					WorkspaceManager.getInstance().removeFromAngstd(selected);
//					
//					// if category is empty remove it as well
//					File catFile = new File(Configuration.getInstance().getWorkspaceDir()+"/"+project.getTitle()+"/"+cat.getTitle());
//					if (catFile.list().length == 0)
//						catFile.delete();
//				}
//			
//			return;
//		}
//		
//		
//		if (selected.isCategory()) {
//			WorkspaceElement project = selected.getProject();
////			List<WorkspaceElement> views = selected.getChildren();
//			
//			File catFile = new File(Configuration.getInstance().getWorkspaceDir()+"/"+project.getTitle()+"/"+selected.getTitle());
//			if (catFile.exists()) {
//				if (MessageUtil.showDialog(""+ selected.getTypeName()+" \""+selected.getTitle()+"\"+ from hard drive as well?")) {
//					catFile.delete();
//				}
//			}
//		
////			for (WorkspaceElement view : views) {
////				File viewFile = new File(Configuration.getInstance().getWorkspaceDir()+"/"+project.getTitle()+"/"+cat.getTitle()+"/"+selected.getTitle());
////			}
//			
//			return;
//		}
//		
//		
//		if (selected.isProject()) {
//			File projectFile = new File(Configuration.getInstance().getWorkspaceDir()+"/"+selected.getTitle());
//			if (projectFile.exists()) {
//				if (MessageUtil.showDialog(""+ selected.getTypeName()+" \""+selected.getTitle()+"\"+ from hard drive as well?")) {
//					projectFile.delete();
//				}
//			}
//		}
		
		
		// show confirm dialog before deletion
		String msg = "Delete "+ selected.getTypeName()+" \""+selected.getTitle()+"\"";
		if (selected.isView())
			msg = msg + "?";
		else
			msg = msg + " and all its children?";
			
		boolean delete = MessageUtil.showDialog(msg);
		
		if (!delete)
			return;
		
		// delete the workspace element and its children if there are any
		WorkspaceManager.getInstance().removeFromAngstd(selected);
	}

}
