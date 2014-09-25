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
package org.aavso.tools.vstar.util.stats.anova;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.aavso.tools.vstar.data.DateInfo;
import org.aavso.tools.vstar.data.Magnitude;
import org.aavso.tools.vstar.data.ValidObservation;
import org.aavso.tools.vstar.ui.model.plot.JDTimeElementEntity;
import org.aavso.tools.vstar.util.stats.BinningResult;
import org.aavso.tools.vstar.util.stats.DescStats;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.inference.OneWayAnova;
import org.apache.commons.math.stat.inference.OneWayAnovaImpl;

/**
 * A test of the Apache Commons Math One-way ANOVA functions with known data.
 * 
 * The data is the same as the example used in Grant Foster's "Analyzing Light
 * Curves" for Eps Aur, section 4.2.
 * 
 * With the same data and a bin size of 10, here is the result obtained with the
 * R statistical language (showing R code and results):
 * 
 * epsaur = read.table("eps_aur_vis_2454700_2455000_jd_mag.txt")<br/>
 * x = epsaur[,2]<br/>
 * t = epsaur[,1]<br/>
 * bin = floor(t/10)<br/>
 * bin = as.factor(bin)<br/>
 * xmodel = lm(x ~ bin)<br/>
 * anova(xmodel)<br/>
 * 
 * Analysis of Variance Table
 * 
 * Response: x Df Sum Sq Mean Sq F value Pr(>F)<br/>
 * bin 28 1.1433 0.040830 2.8675 1.306e-06 ***<br/>
 * Residuals 910 12.9576 0.014239<br/>
 * ---<br/>
 * 
 * Using Grant Foster's anova1 function:<br/>
 * 
 * anova1(x, floor(t/10))<br/>
 *$bylevel<br/>
 * level count ave std.dev se t.val<br/>
 *1 245470 16 3.112500 0.12179217 0.030448043 1.15<br/>
 *2 245471 19 3.078947 0.07873265 0.018062509 0.09<br/>
 *3 245472 13 3.138462 0.11208971 0.031088091 1.96<br/>
 *4 245473 19 3.081053 0.10692130 0.024529428 0.15<br/>
 *5 245474 28 3.110714 0.09164863 0.017319963 1.92<br/>
 *6 245475 27 3.090741 0.09097446 0.017508044 0.76<br/>
 *7 245476 32 3.071875 0.08513509 0.015049900 -0.37<br/>
 *8 245477 32 3.098438 0.07012014 0.012395607 1.70<br/>
 *9 245478 26 3.063462 0.07688653 0.015078689 -0.92<br/>
 *10 245479 25 3.050000 0.10206207 0.020412415 -1.34<br/>
 *11 245480 28 3.084286 0.04059087 0.007670954 0.90<br/>
 *12 245481 19 3.057895 0.11088427 0.025438596 -0.77<br/>
 *13 245482 32 3.035625 0.12181259 0.021533627 -1.94<br/>
 *14 245483 37 3.089189 0.12141248 0.019960089 0.59<br/>
 *15 245484 43 3.065349 0.09981766 0.015222051 -0.79<br/>
 *16 245485 35 3.070000 0.09251391 0.015637705 -0.47<br/>
 *17 245486 47 3.056383 0.10663684 0.015554582 -1.35<br/>
 *18 245487 36 3.073889 0.09842409 0.016404015 -0.21<br/>
 *19 245488 43 3.071860 0.09056058 0.013810359 -0.40<br/>
 *20 245489 32 3.096250 0.11166627 0.019739993 0.96<br/>
 *21 245490 60 3.112500 0.11222896 0.014488696 2.42<br/>
 *22 245491 45 3.119778 0.13009592 0.019393555 2.19<br/>
 *23 245492 48 3.101771 0.11968705 0.017275337 1.41<br/>
 *24 245493 69 3.110870 0.22060571 0.026557807 1.26<br/>
 *25 245494 51 3.063725 0.12770217 0.017881880 -0.76<br/>
 *26 245495 34 3.029412 0.15769055 0.027043707 -1.77<br/>
 *27 245496 29 2.974138 0.10907131 0.020254035 -5.10<br/>
 *28 245497 10 2.940000 0.06992059 0.022110832 -6.21<br/>
 *29 245498 4 2.975000 0.05000000 0.025000000 -4.09<br/>
 * 
 *$grandave<br/>
 *[1] 3.077375<br/>
 * 
 *$Ftest<br/>
 * Fstat df.between df.within p<br/>
 *1 2.867485 28 910 1e-06<br/>
 */
public class EpsAurVisJD2454700ToJD2455000AnovaTest extends TestCase {

