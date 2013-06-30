package domosaics.model.configuration;

import java.io.BufferedReader;
import java.io.FileReader;

import domosaics.ApplicationHandler;
import domosaics.ui.util.DocumentationHandler;

public class ConfigurationReader {

	public static void read() {
		 try {
		    BufferedReader in = new BufferedReader(new FileReader(Configuration.getInstance().getConfigFile()));
	
		    String line;
			while((line = in.readLine()) != null) {
				if (line.isEmpty())					// ignore empty lines
					continue;

				// if version differs, update documentation
				if (line.contains(ConfigurationWriter.VERSION)) {
					Double localVersion = 0.0;
					try {
						localVersion = Double.parseDouble(
							line.replace(ConfigurationWriter.VERSION, "").trim());
					}
					catch ( Exception e ) {
						Configuration.getLogger().debug("Unable to parse versioning!");
						continue;
					}
					
					if (Configuration.CURRENT_PROGRAM_VERSION > localVersion) {
						// update documentation
						Configuration.getLogger().debug("Updating documentation");
						DocumentationHandler.extractDocumentation();
					}
					continue;
				}
				
				

				if (line.contains(ConfigurationWriter.WORKSPACE_LOCATION)) {
					Configuration.getInstance().setWorkspaceDir(
							line.replace(ConfigurationWriter.WORKSPACE_LOCATION, "").trim()
					);
				} else {
					if (line.contains(ConfigurationWriter.GOOGLE_URL)) { 
						Configuration.getInstance().setGoogleUrl(
								line.replace(ConfigurationWriter.GOOGLE_URL, "").trim()
								);
					} else {
						if (line.contains(ConfigurationWriter.PFAM_URL)) {
							Configuration.getInstance().setPfamUrl(
									line.replace(ConfigurationWriter.PFAM_URL, "").trim()
									);							
						} else {
							if (line.contains(ConfigurationWriter.UNIPROT_URL)) {
								Configuration.getInstance().setUniprotUrl(
										line.replace(ConfigurationWriter.UNIPROT_URL, "").trim()
										);
							} else {
								if (line.contains(ConfigurationWriter.EMAIL_ADDR)) { 
									Configuration.getInstance().setEmailAddr(
											line.replace(ConfigurationWriter.EMAIL_ADDR, "").trim()
											);
								} else {
									if (line.contains(ConfigurationWriter.HMMER_SCAN_BIN)) { 
										Configuration.getInstance().setHmmScanBin(
												line.replace(ConfigurationWriter.HMMER_SCAN_BIN, "").trim()
												);
									} else {
										if (line.contains(ConfigurationWriter.HMMER_PRESS_BIN)) { 
											Configuration.getInstance().setHmmPressBin(
													line.replace(ConfigurationWriter.HMMER_PRESS_BIN, "").trim()
													);
										} else {
											if (line.contains(ConfigurationWriter.HMMER_PROFILE_DB)) {
												Configuration.getInstance().setHmmerDB(
														line.replace(ConfigurationWriter.HMMER_PROFILE_DB, "").trim()
														);
											} else {
												if (line.contains(ConfigurationWriter.SAVEONEXIT)) {
													Configuration.getInstance().setSaveOnExit(
															Boolean.parseBoolean(line.replace(ConfigurationWriter.SAVEONEXIT, "").trim())
															);
												} else {
													if (line.contains(ConfigurationWriter.OVERWRITEPROJECTS)) {
														Configuration.getInstance().setOverwriteProjects(
																Boolean.parseBoolean(line.replace(ConfigurationWriter.OVERWRITEPROJECTS, "").trim())
																);
													} else {
														if (line.contains(ConfigurationWriter.DOMAINBYNAME)) {
															Configuration.setNamePreferedToAcc(
																	Boolean.parseBoolean(line.replace(ConfigurationWriter.DOMAINBYNAME, "").trim())
																	);
														} else {
															if (line.contains(ConfigurationWriter.HELPIMPROVE)) {
																Configuration.getInstance().setHelpImprove(
																		Boolean.parseBoolean(line.replace(ConfigurationWriter.HELPIMPROVE, "").trim())
																);
															} else {
																if (line.contains(ConfigurationWriter.DOCUMENTATION_LOCATION)) {
																	Configuration.getInstance().setDocuPath(
																			line.replace(ConfigurationWriter.DOCUMENTATION_LOCATION, "").trim()
																	);
																}
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			//if (line.contains(ConfigurationWriter.NCBI_URL))
			//	Configuration.getInstance().setNcbiUrl(line.replace(ConfigurationWriter.NCBI_URL, "").trim());
			//if (line.contains(ConfigurationWriter.SHOWADVISES))
			//	Configuration.getInstance().setShowAdvices(Boolean.parseBoolean(line.replace(ConfigurationWriter.SHOWADVISES, "").trim()));
		    in.close();
		 } 
		 catch (Exception e) {
				if (Configuration.getReportExceptionsMode(true))
					Configuration.getInstance().getExceptionComunicator().reportBug(e);
				else			
					Configuration.getLogger().debug(e.toString());
		 }
	}
}
