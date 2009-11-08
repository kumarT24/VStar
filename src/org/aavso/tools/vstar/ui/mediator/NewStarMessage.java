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

import org.aavso.tools.vstar.ui.model.NewStarType;

/**
 * A message class containing new star type and GUI component information.
 */
public class NewStarMessage {

	private NewStarType newStarType;
	private String newStarName;
	
	// GUI table and chart components.
	// TODO: we could use NamedComponents instead, e.g. to get tooltips
//	private ObservationPlotPane obsChartPane;
//	private ObservationAndMeanPlotPane obsAndMeanChartPane;
//	private ObservationListPane obsListPane;
//	private MeanObservationListPane meansListPane;

	/**
	 * @param newStarType
	 * @param newStarName
	 */
	public NewStarMessage(NewStarType newStarType, String newStarName) {
		this.newStarType = newStarType;
		this.newStarName = newStarName;
	}
	
	/**
	 * Constructor
	 * 
	 * @param newStarType
	 *            The new star type enum.
	 * @param obsChartPane
	 *            The observation plot GUI component.
	 * @param obsWithMeanChartPane
	 *            The observation-and-mean plot GUI component.
	 * @param obsListPane
	 *            The observation table GUI component.
	 * @param meansTablePane           
	 */
//	public NewStarMessage(NewStarType newStarType,
//			ObservationPlotPane obsChartPane,
//			ObservationAndMeanPlotPane obsAndMeanChartPane,
//			ObservationListPane obsListPane,
//			MeanObservationListPane meansListPane) {
//		this.newStarType = newStarType;
//		
//		this.obsChartPane = obsChartPane;
//		this.obsAndMeanChartPane = obsAndMeanChartPane;
//		
//		this.obsListPane = obsListPane;
//		this.meansListPane = meansListPane;
//	}

//	/**
//	 * @return the newStarType
//	 */
//	public NewStarType getNewStarType() {
//		return newStarType;
//	}
//
//	/**
//	 * @return the obsChartPane
//	 */
//	public ObservationPlotPane getObsChartPane() {
//		return obsChartPane;
//	}
//
//	/**
//	 * @return the obsListPane
//	 */
//	public ObservationListPane getObsTablePane() {
//		return obsListPane;
//	}
//
//	/**
//	 * @return the obsAndMeanChartPane
//	 */
//	public ObservationAndMeanPlotPane getObsAndMeanChartPane() {
//		return obsAndMeanChartPane;
//	}
//
//	/**
//	 * @return the meansListPane
//	 */
//	public MeanObservationListPane getMeansListPane() {
//		return meansListPane;
//	}

	/**
	 * @return the newStarType
	 */
	public NewStarType getNewStarType() {
		return newStarType;
	}

	/**
	 * @return the newStarName
	 */
	public String getNewStarName() {
		return newStarName;
	}

	
}