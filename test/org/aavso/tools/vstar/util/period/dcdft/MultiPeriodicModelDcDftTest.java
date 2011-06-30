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

import org.aavso.tools.vstar.data.ValidObservation;
import org.aavso.tools.vstar.util.TCasData;
import org.aavso.tools.vstar.util.model.MultiPeriodicFit;
import org.aavso.tools.vstar.util.model.PeriodFitParameters;
import org.aavso.tools.vstar.util.period.PeriodAnalysisCoordinateType;

/**
 * Multi-periodic model creation test.
 * 
 * These test cases are equivalent to running a "1: standard scan" from the
 * AAVSO's TS (t1201.f) Fortran program's Fourier analysis menu with the
 * tcas.dat file supplied with that program, followed by model the data (option
 * 6 from the Fourier analysis menu).
 */
public class MultiPeriodicModelDcDftTest extends DataTestBase {

	private static final double[][] expectedModelData = {
			{ 47003.5684, 11.3792 }, { 47010.5407, 11.4203 },
			{ 47021.8561, 11.4508 }, { 47029.2709, 11.4462 },
			{ 47038.4757, 11.4135 }, { 47052.2739, 11.3098 },
			{ 47060.5963, 11.2170 }, { 47069.4640, 11.0949 },
			{ 47081.0427, 10.9025 }, { 47089.6299, 10.7387 },
			{ 47098.0038, 10.5643 }, { 47112.0430, 10.2465 },
			{ 47120.9607, 10.0331 }, { 47131.0238, 9.7867 },
			{ 47139.5317, 9.5775 }, { 47150.6185, 9.3091 },
			{ 47159.6000, 9.0995 }, { 47170.0499, 8.8694 },
			{ 47180.7742, 8.6540 }, { 47190.0126, 8.4891 },
			{ 47200.4612, 8.3295 }, { 47209.4863, 8.2173 },
			{ 47217.6407, 8.1381 }, { 47227.8922, 8.0702 },
			{ 47239.9536, 8.0370 }, { 47248.5667, 8.0448 },
			{ 47262.9154, 8.1158 }, { 47271.3867, 8.1906 },
			{ 47280.6429, 8.2987 }, { 47290.9088, 8.4486 },
			{ 47300.4666, 8.6138 }, { 47309.6426, 8.7925 },
			{ 47321.4500, 9.0465 }, { 47329.6271, 9.2347 },
			{ 47341.4963, 9.5197 }, { 47351.5836, 9.7674 },
			{ 47360.4860, 9.9858 }, { 47369.8632, 10.2114 },
			{ 47380.7299, 10.4620 }, { 47389.4961, 10.6516 },
			{ 47399.1541, 10.8435 }, { 47411.3265, 11.0548 },
			{ 47420.0914, 11.1822 }, { 47430.0593, 11.2992 },
			{ 47439.2429, 11.3787 }, { 47450.2257, 11.4360 },
			{ 47460.7404, 11.4512 }, { 47469.2076, 11.4350 },
			{ 47480.1656, 11.3767 }, { 47491.0182, 11.2789 },
			{ 47500.9629, 11.1562 }, { 47509.3253, 11.0305 },
			{ 47519.8413, 10.8462 }, { 47528.8256, 10.6685 },
			{ 47539.0342, 10.4479 }, { 47551.6140, 10.1557 },
			{ 47560.6750, 9.9363 }, { 47568.9280, 9.7335 },
			{ 47580.0072, 9.4620 }, { 47590.7887, 9.2047 },
			{ 47599.2001, 9.0126 }, { 47609.6262, 8.7898 },
			{ 47617.7858, 8.6301 }, { 47630.0020, 8.4206 },
			{ 47638.4500, 8.2993 }, { 47650.5870, 8.1628 },
			{ 47662.4667, 8.0761 }, { 47671.2478, 8.0432 },
			{ 47680.0444, 8.0376 }, { 47691.0200, 8.0691 },
			{ 47698.7596, 8.1165 }, { 47710.5607, 8.2275 },
			{ 47719.4446, 8.3402 }, { 47730.0078, 8.5040 },
			{ 47741.2246, 8.7092 }, { 47749.9070, 8.8869 },
			{ 47761.6323, 9.1476 }, { 47770.2394, 9.3503 },
			{ 47779.6892, 9.5796 }, { 47791.7764, 9.8768 },
			{ 47801.1417, 10.1048 }, { 47808.5663, 10.2811 },
			{ 47820.9109, 10.5596 }, { 47830.2777, 10.7541 },
			{ 47840.5543, 10.9463 }, { 47851.0311, 11.1151 },
			{ 47859.3312, 11.2268 }, { 47869.3116, 11.3329 },
			{ 47880.6122, 11.4133 }, { 47890.7688, 11.4478 },
			{ 47899.2570, 11.4487 }, { 47910.5288, 11.4105 },
			{ 47919.6943, 11.3470 }, { 47930.7554, 11.2333 },
			{ 47941.0819, 11.0929 }, { 47950.0332, 10.9469 },
			{ 47960.2150, 10.7567 }, { 47968.1233, 10.5937 },
			{ 47980.0083, 10.3287 }, { 47992.0760, 10.0421 },
			{ 47998.7288, 9.8797 }, { 48010.8496, 9.5816 },
			{ 48018.8000, 9.3883 }, { 48031.5667, 9.0886 },
			{ 48040.0022, 8.9021 }, { 48061.0996, 8.4958 },
			{ 48069.8257, 8.3594 }, { 48079.0601, 8.2390 },
			{ 48089.6213, 8.1340 }, { 48099.4871, 8.0696 },
			{ 48109.6716, 8.0386 }, { 48119.8662, 8.0444 },
			{ 48129.7542, 8.0850 }, { 48140.3591, 8.1659 },
			{ 48149.6238, 8.2668 }, { 48160.0388, 8.4116 },
			{ 48170.8118, 8.5928 }, { 48178.8247, 8.7459 },
			{ 48189.3672, 8.9672 }, { 48201.9370, 9.2540 },
			{ 48209.3259, 9.4308 }, { 48219.1467, 9.6708 },
			{ 48231.6846, 9.9787 }, { 48239.1638, 10.1594 },
			{ 48248.5486, 10.3791 }, { 48260.7268, 10.6462 },
			{ 48271.0923, 10.8520 }, { 48279.4824, 11.0007 },
			{ 48290.7073, 11.1706 }, { 48299.5259, 11.2780 },
			{ 48308.2283, 11.3598 }, { 48321.7009, 11.4360 },
			{ 48329.6880, 11.4512 }, { 48343.9099, 11.4222 },
			{ 48351.2241, 11.3798 }, { 48359.3899, 11.3109 },
			{ 48380.3252, 11.0384 }, { 48390.9629, 10.8530 },
			{ 48396.1001, 10.7539 }, { 48412.1309, 10.4116 },
			{ 48420.4121, 10.2196 }, { 48430.2634, 9.9827 },
			{ 48442.1897, 9.6898 }, { 48451.7485, 9.4558 },
			{ 48461.9749, 9.2117 }, { 48470.0071, 9.0278 },
			{ 48480.1179, 8.8102 }, { 48489.9624, 8.6173 },
			{ 48501.2285, 8.4246 }, { 48509.6023, 8.3037 },
			{ 48519.9041, 8.1838 }, { 48530.9473, 8.0935 },
			{ 48540.3118, 8.0496 }, { 48548.2856, 8.0365 },
			{ 48559.7793, 8.0573 }, { 48569.5684, 8.1115 },
			{ 48579.9109, 8.2040 }, { 48589.7559, 8.3239 },
			{ 48600.6345, 8.4895 }, { 48611.2344, 8.6805 },
			{ 48620.5674, 8.8693 }, { 48630.3418, 9.0840 },
			{ 48640.8767, 9.3299 }, { 48651.3557, 9.5840 },
			{ 48659.4836, 9.7839 }, { 48671.5098, 10.0778 },
			{ 48679.4741, 10.2675 }, { 48689.3201, 10.4922 },
			{ 48698.7534, 10.6934 }, { 48710.9131, 10.9264 },
			{ 48719.8894, 11.0755 }, { 48729.8650, 11.2150 },
			{ 48740.6499, 11.3315 }, { 48751.2871, 11.4090 },
			{ 48761.3633, 11.4462 }, { 48769.2251, 11.4504 },
			{ 48777.6499, 11.4306 }, { 48786.1499, 11.3854 },
			{ 48799.9268, 11.2603 }, { 48810.0239, 11.1304 },
			{ 48821.9248, 10.9398 }, { 48830.5422, 10.7796 },
			{ 48840.4607, 10.5755 }, { 48851.8171, 10.3212 },
			{ 48860.8425, 10.1078 }, { 48869.9634, 9.8859 },
			{ 48880.3892, 9.6294 }, { 48889.4309, 9.4089 },
			{ 48899.2102, 9.1769 }, { 48910.1455, 8.9310 },
			{ 48920.1541, 8.7235 }, { 48929.3918, 8.5507 },
			{ 48938.6980, 8.3981 }, { 48949.9512, 8.2460 },
			{ 48959.6143, 8.1467 }, { 48971.0945, 8.0691 },
			{ 48980.1621, 8.0400 }, { 48988.7207, 8.0394 },
			{ 49000.6912, 8.0818 }, { 49009.8779, 8.1480 },
			{ 49020.0649, 8.2541 }, { 49030.4153, 8.3946 },
			{ 49040.4304, 8.5593 }, { 49048.3894, 8.7079 },
			{ 49059.6382, 8.9407 }, { 49069.5571, 9.1637 },
			{ 49079.4900, 9.3988 }, { 49089.4617, 9.6420 },
			{ 49101.0200, 9.9262 }, { 49114.8000, 10.2577 },
			{ 49120.3049, 10.3852 }, { 49130.0139, 10.5998 },
			{ 49141.6394, 10.8343 }, { 49152.4800, 11.0256 },
			{ 49160.5017, 11.1472 }, { 49171.0425, 11.2784 },
			{ 49179.0273, 11.3543 }, { 49189.3035, 11.4205 },
			{ 49200.2139, 11.4505 }, { 49209.5208, 11.4428 },
			{ 49220.6260, 11.3938 }, { 49228.6448, 11.3320 },
			{ 49239.5488, 11.2141 }, { 49250.3057, 11.0623 },
			{ 49259.2917, 10.9110 }, { 49269.4036, 10.7175 },
			{ 49280.8909, 10.4729 }, { 49290.4624, 10.2535 },
			{ 49300.4575, 10.0141 }, { 49309.4607, 9.7936 },
			{ 49320.3677, 9.5257 }, { 49330.5713, 9.2798 },
			{ 49339.1772, 9.0799 }, { 49350.1953, 8.8394 },
			{ 49359.1204, 8.6610 }, { 49370.1724, 8.4653 },
			{ 49380.3220, 8.3139 }, { 49389.7864, 8.2003 },
			{ 49398.6331, 8.1200 }, { 49410.9480, 8.0523 },
			{ 49418.8733, 8.0369 }, { 49429.6616, 8.0516 },
			{ 49441.4116, 8.1141 }, { 49449.3374, 8.1828 },
			{ 49460.3923, 8.3124 }, { 49473.4209, 8.5114 },
			{ 49479.7405, 8.6241 }, { 49487.3953, 8.7729 },
			{ 49501.7156, 9.0814 }, { 49509.7275, 9.2673 },
			{ 49519.4951, 9.5022 }, { 49530.4292, 9.7706 },
			{ 49540.6863, 10.0219 }, { 49549.8003, 10.2402 },
			{ 49559.6528, 10.4666 }, { 49570.2502, 10.6936 },
			{ 49580.3301, 10.8892 }, { 49592.3550, 11.0905 },
			{ 49600.0525, 11.1985 }, { 49608.7734, 11.2992 },
			{ 49620.6082, 11.3963 }, { 49629.8691, 11.4389 },
			{ 49637.9727, 11.4515 }, { 49651.5464, 11.4204 },
			{ 49660.9175, 11.3614 }, { 49669.7927, 11.2783 },
			{ 49680.2468, 11.1483 }, { 49689.7749, 11.0021 },
			{ 49699.3335, 10.8315 }, { 49710.6138, 10.6040 },
			{ 49720.2925, 10.3904 }, { 49731.7361, 10.1221 },
			{ 49741.0706, 9.8952 }, { 49749.7312, 9.6822 },
			{ 49761.4399, 9.3963 }, { 49769.2483, 9.2107 },
			{ 49779.5640, 8.9762 }, { 49787.6467, 8.8040 },
			{ 49798.9131, 8.5858 }, { 49809.2349, 8.4126 },
			{ 49819.2400, 8.2727 }, { 49831.4666, 8.1435 },
			{ 49838.8413, 8.0894 }, { 49848.1001, 8.0480 },
			{ 49859.1162, 8.0380 }, { 49869.3145, 8.0671 },
			{ 49884.3999, 8.1761 }, { 49890.9819, 8.2472 },
			{ 49899.2625, 8.3558 }, { 49908.5847, 8.5016 },
			{ 49921.4946, 8.7398 }, { 49930.2036, 8.9207 },
			{ 49939.3235, 9.1240 }, { 49950.3618, 9.3840 },
			{ 49960.1179, 9.6216 }, { 49969.6931, 9.8572 },
			{ 49979.2898, 10.0911 }, { 49990.1238, 10.3470 },
			{ 50000.3779, 10.5757 }, { 50010.7258, 10.7882 },
			{ 50019.8032, 10.9556 }, { 50029.6235, 11.1133 },
			{ 50040.5103, 11.2559 }, { 50048.7349, 11.3392 },
			{ 50059.4429, 11.4139 }, { 50069.6663, 11.4481 },
			{ 50078.0090, 11.4487 }, { 50089.7605, 11.4077 },
			{ 50098.0664, 11.3499 }, { 50109.4731, 11.2333 },
			{ 50121.0239, 11.0742 }, { 50131.1040, 10.9051 },
			{ 50139.8784, 10.7379 }, { 50150.3179, 10.5184 },
			{ 50159.2695, 10.3161 }, { 50170.2480, 10.0553 },
			{ 50179.4995, 9.8292 }, { 50188.5771, 9.6059 },
			{ 50197.8154, 9.3811 }, { 50210.5557, 9.0824 },
			{ 50223.5605, 8.8005 }, { 50230.5376, 8.6624 },
			{ 50240.6533, 8.4818 }, { 50249.4692, 8.3462 },
			{ 50260.2563, 8.2111 }, { 50270.1099, 8.1200 },
			{ 50279.5513, 8.0634 }, { 50289.0376, 8.0379 },
			{ 50300.3882, 8.0492 }, { 50310.4990, 8.0975 },
			{ 50320.5752, 8.1803 }, { 50331.1274, 8.3024 },
			{ 50340.0112, 8.4311 }, { 50348.2881, 8.5705 },
			{ 50357.0029, 8.7352 } };