	private double[][] jdMagData = { { 2454700.8778, 3.4 },
			{ 2454702.96736, 3.1 }, { 2454703.93403, 2.9 },
			{ 2454703.93681, 2.9 }, { 2454704.69097, 3.2 },
			{ 2454705.54722, 3.05 }, { 2454705.58958, 3.0 },
			{ 2454705.68819, 3.2 }, { 2454706.68958, 3.2 }, { 2454707.0, 3.1 },
			{ 2454707.68125, 3.2 }, { 2454708.00347, 3.1 },
			{ 2454708.56042, 3.15 }, { 2454708.67986, 3.1 },
			{ 2454709.00694, 3.1 }, { 2454709.67361, 3.1 },
			{ 2454710.58681, 3.1 }, { 2454710.71528, 3.1 },
			{ 2454711.71389, 3.1 }, { 2454712.00139, 3.0 },
			{ 2454712.525, 3.1 }, { 2454712.70833, 3.1 },
			{ 2454713.59653, 3.1 }, { 2454713.71528, 3.1 },
			{ 2454715.00347, 3.1 }, { 2454715.00347, 2.8 },
			{ 2454716.5125, 3.1 }, { 2454716.7, 3.2 }, { 2454717.00417, 3.1 },
			{ 2454717.68403, 3.1 }, { 2454717.74583, 3.1 },
			{ 2454718.00486, 3.1 }, { 2454719.0, 3.1 }, { 2454719.4583, 3.0 },
			{ 2454719.67014, 3.1 }, { 2454720.00486, 3.1 },
			{ 2454720.53472, 3.1 }, { 2454720.7625, 3.1 },
			{ 2454721.00139, 3.0 }, { 2454723.67014, 3.2 },
			{ 2454725.64306, 3.1 }, { 2454725.70833, 3.1 },
			{ 2454726.68819, 3.2 }, { 2454727.00139, 3.0 },
			{ 2454727.74028, 3.4 }, { 2454728.75, 3.1 }, { 2454728.8667, 3.3 },
			{ 2454729.69236, 3.1 }, { 2454730.75694, 3.1 }, { 2454733.0, 3.1 },
			{ 2454733.00139, 3.0 }, { 2454734.00208, 2.8 },
			{ 2454734.95833, 3.2 }, { 2454735.34653, 3.3 },
			{ 2454736.68333, 3.1 }, { 2454736.75, 3.1 },
			{ 2454737.50139, 3.05 }, { 2454737.573, 3.1 },
			{ 2454737.60069, 3.0 }, { 2454737.67014, 3.1 },
			{ 2454737.75694, 3.1 }, { 2454737.91736, 2.9 },
			{ 2454738.0097, 3.1 }, { 2454738.37361, 3.09 },
			{ 2454738.458, 3.1 }, { 2454738.68611, 3.2 },
			{ 2454738.71528, 3.1 }, { 2454740.0125, 3.1 },
			{ 2454740.66667, 3.1 }, { 2454740.975, 3.0 },
			{ 2454741.48611, 3.1 }, { 2454741.6875, 3.1 },
			{ 2454742.69583, 3.1 }, { 2454743.02083, 3.1 },
			{ 2454743.48472, 3.1 }, { 2454743.48681, 3.2 },
			{ 2454743.70208, 3.2 }, { 2454744.71181, 3.1 },
			{ 2454744.97917, 3.1 }, { 2454745.351, 3.2 },
			{ 2454745.48958, 3.1 }, { 2454745.79861, 3.0 },
			{ 2454746.00694, 3.1 }, { 2454746.98333, 3.1 },
			{ 2454747.00694, 3.1 }, { 2454747.00764, 3.0 },
			{ 2454747.69653, 3.1 }, { 2454747.9236, 3.4 },
			{ 2454748.7125, 3.2 }, { 2454748.88542, 3.1 },
			{ 2454749.0125, 3.0 }, { 2454749.65069, 3.1 },
			{ 2454749.69097, 3.2 }, { 2454749.91528, 2.9 },
			{ 2454749.95833, 3.2 }, { 2454750.00694, 3.1 },
			{ 2454750.48264, 3.05 }, { 2454750.67847, 3.15 },
			{ 2454750.92847, 3.1 }, { 2454751.00694, 3.1 },
			{ 2454751.61111, 3.1 }, { 2454751.8917, 3.1 },
			{ 2454751.96875, 3.1 }, { 2454753.01319, 3.0 },
			{ 2454753.1016, 3.1 }, { 2454753.36806, 2.95 },
			{ 2454753.99653, 3.1 }, { 2454754.01458, 3.0 },
			{ 2454754.90278, 3.1 }, { 2454755.01042, 2.9 }, { 2454755.6, 3.0 },
			{ 2454755.875, 3.1 }, { 2454756.66597, 3.2 },
			{ 2454756.9193, 3.3 }, { 2454757.00694, 3.1 },
			{ 2454758.00347, 3.1 }, { 2454758.45139, 3.0 },
			{ 2454758.64861, 3.1 }, { 2454758.69236, 3.2 },
			{ 2454758.8965, 3.0 }, { 2454759.61042, 3.1 },
			{ 2454759.68264, 3.3 }, { 2454760.01042, 3.1 },
			{ 2454760.31597, 3.2 }, { 2454760.431, 3.1 },
			{ 2454760.60139, 3.0 }, { 2454760.71875, 3.1 },
			{ 2454760.77431, 3.1 }, { 2454761.43889, 3.0 },
			{ 2454762.00694, 3.1 }, { 2454762.4045, 3.0 },
			{ 2454762.5521, 2.8 }, { 2454762.8917, 3.0 },
			{ 2454763.01389, 3.1 }, { 2454763.34167, 3.1 },
			{ 2454763.63333, 3.1 }, { 2454763.77083, 3.1 },
			{ 2454764.00972, 3.1 }, { 2454764.72431, 3.0 },
			{ 2454764.93611, 3.0 }, { 2454765.00694, 3.1 },
			{ 2454765.68403, 3.2 }, { 2454765.89028, 3.0 },
			{ 2454766.00694, 3.1 }, { 2454766.39236, 3.1 }, { 2454766.6, 3.0 },
			{ 2454766.88264, 3.1 }, { 2454766.8979, 3.0 },
			{ 2454766.99583, 3.1 }, { 2454767.93681, 3.1 },
			{ 2454768.00694, 3.1 }, { 2454768.01042, 3.3 },
			{ 2454769.00486, 3.0 }, { 2454769.00694, 3.1 },
			{ 2454770.00694, 3.1 }, { 2454770.59028, 3.1 },
			{ 2454770.73472, 3.15 }, { 2454770.98403, 3.0 },
			{ 2454771.00694, 3.1 }, { 2454771.62917, 3.0 },
			{ 2454771.757, 3.0 }, { 2454771.9375, 3.1 },
			{ 2454772.00694, 3.1 }, { 2454772.52569, 3.1 },
			{ 2454772.8972, 3.0 }, { 2454772.9028, 3.1 },
			{ 2454773.70833, 3.1 }, { 2454774.3, 3.0 }, { 2454774.451, 3.2 },
			{ 2454774.54236, 3.1 }, { 2454774.7361, 3.1 },
			{ 2454774.73611, 3.1 }, { 2454775.89583, 3.1 },
			{ 2454776.333, 3.1 }, { 2454776.60764, 3.3 },
			{ 2454776.96528, 3.1 }, { 2454777.00694, 3.1 },
			{ 2454777.05347, 3.1 }, { 2454777.74028, 3.1 },
			{ 2454778.00694, 3.1 }, { 2454778.48819, 3.1 },
			{ 2454778.63125, 3.1 }, { 2454778.9139, 3.1 },
			{ 2454779.00694, 3.1 }, { 2454779.55764, 3.3 },
			{ 2454779.94028, 3.0 }, { 2454780.0, 3.1 }, { 2454780.33125, 3.0 },
			{ 2454780.4625, 3.1 }, { 2454780.93056, 3.1 },
			{ 2454781.05556, 3.1 }, { 2454781.56597, 3.1 },
			{ 2454781.65972, 3.1 }, { 2454782.3, 2.95 }, { 2454782.3208, 2.8 },
			{ 2454782.63194, 3.1 }, { 2454782.9729, 3.2 },
			{ 2454783.00694, 3.1 }, { 2454784.00694, 3.1 },
			{ 2454784.6799, 3.1 }, { 2454785.66528, 3.1 },
			{ 2454786.2125, 3.1 }, { 2454786.83264, 3.1 },
			{ 2454786.9493, 3.1 }, { 2454787.52639, 3.1 },
			{ 2454787.6208, 3.0 }, { 2454788.2111, 3.1 }, { 2454788.292, 3.1 },
			{ 2454788.3405, 3.0 }, { 2454788.50556, 3.0 },
			{ 2454788.59097, 3.0 }, { 2454789.5229, 3.0 },
			{ 2454790.05556, 3.1 }, { 2454790.37153, 3.1 }, { 2454790.5, 2.8 },
			{ 2454791.63194, 3.1 }, { 2454792.00694, 3.1 },
			{ 2454792.61319, 3.0 }, { 2454792.625, 3.0 },
			{ 2454792.8389, 3.1 }, { 2454793.00694, 3.1 }, { 2454793.2, 2.95 },
			{ 2454793.67708, 2.9 }, { 2454793.69306, 3.1 },
			{ 2454794.01042, 3.1 }, { 2454794.4167, 2.8 },
			{ 2454795.31042, 3.2 }, { 2454797.34167, 3.0 },
			{ 2454797.58333, 3.0 }, { 2454798.29236, 3.1 }, { 2454798.3, 3.1 },
			{ 2454798.6563, 3.1 }, { 2454798.69444, 3.0 },
			{ 2454799.01042, 3.1 }, { 2454799.2806, 3.2 },
			{ 2454799.37222, 3.1 }, { 2454799.57431, 3.1 },
			{ 2454800.095, 3.0 }, { 2454800.1944, 3.1 }, { 2454800.4375, 3.0 },
			{ 2454800.71042, 3.1 }, { 2454801.01389, 3.1 },
			{ 2454801.2049, 3.1 }, { 2454801.3, 3.1 }, { 2454801.3819, 3.11 },
			{ 2454802.00694, 3.1 }, { 2454802.3792, 3.1 },
			{ 2454802.59931, 3.1 }, { 2454802.6792, 3.1 },
			{ 2454803.2208, 3.12 }, { 2454803.6375, 3.0 },
			{ 2454803.6771, 3.1 }, { 2454804.00694, 3.1 },
			{ 2454805.00694, 3.1 }, { 2454805.8465, 3.1 },
			{ 2454805.9563, 3.0 }, { 2454806.51042, 3.1 },
			{ 2454806.63194, 3.1 }, { 2454806.75347, 3.0 },
			{ 2454807.531, 3.1 }, { 2454808.2847, 3.13 },
			{ 2454808.51458, 3.1 }, { 2454809.01389, 3.1 },
			{ 2454809.64583, 3.1 }, { 2454809.6771, 3.1 },
			{ 2454810.25972, 3.0 }, { 2454810.3, 2.95 }, { 2454810.3125, 3.0 },
			{ 2454811.54236, 3.1 }, { 2454811.8986, 3.3 },
			{ 2454813.6132, 2.9 }, { 2454814.45694, 2.95 },
			{ 2454814.625, 3.1 }, { 2454815.1882, 3.1 },
			{ 2454815.63542, 3.1 }, { 2454815.6771, 3.1 },
			{ 2454816.1847, 3.1 }, { 2454816.23889, 3.0 },
			{ 2454816.37222, 3.0 }, { 2454816.4757, 3.3 },
			{ 2454816.63889, 3.1 }, { 2454818.35694, 2.9 },
			{ 2454819.60417, 3.0 }, { 2454819.72847, 3.1 },
			{ 2454820.31944, 3.0 }, { 2454820.406, 3.1 },
			{ 2454820.75903, 2.8 }, { 2454821.48958, 3.0 },
			{ 2454821.65347, 2.9 }, { 2454822.22, 3.1 },
			{ 2454822.43264, 3.0 }, { 2454822.5, 3.0 }, { 2454822.67361, 3.1 },
			{ 2454823.1882, 3.1 }, { 2454824.19097, 3.0 }, { 2454824.2, 2.95 },
			{ 2454825.33056, 3.0 }, { 2454825.597, 3.0 },
			{ 2454825.63333, 3.1 }, { 2454825.734, 3.1 },
			{ 2454826.27917, 3.3 }, { 2454826.60417, 3.1 },
			{ 2454827.2674, 3.09 }, { 2454827.2917, 3.0 },
			{ 2454827.35625, 3.0 }, { 2454827.4688, 3.1 }, { 2454828.28, 3.1 },
			{ 2454828.3333, 2.6 }, { 2454828.4236, 3.2 },
			{ 2454828.4722, 2.9 }, { 2454828.52639, 3.1 },
			{ 2454828.60417, 3.1 }, { 2454829.2083, 3.1 },
			{ 2454829.4896, 3.0 }, { 2454829.5625, 3.1 },
			{ 2454829.63125, 3.1 }, { 2454830.2375, 3.1 },
			{ 2454830.28125, 3.0 }, { 2454830.72639, 2.7 },
			{ 2454830.79306, 3.1 }, { 2454830.8799, 3.3 },
			{ 2454831.1993, 3.1 }, { 2454831.27222, 3.0 },
			{ 2454831.52083, 3.1 }, { 2454831.56597, 3.1 },
			{ 2454831.5674, 3.2 }, { 2454831.72569, 3.0 },
			{ 2454832.1931, 3.1 }, { 2454832.53681, 3.1 },
			{ 2454832.8375, 3.1 }, { 2454833.5486, 3.3 },
			{ 2454834.32986, 3.0 }, { 2454834.51389, 3.0 },
			{ 2454834.616, 3.3 }, { 2454834.67847, 3.1 },
			{ 2454835.1924, 3.1 }, { 2454835.2625, 3.0 },
			{ 2454835.4688, 3.2 }, { 2454835.4833, 3.25 }, { 2454835.54, 3.0 },
			{ 2454835.56042, 3.1 }, { 2454835.73472, 3.1 },
			{ 2454836.5118, 2.9 }, { 2454836.56667, 3.1 },
			{ 2454836.67014, 3.0 }, { 2454837.4722, 3.25 },
			{ 2454837.5375, 3.0 }, { 2454837.54, 3.1 }, { 2454838.13958, 3.0 },
			{ 2454838.1931, 3.1 }, { 2454838.3542, 3.3 }, { 2454838.675, 3.1 },
			{ 2454839.42014, 3.0 }, { 2454840.1979, 3.1 },
			{ 2454840.4722, 3.3 }, { 2454840.5799, 3.0 },
			{ 2454840.6736, 3.1 }, { 2454842.1944, 3.1 }, { 2454842.625, 3.1 },
			{ 2454843.191, 3.1 }, { 2454843.26007, 3.0 },
			{ 2454843.275, 3.11 }, { 2454843.29861, 3.1 },
			{ 2454843.6611, 3.0 }, { 2454843.675, 3.1 }, { 2454844.1979, 3.1 },
			{ 2454844.23194, 3.0 }, { 2454844.3, 3.1 }, { 2454844.36181, 3.1 },
			{ 2454844.4757, 3.1 }, { 2454844.58611, 3.0 },
			{ 2454845.19792, 3.1 }, { 2454845.2014, 3.1 },
			{ 2454845.5083, 3.1 }, { 2454845.61458, 3.1 },
			{ 2454845.63056, 3.1 }, { 2454845.6792, 3.1 },
			{ 2454846.60417, 3.1 }, { 2454846.6854, 2.9 },
			{ 2454847.29444, 3.1 }, { 2454847.36806, 3.0 },
			{ 2454847.3799, 3.1 }, { 2454847.4743, 3.3 },
			{ 2454847.60417, 3.1 }, { 2454847.68125, 3.1 },
			{ 2454847.7181, 2.8 }, { 2454847.7715, 3.0 },
			{ 2454848.2069, 3.1 }, { 2454848.3993, 3.0 }, { 2454848.431, 3.1 },
			{ 2454848.5799, 2.7 }, { 2454848.625, 3.1 },
			{ 2454848.67153, 3.0 }, { 2454848.77361, 3.1 },
			{ 2454849.3102, 3.0 }, { 2454849.625, 3.1 },
			{ 2454850.26111, 3.2 }, { 2454850.375, 3.0 },
			{ 2454850.55556, 3.0 }, { 2454850.6688, 3.1 },
			{ 2454851.45903, 3.1 }, { 2454851.5125, 2.9 },
			{ 2454852.23472, 3.1 }, { 2454852.3389, 3.0 },
			{ 2454852.4757, 3.2 }, { 2454852.49167, 3.1 },
			{ 2454852.58264, 3.1 }, { 2454852.60417, 3.1 },
			{ 2454852.6542, 3.2 }, { 2454853.534, 3.0 }, { 2454853.6563, 3.1 },
			{ 2454853.7, 3.0 }, { 2454854.2333, 3.1 }, { 2454854.6771, 3.1 },
			{ 2454855.33333, 3.1 }, { 2454855.5, 3.0 }, { 2454855.5861, 2.9 },
			{ 2454855.77847, 3.1 }, { 2454856.375, 3.2 },
			{ 2454856.4826, 3.25 }, { 2454856.52847, 3.0 },
			{ 2454856.58194, 3.1 }, { 2454856.64583, 3.1 },
			{ 2454856.6931, 2.9 }, { 2454857.2326, 3.1 }, { 2454857.3, 3.1 },
			{ 2454857.384, 2.9 }, { 2454857.46181, 3.0 }, { 2454857.7, 3.0 },
			{ 2454858.3785, 3.2 }, { 2454858.85417, 3.1 },
			{ 2454860.3806, 3.1 }, { 2454860.5153, 3.0 },
			{ 2454860.54167, 3.0 }, { 2454860.60417, 3.1 },
			{ 2454860.68056, 2.9 }, { 2454861.3701, 3.1 },
			{ 2454861.37083, 3.1 }, { 2454861.47222, 3.1 },
			{ 2454861.5167, 3.0 }, { 2454861.58403, 3.0 },
			{ 2454861.625, 3.1 }, { 2454862.3124, 3.0 }, { 2454862.3993, 2.9 },
			{ 2454862.5347, 3.0 }, { 2454862.655, 3.0 },
			{ 2454862.70972, 3.1 }, { 2454863.27431, 3.0 },
			{ 2454863.3464, 3.0 }, { 2454863.5444, 3.0 },
			{ 2454863.7701, 2.9 }, { 2454864.33889, 3.1 },
			{ 2454864.5132, 3.4 }, { 2454864.5479, 3.1 },
			{ 2454864.6563, 3.1 }, { 2454864.73889, 3.1 },
			{ 2454865.4125, 3.2 }, { 2454865.491, 3.35 },
			{ 2454865.50694, 3.0 }, { 2454865.6181, 3.1 },
			{ 2454865.6333, 2.9 }, { 2454865.68056, 3.1 },
			{ 2454866.21181, 2.9 }, { 2454866.2188, 3.1 }, { 2454866.3, 2.9 },
			{ 2454866.625, 3.1 }, { 2454866.666, 3.2 }, { 2454867.2, 3.0 },
			{ 2454867.2201, 3.1 }, { 2454867.3202, 3.0 },
			{ 2454867.5625, 3.0 }, { 2454867.61111, 3.1 },
			{ 2454867.6472, 3.0 }, { 2454867.67222, 3.0 },
			{ 2454868.61667, 3.2 }, { 2454868.6264, 3.2 },
			{ 2454868.64028, 3.0 }, { 2454869.61736, 3.0 },
			{ 2454870.4917, 3.1 }, { 2454870.5479, 2.9 },
			{ 2454871.5063, 3.4 }, { 2454871.50694, 3.1 },
			{ 2454871.5563, 3.1 }, { 2454872.2729, 3.1 }, { 2454872.34, 3.0 },
			{ 2454872.47917, 3.1 }, { 2454872.6875, 3.0 }, { 2454873.2, 3.0 },
			{ 2454873.492, 3.2 }, { 2454873.5639, 3.1 },
			{ 2454873.59028, 3.1 }, { 2454874.2396, 3.2 },
			{ 2454874.3417, 3.1 }, { 2454874.58681, 3.1 },
			{ 2454874.8534, 3.0 }, { 2454875.2743, 3.16 },
			{ 2454875.3704, 3.1 }, { 2454875.6076, 3.2 },
			{ 2454876.3012, 3.0 }, { 2454876.57222, 3.0 },
			{ 2454876.59722, 3.1 }, { 2454877.3201, 3.0 },
			{ 2454877.384, 2.9 }, { 2454877.5306, 3.2 }, { 2454878.2964, 3.0 },
			{ 2454878.31597, 3.0 }, { 2454878.5729, 3.0 },
			{ 2454878.6076, 3.0 }, { 2454879.29167, 3.0 },
			{ 2454879.328, 3.0 }, { 2454879.5194, 3.2 },
			{ 2454879.57083, 3.0 }, { 2454879.61806, 3.1 },
			{ 2454879.625, 3.1 }, { 2454880.2813, 2.9 }, { 2454880.328, 3.0 },
			{ 2454880.48542, 3.1 }, { 2454880.60417, 3.1 },
			{ 2454880.64583, 3.0 }, { 2454881.318, 3.0 },
			{ 2454881.3799, 3.18 }, { 2454881.58333, 3.1 },
			{ 2454881.6875, 3.0 }, { 2454882.23472, 3.1 },
			{ 2454882.317, 3.0 }, { 2454882.4104, 3.21 },
			{ 2454882.60417, 3.1 }, { 2454882.6111, 3.2 },
			{ 2454882.6465, 3.0 }, { 2454882.6549, 2.8 },
			{ 2454882.74653, 3.0 }, { 2454883.26181, 3.1 },
			{ 2454883.327, 3.0 }, { 2454883.60417, 3.1 },
			{ 2454883.65833, 3.2 }, { 2454884.317, 3.0 }, { 2454884.625, 3.1 },
			{ 2454884.7917, 3.2 }, { 2454885.1, 3.2 }, { 2454885.327, 3.0 },
			{ 2454885.38194, 3.0 }, { 2454885.5583, 3.2 },
			{ 2454885.61111, 3.1 }, { 2454886.317, 3.0 },
			{ 2454886.60417, 3.1 }, { 2454887.315, 3.1 },
			{ 2454887.3958, 3.2 }, { 2454887.61458, 3.1 },
			{ 2454888.31597, 3.0 }, { 2454888.325, 3.1 },
			{ 2454888.5611, 3.0 }, { 2454888.6632, 3.0 },
			{ 2454888.7319, 3.2 }, { 2454889.22014, 3.0 },
			{ 2454889.3111, 3.1 }, { 2454889.322, 3.1 },
			{ 2454889.59444, 3.1 }, { 2454890.312, 3.1 },
			{ 2454890.3326, 3.21 }, { 2454890.60417, 3.1 },
			{ 2454890.6299, 3.1 }, { 2454891.25278, 3.1 },
			{ 2454891.328, 3.1 }, { 2454891.5694, 3.2 },
			{ 2454891.59167, 3.0 }, { 2454891.69236, 3.1 },
			{ 2454892.2708, 3.2 }, { 2454892.34722, 3.1 },
			{ 2454892.541, 3.2 }, { 2454892.60417, 3.1 },
			{ 2454892.66736, 3.0 }, { 2454893.3, 2.9 }, { 2454893.35764, 3.0 },
			{ 2454893.4979, 2.9 }, { 2454893.5319, 3.2 }, { 2454893.601, 3.1 },
			{ 2454894.1, 3.3 }, { 2454894.5, 3.3 }, { 2454894.60417, 3.1 },
			{ 2454895.6201, 3.0 }, { 2454896.7132, 3.2 },
			{ 2454898.24653, 3.0 }, { 2454898.3, 3.17 }, { 2454898.3301, 3.2 },
			{ 2454898.38194, 3.1 }, { 2454898.83333, 3.0 }, { 2454899.3, 2.9 },
			{ 2454899.3401, 3.2 }, { 2454899.6326, 2.9 },
			{ 2454900.32361, 3.1 }, { 2454900.3381, 3.2 },
			{ 2454900.36806, 3.0 }, { 2454900.60417, 3.1 },
			{ 2454900.6396, 3.0 }, { 2454901.3361, 3.1 },
			{ 2454901.3481, 3.2 }, { 2454901.60417, 3.1 },
			{ 2454902.3471, 3.2 }, { 2454902.6139, 2.9 },
			{ 2454902.61458, 3.1 }, { 2454903.3571, 3.2 },
			{ 2454903.6951, 3.2 }, { 2454904.25556, 3.1 },
			{ 2454904.3125, 3.0 }, { 2454904.5, 3.4 }, { 2454904.5563, 3.2 },
			{ 2454904.56944, 3.1 }, { 2454904.58472, 3.1 },
			{ 2454904.625, 3.1 }, { 2454904.70833, 3.1 }, { 2454905.0, 3.2 },
			{ 2454905.26806, 3.1 }, { 2454905.3125, 3.15 },
			{ 2454905.52153, 3.0 }, { 2454905.56, 3.1 }, { 2454905.7993, 2.9 },
			{ 2454906.33472, 3.0 }, { 2454906.3571, 3.2 },
			{ 2454906.54167, 3.1 }, { 2454906.56736, 3.1 },
			{ 2454906.60417, 3.0 }, { 2454906.60417, 3.1 },
			{ 2454907.22917, 3.2 }, { 2454907.3547, 3.2 },
			{ 2454907.3958, 3.0 }, { 2454907.42361, 3.0 },
			{ 2454907.448, 3.2 }, { 2454907.5688, 3.1 },
			{ 2454907.72222, 3.2 }, { 2454908.2743, 3.3 }, { 2454908.3, 2.9 },
			{ 2454908.3447, 3.2 }, { 2454908.4, 2.8 }, { 2454908.4, 3.4 },
			{ 2454908.4063, 3.1 }, { 2454908.5063, 3.0 },
			{ 2454908.59028, 3.1 }, { 2454908.5924, 3.2 },
			{ 2454908.63889, 3.2 }, { 2454908.64931, 3.1 }, { 2454909.0, 3.1 },
			{ 2454909.313, 3.2 }, { 2454909.3487, 3.2 },
			{ 2454909.35417, 3.1 }, { 2454909.3611, 3.1 },
			{ 2454909.40625, 3.0 }, { 2454909.64931, 3.0 },
			{ 2454909.67014, 3.1 }, { 2454909.6931, 3.3 },
			{ 2454910.2861, 3.4 }, { 2454910.3, 3.5 }, { 2454910.3056, 3.1 },
			{ 2454910.3587, 3.2 }, { 2454910.4028, 3.15 },
			{ 2454910.6326, 3.0 }, { 2454910.67361, 3.0 },
			{ 2454911.2917, 2.9 }, { 2454911.3487, 3.2 },
			{ 2454911.51806, 3.0 }, { 2454911.56042, 3.1 },
			{ 2454911.58333, 3.1 }, { 2454911.68403, 2.9 },
			{ 2454912.2778, 3.2 }, { 2454912.28264, 3.1 },
			{ 2454912.29167, 3.05 }, { 2454912.3, 3.17 },
			{ 2454912.30556, 2.92 }, { 2454912.313, 3.3 },
			{ 2454912.31806, 3.3 }, { 2454912.3587, 3.2 },
			{ 2454912.551, 3.3 }, { 2454912.641, 3.0 }, { 2454913.0, 3.1 },
			{ 2454913.3587, 3.2 }, { 2454913.54792, 3.1 },
			{ 2454913.56389, 3.0 }, { 2454913.61389, 3.1 },
			{ 2454914.3587, 3.2 }, { 2454914.36806, 3.0 },
			{ 2454914.60417, 3.1 }, { 2454915.3587, 3.2 },
			{ 2454916.32639, 3.0 }, { 2454916.3687, 3.2 },
			{ 2454916.60764, 3.1 }, { 2454916.67708, 3.1 },
			{ 2454917.29514, 3.1 }, { 2454917.6104, 3.0 },
			{ 2454917.66389, 3.0 }, { 2454918.26528, 3.0 }, { 2454918.4, 3.4 },
			{ 2454918.575, 3.1 }, { 2454918.65069, 3.1 },
			{ 2454919.29167, 3.1 }, { 2454919.29861, 3.1 }, { 2454920.3, 3.1 },
			{ 2454920.3153, 3.2 }, { 2454920.5819, 2.9 },
			{ 2454920.67847, 3.0 }, { 2454921.3, 3.0 }, { 2454921.34375, 3.0 },
			{ 2454921.391, 3.0 }, { 2454921.4, 3.4 }, { 2454921.53264, 3.0 },
			{ 2454921.573, 3.1 }, { 2454922.28472, 3.1 }, { 2454922.3, 2.8 },
			{ 2454922.3, 3.2 }, { 2454922.60417, 3.1 }, { 2454923.23, 3.15 },
			{ 2454923.26, 3.15 }, { 2454923.26736, 3.0 }, { 2454923.3, 3.1 },
			{ 2454923.58333, 3.1 }, { 2454923.59028, 3.0 },
			{ 2454923.60417, 3.1 }, { 2454924.29583, 3.0 },
			{ 2454924.32431, 3.005 }, { 2454924.3313, 3.2 },
			{ 2454924.37, 3.1 }, { 2454924.60069, 3.1 },
			{ 2454924.64583, 3.0 }, { 2454925.26, 3.15 }, { 2454925.26, 3.1 },
			{ 2454925.2993, 3.1 }, { 2454925.3, 3.1 }, { 2454925.54861, 3.0 },
			{ 2454926.26, 3.3 }, { 2454926.27, 3.3 }, { 2454926.29653, 2.98 },
			{ 2454926.60417, 3.1 }, { 2454927.0, 3.1 }, { 2454927.28, 3.25 },
			{ 2454927.5243, 3.2 }, { 2454928.28, 3.3 }, { 2454928.3139, 3.0 },
			{ 2454928.55556, 3.1 }, { 2454928.60417, 3.1 },
			{ 2454928.7007, 3.2 }, { 2454929.26, 3.3 }, { 2454929.28, 3.3 },
			{ 2454929.5715, 2.9 }, { 2454929.60625, 3.1 }, { 2454930.26, 3.3 },
			{ 2454930.27, 3.3 }, { 2454930.29167, 3.0 }, { 2454930.4181, 3.1 },
			{ 2454930.6326, 2.9 }, { 2454931.27986, 3.0 }, { 2454931.3, 3.4 },
			{ 2454931.3, 3.3 }, { 2454931.3021, 2.9 }, { 2454932.3, 3.2 },
			{ 2454932.3, 3.3 }, { 2454932.3125, 3.1 }, { 2454932.3396, 3.0 },
			{ 2454932.4, 2.8 }, { 2454932.592, 3.0 }, { 2454932.625, 3.1 },
			{ 2454933.29, 3.2 }, { 2454933.3, 3.2 }, { 2454933.3014, 3.2 },
			{ 2454933.3229, 3.15 }, { 2454933.354, 3.3 }, { 2454933.4, 3.0 },
			{ 2454933.55278, 3.0 }, { 2454933.58333, 3.1 },
			{ 2454933.591, 2.9 }, { 2454933.60417, 2.9 }, { 2454933.625, 3.1 },
			{ 2454934.27, 3.1 }, { 2454934.3, 3.3 }, { 2454934.3, 2.3 },
			{ 2454934.30069, 3.0 }, { 2454934.3014, 3.2 }, { 2454934.31, 3.3 },
			{ 2454934.3368, 3.4 }, { 2454934.38889, 3.2 },
			{ 2454934.64792, 3.1 }, { 2454935.27, 3.25 }, { 2454935.27, 4.25 },
			{ 2454935.27, 3.35 }, { 2454935.3, 3.4 }, { 2454935.30347, 3.0 },
			{ 2454935.3368, 3.1 }, { 2454935.3722, 3.0 },
			{ 2454935.60417, 3.1 }, { 2454936.4, 3.1 }, { 2454936.6, 3.0 },
			{ 2454936.67778, 3.0 }, { 2454937.29, 3.25 },
			{ 2454937.30208, 3.0 }, { 2454937.36111, 3.0 },
			{ 2454937.37361, 3.0 }, { 2454937.39583, 3.0 },
			{ 2454937.54, 3.1 }, { 2454937.55139, 3.05 },
			{ 2454937.5521, 3.35 }, { 2454937.56736, 3.1 },
			{ 2454937.62153, 3.1 }, { 2454937.6243, 2.8 }, { 2454938.29, 3.1 },
			{ 2454938.29, 3.2 }, { 2454938.40278, 3.0 }, { 2454938.56, 3.0 },
			{ 2454938.61944, 3.1 }, { 2454938.68194, 3.0 },
			{ 2454938.7028, 3.0 }, { 2454939.2986, 3.0 },
			{ 2454939.38889, 3.0 }, { 2454939.61458, 3.1 },
			{ 2454939.659, 3.2 }, { 2454940.29514, 3.0 },
			{ 2454940.36806, 2.95 }, { 2454941.3, 3.4 }, { 2454941.3, 3.0 },
			{ 2454941.30972, 2.9 }, { 2454941.3125, 3.0 }, { 2454941.32, 3.2 },
			{ 2454941.3771, 3.0 }, { 2454941.4, 3.0 }, { 2454941.5, 3.3 },
			{ 2454941.61458, 3.1 }, { 2454941.62917, 3.1 },
			{ 2454941.69583, 3.0 }, { 2454942.28, 3.2 }, { 2454942.29, 3.3 },
			{ 2454942.3056, 3.0 }, { 2454942.3472, 3.0 },
			{ 2454942.35417, 3.0 }, { 2454942.61111, 3.1 },
			{ 2454943.29, 3.25 }, { 2454943.29, 3.3 }, { 2454943.5944, 2.9 },
			{ 2454943.6, 3.0 }, { 2454943.69444, 3.1 }, { 2454944.29, 3.2 },
			{ 2454944.32361, 2.9 }, { 2454944.3333, 3.0 },
			{ 2454944.35427, 3.0 }, { 2454944.7, 3.0 }, { 2454945.31944, 3.0 },
			{ 2454945.34427, 3.0 }, { 2454945.63056, 3.1 },
			{ 2454946.29167, 3.0 }, { 2454946.3, 3.1 }, { 2454946.33333, 2.9 },
			{ 2454946.3993, 3.1 }, { 2454946.4, 3.0 }, { 2454946.53819, 3.0 },
			{ 2454946.5785, 3.0 }, { 2454946.595, 2.9 }, { 2454947.29, 3.05 },
			{ 2454947.3125, 2.9 }, { 2454947.344, 3.3 },
			{ 2454947.62569, 3.1 }, { 2454948.29, 3.25 }, { 2454948.31, 3.2 },
			{ 2454948.70972, 3.0 }, { 2454949.26806, 3.0 },
			{ 2454949.3, 3.25 }, { 2454949.3035, 2.9 }, { 2454949.72778, 3.0 },
			{ 2454950.27292, 3.0 }, { 2454950.3, 3.2 }, { 2454950.31806, 2.9 },
			{ 2454950.35417, 2.9 }, { 2454950.70347, 3.0 },
			{ 2454951.29, 3.15 }, { 2454951.3, 3.15 }, { 2454951.34722, 3.0 },
			{ 2454951.55486, 3.0 }, { 2454952.3, 3.4 }, { 2454952.3264, 2.9 },
			{ 2454952.4, 3.1 }, { 2454952.70694, 3.0 }, { 2454953.31, 3.1 },
			{ 2454953.31597, 3.0 }, { 2454953.3417, 3.0 },
			{ 2454953.361, 3.0 }, { 2454953.71528, 2.9 },
			{ 2454954.27222, 2.9 }, { 2454954.32986, 2.9 },
			{ 2454954.3368, 3.4 }, { 2454954.367, 3.0 },
			{ 2454954.74306, 2.9 }, { 2454955.295, 3.15 }, { 2454955.3, 3.15 },
			{ 2454955.377, 3.0 }, { 2454956.27083, 3.0 }, { 2454956.373, 2.9 },
			{ 2454957.36111, 3.0 }, { 2454957.368, 2.9 },
			{ 2454957.6403, 2.6 }, { 2454959.27153, 3.0 }, { 2454959.3, 3.2 },
			{ 2454959.4, 3.3 }, { 2454960.34028, 2.9 }, { 2454960.625, 3.0 },
			{ 2454960.6771, 3.0 }, { 2454961.63194, 3.0 },
			{ 2454961.7083, 3.0 }, { 2454961.73611, 2.9 }, { 2454962.29, 3.1 },
			{ 2454962.34236, 3.2 }, { 2454962.364, 2.9 },
			{ 2454962.5542, 3.0 }, { 2454962.6667, 2.9 }, { 2454963.3, 3.1 },
			{ 2454963.31, 3.1 }, { 2454963.3743, 2.9 }, { 2454963.4, 2.9 },
			{ 2454963.625, 3.0 }, { 2454963.7104, 2.6 },
			{ 2454964.33681, 2.95 }, { 2454964.61944, 3.0 },
			{ 2454965.62014, 3.0 }, { 2454966.364, 2.9 },
			{ 2454966.37153, 2.9 }, { 2454967.3, 3.1 }, { 2454967.364, 2.9 },
			{ 2454968.3333, 2.9 }, { 2454968.364, 3.0 }, { 2454968.3715, 3.0 },
			{ 2454968.57292, 3.0 }, { 2454969.32, 3.1 },
			{ 2454970.36458, 3.0 }, { 2454971.3285, 2.9 },
			{ 2454971.3333, 2.9 }, { 2454973.59514, 3.1 },
			{ 2454974.6611, 2.9 }, { 2454974.73264, 2.9 },
			{ 2454976.38194, 2.9 }, { 2454976.6715, 2.9 },
			{ 2454976.73958, 2.9 }, { 2454978.36458, 3.0 },
			{ 2454981.375, 3.0 }, { 2454984.3569, 3.0 }, { 2454986.3403, 2.9 },
			{ 2454988.3576, 3.0 } };

