package com.cs633.team1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class PreferencesUI extends JPanel implements ActionListener, PropertyChangeListener {
	
	public PreferencesUI() {
		Preferences preferences = new Preferences();
		JPanel p1 = new JPanel();
		p1.setPreferredSize(new Dimension(325, 40));
		p1.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JTextField titleText = new JTextField("Continuous Run Preferences");
		titleText.setFont(new Font("Sans Serif", Font.BOLD, 14));
		titleText.setEditable(false);
		titleText.setBorder(null);
		p1.add(titleText);
		add(p1);
		
		JPanel p2 = new JPanel();
		p2.setPreferredSize(new Dimension(275, 70));
		p2.setBorder(BorderFactory.createLineBorder(Color.gray));
		p2.setLayout(new FlowLayout(FlowLayout.LEFT));

		JTextField loopWaitText = new JTextField(" Run continuous tests every ");
		JTextField loopCount = new JTextField(String.valueOf(preferences.getLoopWaitCount()), 10);
		JTextField loopWaitText2 = new JTextField(" ms");
		
		loopWaitText.setEditable(false);
		loopWaitText.setBorder(null);
		loopWaitText2.setEditable(false);
		loopWaitText2.setBorder(null);

		p2.add(loopWaitText);
		p2.add(loopCount);
		p2.add(loopWaitText2);

		JCheckBox rewriteResults = new JCheckBox();
		rewriteResults.setSelected(preferences.isRewriteResults());
		JTextField rewriteText = new JTextField(" Rewrite previous run's results");
		rewriteText.setEditable(false);
		rewriteText.setBorder(null);
		
		p2.add(rewriteResults);
		p2.add(rewriteText);
		
		add(p2);

		JPanel p3 = new JPanel();
		p3.setPreferredSize(new Dimension(175, 35));
		p3.setLayout(new FlowLayout(FlowLayout.CENTER));

		JButton saveButton = new JButton("Save");
		p3.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				//Make sure the loop count is numeric before saving
				try {
					preferences.setLoopWaitCount(Integer.parseInt(loopCount.getText()), true);
					preferences.setRewriteResults(rewriteResults.isSelected(), true);
					E2ETester.closePreferWindow();
				} catch (NumberFormatException e) {
		        	JOptionPane.showMessageDialog(null, "Loop count is not numeric - please correct");
				}
			}
		});

		JButton cancelButton = new JButton("Cancel");
		p3.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				E2ETester.closePreferWindow();
			}
		});

		add(p3);

	}

	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
