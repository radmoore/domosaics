package domosaics.ui.wizards;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.UIManager;

import org.netbeans.api.wizard.WizardDisplayer;

import domosaics.model.configuration.Configuration;
import domosaics.model.sequence.SequenceI;
import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.WorkspaceElement;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.components.ArrangementComponent;
import domosaics.ui.wizards.createtree.CreateTreeBranchController;
import domosaics.ui.wizards.createtree.CreateTreeResultProducer;
import domosaics.ui.wizards.dialogs.AssociateWithSeqsWizard;
import domosaics.ui.wizards.dialogs.ChangeSequenceWizard;
import domosaics.ui.wizards.dialogs.ClustalW2Dialog;
import domosaics.ui.wizards.dialogs.CreateDomainTreeDialog;
import domosaics.ui.wizards.dialogs.CreateProjectDialog;
import domosaics.ui.wizards.dialogs.CreateSpeciesTreeDialog;
import domosaics.ui.wizards.dialogs.EditDatasetWizard;
import domosaics.ui.wizards.dialogs.ImportViewDialog;
import domosaics.ui.wizards.dialogs.LoadFastaDialog;
import domosaics.ui.wizards.dialogs.SaveProjectDialog;
import domosaics.ui.wizards.dialogs.SaveViewDialog;
import domosaics.ui.wizards.dialogs.SelectNameDialog;
import domosaics.ui.wizards.dialogs.SelectRenameDialog;
import domosaics.ui.wizards.dialogs.SelectViewDialog;
import domosaics.ui.wizards.dialogs.WorkspaceDirectoryWizard;
import domosaics.ui.wizards.importdata.ImportDataBranchController;
import domosaics.ui.wizards.importdata.ImportDataResultProducer;


/**
 * The WizardManager can be used to spawn wizard dialogs and therefore
 * force the user to give necessary input information to the program.
 * <p>
 * The class follows the singleton pattern and can therefore be used
 * at any place within the program.
 * 
 * @author Andreas Held
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 *
 */
public class WizardManager {
	
	/** the image shown at the wizards manager side */
	protected final static String SIDE_IMAGE = "resources/wizBack.JPG";
	
	/** instance of the WizardManager object which follows the singleton pattern */
	public static WizardManager instance;
	
	
	/**
	 * Constructor for a new WizardManager initializing the side image
	 */
	protected WizardManager() {
		// set the sidebar image
		BufferedImage img = null;
		try {
			img = ImageIO.read (getClass().getResourceAsStream(SIDE_IMAGE));
			UIManager.put ("wizard.sidebar.image", img);
		} 
		catch (IOException e1) {
			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(e1);
			else			
				Configuration.getLogger().debug(e1.toString());
		}
	}
	
	/**
	 * Method which should be used to get a grip on the WizardManager.
	 * 
	 * @return
	 * 		the WizardManager
	 */
	public static WizardManager getInstance() {
		if (instance == null)
			instance = new WizardManager();
		return instance;
	}
	
	/**
	 * The parent component must be specified, because its possible that 
	 * the main GUI is not initialized already when this method is triggered.
	 * 
	 * @param parent
	 * @param defaultDir
	 * @return
	 */
	public File showWorkingDirectoyWizard(Component parent, String defaultDir) {
		return (File) new WorkspaceDirectoryWizard(parent, defaultDir).show();
	}
	
	/**
	 * Opens a wizard for renaming views
	 * (for renaming _existing_ views)
	 * 
	 * @param defaultName
	 * 		the name shown by default
	 * @param objectName
	 * 		the object to name , e.g. view
	 * @return
	 * 		the name chosen by the user
	 */
	public String renameWizard(String defaultName, String objectName, WorkspaceElement elem) {
		return (String) new SelectRenameDialog(defaultName, objectName, elem).show();
	}
	
