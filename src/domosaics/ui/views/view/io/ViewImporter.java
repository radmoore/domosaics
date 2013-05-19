package domosaics.ui.views.view.io;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import domosaics.model.configuration.Configuration;
import domosaics.model.workspace.CategoryElement;
import domosaics.model.workspace.ProjectElement;
import domosaics.ui.ViewHandler;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.ViewType;
import domosaics.ui.views.domaintreeview.DomainTreeViewI;
import domosaics.ui.views.domaintreeview.actions.CollapseSameArrangementsAtNodeAction;
import domosaics.ui.views.domaintreeview.io.DomainTreeViewImporter;
import domosaics.ui.views.domainview.DomainViewI;
import domosaics.ui.views.domainview.io.DomainViewImporter;
import domosaics.ui.views.sequenceview.SequenceView;
import domosaics.ui.views.sequenceview.io.SequenceViewImporter;
import domosaics.ui.views.treeview.TreeViewI;
import domosaics.ui.views.treeview.components.NodeComponent;
import domosaics.ui.views.treeview.io.TreeViewImporter;
import domosaics.ui.views.view.View;

/**
 * Importer for views
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 * 
 * @param <V>
 * 
 * FIXME
 *  -> Name of view window after import
 */
public abstract class ViewImporter<V extends View> {

	protected String viewName = null;
	protected boolean readDataFlag = false;
	protected boolean readAttributesFlag = false;
	protected StringBuffer data = new StringBuffer();
	protected StringBuffer attributes = new StringBuffer();
	private static boolean select = false; 
	

	
	public static ViewType detectViewType(File viewFile) {
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(viewFile));
			String firstLine;

