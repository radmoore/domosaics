package domosaics.ui.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import domosaics.ui.DoMosaicsUI;




/**
 * JFontChooser.java
 *
 * The JFontChooser class is in the Public Domain, the code may be used
 * for any purpose.  It is provided as is with no warranty.
 *
 * Author:			James Bardsley (torasin@torasin.com)
 * Last Modfied:	29th September 2005
 */
public class JFontChooser extends JDialog implements ActionListener, ListSelectionListener
{
	public static final long serialVersionUID = 62256323L;
	
	private static String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	private static String[] style = {"Regular", "Bold", "Italic", "Bold Italic"};
	private static String[] size = {"8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28"};
	private Font font;
	private int option;
	private String textType;
	private int textStyle;
	private int textSize;
	
	private JList fList = new JList(fonts);
	private JList stList = new JList(style);
	private JList sizeList = new JList(size);
	private JLabel jlbFonts = new JLabel("Font:");
	private JLabel jlbStyle = new JLabel("Style:");
	private JLabel jlbSize = new JLabel("Size:");
	private JScrollPane jspFont = new JScrollPane(fList);
	private JScrollPane jspStyle = new JScrollPane(stList);
	private JScrollPane jspSize = new JScrollPane(sizeList);
	private JButton jbtOK = new JButton("OK");
	private JButton jbtCancel = new JButton("Cancel");
	private JTextField jtfTest = new JTextField("AaBbYyZz");
	
	public static final int OK_OPTION = 1;
	public static final int CANCEL_OPTION = 2;
	
	/**
	 * Constructs a JFontChooser that uses the default font.
	 */
	 
	public JFontChooser()
	{	
		this(new Font("Courier New", Font.PLAIN, 12));
	}
	
	/**
	 * Constructs a JFontChooser using the given font.
	 */
	public JFontChooser(Font aFont)
	{
		super(DoMosaicsUI.getInstance(), "DoMosaicS");
		
		Container container = getContentPane();
		JPanel panel = new JPanel();
		TitledBorder panelBorder = new TitledBorder("DoMosaicS");
		font = aFont;
		textType = font.getFontName();
		textStyle = font.getStyle();
		textSize = font.getSize();
		fList.setSelectionMode(0);
		stList.setSelectionMode(0);
		sizeList.setSelectionMode(0);
		jspFont.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jspStyle.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jspSize.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		panel.setBorder(panelBorder);
		
		jlbFonts.setBounds(8, 8, 161, 20);
		jspFont.setBounds(8, 32, 161, 114);

		jlbStyle.setBounds(176, 8, 81, 20);
		jspStyle.setBounds(176, 32, 81, 114);
		
		jlbSize.setBounds(264, 8, 41, 20);
		jspSize.setBounds(264, 32, 41, 114);
		
		jbtOK.setBounds(320, 8, 89, 25);
		jbtCancel.setBounds(320, 40, 89, 25);
	
		panel.setBounds(320, 72, 89, 73);
		
		container.add(jlbFonts);
		container.add(jspFont);
		
		container.add(jlbStyle);
		container.add(jspStyle);
		
		container.add(jlbSize);
		container.add(jspSize);
		
		container.add(jbtOK);
		container.add(jbtCancel);
		
		container.add(panel);
		
		jtfTest.setBounds(8, 25, 74, 30);
		
		panel.add(jtfTest);
		
		container.setLayout(null);
		panel.setLayout(null);
		
		setSize(424, 185);
		setResizable(false);
		setModal(true);
		
		jbtCancel.addActionListener(this);
		jbtOK.addActionListener(this);
		fList.addListSelectionListener(this);
		stList.addListSelectionListener(this);
		sizeList.addListSelectionListener(this);
	}
	
