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
package org.aavso.tools.vstar.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.aavso.tools.vstar.ui.dialog.MessageBox;
import org.aavso.tools.vstar.ui.dialog.PhaseParameterDialog;
import org.aavso.tools.vstar.ui.mediator.Mediator;
import org.aavso.tools.vstar.ui.model.ObservationPlotModel;
import org.aavso.tools.vstar.util.stats.epoch.IEpochStrategy;

/**
 * This class represents a chart pane containing a phase plot for a set of valid
 * observations (magnitude vs standard phase).
 */
public class PhasePlotPane extends ObservationPlotPane {

	/**
	 * Constructor.
	 * 
	 * @param title
	 *            The title of the plot.
	 * @param subTitle
	 *            The sub-title for the chart.
	 * @param obsModel
	 *            The observation model.
	 * @param bounds
	 *            The bounds of the pane.
	 */
	public PhasePlotPane(String title, String subTitle,
			ObservationPlotModel obsModel, Dimension bounds) {
		super(title, subTitle, PHASE_TITLE, MAG_TITLE, obsModel, bounds);

		addToChartControlPanel(this.getChartControlPanel());
	}

	// TODO: factor following out into common class for means phase plot also

	// Add means-specific widgets to chart control panel.
	private void addToChartControlPanel(JPanel chartControlPanel) {
		JButton newPhasePlotButton = new JButton("New Phase Plot");
		newPhasePlotButton
				.addActionListener(createNewPhasePlotButtonListener());
		chartControlPanel.add(newPhasePlotButton);
	}

	// Return a listener for the "new phase plot" button.
	private ActionListener createNewPhasePlotButtonListener() {
		final PhasePlotPane self = this;
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					PhaseParameterDialog phaseDialog = new PhaseParameterDialog();
					if (!phaseDialog.isCancelled()) {
						double period = phaseDialog.getPeriod();
						IEpochStrategy epochStrategy = phaseDialog
								.getEpochStrategy();

						// This will be the final act of this object before
						// it is usurped by another model+phase-plot-pane pair.
						Mediator.getInstance().createPhasePlotArtefacts(period,
								epochStrategy,
								obsModel.getSeriesVisibilityMap());
					}
				} catch (Exception ex) {
					MessageBox.showErrorDialog(MainFrame.getInstance(),
							"New Phase Plot", ex);
				}
			}
		};
	}
}