	private static final double[][] expectedResidualData = {
			{ 47003.5684, 0.3208 }, { 47010.5407, 0.2672 },
			{ 47021.8561, 0.2575 }, { 47029.2709, 0.2938 },
			{ 47038.4757, 0.3436 }, { 47052.2739, 0.1985 },
			{ 47060.5963, 0.2780 }, { 47069.4640, -0.0949 },
			{ 47081.0427, -0.6378 }, { 47089.6299, -1.1794 },
			{ 47098.0038, -1.2726 }, { 47112.0430, -1.2829 },
			{ 47120.9607, -1.1566 }, { 47131.0238, -0.9049 },
			{ 47139.5317, -0.7775 }, { 47150.6185, -0.3830 },
			{ 47159.6000, -0.3877 }, { 47170.0499, 0.0344 },
			{ 47180.7742, 0.1828 }, { 47190.0126, 0.1609 },
			{ 47200.4612, 0.4372 }, { 47209.4863, 0.6538 },
			{ 47217.6407, 0.7244 }, { 47227.8922, 0.4827 },
			{ 47239.9536, 0.3059 }, { 47248.5667, 0.2552 },
			{ 47262.9154, 0.0996 }, { 47271.3867, -0.0506 },
			{ 47280.6429, -0.1558 }, { 47290.9088, -0.2611 },
			{ 47300.4666, -0.1502 }, { 47309.6426, -0.2175 },
			{ 47321.4500, -0.0715 }, { 47329.6271, -0.0983 },
			{ 47341.4963, -0.1126 }, { 47351.5836, 0.1267 },
			{ 47360.4860, 0.3318 }, { 47369.8632, 0.5219 },
			{ 47380.7299, 0.7222 }, { 47389.4961, 0.6859 },
			{ 47399.1541, 0.6832 }, { 47411.3265, 0.6822 },
			{ 47420.0914, 0.6223 }, { 47430.0593, 0.6008 },
			{ 47439.2429, 0.5691 }, { 47450.2257, 0.5228 },
			{ 47460.7404, 0.4821 }, { 47469.2076, 0.4221 },
			{ 47480.1656, 0.4566 }, { 47491.0182, 0.0938 },
			{ 47500.9629, -0.1242 }, { 47509.3253, -0.3843 },
			{ 47519.8413, -0.7924 }, { 47528.8256, -1.0276 },
			{ 47539.0342, -1.0590 }, { 47551.6140, -0.8486 },
			{ 47560.6750, -0.5951 }, { 47568.9280, -0.2935 },
			{ 47580.0072, -0.0682 }, { 47590.7887, 0.3882 },
			{ 47599.2001, 0.3707 }, { 47609.6262, 0.6564 },
			{ 47617.7858, 0.9899 }, { 47630.0020, 0.8394 },
			{ 47638.4500, 0.9757 }, { 47650.5870, 0.7072 },
			{ 47662.4667, 0.7906 }, { 47671.2478, 0.6068 },
			{ 47680.0444, 0.3957 }, { 47691.0200, -0.0091 },
			{ 47698.7596, -0.0609 }, { 47710.5607, -0.0918 },
			{ 47719.4446, -0.0652 }, { 47730.0078, -0.0969 },
			{ 47741.2246, -0.1370 }, { 47749.9070, 0.0331 },
			{ 47761.6323, 0.2086 }, { 47770.2394, 0.3402 },
			{ 47779.6892, 0.5109 }, { 47791.7764, 0.4687 },
			{ 47801.1417, 0.5984 }, { 47808.5663, 0.6789 },
			{ 47820.9109, 0.6960 }, { 47830.2777, 0.6997 },
			{ 47840.5543, 0.6922 }, { 47851.0311, 0.7588 },
			{ 47859.3312, 0.6252 }, { 47869.3116, 0.5796 },
			{ 47880.6122, 0.6329 }, { 47890.7688, 0.5835 },
			{ 47899.2570, 0.5968 }, { 47910.5288, 0.5579 },
			{ 47919.6943, 0.4530 }, { 47930.7554, 0.3734 },
			{ 47941.0819, 0.2917 }, { 47950.0332, 0.4377 },
			{ 47960.2150, 0.1683 }, { 47968.1233, 0.1930 },
			{ 47980.0083, 0.2296 }, { 47992.0760, 0.4779 },
			{ 47998.7288, 0.6203 }, { 48010.8496, 0.6184 },
			{ 48018.8000, 0.7117 }, { 48031.5667, 0.9781 },
			{ 48040.0022, 0.7757 }, { 48061.0996, 0.3917 },
			{ 48069.8257, 0.0977 }, { 48079.0601, 0.2610 },
			{ 48089.6213, 0.2573 }, { 48099.4871, 0.2648 },
			{ 48109.6716, 0.2719 }, { 48119.8662, 0.3027 },
			{ 48129.7542, 0.0750 }, { 48140.3591, -0.1123 },
			{ 48149.6238, -0.0807 }, { 48160.0388, -0.0541 },
			{ 48170.8118, 0.1345 }, { 48178.8247, 0.1882 },
			{ 48189.3672, 0.2152 }, { 48201.9370, 0.1182 },
			{ 48209.3259, 0.2585 }, { 48219.1467, 0.1592 },
			{ 48231.6846, 0.2893 }, { 48239.1638, -0.1594 },
			{ 48248.5486, 0.2300 }, { 48260.7268, 0.2846 },
			{ 48271.0923, 0.2202 }, { 48279.4824, -0.0622 },
			{ 48290.7073, 0.0669 }, { 48299.5259, -0.1011 },
			{ 48308.2283, -0.0798 }, { 48321.7009, 0.0084 },
			{ 48329.6880, 0.1321 }, { 48343.9099, 0.2778 },
			{ 48351.2241, 0.2802 }, { 48359.3899, 0.2224 },
			{ 48380.3252, 0.4616 }, { 48390.9629, 0.4803 },
			{ 48396.1001, 0.5961 }, { 48412.1309, 0.7884 },
			{ 48420.4121, -0.1496 }, { 48430.2634, -0.4660 },
			{ 48442.1897, -0.6898 }, { 48451.7485, -0.6327 },
			{ 48461.9749, -0.7617 }, { 48470.0071, -0.7722 },
			{ 48480.1179, -0.5435 }, { 48489.9624, -0.2411 },
			{ 48501.2285, -0.1965 }, { 48509.6023, 0.0741 },
			{ 48519.9041, 0.0962 }, { 48530.9473, 0.1951 },
			{ 48540.3118, 0.2975 }, { 48548.2856, 0.3397 },
			{ 48559.7793, 0.3867 }, { 48569.5684, 0.1724 },
			{ 48579.9109, 0.0278 }, { 48589.7559, -0.0065 },
			{ 48600.6345, -0.0743 }, { 48611.2344, -0.1043 },
			{ 48620.5674, -0.2693 }, { 48630.3418, -0.2495 },
			{ 48640.8767, -0.4632 }, { 48651.3557, -0.5126 },
			{ 48659.4836, -0.6251 }, { 48671.5098, -0.8421 },
			{ 48679.4741, -0.9627 }, { 48689.3201, -0.9422 },
			{ 48698.7534, -0.8934 }, { 48710.9131, -0.2764 },
			{ 48719.8894, -0.1255 }, { 48729.8650, -0.3483 },
			{ 48740.6499, -0.1815 }, { 48751.2871, -0.1590 },
			{ 48761.3633, -0.0795 }, { 48769.2251, 0.1746 },
			{ 48777.6499, 0.2694 }, { 48786.1499, 0.4646 },
			{ 48799.9268, 0.4968 }, { 48810.0239, 0.4140 },
			{ 48821.9248, 0.5102 }, { 48830.5422, 0.4704 },
			{ 48840.4607, 0.4790 }, { 48851.8171, 0.3502 },
			{ 48860.8425, 0.2255 }, { 48869.9634, 0.1256 },
			{ 48880.3892, -0.0974 }, { 48889.4309, 0.0603 },
			{ 48899.2102, -0.0555 }, { 48910.1455, -0.1360 },
			{ 48920.1541, -0.1120 }, { 48929.3918, -0.0871 },
			{ 48938.6980, 0.0019 }, { 48949.9512, 0.0730 },
			{ 48959.6143, 0.0066 }, { 48971.0945, 0.1427 },
			{ 48980.1621, 0.1337 }, { 48988.7207, 0.0529 },
			{ 49000.6912, -0.0340 }, { 49009.8779, -0.1430 },
			{ 49020.0649, -0.2470 }, { 49030.4153, -0.2884 },
			{ 49040.4304, -0.4055 }, { 49048.3894, -0.4454 },
			{ 49059.6382, -0.4969 }, { 49069.5571, -0.5018 },
			{ 49079.4900, -0.1988 }, { 49089.4617, -0.3020 },
			{ 49101.0200, -0.3262 }, { 49114.8000, 0.0423 },
			{ 49120.3049, -0.5102 }, { 49130.0139, -0.4798 },
			{ 49141.6394, -0.2676 }, { 49152.4800, -0.3256 },
			{ 49160.5017, -0.3172 }, { 49171.0425, -0.1784 },
			{ 49179.0273, -0.2400 }, { 49189.3035, -0.1205 },
			{ 49200.2139, 0.0572 }, { 49209.5208, 0.2110 },
			{ 49220.6260, 0.2926 }, { 49228.6448, 0.4109 },
			{ 49239.5488, 0.5159 }, { 49250.3057, 0.6650 },
			{ 49259.2917, 0.7163 }, { 49269.4036, 0.8968 },
			{ 49280.8909, 0.8923 }, { 49290.4624, 0.7465 },
			{ 49300.4575, 0.6081 }, { 49309.4607, 0.7135 },
			{ 49320.3677, 0.3410 }, { 49330.5713, 0.1827 },
			{ 49339.1772, 0.1383 }, { 49350.1953, 0.1249 },
			{ 49359.1204, 0.4098 }, { 49370.1724, 0.3097 },
			{ 49380.3220, 0.4075 }, { 49389.7864, 0.2728 },
			{ 49398.6331, 0.3050 }, { 49410.9480, 0.1906 },
			{ 49418.8733, 0.0964 }, { 49429.6616, 0.1609 },
			{ 49441.4116, 0.1041 }, { 49449.3374, 0.0305 },
			{ 49460.3923, -0.2981 }, { 49473.4209, -0.2614 },
			{ 49479.7405, -0.2384 }, { 49487.3953, 0.0604 },
			{ 49501.7156, 0.0615 }, { 49509.7275, -0.2784 },
			{ 49519.4951, -0.1911 }, { 49530.4292, -0.1524 },
			{ 49540.6863, 0.0581 }, { 49549.8003, -0.0569 },
			{ 49559.6528, -0.0555 }, { 49570.2502, -0.2230 },
			{ 49580.3301, -0.2159 }, { 49592.3550, -0.1290 },
			{ 49600.0525, -0.3456 }, { 49608.7734, -0.4436 },
			{ 49620.6082, -0.3690 }, { 49629.8691, -0.3336 },
			{ 49637.9727, -0.2765 }, { 49651.5464, -0.1641 },
			{ 49660.9175, -0.1845 }, { 49669.7927, -0.1033 },
			{ 49680.2468, -0.1608 }, { 49689.7749, -0.2252 },
			{ 49699.3335, -0.7426 }, { 49710.6138, -0.9183 },
			{ 49720.2925, -1.1483 }, { 49731.7361, -1.1364 },
			{ 49741.0706, -0.9702 }, { 49749.7312, -0.7222 },
			{ 49761.4399, -0.7863 }, { 49769.2483, -0.6107 },
			{ 49779.5640, -0.7183 }, { 49787.6467, -0.5655 },
			{ 49798.9131, -0.4429 }, { 49809.2349, -0.0709 },
			{ 49819.2400, -0.0327 }, { 49831.4666, 0.1994 },
			{ 49838.8413, 0.5106 }, { 49848.1001, 0.4520 },
			{ 49859.1162, 0.3120 }, { 49869.3145, 0.1874 },
			{ 49884.3999, -0.1761 }, { 49890.9819, -0.0972 },
			{ 49899.2625, -0.1808 }, { 49908.5847, -0.2599 },
			{ 49921.4946, -0.4991 }, { 49930.2036, -0.5243 },
			{ 49939.3235, -0.6181 }, { 49950.3618, -0.6290 },
			{ 49960.1179, -0.7334 }, { 49969.6931, -0.7134 },
			{ 49979.2898, -0.6631 }, { 49990.1238, -0.6743 },
			{ 50000.3779, -0.4997 }, { 50010.7258, -0.5182 },
			{ 50019.8032, -0.4199 }, { 50029.6235, -0.1919 },
			{ 50040.5103, -0.0940 }, { 50048.7349, -0.0455 },
			{ 50059.4429, -0.0568 }, { 50069.6663, 0.1701 },
			{ 50078.0090, 0.0930 }, { 50089.7605, 0.0840 },
			{ 50098.0664, 0.1445 }, { 50109.4731, 0.0084 },
			{ 50121.0239, -0.0186 }, { 50131.1040, 0.1616 },
			{ 50139.8784, -0.1106 }, { 50150.3179, -0.2734 },
			{ 50159.2695, -0.3494 }, { 50170.2480, -0.8644 },
			{ 50179.4995, -1.2542 }, { 50188.5771, -1.2659 },
			{ 50197.8154, -1.2367 }, { 50210.5557, -0.9224 },
			{ 50223.5605, -0.8255 }, { 50230.5376, -0.5291 },
			{ 50240.6533, -0.3727 }, { 50249.4692, -0.0109 },
			{ 50260.2563, -0.0444 }, { 50270.1099, 0.3175 },
			{ 50279.5513, 0.4545 }, { 50289.0376, 0.4621 },
			{ 50300.3882, 0.5094 }, { 50310.4990, 0.5065 },
			{ 50320.5752, 0.2833 }, { 50331.1274, 0.0214 },
			{ 50340.0112, 0.0035 }, { 50348.2881, -0.2179 },
			{ 50357.0029, -0.5638 } };