	public EpsAurVisJD2454700ToJD2455000AnovaTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	// ANOVA test with raw eps aur data.
	public void testRawEpsAurAnova() {
		List<double[]> bins = getBins(10, jdMagData);
		OneWayAnova anova = new OneWayAnovaImpl();

		try {
			double fValue = anova.anovaFValue(bins);
			assertEquals("2.91", String.format("%2.2f", fValue));

			double pValue = anova.anovaPValue(bins);
			assertEquals("0.000002", String.format("%1.6f", pValue));

			boolean rejectNullHypothesis = anova.anovaTest(bins, 0.05);
			assertTrue(rejectNullHypothesis);
		} catch (MathException e) {
			System.err.println(e.getMessage());
			fail();
		} catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
	}

	// ANOVA test with same raw data but using a list of observations
	// and symmetric binning. This is what will be used for actual loaded
	// datasets.
	public void testValidObsEpsAurAnova() {
		List<ValidObservation> obs = new ArrayList<ValidObservation>();

		for (double[] pair : jdMagData) {
			ValidObservation ob = new ValidObservation();
			ob.setDateInfo(new DateInfo(pair[0]));
			ob.setMagnitude(new Magnitude(pair[1], 0));
			obs.add(ob);
		}

		BinningResult result = DescStats.createSymmetricBinnedObservations(obs,
				JDTimeElementEntity.instance, 10);
		
		double fValue = result.getFValue();
		assertEquals("2.87", String.format("%2.2f", fValue));

		double pValue = result.getPValue();
		assertEquals("0.000002", String.format("%1.6f", pValue));
		
		int k = result.getMagnitudeBins().size();
		
		int dfW = result.getWithinGroupDF();
		assertEquals(obs.size()-k, dfW);
		
		int dfB = result.getBetweenGroupDF();
		assertEquals(k-1, dfB);
	}

	// Helpers

	// Returns bins of magnitudes according to the specified days in bin
	// parameter,
	// collected from left to right.
	private List<double[]> getBins(int daysInBin, double[][] data) {
		List<double[]> bins = new ArrayList<double[]>();

		double minJD = data[0][0];

		int minIndex = 0;
		int maxIndex = 0;

		int i = 1;

		while (i < data.length) {
			double currJD = data[i][0];
			if (i < data.length && currJD < minJD + daysInBin) {
				i++;
			} else {
				// Top of current JD range or end of data reached.
				maxIndex = i - 1;
				int size = (maxIndex - minIndex) + 1;
				double[] binData = new double[size];
				for (int j = minIndex; j <= maxIndex; j++) {
					binData[j - minIndex] = data[j][1];
				}

				bins.add(binData);

				minIndex = i;
				minJD = data[i][0];
			}
		}

		return bins;
	}
}