	/**
	 * Opens a selectNameWizard for views
	 * (for naming new views and associating with projects)
	 * 
	 * @param defaultName
	 * 		the name shown by default
	 * 
	 * @param objectName
	 * 		the object to name , e.g. view
	 * 
	 * @param project
	 * 		the project with which new view is to be associated 
	 * 
	 * @param allowProjectSelection
	 * 		project selection combobox is active or not
	 *      (can user select project with which to assoc. view)
	 * 
	 * @return
	 * 		the name chosen by the user
	 */
	public Map selectNameWizard(String defaultName, String objectName, ProjectElement project, boolean allowProjectSelection) {
		return (Map) new SelectNameDialog(defaultName, objectName, project, allowProjectSelection).show();
	}
	
	public Map selectViewWizard(ProjectElement project, int selectedElements) {
		return (Map) new SelectViewDialog(project, selectedElements).show();
	}
	
	
	/**
	 * Opens a createProject wizard which also adds 
	 * the new project to the workspace.
	 * 
	 * @return
	 * 		the created project element.
	 */
	public ProjectElement showCreateProjectWizard(String projectName) {	
		return (ProjectElement) CreateProjectDialog.show(projectName);
	}

	/**
	 * Opens a new import data wizard allowing the user to choose
	 * the view he wants to create. The wizard also creates the new
	 * view using the {@link ImportDataResultProducer}.
	 */
	public void startImportDataWizard() {
		WizardDisplayer.showWizard (new ImportDataBranchController().createWizard());
	}
	
	/**
	 * Opens a new import data wizard allowing the user to choose
	 * the view he wants to create. The wizard also creates the new
	 * view using the {@link ImportDataResultProducer}.
	 * @return 
	 */
	public void startImportViewWizard(ProjectElement project) {
		new ImportViewDialog(project).show();
		//return (Map) new ImportViewDialog(project).show();
	}
	
	/**
	 * Opens a load fasta file wizard allowing the user to choose
	 * the fasta he wants to load. The wizard also creates the new
	 * view
	 * @return 
	 */
	public void startLoadFastaWizard() {
		new LoadFastaDialog().show();
		//return (Map) new ImportViewDialog(project).show();
	}
	
	
	/**
	 * Opens a new create tree wizard allowing the user to choose
	 * the backend data structure and some other parameters to
	 * to create a tree. The wizard also processes this information
	 * using the {@link CreateTreeResultProducer} which adds the 
	 * created tree as tree view to the workspace.
	 */
	public void startCreateTreeWizard() {
		WizardDisplayer.showWizard (new CreateTreeBranchController().createWizard());
	}
	
	/**
	 * Starts the wizard which allows the user to use the EBI webservice
	 * ClustalW2 to align sequences.
	 * 
	 * @param seqs
	 * 		the sequences to be aligned
	 * @return
	 * 		aligned sequences
	 */
	public SequenceI[] startAlignWizard(SequenceI[] seqs) {
		return new ClustalW2Dialog(seqs).show();
	}
	
	/**
	 * Starts the wizard which allows the user to use to connect a tree view and a 
	 * domain view to a domain tree view
	 */
	public void startCreateDomTreeWizard() {
		CreateDomainTreeDialog.show();
	}
	
	/**
	 * Creates a new species tree based on a domain view.
	 */
	public boolean startCreateSpeciesTreeWizard() {
		if (CreateSpeciesTreeDialog.show() != null)
			return true;
		return false;
	}
	
	/**
	 * Starts the wizard which allows the user to export a project.
	 * 
	 * @param project
	 *    a project element which is to be preselected (can be null)
	 * 
	 */
	public void startSaveProjectWizard(ProjectElement project) {
		SaveProjectDialog.show(project);
	}
	
	/**
	 * Starts the wizard which allows the user to export a view.
	 */
	public void startSaveViewWizard(WorkspaceElement view) {
		SaveViewDialog.show(view);
	}
	
	/**
	 * Starts the wizard which allows the user to add 
	 * arrangements to a domain view.
	 */
	public void startEditDatasetWizard(DomainViewI view) {
		new EditDatasetWizard(view).show();
	}
	
	public void startAssociateWithSeqsWizard(DomainViewI view) {
		new AssociateWithSeqsWizard(view).show();
	}
	
	public void startChangeSequenceWizard(DomainViewI view, ArrangementComponent selectedDA) {
		new ChangeSequenceWizard(view, selectedDA).show();
	}
}
