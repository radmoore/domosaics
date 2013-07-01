package domosaics.ui.wizards.importdata;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;
import org.netbeans.spi.wizard.WizardPage;

import domosaics.model.DataType;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.model.arrangement.io.ArrangementImporterUtil;
import domosaics.model.sequence.SequenceI;
import domosaics.model.sequence.io.FastaReader;
import domosaics.model.tree.Tree;
import domosaics.model.tree.TreeI;
import domosaics.model.tree.io.NewickTreeReader;
import domosaics.model.workspace.ProjectElement;
import domosaics.model.workspace.ViewElement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.ViewHandler;
import domosaics.ui.util.FileDialogs;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.domainview.DomainView;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.wizards.GUIComponentFactory;




/**
 * WizardPage allowing to choose a file which is used to create a sequence view.
 * Additionally a domain view can be chosen which then will be used to associate the
 * sequences with it.
 * 
 * @author Andreas Held
 *
 */
public class SelectDataPage extends WizardPage implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	/** the text field displaying the chosen file path */
	protected JTextField path;
	
	/** the text field for choosing a view name */
	protected JTextField viewName;
	
	/** the check for correct fasta format */
	protected SequenceI[] seqs = null;

	/**  list displaying all domain views of all projects */
	protected JComboBox selectViewList, selectTreeViewList, selectSeqViewList;

	/** the check for correct file format */
	protected TreeI tree = null;
	
	private ProjectElement project;
	
	private DataType datatype;

	private DomainArrangement[] daSet = null;
	
	/** size of the page */
	private final static Dimension p_size = new Dimension(400,300);
	
	
	/**
	 * Constructor for a new SelectArrangementDataPage
	 */

	public SelectDataPage(ProjectElement project, DataType dt) {
		super("Open File");
		this.project = project;
		datatype=dt;
		init();	
	}
	
	
	private void init() {
		setLayout(new MigLayout());
		//setPreferredSize(p_size);

		// init components
		path = new JTextField(20);
		path.setEditable(false);

		viewName = new JTextField(20);
		viewName.setEditable(true);

		JButton browse = new JButton("Browse...");
		browse.addActionListener(this);	

		if (project == null) {
			selectViewList = GUIComponentFactory.createSelectDomViewBox(false);
		} else {
			selectViewList = GUIComponentFactory.createSelectDomViewBox(project);
		}

		if(datatype==DataType.TREE) {
			// layout panel
			add(new JXTitledSeparator("Select tree file"),"growx, span, wrap");
			add(new JLabel("Select file:"), "gap 10");
			add(path, "w 150!, h 25!, gap 5");
			add(browse, "gapright 10, wrap");

			//add(new JLabel("Enter a name:"), "gap 10");
			add(viewName, "w 50!, h 20!, wrap");
			viewName.setVisible(false);

			add(new JXTitledSeparator("Associate with arrangements to domain tree"),"growx, span, wrap, gaptop 20");
			add(new JLabel("Select view: "), "gap 10");
			add(selectViewList, "w 270!, gap 5, gapright 10, span, wrap");

		} else {
			if(datatype==DataType.SEQUENCE) {

				// associate names
				path.setName(ImportDataBranchController.FILEPATH_KEY);
				viewName.setName(ImportDataBranchController.VIEWNAME_KEY);
				selectViewList.setName(ImportDataBranchController.DOMVIEW_KEY);

				// layout panel
				add(new JXTitledSeparator("Select sequence file"),"growx, span, wrap");
				add(new JLabel("Select file:"), "gap 10");
				add(path, "w 150!, h 25!, gap 5");
				add(browse, "gap 5, gapright 10, wrap");

				//add(new JLabel("Enter a name:"), "gap 10");
				add(viewName,  "w 50!, h 40!, wrap");
				viewName.setVisible(false);

				add(new JXTitledSeparator("Associate with arrangement view"),"growx, span, wrap, gaptop 20");
				add(new JLabel("Select view:"), "gap 10");
				add(selectViewList, "w 270!, gap 5, gapright 10, span 2, wrap");
			} else {
				if(datatype==DataType.DOMAINS) {
					if (project == null) {
						selectTreeViewList = GUIComponentFactory.createSelectTreeViewBox(false);
						selectSeqViewList = GUIComponentFactory.createSelectSeqViewBox(false);
					}
					else {
						selectTreeViewList = GUIComponentFactory.createSelectTreeViewBox(project);
						selectSeqViewList = GUIComponentFactory.createSelectSeqViewBox(project);
					}

					// associate names
					path.setName(ImportDataBranchController.FILEPATH_KEY);
					viewName.setName(ImportDataBranchController.VIEWNAME_KEY);
					selectTreeViewList.setName(ImportDataBranchController.TREEVIEW_KEY);
					selectSeqViewList.setName(ImportDataBranchController.SEQVIEW_KEY);

					// layout panel
					add(new JXTitledSeparator("Select arrangement file"),"growx, span, wrap");
					add(new JLabel("Select file:"), "gap 10");
					add(path, "w 150!, h 25!, gap 5");
					add(browse, "wrap");

					//add(new JLabel("Enter a name:"), "gap 10");
					add(viewName, "w 50!, h 20!, wrap");
					viewName.setVisible(false);

					add(new JXTitledSeparator("Associate with tree to domain tree"),"growx, span, wrap, gaptop 20");
					add(new JLabel("Select view: "), "gap 10");
					add(selectTreeViewList, "w 270!, gap 5, gapright 10, span, wrap");

					add(new JXTitledSeparator("Associate with sequence view"),"growx, span, wrap, gaptop 20");
					add(new JLabel("Select view: "), "gap 10");
					add(selectSeqViewList, "w 270!, gap 5, gapright 10, span, wrap");
				}
			}
		}
	}
	
	/**
	 * Action performed when the browse button was clicked
	 */
	public void actionPerformed(ActionEvent e) {
		File file = FileDialogs.showOpenDialog(DoMosaicsUI.getInstance());
		if(datatype==DataType.SEQUENCE) {
			if(file != null) {
				if (file.canRead()) {
					if ( FastaReader.isValidFasta(file) ) {
						seqs = new FastaReader().getDataFromFile(file);
						if(seqs != null) {
							path.setText(file.getAbsolutePath());
							viewName.setText(file.getName().split("\\.")[0]);
						}
						else {
							MessageUtil.showWarning(DoMosaicsUI.getInstance(),"No sequence in file or invalid fasta format");
						}
					}
					else {
						MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Failed to read file - unknown file format");
					}
				}
				else {
					MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Cannot read "+file.getName());
					path.setText("");
				}
			}
		} else {
			if(datatype==DataType.DOMAINS) {
				if(file != null) {
					File test = new File(file.getAbsolutePath());
					daSet = ArrangementImporterUtil.importData(test);
				}
				if(daSet != null) {
					path.setText(file.getAbsolutePath());
					viewName.setText(file.getName().split("\\.")[0]);
				}
			} else {
				if(datatype==DataType.TREE) {
					if(file != null) {
						File test = new File(file.getAbsolutePath());
						tree = new NewickTreeReader().getTreeFromFile(test);
					}
					if(tree != null) {
						path.setText(file.getAbsolutePath());
						viewName.setText(file.getName().split("\\.")[0]);
					}
				}
			}
		}
	}
    
    /**
     * Checks if all necessary inputs are made.
     */
    protected String validateContents (Component component, Object o) {
    	if(datatype==DataType.DOMAINS) {
    		if (daSet == null)
        		return "Please select a correctly formatted xdom file";
        	if (path.getText().isEmpty())
    			return "Please select a file";
        	ArrayList<String> treeLabels = ((Tree)((TreeViewI)ViewHandler.getInstance().getView(((ViewElement)selectTreeViewList.getSelectedItem()).getViewInfo())).getTree()).getLeavesName();
    		ArrayList<String> arrLabels = new ArrayList<String>();
    		for(int i=0; i<daSet.length; i++)
    			arrLabels.add(daSet[i].getName());
    		int mem = treeLabels.size();
    		treeLabels.retainAll(arrLabels);
    		if(treeLabels.size()==0) {
				MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Arrangement and tree views do not have any common sequence!");
			} else {
    			if(treeLabels.size()!=arrLabels.size() || treeLabels.size()!=mem)
    				MessageUtil.showInformation(DoMosaicsUI.getInstance(),"Protein sets in arrangement and tree views do not perfectly overlap!");    			
    		}
    		ArrayList<String> seqLabels = ((Tree)((TreeViewI)ViewHandler.getInstance().getView(((ViewElement)selectSeqViewList.getSelectedItem()).getViewInfo())).getTree()).getLeavesName();
    		mem = seqLabels.size();
    		seqLabels.retainAll(arrLabels);
    		if(seqLabels.size()==0) {
				MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Arrangement and sequence views do not have any common sequence!");
			} else {
    			if(seqLabels.size()!=arrLabels.size() || seqLabels.size()!=mem)
    				MessageUtil.showInformation(DoMosaicsUI.getInstance(),"Protein sets in arrangement and sequence views do not perfectly overlap!");    			
    		}
    	} else {
    		if(datatype==DataType.SEQUENCE) {
    			if (seqs == null)
        			return "Please select a correctly formatted fasta file";
        	    if(path.getText().isEmpty())
        	    	return "Please select a file";
        	    if(selectViewList.getSelectedItem()!=null) {
        	    	ArrayList<String> seqLabels = new ArrayList(Arrays.asList(seqs));
        	    	ArrayList<String> arrLabels = ((DomainView)selectViewList.getSelectedItem()).getLabels();
        	    	int mem = seqLabels.size();
        	    	seqLabels.retainAll(arrLabels);
        	    	if(seqLabels.size()==0) {
        	    		MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Arrangement and sequence views do not have any common sequence!");
        	    		return "Please select corresponding views";  
        	    	} else {
        	    		if(seqLabels.size()!=arrLabels.size() || seqLabels.size()!=mem)
        	    			MessageUtil.showInformation(DoMosaicsUI.getInstance(),"Protein sets in arrangement and sequence views do not perfectly overlap!");    			
        	    	}
        	    }
    		} else {
    			if(datatype==DataType.TREE) {
    				if (tree == null)
    					return "Please select a correctly formatted newick file";
    				if (path.getText().isEmpty())
    					return "Please select a file";
					ArrayList<String> treeLabels = ((Tree)tree).getLeavesName();
					ArrayList<String> arrLabels = ((DomainView)selectViewList.getSelectedItem()).getLabels();
        	    	int mem = treeLabels.size();
		    		treeLabels.retainAll(arrLabels);
		    		if(treeLabels.size()==0) {
						MessageUtil.showWarning(DoMosaicsUI.getInstance(),"Arrangement and tree views do not have any common sequence!");
						return "Please provide corresponding views";
					} else {
		    			if(treeLabels.size()!=arrLabels.size() || treeLabels.size()!=mem)
		    				MessageUtil.showInformation(DoMosaicsUI.getInstance(),"Protein sets in arrangement and tree views do not perfectly overlap!");    			
		    		}
    			}
    		}
    	}
    	return null;
    }   
}