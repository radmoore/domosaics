package angstd.ui.wizards;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.UIManager;

import org.netbeans.api.wizard.WizardDisplayer;

import angstd.model.configuration.Configuration;
import angstd.model.sequence.SequenceI;
import angstd.model.workspace.ProjectElement;
import angstd.model.workspace.WorkspaceElement;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.ui.wizards.createtree.CreateTreeBranchController;
import angstd.ui.wizards.createtree.CreateTreeResultProducer;
import angstd.ui.wizards.dialogs.AssociateWithSeqsWizard;
import angstd.ui.wizards.dialogs.ChangeSequenceWizard;
import angstd.ui.wizards.dialogs.ClustalW2Dialog;
import angstd.ui.wizards.dialogs.CreateDomainTreeDialog;
import angstd.ui.wizards.dialogs.CreateProjectDialog;
import angstd.ui.wizards.dialogs.CreateSpeciesTreeDialog;
import angstd.ui.wizards.dialogs.EditDatasetWizard;
import angstd.ui.wizards.dialogs.SaveProjectDialog;
import angstd.ui.wizards.dialogs.SelectNameDialog;
import angstd.ui.wizards.dialogs.SelectRenameDialog;
import angstd.ui.wizards.dialogs.WorkspaceDirectoryWizard;
import angstd.ui.wizards.importdata.ImportDataBranchController;
import angstd.ui.wizards.importdata.ImportDataResultProducer;

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
	 * Opens a selectName Wizard for views.
	 * 
	 * @param defaultName
	 * 		the name shown by default
	 * @param objectName
	 * 		the object to name , e.g. view
	 * @return
	 * 		the name chosen by the user
	 */
	public String selectRenameWizard(String defaultName, String objectName, WorkspaceElement elem) {
		return (String) new SelectRenameDialog(defaultName, objectName, elem).show();
	}
	
	
	public Map selectNameWizard(String defaultName, String objectName, ProjectElement project) {
		return (Map) new SelectNameDialog(defaultName, objectName, project).show();
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
	 */
	public void startSaveProjectWizard() {
		SaveProjectDialog.show();
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
