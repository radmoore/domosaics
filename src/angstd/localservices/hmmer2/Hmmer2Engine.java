package angstd.localservices.hmmer2;

import java.io.File;
import java.util.HashMap;

/**
 * Prototype of a Local annotation interface
 * @author Andrew Moore

public class Hmmer2Engine {
	
	/** mapping between a hmmer2 program and its file path
	protected HashMap<Hmmer2ProgramType, String> progs;
	
	/** instance of this class necessary to support the singleton pattern
	protected static Hmmer2Engine instance;
	
	/** the running hmmer2 service
	protected HmmerService service;
	
	
	/**
	 * Contructor for a new Hmmer2Engine. The constructor is protected to 
	 * support the singleton pattern. 
	 * To get a grip on an instance use getInstance().

	protected Hmmer2Engine() {
		reset();
	}
	
	/**
	 * resets the class variables

	public void reset() {
		progs = new HashMap<Hmmer2ProgramType, String>();
	}
	
	/**
	 * Method which delivers an instance of this class. 
	 * 
	 * @return
	 * 		instance to the Hmmer2Engine
	 
	public static Hmmer2Engine getInstance() {
		if (instance == null)
			instance = new Hmmer2Engine();
		return instance;
	}
	
	/**
	 * Returns the {@link Hmmer2ProgramType} based on the specified
	 * name.
	 * 
	 * @param name
	 * 		the name used to identify the correct Hmmer2ProgramType
	 * @return
	 * 		the Hmmer2ProgramType based on the specified name
	 
	public Hmmer2ProgramType getProgramType(String name) {
		for (Hmmer2ProgramType type : Hmmer2ProgramType.values())
			if (name.toLowerCase().contains(type.getName()))
				return type;
		return null;
	}
	
	/**
	 * Adds a mapping between a program and its file path
	 * 
	 * @return 
	 * 		whether or not the program was added successfully
	 
	public boolean addProgram(String name, String path) {
		Hmmer2ProgramType type = getProgramType(name);
		if (type == null) {
			System.out.println("hmmer2Service nof found or not supported: "+name);
			return false;
		}
		progs.put(type, path);
		return true;
	}
	
	/**
	 * Launches a HmmerService
	 
	public void launch(Hmmer2ProgramType type, File fastaFile, String[] args) {
		service = new HmmerService(type, progs.get(type), fastaFile , args);
		service.start();
	}
	
	public void stop() {
		if (service != null)
			service.stop();
	}
	
}

*/