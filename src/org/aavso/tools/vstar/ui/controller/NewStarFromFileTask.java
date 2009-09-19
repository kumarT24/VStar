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
package org.aavso.tools.vstar.ui.controller;

import java.awt.Component;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;

import org.aavso.tools.vstar.input.ObservationRetrieverBase;
import org.aavso.tools.vstar.input.text.ObservationSourceAnalyser;
import org.aavso.tools.vstar.input.text.TextFormatObservationReader;
import org.aavso.tools.vstar.ui.MainFrame;
import org.aavso.tools.vstar.ui.dialog.MessageBox;
import org.aavso.tools.vstar.ui.model.NewStarMessage;
import org.aavso.tools.vstar.ui.model.ProgressInfo;
import org.jdesktop.swingworker.SwingWorker;

/**
 * A concurrent task in which a new star from file request task is handled.
 */
public class NewStarFromFileTask extends SwingWorker<Void, Void> {
	private ModelManager modelMgr = ModelManager.getInstance();

	private File obsFile;
	private ObservationSourceAnalyser analyser;
	private int plotTaskPortion;
	private boolean success;

	/**
	 * Constructor.
	 * 
	 * @param obsFile
	 *            The file from which to load the star observations.
	 * @param analyser
	 *            An observation file analyser.
	 * @param plotTaskPortion
	 *            The portion of the total task that involves the light curve
	 *            plot.
	 */
	public NewStarFromFileTask(File obsFile,
			ObservationSourceAnalyser analyser,
			int plotTaskPortion) {
		this.obsFile = obsFile;
		this.analyser = analyser;
		this.plotTaskPortion = plotTaskPortion;
		this.success = false;
	}

	/**
	 * Main task. Executed in background thread.
	 */
	public Void doInBackground() {
		this.success = createFileBasedObservationArtefacts(obsFile,
				analyser);
		return null;
	}

	/**
	 * Create observation table and plot models from a file.
	 * 
	 * @param obsFile
	 *            The file from which to load the star observations.
	 * @param analyser
	 *            An observation file analyser.
	 */
	protected boolean createFileBasedObservationArtefacts(File obsFile,
			ObservationSourceAnalyser analyser) {

		boolean success = true;
		
		try {
			ObservationRetrieverBase textFormatReader = new TextFormatObservationReader(
					new LineNumberReader(new FileReader(obsFile.getPath())),
					analyser);

			textFormatReader.retrieveObservations();

			modelMgr.clearData();

			modelMgr.setValidObsList(textFormatReader.getValidObservations());
			modelMgr.setInvalidObsList(textFormatReader
					.getInvalidObservations());
			modelMgr.setValidObservationCategoryMap(textFormatReader
					.getValidObservationCategoryMap());

			modelMgr.createObservationArtefacts(analyser.getNewStarType(), obsFile
					.getName(), plotTaskPortion);

		} catch (Exception e) {
			MessageBox.showErrorDialog(MainFrame.getInstance(),
					"New Star From File Read Error", e);
			modelMgr.setNewStarName(null);
			modelMgr.clearData();
			success = false;
		}

		return success;
	}

	/**
	 * Executed in event dispatching thread.
	 */
	public void done() {
		// Task ends.
		modelMgr.getProgressNotifier().notifyListeners(
				ProgressInfo.COMPLETE_PROGRESS);

		if (success) {
			// Notify whoever is listening that a new star has been loaded,
			// passing GUI components in the message.
			NewStarMessage msg = new NewStarMessage(analyser.getNewStarType(),
					modelMgr.getObsChartPane(), modelMgr
							.getObsAndMeanChartPane(), modelMgr
							.getObsListPane(), modelMgr.getMeansListPane());

			modelMgr.getNewStarNotifier().notifyListeners(msg);
		}
	}
}