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
package org.aavso.tools.vstar.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.aavso.tools.vstar.data.ValidObservation;
import org.aavso.tools.vstar.util.notification.Notifier;
import org.aavso.tools.vstar.util.stats.DescStats;

/**
 * This class is a model that represents a series of valid variable star
 * observations, e.g. for different bands (or from different sources) along with
 * a means series that can change over time.
 */
public class ObservationAndMeanPlotModel extends ObservationPlotModel {

	// public static final String MEANS_SERIES_NAME =
	// SeriesType.MEAN.getDescription();

	public static final int NO_MEANS_SERIES = -1;

	// The series number of the series that is the source of the
	// means series.
	protected int meanSourceSeriesNum;

	// The series number of the means series.
	protected int meansSeriesNum;

	// An observation time source.
	protected ITimeElementEntity timeElementEntity;

	// The number of time elements in a means series bin.
	protected double timeElementsInBin;

	// The observations that constitute the means series.
	protected List<ValidObservation> meanObsList;

	protected Notifier<List<ValidObservation>> meansChangeNotifier;

	/**
	 * Constructor
	 * 
	 * We add named observation source lists to unique series numbers. Then we
	 * add the initial mean-based series.
	 * 
	 * @param obsSourceListMap
	 *            A mapping from source series to lists of observation sources.
	 * @param coordSrc
	 *            coordinate and error source.
	 * @param timeElementEntity
	 *            A time element source for observations.
	 */
	public ObservationAndMeanPlotModel(
			Map<SeriesType, List<ValidObservation>> obsSourceListMap,
			ICoordSource coordSrc, ITimeElementEntity timeElementEntity) {

		super(obsSourceListMap, coordSrc);

		this.meansSeriesNum = NO_MEANS_SERIES;

		this.timeElementEntity = timeElementEntity;

		this.timeElementsInBin = this.timeElementEntity.getDefaultTimeElementsInBin();

		this.meansChangeNotifier = new Notifier<List<ValidObservation>>();

		this.meanSourceSeriesNum = determineMeanSeriesSource();

		this.setMeanSeries();
	}

	/**
	 * Set the mean-based series.
	 * 
	 * This method creates a new means series based upon the current mean source
	 * series index and time-elements-in-bin. It then updates the view and any listeners.
	 */
	public void setMeanSeries() {

		meanObsList = DescStats.createdBinnedObservations(
				seriesNumToObSrcListMap.get(meanSourceSeriesNum),
				timeElementEntity, timeElementsInBin);

		// As long as there were enough observations to create a means list
		// to make a "means" series, we do so.
		if (!meanObsList.isEmpty()) {
			boolean found = false;

			// TODO: instead of this, why not just ask:
			// if (this.meansSeriesNum != NO_MEANS_SERIES) {
			// ...
			// } else {
			// ...do the if (!found) code below...
			// }
			for (Map.Entry<Integer, SeriesType> entry : this.seriesNumToSrcTypeMap
					.entrySet()) {
				if (SeriesType.MEANS.equals(entry.getValue())) {
					int series = entry.getKey();
					this.seriesNumToObSrcListMap.put(series, meanObsList);
					this.fireDatasetChanged();
					found = true;
					break;
				}
			}

			// Is this the first time the means series has been added?
			if (!found) {
				this.meansSeriesNum = this.addObservationSeries(
						SeriesType.MEANS, meanObsList);

				// Make sure it's rendered!
				this.getSeriesVisibilityMap().put(this.meansSeriesNum, true);
			}

			// Notify listeners.
			this.meansChangeNotifier.notifyListeners(meanObsList);

		} else {
			// TODO: remove empty check; should never happen because of way
			// binning is done
		}
	}

	public void changeMeansSeries(double timeElementsInBin) {
		this.timeElementsInBin = timeElementsInBin;
		this.setMeanSeries();
	}

	/**
	 * @see org.aavso.tools.vstar.ui.model.ObservationPlotModel#changeSeriesVisibility(int,
	 *      boolean)
	 */
	public boolean changeSeriesVisibility(int seriesNum, boolean visibility) {
		// It doesn't make sense to remove the means series from a plot
		// whose purpose is to render a means series. :)
		if (seriesNum != meansSeriesNum) {
			return super.changeSeriesVisibility(seriesNum, visibility);
		} else {
			return false;
		}
	}

