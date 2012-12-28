package angstd.ui.views;

import java.net.URL;

import javax.swing.JMenuBar;

import angstd.model.configuration.Configuration;
import angstd.ui.io.menureader.JMenuBarFactory;
import angstd.ui.io.menureader.MenuReader;
import angstd.ui.tools.Tool;
import angstd.ui.tools.ToolFrameI;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.view.View;
import angstd.ui.views.view.ViewInfo;
import angstd.ui.views.view.ViewPanel;
import angstd.ui.views.view.io.ViewPropertyReader;




/**
 * AngstdViewFactory specifies the method which is used to create new views.
 * <p>
 * The coupling between the view and its displaying component {@link ViewPanel}
 * should be done in method {@link #createView(ViewType)}. Also a unique id is
 * set there for the view. Therefore to create new views, 
 * always the AngstdViewFactory class should be used.
 * <p>
 * When a new view is created, a properties.file and a menu.file is used to 
 * do so. Both files have to be located in a sub folder of the views package 
 * called resources.
 * <p>
 * The properties.file specifies the following properties: <br>
 * 1. view name <br>
 * 2. a view icon, which is used within the workspace view <br>
 * 3. a boolean flag whether or not the view is a tool and therefore needs to be
 *    embedded in an own frame. <br>
 * 4. (optional) the class url to the tools frame, if the view is a tool <br>
 * <p>
 * The menu.file is just an xml formatted file, which can be parsed using the
 * {@link MenuReader}.
 * <p>
 * The properties file is processed by {@link ViewPropertyReader} , so for details on
 * the actions happening when the properties.file is parsed look into the
 * {@link ViewPropertyReader}.
 * 
 * @author Andreas Held
 *
 */
public class AngstdViewFactory {
	
	/** the property file used to set view attributes */
	protected static String PROPERTYFILE = "resources/properties.file";
	
	/** the menu file describing the view specific menu structure */
	protected static String MENUFILE = "resources/menu.file";
	
	/** the unique id for a view */
	protected static int id = 0;
	
	
	/**
	 * The method creating a new view based on a properties file and a
	 * menu file. The {@link ViewPropertyReader} parses the properties file and
	 * initializes the ViewInfo. For details see source code comments.
	 * 
	 * @param <V>
	 * 		view type (e.g. DomainViewI)
	 * @param type
	 * 		the ViewType (e.g. DOMAINS)
	 * @return
	 * 		the created view object of the specified type.
	 */
	@SuppressWarnings("unchecked")
	public static <V extends View> V createView(ViewType type) {
		try {
			// get the class location from the ViewType and instanciate it, 
			// this location is used to retrieve the properties and menu file
			Class viewClazz =  type.getClazz();
			View view = (V) viewClazz.newInstance();
			
			// read all properties for the view from the properties file
			URL url = viewClazz.getResource(PROPERTYFILE);
			ViewInfo viewInfo = new ViewPropertyReader().getView(url);
			
			// read the views menu as well using MenuReader
			URL menuUrl = viewClazz.getResource(MENUFILE);
			JMenuBar jMenuBar = JMenuBarFactory.createMenuBar(menuUrl, viewInfo.getActionManager());
			
			// set the unique views id
			if (!viewInfo.isTool()) 
				viewInfo.setID(id++);
			
			viewInfo.setName(viewInfo.getName());
			
			// set the views type
			viewInfo.setViewType(type);
			
			// add the viewInfo to the view object
			view.setViewInfo(viewInfo);
			
			// create a new ViewPanel: 
			// a cross reference between the view and its embedding panel is set
			ViewPanel viewPane = new ViewPanel(viewInfo.getName(), view);
			
			// if the view is not a tool (e.g. treeView, domainView) 
			// add the menu to the panel and the view is complete then
			if (!viewInfo.isTool()) {
				viewPane.setToolbar(jMenuBar);
				return (V) view;
			}
			
			// if the view is a tool and therefore needs a tool frame 
			// where it is embedded in, create it
			ToolFrameI frame = (ToolFrameI) viewInfo.getFrameClazz().newInstance();
			
			// the menu is then added to the frame instead of the view panel
			frame.setJMenuBar(jMenuBar);
			
			// add the tool frame to the view, the tool is completed then
			((Tool) view).setToolFrame(frame);	
			return (V) view;
		} 
		catch (Exception e) {
       	 	Configuration.getLogger().debug(e.toString());
			MessageUtil.showWarning("Failed to create View!");
			return null;
		}
	}
	
}