	/**
	 * Displays the font dialog on the screen positioned relative to
	 * the parent and blocks until the dialog is hidden.
	 */
	public int showDialog(Component parent, String title)
	{
		boolean found = false;
		option = CANCEL_OPTION;
		
		this.setTitle(title);
		jtfTest.setFont(new Font(textType, textStyle, textSize));
		
		/*
		 * Traverse through the lists and find the values that correspond
		 * to the selected font.  If it can't find the values then clear the
		 * selection.
		 */
		
		for (int i = 0; i < fList.getModel().getSize(); i++)
		{
			fList.setSelectedIndex(i);
			
			if (font.getName().equals((String)fList.getSelectedValue()))
			{
				found = true;
				setScrollPos(jspFont, fList, i);
				
				break;
			}
		}
		
		if (!found)
		{
			fList.clearSelection();
		}
		
		stList.setSelectedIndex(font.getStyle());
		
		found = false;
		
		for (int i = 0; i < sizeList.getModel().getSize(); i++)
		{
			sizeList.setSelectedIndex(i);
			
			if (font.getSize() == Integer.parseInt((String)sizeList.getSelectedValue()))
			{
				found = true;
				setScrollPos(jspSize, sizeList, i);
				
				break;
			}
		}
		
		if (!found)
		{
			sizeList.clearSelection();
		}
		
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
		
		return option;
	}
	
	/**
	 * Sets the current font of the font chooser.
	 */
	public void setFont(Font aFont)
	{
		font = aFont;
	}
	
	/**
	 * Gets the current font of the font chooser.
	 */
	public Font getFont()
	{
		return font;
	}
	
	/**
	 * Gets the name of the font chooser's current font.
	 */
	public String getFontName()
	{
		return font.getFontName();
	}
	
	/**
	 * Gets the style of the font chooser's current font.
	 */
	public int getFontStyle()
	{
		return font.getStyle();
	}
	
	/**
	 * Gets the size of the font chooser's current font.
	 */
	public int getFontSize()
	{
		return font.getSize();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		boolean found = false;
		
		if (e.getSource() == fList)
		{
			textType = (String) fList.getSelectedValue();
			jtfTest.setFont(new Font(textType, textStyle, textSize));
		}
		else if (e.getSource() == sizeList)
		{
			textSize = (Integer.parseInt((String)sizeList.getSelectedValue()));
			jtfTest.setFont(new Font(textType, textStyle, textSize));
		}
		else if (e.getSource() == stList)
		{
			if (stList.getSelectedValue().equals("Regular"))
			{
				textStyle = Font.PLAIN;
			}
			else if (stList.getSelectedValue().equals("Bold"))
			{
				textStyle = Font.BOLD;
			}
			else if (stList.getSelectedValue().equals("Italic"))
			{
				textStyle = Font.ITALIC;
			}
			else if (stList.getSelectedValue().equals("Bold Italic"))
			{
				textStyle = Font.BOLD & Font.ITALIC;
			}
			
			stList.setSelectedIndex(textStyle);
			
			jtfTest.setFont(new Font(textType, textStyle, textSize));
		}
		else if (e.getSource() == jbtOK)
		{
			option = OK_OPTION;
			font = new Font(textType, textStyle, textSize);
			this.setVisible(false);
		}
		else if (e.getSource() == jbtCancel)
		{
			option = CANCEL_OPTION;
			this.setVisible(false);
		}
	}
	
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getSource() == fList)
		{		
			textType = (String)fList.getSelectedValue();
			jtfTest.setFont(new Font(textType, textStyle, textSize));
		}
		else if (e.getSource() == stList)
		{
			//jtfStyle.setText(((String)(stList.getSelectedValue())));
			
			if (stList.getSelectedValue().equals("Regular"))
			{
				textStyle = 0;
			}
			else if (stList.getSelectedValue().equals("Bold"))
			{
				textStyle = 1;
			}
			else if (stList.getSelectedValue().equals("Italic"))
			{
				textStyle = 2;
			}
			else if (stList.getSelectedValue().equals("Bold Italic"))
			{
				textStyle = 3;
			}
			
			jtfTest.setFont(new Font(textType, textStyle, textSize));
		}
		else if (e.getSource() == sizeList)
		{			
			textSize = (Integer.parseInt((String)sizeList.getSelectedValue()));
			jtfTest.setFont(new Font(textType, textStyle, textSize));
		}
	}
	
	/*
	 * Takes a scrollPane, a JList and an index in the JList and sets the scrollPane's
	 * scrollbar so that the selected item in the JList is in about the middle of the
	 * scrollPane.
	 */
	private void setScrollPos(JScrollPane sp, JList list, int index)
	{
		int unitSize = sp.getVerticalScrollBar().getMaximum() / list.getModel().getSize();
		
		sp.getVerticalScrollBar().setValue((index - 2) * unitSize);
	}
}

