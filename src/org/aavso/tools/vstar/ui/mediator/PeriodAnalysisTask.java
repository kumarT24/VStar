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
package org.aavso.tools.vstar.ui.mediator;

import org.aavso.tools.vstar.ui.MainFrame;
import org.aavso.tools.vstar.ui.dialog.PeriodAnalysis2DPlotDialog;
import org.aavso.tools.vstar.ui.model.plot.PeriodAnalysis2DPlotModel;
import org.jdesktop.swingworker.SwingWorker;

/**
 * A concurrent task in which a potentially long-running periodAnalysisAlgorithm is executed.
 */
public class PeriodAnalysisTask extends SwingWorker<Void, Void> {

	private IPeriodAnalysisAlgorithm periodAnalysisAlgorithm;
	private StarInfo starInfo;

	/**
	 * Constructor
	 * 
	 * @param periodAnalysisAlgorithm
	 *            The periodAnalysisAlgorithm to be executed.
	 * @param info
	 *            Information about the star.
	 */
	public PeriodAnalysisTask(IPeriodAnalysisAlgorithm periodAnalysisAlgorithm, StarInfo info) {
		this.periodAnalysisAlgorithm = periodAnalysisAlgorithm;
		this.starInfo = info;
	}

	/**
	 * @see org.jdesktop.swingworker.SwingWorker#doInBackground()
	 */
	protected Void doInBackground() throws Exception {
		MainFrame.getInstance().getStatusPane().setMessage(
				"Performing Period Analysis...");
		periodAnalysisAlgorithm.execute();
		MainFrame.getInstance().getStatusPane().setMessage("");
		return null;
	}

	/**
	 * Executed in event dispatching thread.
	 */
	public void done() {
		Mediator.getInstance().getProgressNotifier().notifyListeners(
				ProgressInfo.COMPLETE_PROGRESS);

		// Mediator.getInstance().getProgressNotifier().notifyListeners(
		// ProgressInfo.RESET_PROGRESS);
		
		PeriodAnalysis2DPlotModel model = new PeriodAnalysis2DPlotModel(
				periodAnalysisAlgorithm.getResultSeries());

		new PeriodAnalysis2DPlotDialog("Date Compensated DFT for "
				+ starInfo.getDesignation(), "Frequency",
				"Period/Power/Amplitude", model);

		// TODO: how to detect task cancellation and clean up map etc
	}
}