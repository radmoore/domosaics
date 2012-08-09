package angstd.ui.tools.radscan;

import info.radm.pbar.ProgressBar;
import info.radm.radscan.Parser;
import info.radm.radscan.QueryBuilder;
import info.radm.radscan.RADSResults;
import info.radm.radscan.RADSRunner;
import info.radm.radscan.ds.Protein;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.JXTitledSeparator;

import angstd.model.arrangement.DomainArrangement;
import angstd.ui.util.MessageUtil;
import angstd.ui.views.domainview.components.ArrangementComponent;
import angstd.webservices.RADS.RadsParms;

/**
 * 
 * @author <a href='http://radm.info'>Andrew D. Moore</a>
 *
 */
public class RadScanPanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	private JTextField matchScore, mismatchPen, intOpenGapPen, intExtenGapPen, 
	terOpenGapPen, terExtenGapPen;
	private JCheckBox domLenScoring, resolveOverlaps, mergeHits;
	private JButton runScan, reset, close, apply;
	private JProgressBar progressBar;
	private RadScanView view;
	private ArrangementComponent arrComp;
	private RADSRunner radsRunner;
	private TreeSet<Protein> proteins;
	private boolean radsRunning = false;
	
	
	public RadScanPanel(RadScanView view) {
		super(new MigLayout());
		this.view = view;
		initComponents();
		
		add(new JXTitledSeparator("RADS match scores"), "growx, span, wrap, gaptop 10");
		add(new JLabel("Match: "), "gap 10, gaptop 10");
		add(matchScore, "span 2, h 25!, wrap");
		add(new JLabel("Mismatch: "), "gap 10, gaptop 10");
		add(mismatchPen, "span 2, h 25!, wrap");

		add(new JXTitledSeparator("RADS gap penalties"), "growx, span, wrap, gaptop 10");
		
		// internal gap pen
		add(new JLabel("Internal"), "gap 10, gaptop 10");
		add(new JLabel("open: "), "gaptop 5");
		add(intOpenGapPen, "h 25!");
		add(new JLabel("extend:"), "gaptop 5");
		add(intExtenGapPen, "h 25!, wrap");
		
		//terminal gap pen
		add(new JLabel("Terminal"), "gap 10, gaptop 10");
		add(new JLabel("open: "), "gaptop 5");
		add(terOpenGapPen, "h 25!");
		add(new JLabel("extend: "), "gaptop 5");
		add(terExtenGapPen, "h 25!, wrap");
		
		add(domLenScoring, "gap 10, gaptop 10");
		add(new JLabel("No length dependant scoring"), "span2, gap 1, gaptop 10, wrap");
		
	
		add(new JXTitledSeparator("Post-processing"), "growx, span, wrap, gaptop 10");
		add(resolveOverlaps, "gap 10, gaptop 10");
		add(new JLabel("Resolve overlaps"), "span2, gap 1, gaptop 10, wrap");
		add(mergeHits, "gap 10, gaptop 5");
		add(new JLabel("Merge split hits"), "span2, gap 1, gaptop 10, wrap");
		
		//add(new JLabel(" "), "gap 10, gaptop 10");
		add(runScan, "growx");
		add(reset, "growx, wrap");
		add(new JXTitledSeparator("Progress"), "growx, span, wrap, gaptop 10");
		add(progressBar, "h 25!, gap 10, gapright 10, span, growX, wrap");
		
		add(new JXTitledSeparator("Apply Results"), "growx, span, wrap, gaptop 10");
		add(apply, "growx, gap 1");
		add(close, "growx, wrap");
		//setSize(600,350);
	}
	
	
	private void initComponents() {
		
		matchScore = new JTextField(5);
		matchScore.setText(""+RadsParms.DEFAULT_MATCHSCORE.getDeafultValue());
		
		mismatchPen = new JTextField(5);
		mismatchPen.setText(""+RadsParms.DEFAULT_MISMATCH_PEN.getDeafultValue());
		
		intOpenGapPen = new JTextField(5);
		intOpenGapPen.setText(""+RadsParms.DEFAULT_INTERNAL_GAP_OPEN_PEN.getDeafultValue());
		
		intExtenGapPen = new JTextField(5);
		intExtenGapPen.setText(""+RadsParms.DEFAULT_INTERNAL_GAP_EXTEN_PEN.getDeafultValue());
		
		terOpenGapPen = new JTextField(5);
		terOpenGapPen.setText(""+RadsParms.DEFAULT_TERMINAL_GAP_OPEN_PEN.getDeafultValue());
		
		terExtenGapPen = new JTextField(5);
		terExtenGapPen.setText(""+RadsParms.DEFAULT_TERMINAL_GAP_EXTEN_PEN.getDeafultValue());
				
		domLenScoring = new JCheckBox();
		domLenScoring.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		
		resolveOverlaps = new JCheckBox();
		resolveOverlaps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		
		mergeHits = new JCheckBox();
		mergeHits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO		
			}
		});
		
		runScan = new JButton("Submit Job");
		runScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runScan();
			}
		});
		
		apply = new JButton("Apply");
		apply.setEnabled(false);
		apply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createResultView();
			}
		});
		
		reset = new JButton("Defaults");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();	
			}
		});
		
		close = new JButton("Cancel");
		close.setActionCommand("close");
		close.addActionListener(this);
		
		progressBar = new JProgressBar(0, 105);
		progressBar.setValue(0);
	}
	
	private void runScan(){
		//validateParams();
		runScan.setEnabled(false);
		reset.setEnabled(false);
		DomainArrangement da = view.getArrangementComponent().getDomainArrangement();
		QueryBuilder qBuilder = new QueryBuilder();
		qBuilder.setQuietMode(true);
		qBuilder.setQueryXdomString(da.toXdom());

		// TODO set params
		this.radsRunner = new RADSRunner(qBuilder.build());
		progressBar.setIndeterminate(true);
		radsRunning = true;
		SwingWorker<TreeSet<Protein>, Void> worker = new SwingWorker<TreeSet<Protein>, Void>() {
			protected TreeSet<Protein> doInBackground() throws Exception {
				RADSResults results = radsRunner.submit();
				Parser resultParser = new Parser(results);
				return resultParser.parse();
			}
			
			public void done() {
				try {
					proteins = get();
				}
				catch (Exception e) {};
			}
		};
		worker.execute();
		worker.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("state".equals(evt.getPropertyName())) {
					if ( "DONE".equals(evt.getNewValue().toString()) ) {
						System.out.println("Scan Complete.");
						runScan.setEnabled(false);
						radsRunning = false;
						processResults();
					}
					else {
						System.out.println("COmparison was false!");
					}
				}
			}
		});
	}
	
	private void validateParams() {
		//TODO
	}
	
	private void reset() {
		matchScore.setText(""+RadsParms.DEFAULT_MATCHSCORE.getDeafultValue());
		mismatchPen.setText(""+RadsParms.DEFAULT_MISMATCH_PEN.getDeafultValue());
		intOpenGapPen.setText(""+RadsParms.DEFAULT_INTERNAL_GAP_OPEN_PEN.getDeafultValue());
		intExtenGapPen.setText(""+RadsParms.DEFAULT_INTERNAL_GAP_EXTEN_PEN.getDeafultValue());
		terOpenGapPen.setText(""+RadsParms.DEFAULT_TERMINAL_GAP_OPEN_PEN.getDeafultValue());
		terExtenGapPen.setText(""+RadsParms.DEFAULT_TERMINAL_GAP_EXTEN_PEN.getDeafultValue());
		domLenScoring.setSelected(false);
		resolveOverlaps.setSelected(false);
		mergeHits.setSelected(false);
	}
	
	private void createResultView() {
		
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("close"))
			checkScanState(e);
		
	}
	
	private void checkScanState(ActionEvent e) {
		if (radsRunning) {
			boolean choice = MessageUtil.showDialog(this, "You are running RadScan. Your results will be lost. Are you sure?");
			if (choice)
				view.closeWindow();
			else
				return;
		}
		view.closeWindow();
	}
	
	private void processResults() {
		if (proteins == null) {
			view.closeWindow();
			MessageUtil.showInformation("No hits found");
			return;
		}
		progressBar.setIndeterminate(false);
		progressBar.setMaximum(proteins.size());
		progressBar.setValue(0);
		int i = 1;
		for (Protein p: proteins) {
			progressBar.setValue(i);
			progressBar.setString("Processing hit "+i+ " of "+progressBar.getMaximum());
			i++;
			System.out.println(p.toString());
		}
		progressBar.setString("Scan complete");
		apply.setEnabled(true);
	}
	
}
