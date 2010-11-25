package angstd.webservices;

public interface WebservicePrinter {
	
	public void print(String msg);
	
	public void setJobDone(boolean state);
	
	public boolean isJobDone();
	
	public String getResult();

}
