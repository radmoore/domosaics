package domosaics.ui.views.domainview.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import domosaics.localservices.codd.ConditionallyDependentDomainPairMap;
import domosaics.model.arrangement.DomainArrangement;
import domosaics.ui.DoMosaicsUI;
import domosaics.ui.util.MessageUtil;
import domosaics.ui.views.domainview.DomainViewI;



public class SearchProteinTool extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	protected JButton jbtSearch, jbtClose;
	protected JTextField protQuery;
	protected String lastSearchMemory;
	
	/** the view providing this feature */
	protected DomainViewI view;
	
	public SearchProteinTool(DomainViewI view) {
		this.view = view;
		lastSearchMemory="";
		JPanel componentHolder = new JPanel();
		setLayout(new MigLayout("", "[left]"));
		
		protQuery=new JTextField(25);
		jbtSearch = new JButton("Search");
	    jbtSearch.addActionListener(this);
		jbtClose = new JButton("Close");
	    jbtClose.addActionListener(this);

	    componentHolder.add(protQuery, "h 25!, gap 5, gapright 5, span 2, growX, wrap");
	    componentHolder.add(jbtSearch, "h 25!, gap 5, gapright 5");
	    componentHolder.add(jbtClose, "h 25!, gap 5, gapright 5, wrap");
		getContentPane().add(componentHolder);
		setSize(200, 50);
		pack();
		setResizable(false);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    		
	}

	/**
	 * Shows the dialog
	 * 
	 * @param parent
	 * 		the component used to show the dialog
	 * @param title
	 * 		the dialogs title
	 * @return
	 * 		always 0
	 */
	public void showDialog(Component parent, String title) {
		this.setTitle(title);
		this.setLocationRelativeTo(parent);
		setLocation(10, getLocation().y);
		this.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		 if(e.getSource() == jbtSearch)
	     {
			 String protein = protQuery.getText();
			 if(!protein.trim().equals(""))
				 searchAction(protein);
			 else {
				 setAlwaysOnTop(false);
				 MessageUtil.showWarning(DoMosaicsUI.getInstance(), "Please enter a character to search for!");				 
				 setAlwaysOnTop(true);
		     }
	     }else
	     {
	      if(e.getSource() == jbtClose)
	      {  
	          setVisible(false);
	      }
	     }
	}
	
	void searchAction(String query) {
		int i;
		DomainArrangement[] daSet=view.getDaSet();
		boolean stop=false, goUntilPrevQuery=lastSearchMemory.contains(query);
		for(i=0; i!= daSet.length && !stop; i++)
		{
			System.out.println(daSet[i].getName());
			if(goUntilPrevQuery) {
				if(daSet[i].equals(lastSearchMemory))
					goUntilPrevQuery=false;
			} else {				
				if(daSet[i].getName().contains(query)) {
					stop=true;
				}
			}
		}
		if(!stop) {
			for(i=0; i!= daSet.length && !stop; i++)
			{
				if(daSet[i].getName().contains(query)) 
					stop=true;	
			}
			if(!stop) {
				setAlwaysOnTop(false);
				MessageUtil.showWarning(DoMosaicsUI.getInstance(), "No corresponding protein IDs!");				 
				setAlwaysOnTop(true);
			}
		} else {
			lastSearchMemory=daSet[i].getName();
			JScrollPane scrollPane = (JScrollPane)view.getComponent();
			scrollPane.getVerticalScrollBar().setValue((int)(i*scrollPane.getVerticalScrollBar().getMaximum()/(double)(daSet.length)));
			System.out.println(i+" "+(int)(i*scrollPane.getVerticalScrollBar().getMaximum()/(double)(daSet.length))+" "+scrollPane.getVerticalScrollBar().getMaximum()+" "+scrollPane.getVerticalScrollBar().getMinimum());
		}
	}

}