	/**
	 * Which series' elements should be joined visually (e.g. with lines)?
	 * 
	 * @return A collection of series numbers for series whose elements should
	 *         be joined visually.
	 */
	public Collection<Integer> getSeriesWhoseElementsShouldBeJoinedVisually() {
		List<Integer> seriesNumList = new ArrayList<Integer>();

		for (Map.Entry<Integer, SeriesType> entry : this.seriesNumToSrcTypeMap
				.entrySet()) {
			if (SeriesType.MEANS == entry.getValue()) {
				seriesNumList.add(entry.getKey());
				break;
			}
		}

		return seriesNumList;
	}

	/**
	 * Return the error associated with the magnitude. We skip the series and
	 * item legality check to improve performance on the assumption that this
	 * has been checked already when calling getMagAsYCoord(). So this is a
	 * precondition of calling the current function.
	 * 
	 * @param series
	 *            The series number.
	 * @param item
	 *            The item number within the series.
	 * @return The error value associated with the mean.
	 */
	protected double getMagError(int series, int item) {
		if (series != this.meansSeriesNum) {
			// The series is something other than the means series
			// so just default to the superclass behaviour.
			return super.getMagError(series, item);
		} else {
			// For the means series, we store the mean magnitude error
			// value as the magnitude's uncertainty. TODO: change this?
			return this.seriesNumToObSrcListMap.get(series).get(item)
					.getMagnitude().getUncertainty();
		}
	}

	/**
	 * @return the meanSourceSeriesNum
	 */
	public int getMeanSourceSeriesNum() {
		return meanSourceSeriesNum;
	}

	/**
	 * @param meanSourceSeriesNum
	 *            the meanSourceSeriesNum to set
	 */
	public void setMeanSourceSeriesNum(int meanSourceSeriesNum) {
		this.meanSourceSeriesNum = meanSourceSeriesNum;
	}

	/**
	 * @return the means series number
	 */
	public int getMeansSeriesNum() {
		return meansSeriesNum;
	}

	/**
	 * @return the timeElementsInBin
	 */
	public double getTimeElementsInBin() {
		return timeElementsInBin;
	}

	/**
	 * @param timeElementsInBin
	 *            the timeElementsInBin to set
	 */
	public void setTimeElementsInBin(double timeElementsInBin) {
		this.timeElementsInBin = timeElementsInBin;
	}

	/**
	 * @return the meanObsList
	 */
	public List<ValidObservation> getMeanObsList() {
		return meanObsList;
	}

	/**
	 * @return the meansChangeNotifier
	 */
	public Notifier<List<ValidObservation>> getMeansChangeNotifier() {
		return meansChangeNotifier;
	}

	/**
	 * Listen for valid observation change notification, e.g. an observation is
	 * marked as discrepant. Since a discrepant observation is ignored for
	 * statistical analysis purposes (see DescStats class), we need to
	 * re-calculate the means series.
	 */
	public void update(ValidObservation ob) {
		setMeanSeries();
	}

	// Helper methods.

	/**
	 * Determine which series will be the source of the mean series. Note that
	 * this may be changed subsequently. Visual bands are highest priority, and
	 * if not found, the first band will be chosen at random. TODO: should this
	 * be refined?
	 * 
	 * @return The series number on which to base the mean series.
	 */
	private int determineMeanSeriesSource() {
		int seriesNum = -1;

		// Look for Visual, then V.

		for (SeriesType series : srcTypeToSeriesNumMap.keySet()) {
			if (series == SeriesType.Visual) {
				// Visual band
				seriesNum = srcTypeToSeriesNumMap.get(series);
				break;
			}
		}

		if (seriesNum == -1) {
			for (SeriesType series : srcTypeToSeriesNumMap.keySet()) {
				if (series == SeriesType.Johnson_V) {
					// Johnson V band
					seriesNum = srcTypeToSeriesNumMap.get(series);
					break;
				}
			}
		}

		// No match: choose some series other than "fainter than".
		if (seriesNum == -1) {
			for (SeriesType series : srcTypeToSeriesNumMap.keySet()) {
				if (series != SeriesType.FAINTER_THAN) {
					seriesNum = srcTypeToSeriesNumMap.get(series);
					break;
				}
			}
		}

		assert seriesNum != -1;

		return seriesNum;
	}
}
