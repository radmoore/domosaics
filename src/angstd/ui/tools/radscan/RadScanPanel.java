package angstd.ui.tools.radscan;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
public class RadScanPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private JTextField matchScore, mismatchPen, intOpenGapPen, intExtenGapPen, 
	terOpenGapPen, terExtenGapPen;
	private JCheckBox domLenScoring, resolveOverlaps, mergeHits;
	private JButton runScan, reset, close;
	private RadScanView view;
	private ArrangementComponent arrComp;
	
	
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
		
		add(new JLabel(" "), "gap 10, gaptop 10");
		add(runScan, "growx");
		add(reset, "growx");
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
		
		runScan = new JButton("Run Scan");
		runScan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runScan();
			}
		});
		
		reset = new JButton("Defaults");
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();	
			}
		});
		
		close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				view.closeWindow();
			}
		});
	}
	
	private void runScan(){
		DomainArrangement da = view.getArrangementComponent().getDomainArrangement();
		MessageUtil.showDialog("This is your arrangement id: "+da.toString());
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
	
}
