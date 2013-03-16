package domosaics;

import java.io.PrintWriter;
import java.io.StringWriter;

import domosaics.model.configuration.Configuration;
import domosaics.model.configuration.ConfigurationWriter;
import domosaics.ui.util.MessageUtil;


/**
 * Copyright 2012 Evolutionary Bioinformatics Group, IEB, WWU Muenster, Germany
 * 
 * @author Andrew D. Moore <radmoore@uni-muenster.de>
 * @author Andreas Held <a.held@uni-muenster.de>
 * @author Nicolas Terrapon <n.terrapon@uni-muenster.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.

 * NOTICE: 
 * =======================================================================
 * DoMosaics
 * Copyright 2012, Evolutionary Bioinformatics Group, IEB
 *
 * This product includes software developed at
 * The Apache Software Foundation (http://www.apache.org/),
 * and makes use of the Phylogenetic Analysis Library (PAL), 
 * Version 1.5 (January 15, 2004) [1].
 *
 * Furthermore, this software contains code loosely based on work published
 * by [2] Griebel T et al., Bioinformatics 24(20): 2399-2400 (2008)
 * and [3] Clemente JC et al., BMC Bioinformatics  2009, 10:51. 
 *
 * [1] Drummond, A., and K. Strimmer
 * PAL: An object-oriented programming library for molecular evolution and phylogenetics. 
 * Bioinformatics 17: 662-663, 2001 
 *
 * [2] Thasso Griebel, Malte Brinkmeyer, and Sebastian Böcker
 * EPoS: a modular software framework for phylogenetic analysis 
 * Bioinformatics 24(20): 2399-2400 2008
 *
 * [3] José C Clemente, Kazuho Ikeo, Gabriel Valiente and Takashi Gojobori	
 * Optimized ancestral state reconstruction using Sankoff parsimony
 * BMC Bioinformatics, 10:51, 2009
 * ===============================================================================
 * 
 * 
 * 
 *
 */
public class DoMosaics {
	
	/** 
	 * The main method of DoMosaics and the anchor point to start the program. 
	 * 
	 * @param args
	 * 		arguments you wish to run with DoMosaics
	 */
	public static void main(String[] args) {
		
		Configuration.setDebug(true);
//		Configuration.setReportExceptionsMode(true); 
		 
		if (args.length > 0) {
			for(String a : args) {
				if (a.equals("--debug")) {
					Configuration.setDebug(true);
				}
				if (a.equals("--report")) {
					Configuration.setReportExceptionsMode(true);
				}
				else {
					System.out.println("DoMosaics: dunno what >"+args[0]+"< means. Exiting.");
					System.exit(1);
				}
			}
		}

		if (!Configuration.isDebug()) {
			Thread.setDefaultUncaughtExceptionHandler( new Thread.UncaughtExceptionHandler(){

				public void uncaughtException(Thread t, Throwable e) {
					Configuration.getLogger().debug("Uncaught exception");
					StringWriter w = new StringWriter();
					e.printStackTrace(new PrintWriter(w));
					Configuration.getLogger().debug(w.toString());
					Configuration.getLogger().debug(e.toString());
					MessageUtil.showWarning("There was a problem running DoMosaics; consult log file.");
					// remove lock file if possible
					if (Configuration.getInstance().hasLockfile()) {
						// TODO Save the the views: i) all and the user will remove the busted one ii) only the working ones
						// Save the configuration
						ConfigurationWriter.write();						
						Configuration.getInstance().getLockFile().delete();
					}
					System.exit(1);
				}
			});
		}
		try {
//			Configuration.getLogger().info("*** INFO: Starting DoMosaics.");
			ApplicationHandler.getInstance().start();
		}
		catch (Exception e) {
			if (Configuration.getReportExceptionsMode())
				Configuration.getInstance().getExceptionComunicator().reportBug(e);
			else			
				Configuration.getLogger().debug(e.toString());
			
			MessageUtil.showWarning("There was a problem starting DoMosaics. Please consult log file.");
		}

	}
	
	
	
	
}
