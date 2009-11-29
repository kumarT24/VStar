/**
 * VStar: a statistical analysis tool for variable star data.
 * Copyright (C) 2009  AAVSO (http://www.aavso.org/)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 */
package org.aavso.tools.vstar.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.aavso.tools.vstar.ui.MainFrame;
import org.aavso.tools.vstar.util.stats.PhaseCalcs;
import org.aavso.tools.vstar.util.stats.epoch.IEpochStrategy;

/**
 * This class represents a dialog to obtain parameters for phase plot
 * calculation: period, epoch determination method.
 */
public class PhaseParameterDialog extends AbstractOkCancelDialog {

	private static Pattern periodPattern = Pattern
			.compile("^\\s*(\\d+(\\.\\d+)?)\\s*$");

	private JTextField periodField;

	private double period;
	private IEpochStrategy epochStrategy;

	/**
	 * Constructor.
	 */
	public PhaseParameterDialog() {
		super("Phase Plot");

		period = 0;
		epochStrategy = null;
		
		Container contentPane = this.getContentPane();

		JPanel topPane = new JPanel();
		topPane.setLayout(new BoxLayout(topPane, BoxLayout.PAGE_AXIS));
		topPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		// Period
		topPane.add(createPeriodFieldPane());

		// Epoch determination
		topPane.add(Box.createRigidArea(new Dimension(75, 10)));
		topPane.add(createEpochStrategyPane());
		
		// OK, Cancel
		topPane.add(createButtonPane());

		contentPane.add(topPane);

		this.pack();
		periodField.requestFocusInWindow();
		this.setLocationRelativeTo(MainFrame.getInstance().getContentPane());
		this.setVisible(true);
	}

	private JPanel createPeriodFieldPane() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("Period (days)"));

		periodField = new JTextField();
		periodField.setToolTipText("Enter period in days");
		panel.add(periodField);

		return panel;
	}

	private JPanel createEpochStrategyPane() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory
				.createTitledBorder("Epoch Strategy"));

		ButtonGroup strategyGroup = new ButtonGroup();

		boolean first = true;

		for (String key : PhaseCalcs.epochStrategyMap.keySet()) {
			IEpochStrategy strategy = PhaseCalcs.epochStrategyMap.get(key);
			String strategyDesc = strategy.getDescription();

			JRadioButton strategyRadioButton = new JRadioButton(strategyDesc);
			strategyRadioButton.setActionCommand(key);
			strategyRadioButton
					.addActionListener(createEpochStrategyActionListener());
			panel.add(strategyRadioButton);
			panel.add(Box.createRigidArea(new Dimension(10, 10)));

			strategyGroup.add(strategyRadioButton);

			// Arbitrarily select the first strategy.
			// TODO: should be able to set this as a Preference.
			if (first) {
				strategyRadioButton.setSelected(true);
				epochStrategy = strategy;
				first = false;
			}
		}

		assert(epochStrategy != null);

		// Without this, the bordered radio group will appear right-justified.
		JPanel centeringPanel = new JPanel(new BorderLayout());
		centeringPanel.add(panel, BorderLayout.CENTER);
		
		return centeringPanel;
	}

	private ActionListener createEpochStrategyActionListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = e.getActionCommand();
				epochStrategy = PhaseCalcs.epochStrategyMap.get(name);
			}
		};
	}

	/**
	 * @see org.aavso.tools.vstar.ui.dialog.AbstractOkCancelDialog#cancelAction()
	 */
	protected void cancelAction() {
		// Nothing to do
	}

	/**
	 * @see org.aavso.tools.vstar.ui.dialog.AbstractOkCancelDialog#okAction()
	 */
	protected void okAction() {
		String periodText = periodField.getText();

		if (periodText != null) {
			Matcher periodMatcher = periodPattern.matcher(periodText);
			if (periodMatcher.matches()) {
				String periodStr = periodMatcher.group(1);
				period = Double.parseDouble(periodStr);
				if (period > 0) {
					cancelled = false;
					setVisible(false);
					dispose();
				}
			}
		}
	}

	/**
	 * @return the period
	 */
	public double getPeriod() {
		return period;
	}

	/**
	 * @return the epochStrategy
	 */
	public IEpochStrategy getEpochStrategy() {
		return epochStrategy;
	}
}
