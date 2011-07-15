/**
 * VStar: a statistical analysis tool for variable star data.
 * Copyright (C) 2011  AAVSO (http://www.aavso.org/)
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
package org.aavso.tools.vstar.util.period.dcdft;

import java.util.ArrayList;
import java.util.List;

import org.aavso.tools.vstar.util.DelCepData;
import org.aavso.tools.vstar.util.model.PeriodFitParameters;
import org.aavso.tools.vstar.util.period.PeriodAnalysisCoordinateType;

/**
 * Multiple-period (two) model creation test.
 * 
 * This test case is equivalent to running a "1: standard scan" from the AAVSO's
 * TS (t1201.f) Fortran program's Fourier analysis menu with the delcep.vis file
 * (containing V and Vis data) sent to me by Matt Templeton (see
 * vstar/data/delcep.vis) in March 2010, followed by a "6: model the data" from
 * the Fourier analysis menu.
 * The expected data below is taken directly (via script: see
 * 
 * script/tsresid2csv.pl) from the residuals output file from a TS run.
 */
public class TwoPeriodModelDcDftTest extends MultiPeriodicModelDcDftTestBase {

	public TwoPeriodModelDcDftTest(String name) {
		super(name, DelCepData.jd_and_mag);
	}

	/**
	 * This test creates a model using two top-hits (periods: 5.3655 and
	 * 2.6828).
	 */
	public void testModel() {
		// Perform a standard scan.
		TSDcDft dcdft = new TSDcDft(obs);
		dcdft.execute();

		// Specify the periods upon which the model is to be based.
		double period1 = dcdft.getTopHits().get(
				PeriodAnalysisCoordinateType.PERIOD).get(0);
		assertEquals("5.3655", String.format("%1.4f", period1));

		double period2 = dcdft.getTopHits().get(
				PeriodAnalysisCoordinateType.PERIOD).get(1);
		assertEquals("2.6828", String.format("%1.4f", period2));

		List<Double> periods = new ArrayList<Double>();
		periods.add(period1);
		periods.add(period2);

		// Specify the expected model parameters (generated by the model
		// creation process).
		List<PeriodFitParameters> expectedParams = new ArrayList<PeriodFitParameters>();
		expectedParams.add(new PeriodFitParameters(0.186375252, 5.3655, 0.1998,
				-0.1018, -0.1720, 3.9249));
		expectedParams.add(new PeriodFitParameters(0.372750505, 2.6828, 0.0767,
				-0.0484, -0.0595, 3.9249));

		// Drum roll please...
		commonTest(dcdft, periods, expectedParams,
				TwoPeriodModelExpectedData.expectedModelData,
				TwoPeriodResidualExpectedData.expectedResidualData);
	}
}