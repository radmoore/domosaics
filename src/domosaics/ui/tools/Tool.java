package domosaics.ui.tools;

/**
 * A Tool is basically a view which is embedded into an external frame.
 * Therefore the creation process and everything stays for tools
 * the same, they just have to implement two additional methods regarding
 * the frame in which they are embedded.
 * 
 * @author Andreas Held
 *
 */
public interface Tool {

	/**
	 * Set the tool frame (during the tool creation process)
	 * 
	 * @param frame
	 * 		the tools frame
	 */
	public void setToolFrame(ToolFrameI frame);
	
	/**
	 * Returns the frame in which the view / tool is embedded 
	 * 
	 * @return
	 * 		frame in which the tool is embedded 
	 */
	public ToolFrameI getToolFrame();
}
