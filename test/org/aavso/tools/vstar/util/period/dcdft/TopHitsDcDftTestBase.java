/**
 * VStar: a statistical analysis tool for variable star data.
 * Copyright (C) 2010  AAVSO (http://www.aavso.org/)
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
import java.util.Map;

import junit.framework.TestCase;

import org.aavso.tools.vstar.data.DateInfo;
import org.aavso.tools.vstar.data.Magnitude;
import org.aavso.tools.vstar.data.ValidObservation;
import org.aavso.tools.vstar.util.period.PeriodAnalysisCoordinateType;

/**
 * DC DFT top hits unit test.
 */
public class TopHitsDcDftTestBase extends TestCase {

	protected List<ValidObservation> obs;

	public TopHitsDcDftTestBase(String name, double[][] jdAndMagPairs) {
		super(name);
		this.obs = new ArrayList<ValidObservation>();
		for (double[] jdAndMag : jdAndMagPairs) {
			ValidObservation ob = new ValidObservation();
			ob.setDateInfo(new DateInfo(jdAndMag[0]));
			ob.setMagnitude(new Magnitude(jdAndMag[1], 0));
			obs.add(ob);
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		assert !obs.isEmpty();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testDcDftTopHits(double[] expectedPeriods,
			double[] expectedPowers, double loFreq, double hiFreq,
			double resolution) {
		DateCompensatedDiscreteFourierTransform dcdft = new DateCompensatedDiscreteFourierTransform(
				obs, loFreq, hiFreq, resolution);
		commonTest(dcdft, expectedPeriods, expectedPowers);
	}
	
	public void testDcDftTopHits(double[] expectedPeriods,
			double[] expectedPowers) {
		DateCompensatedDiscreteFourierTransform dcdft = new DateCompensatedDiscreteFourierTransform(
				obs);
		commonTest(dcdft, expectedPeriods, expectedPowers);
	}
	
	protected void commonTest(DateCompensatedDiscreteFourierTransform dcdft, double[] expectedPeriods,
			double[] expectedPowers) {
		
		dcdft.execute();

		// Check the expected periods and powers against the top hits.
		Map<PeriodAnalysisCoordinateType, List<Double>> topHits = dcdft
				.getTopHits();
		List<Double> topHitPeriods = topHits
				.get(PeriodAnalysisCoordinateType.PERIOD);
		List<Double> topHitPowers = topHits
				.get(PeriodAnalysisCoordinateType.POWER);

		for (int i = 0; i < expectedPeriods.length; i++) {
			double expectedPeriod = expectedPeriods[i];
			double actualPeriod = topHitPeriods.get(i);
			assertEquals(String.format("%1.4f", expectedPeriod), String.format(
					"%1.4f", actualPeriod));

			double expectedPower = expectedPowers[i];
			double actualPower = topHitPowers.get(i);
			assertEquals(String.format("%1.2f", expectedPower), String.format(
					"%1.2f", actualPower));
		}
	}
}
