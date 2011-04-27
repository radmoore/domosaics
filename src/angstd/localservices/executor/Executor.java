package angstd.localservices.executor;

import javax.swing.SwingWorker;

import angstd.model.configuration.Configuration;

/**
 * 
 * Class for process creation
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 * @author Andreas Held
 */
public class Executor extends SwingWorker<Integer, Void> {

	protected Process p;
	protected long startTime = 0;
	
	private String[] cmd;
	private ProcessListener listener;
	private int result;
	
	
	public Executor(String[] cmd, ProcessListener listener) {
		this.cmd = cmd;
		this.listener = listener;
	}
	
	@Override
	protected Integer doInBackground() {
		try {
			
			startTime = System.currentTimeMillis();
			System.out.println("START: "+startTime);
			ProcessBuilder hmmerProcess = new ProcessBuilder(cmd);
			p = hmmerProcess.start();	
		
			StreamHandler errHandler = new StreamHandler(p.getErrorStream(), StreamHandler.ERROR, listener); 
            StreamHandler outHandler = new StreamHandler(p.getInputStream(), StreamHandler.OUTPUT, listener);
            
            errHandler.start();
            outHandler.start();
            
    		Configuration.getInstance().setServiceRunning(true);
            result = p.waitFor();
            
		} 
		catch(Exception e) {
			Configuration.getLogger().debug(e.toString());
			result =  -1;
		}
		return null;		
	}

	// called when the worker is complete
	protected void done() {
		Configuration.getInstance().setServiceRunning(false);
		if (isCancelled()) {
     		listener.setResult(-1);
     		return;
     	}	
		try {
			listener.setResult(result);
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
     }
	
	public boolean isRunning() {
		if (getState().equals(StateValue.STARTED) || getState().equals(StateValue.PENDING))
			return true;
		return false;
	}
	
	public void stop() {
		
		try { 
			p.destroy();
			this.cancel(true);
		}
		catch (Exception  e){ }
		
		Configuration.getInstance().setServiceRunning(true);
		
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public long getEllapsedTime() {
		return (System.currentTimeMillis()- startTime);
	}
	
	
	
}
