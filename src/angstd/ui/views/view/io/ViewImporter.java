package angstd.ui.views.view.io;

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

import angstd.model.workspace.ProjectElement;
import angstd.ui.ViewHandler;
import angstd.ui.views.domaintreeview.DomainTreeViewI;
import angstd.ui.views.domaintreeview.actions.CollapseSameArrangementsAtNodeAction;
import angstd.ui.views.domaintreeview.io.DomainTreeViewImporter;
import angstd.ui.views.domaintreeview.layout.CSAModeDomainTreeLayout;
import angstd.ui.views.domaintreeview.layout.DefaultDomainTreeLayout;
import angstd.ui.views.domainview.DomainViewI;
import angstd.ui.views.domainview.io.DomainViewImporter;
import angstd.ui.views.sequenceview.SequenceView;
import angstd.ui.views.sequenceview.io.SequenceViewImporter;
import angstd.ui.views.treeview.TreeViewI;
import angstd.ui.views.treeview.components.NodeComponent;
import angstd.ui.views.treeview.io.TreeViewImporter;
import angstd.ui.views.view.View;

public abstract class ViewImporter<V extends View> {
	
	protected String viewName = null;
	protected boolean readDataFlag = false;
	protected boolean readAttributesFlag = false;
	protected StringBuffer data = new StringBuffer();
	protected StringBuffer attributes = new StringBuffer();
	
	public static void readSequenceView(File viewFile, ProjectElement project) {
		SequenceView view = new SequenceViewImporter().read(viewFile);
		ViewHandler.getInstance().addView(view, project, false);
	}
	
	public static void readTreeView(File viewFile, ProjectElement project) {
		TreeViewI view = new TreeViewImporter().read(viewFile);
		ViewHandler.getInstance().addView(view, project, false);
	}
	
	public static void readDomainView(File viewFile, ProjectElement project) {
		DomainViewI view = new DomainViewImporter().read(viewFile);
		ViewHandler.getInstance().addView(view, project, false);
	}
	
	public static void readDomainTreeView(File viewFile, ProjectElement project) {
		final DomainTreeViewI view = new DomainTreeViewImporter().read(viewFile);
		ViewHandler.getInstance().addView(view, project, true);
		view.getDomainLayoutManager().structuralChange();
		
		// collapse in csa mode (workaround because subtree bounds have to be initialized
		SwingUtilities.invokeLater(new Runnable() {						
			public void run() {		
				if (view.getCSAInSubtreeManager().isActive()) {
					List<NodeComponent> csaNodes = new ArrayList<NodeComponent>(view.getCSAInSubtreeManager().getCollapsedAndCSAModeNodes());
		
					for (NodeComponent nc : csaNodes) 
						CollapseSameArrangementsAtNodeAction.collapse(view, nc);
					
					for (NodeComponent nc : csaNodes) 
						CollapseSameArrangementsAtNodeAction.collapse(view, nc);
				}
				ViewHandler.getInstance().disableActiveView();
			}
		});
	}
	
	public V read(File file)  {
		try {
			BufferedReader in = new BufferedReader(new FileReader(file)); 
			V res = importView(in);
			in.close();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public V read(String str)  {
		try {
			BufferedReader in = new BufferedReader(new StringReader(str)); 
			V res = importView(in);
			in.close();
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public V importView(BufferedReader in) {
		// create data and attribute string
		try {
			String line;
			V view = null;
			
			while((line = in.readLine()) != null) {
				
				// get the views name
				if(line.contains("parameter") && idEquals(line, "VIEWNAME")) {
					viewName = getValue(line); 
					continue;
				}
				
				// set flag that backend data has to be parsed
				if(line.contains("<DATA>")) {
					readDataFlag = true;
					continue;
				}
				
				// set flag that view attributes has to be parsed
				if(line.contains("<ATTRIBUTES>")) {
					readAttributesFlag = true;
					continue;
				}
				
				// all backend data parsed, create the view and set the backend data
				if(line.contains("</DATA>")) {
					readDataFlag = false;
					view = readData(data.toString());
					if (view == null)
						return null;
					continue;
				}
				
				// all attributes parsed apply them to the view now
				if(line.contains("</ATTRIBUTES>")) {
					readAttributesFlag = false;
					readAttributes(attributes.toString(), view);
					continue;
				}
				
				// fill the data buffer 
				// FIXME for really large documents this can lead to a memory overflow
				if (readDataFlag) 
					data.append(line+"\r\n");
	
				// fill attribute buffer 
				// FIXME for really large documents this can lead to a memory overflow
				if (readAttributesFlag) 
					attributes.append(line+"\r\n");
			}
			
			setLayoutSettings(view);
			
			return view;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected abstract V readData(String data);
	protected abstract void readAttributes (String data, V view);
	protected abstract void setLayoutSettings(V view);
	
	/**
	 * Extracts an ID of an item.
	 * 
	 * @param str 
	 * 		the line where the id has to be extracted
	 * @return 
	 * 		the extracted id of a item
	 */
	protected static String getID(String str) {
		int startPos = str.indexOf("id=\"")+4;
		int endPos = str.indexOf("\"", startPos);
		return str.substring(startPos, endPos);
	}
	
	protected static String getValue(String str) {
		int startPos = str.indexOf("value=\"")+7;
		int endPos = str.indexOf("\"", startPos);
		return str.substring(startPos, endPos);
	}
	
	protected static String getValue2(String str) {
		int startPos = str.indexOf("value2=\"")+8;
		int endPos = str.indexOf("\"", startPos);
		return str.substring(startPos, endPos);
	}
	
	protected static boolean idEquals(String line, String id) {
		return getID(line).toUpperCase().equals(id);
	}
	
	protected static Color str2color(String str) {
		String[] token = str.split("-");
		return new Color(str2int(token[0]), str2int(token[1]), str2int(token[2]), str2int(token[3]));
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
	
	protected static boolean str2boolean (String str) {
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
		return new BasicStroke(
				str2float(token[0]),	// width
				str2int(token[1]),		// cap
				str2int(token[2]),		// join
				str2float(token[3]),	// miterlimit
				dash,					// dash
				str2float(token[5]));	// dash_phase
	}	
}