			if ((firstLine = in.readLine()) != null) {
				// check domosaics file format stamp
				if (!firstLine.contains("# domosaics_view: ")) {
					if (!MessageUtil.showDialog( viewFile.getName()+" does not appear to be a view file. Continue?"))
						return null;
				}
				else {
					firstLine = in.readLine();
					in.close();
				}
				
				if (firstLine.contains("SEQUENCEVIEW"))
					return  ViewType.SEQUENCE;

				if (firstLine.contains("DOMAINTREEVIEW"))
					return  ViewType.DOMAINTREE;
				
				if (firstLine.contains("TREEVIEW"))
					return  ViewType.TREE;
					
				if (firstLine.contains("DOMAINVIEW"))
					return  ViewType.DOMAINS;
			}
		}
		catch(Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		return null;
		
	}
	
	/** 
	 * Sets the default for displaying a view after import
	 * 
	 * @param selection
	 * 		whether of not to display an imported view 
	 */
	public static void displayViewAfterImport(boolean selection) {
		select = selection;
	}
	
	
	public static void readSequenceView(File viewFile, ProjectElement project) {
		SequenceView view = new SequenceViewImporter().read(viewFile);
		ViewHandler.getInstance().addView(view, project, select);
	}
	
	public static void readSequenceView(File viewFile, ProjectElement project, String newName) {
		SequenceView view = new SequenceViewImporter().read(viewFile);
		view.getViewInfo().setName(newName);
		view.getParentPane().setName(newName);
		ViewHandler.getInstance().addView(view, project, select);
	}

	public static void readTreeView(File viewFile, ProjectElement project) {
		TreeViewI view = new TreeViewImporter().read(viewFile);
		ViewHandler.getInstance().addView(view, project, select);
	}

	public static void readTreeView(File viewFile, ProjectElement project, String newName) {
		TreeViewI view = new TreeViewImporter().read(viewFile);
		view.getViewInfo().setName(newName);
		view.getParentPane().setName(newName);
		ViewHandler.getInstance().addView(view, project, select);
	}
	
	public static void readDomainView(File viewFile, ProjectElement project) {
		DomainViewI view = new DomainViewImporter().read(viewFile);
		ViewHandler.getInstance().addView(view, project, select);
	}
	
	public static void readDomainView(File viewFile, ProjectElement project, String newName) {
		DomainViewI view = new DomainViewImporter().read(viewFile);
		view.getViewInfo().setName(newName);
		view.getParentPane().setName(newName);
		ViewHandler.getInstance().addView(view, project, select);
	}
	
	public static void readDomainTreeView(File viewFile, ProjectElement project) {
		readDomainTreeView(viewFile, project, null);
	}
	
	public static void readDomainTreeView(File viewFile, ProjectElement project, String newName) {
		final DomainTreeViewI view = new DomainTreeViewImporter().read(viewFile);
		if (newName != null) {
			view.getViewInfo().setName(newName);
			view.getParentPane().setName(newName);
		}
		ViewHandler.getInstance().addView(view, project, true);
		view.getDomainLayoutManager().structuralChange();

		// collapse in csa mode (workaround because subtree bounds have to be
		// initialized
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (view.getCSAInSubtreeManager().isActive()) {
					List<NodeComponent> csaNodes = new ArrayList<NodeComponent>(
							view.getCSAInSubtreeManager()
									.getCollapsedAndCSAModeNodes());

					for (NodeComponent nc : csaNodes)
						CollapseSameArrangementsAtNodeAction.collapse(view, nc);

					for (NodeComponent nc : csaNodes)
						CollapseSameArrangementsAtNodeAction.collapse(view, nc);
				}
				ViewHandler.getInstance().disableActiveView();
			}
		});
	}

	public V read(File file) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));			
			V res = importView(in);
			in.close();
			return res;
		} 
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		return null;
	}

	public V read(String str) {
		try {
			BufferedReader in = new BufferedReader(new StringReader(str));
			V res = importView(in);
			in.close();
			return res;
		} 
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
		}
		return null;
	}

	public V importView(BufferedReader in) {
		// create data and attribute string
		
		try {
			String line;
			V view = null;

			while ((line = in.readLine()) != null) {
				
				// get the views name
				// this also sets the name of the frame				
				if (line.contains("parameter") && idEquals(line, "VIEWNAME")) {
					viewName = getValue(line);
					continue;
				}

				// set flag that backend data has to be parsed
				if (line.contains("<DATA>")) {
					readDataFlag = true;
					continue;
				}

				// set flag that view attributes has to be parsed
				if (line.contains("<ATTRIBUTES>")) {
					readAttributesFlag = true;
					continue;
				}

				// all backend data parsed, create the view and set the backend
				// data
				if (line.contains("</DATA>")) {
					readDataFlag = false;
					view = readData(data.toString());
					if (view == null)
						return null;
					continue;
				}

				// all attributes parsed apply them to the view now
				if (line.contains("</ATTRIBUTES>")) {
					readAttributesFlag = false;
					readAttributes(attributes.toString(), view);
					continue;
				}

				// fill the data buffer
				// FIXME for really large documents this can lead to a memory
				// overflow
				if (readDataFlag)
					data.append(line + "\r\n");

				// fill attribute buffer
				// FIXME for really large documents this can lead to a memory
				// overflow
				if (readAttributesFlag)
					attributes.append(line + "\r\n");
			}

			setLayoutSettings(view);

			return view;
		} 
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode(true))
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			return null;
		}
	}

	protected abstract V readData(String data);

	protected abstract void readAttributes(String data, V view);

	protected abstract void setLayoutSettings(V view);

	/**
	 * Extracts an ID of an item.
	 * 
	 * @param str
	 *            the line where the id has to be extracted
	 * @return the extracted id of a item
	 */
	protected static String getID(String str) {
		int startPos = str.indexOf("id=\"") + 4;
		int endPos = str.indexOf("\"", startPos);
		return str.substring(startPos, endPos);
	}

	protected static String getValue(String str) {
		int startPos = str.indexOf("value=\"") + 7;
		int endPos = str.indexOf("\"", startPos);
		return str.substring(startPos, endPos);
	}

	protected static String getValue2(String str) {
		int startPos = str.indexOf("value2=\"") + 8;
		int endPos = str.indexOf("\"", startPos);
		return str.substring(startPos, endPos);
	}

	protected static boolean idEquals(String line, String id) {
		return getID(line).toUpperCase().equals(id);
	}

	protected static Color str2color(String str) {
		String[] token = str.split("-");
		return new Color(str2int(token[0]), str2int(token[1]),
				str2int(token[2]), str2int(token[3]));
	}

	protected static Font str2font(String str) {
		String[] token = str.split("-");
		return new Font(token[1], str2int(token[2]), str2int(token[3]));
	}

	protected static int str2int(String str) {
		return Integer.valueOf(str).intValue();
	}

	protected static float str2float(String str) {
		return Float.valueOf(str).floatValue();
	}

	protected static boolean str2boolean(String str) {
		if (str.toUpperCase().equals("TRUE"))
			return true;
		return false;
	}

	// width - cap - join - miterlimit - [dash] - dash_phase
	protected static BasicStroke str2stroke(String str) {
		String[] token = str.split("-");

		// read out the dash array
		float[] dash = null;
		if (!token[4].equals("[]")) {
			String dashStr = token[4].replace("[", "");
			dashStr = dashStr.replace("]", "");
			String[] dashToken = dashStr.split(",");
			dash = new float[dashToken.length];
			for (int i = 0; i < dashToken.length; i++)
				dash[i] = str2float(dashToken[i]);
		}

		// create stroke
		return new BasicStroke(str2float(token[0]), // width
				str2int(token[1]), // cap
				str2int(token[2]), // join
				str2float(token[3]), // miterlimit
				dash, // dash
				str2float(token[5])); // dash_phase
	}
}