	public MultiPeriodicModelDcDftTest(String name) {
		super(name, TCasData.data);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * This test creates a model using the top-hit (period = 435.7435).
	 */
	public void testModelWithOneFrequency() {
		// Perform a standard scan.
		TSDcDft dcdft = new TSDcDft(obs);
		dcdft.execute();

		// Create the model.
		double topPeriod = dcdft.getTopHits().get(
				PeriodAnalysisCoordinateType.PERIOD).get(0);

		assertEquals("435.7435", String.format("%1.4f", topPeriod));

		List<Double> periods = new ArrayList<Double>();
		periods.add(topPeriod);

		MultiPeriodicFit fit = dcdft.multiPeriodicFit(periods);

		// Check the single parameter set.
		assertEquals(1, fit.getParameters().size());
		PeriodFitParameters params = fit.getParameters().get(0);
		assertEquals("435.7435", String.format("%1.4f", params.getPeriod()));
		assertEquals("0.0023", String.format("%1.4f", params.getFrequency()));
		assertEquals("1.7075", String.format("%1.4f", params.getAmplitude()));
		assertEquals("0.5359", String.format("%1.4f", params
				.getCosineCoefficient()));
		assertEquals("1.6213", String.format("%1.4f", params
				.getSineCoefficient()));
		assertEquals("9.7440", String.format("%1.4f", params
				.getConstantCoefficient()));

		// Check the model data.
		checkData(expectedModelData, fit.getFit());
				
		// Check the residual data.
		checkData(expectedResidualData, fit.getResiduals());
	}

	// Helpers

	private void checkData(double[][] expectedData, List<ValidObservation> obs) {
		for (int i = 0; i < expectedData.length; i++) {
			// JD
			assertEquals(String.format("%1.4f", expectedData[i][0]), String
					.format("%1.4f", obs.get(i).getJD()));

			// Magnitude
			assertEquals(String.format("%1.4f", expectedData[i][1]), String
					.format("%1.4f", obs.get(i).getMag()));
		}
	}
}
