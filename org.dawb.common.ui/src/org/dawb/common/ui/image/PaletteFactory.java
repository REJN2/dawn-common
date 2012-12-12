/*
 * Copyright (c) 2012 European Synchrotron Radiation Facility,
 *                    Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */ 
package org.dawb.common.ui.image;

import java.util.LinkedHashMap;
import java.util.Map;

import org.dawb.common.util.object.ObjectUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * A utility class for building lookup tables
 * 
 * @author Andy Gotz
 * 
   @Deprecated  use IPaletteService instead
 */
@Deprecated
public class PaletteFactory {
	
	
	public static final Map<String,Integer> PALETTES = PaletteFactory.getPaletteNames();
	
	/**
	 * An array with all the available palettes. They are null until used.
	 */
	static private PaletteData[] palettes = new PaletteData[PALETTES.size()];



	/**
	 * Make 256 level grayscale palette.
	 */
	public static PaletteData makeGrayScalePalette() {
		RGB grayscale[] = new RGB[256];
		for (int i = 0; i < 256; i++) {
			grayscale[i] = new RGB(i, i, i);
		}
		return new PaletteData(grayscale);
	}
	/**
	 * Make 256 level grayscale palette.
	 */
	public static PaletteData makeGrayScalePaletteInverted() {
		RGB grayscale[] = new RGB[256];
		for (int i = 0; i < 256; i++) {
			grayscale[i] = new RGB(255-i, 255-i, 255-i);
		}
		return new PaletteData(grayscale);
	}

	/**
	 * Make 256 level color palette.
	 */
	public static PaletteData makeColorPalette() {
		RGB color[] = new RGB[256];
		color[0] = new RGB(0, 0, 0);
		int i = 1;
		for (int j = 0; j < 3; j++) {
			for (int k = 1; k <= 85; k++) {
				if (j == 0) {
					color[i] = new RGB(0, 0, k * 3);
				} else if (j == 1) {
					color[i] = new RGB(0, k * 3, 3);
				} else {
					color[i] = new RGB(k * 3, 0, 0);
				}
				i++;
			}
		}
		return new PaletteData(color);
	}
	
	
	/**
	 * Make 256 Jet Palette
	 */
	public static PaletteData makeJetPalette() {
		RGB jet[] = new RGB[256];
		
		double colorPos[] = new double[5];
		int nb = 256;

		for (int i = 0; i < nb; i++) {
			
			double value = (double)i/(double)255;

			double outBlue = 0;
			if (value <= 0.1) {outBlue  =  5*value + 0.5;}
			if (value > 0.1 && value <= 1.0/3.0 ) {outBlue  =  1;}
			if (value >1.0/3.0 && value <= 1.0/2.0) {outBlue  =  -6*value +3;}
			
			double outGreen = 0;
			if (value > 1.0/3.0 && value < 2.0/3.0  ) {outGreen = 1;}
			if (value <= 1.0/3.0 && value >= 1.0/8.0) {outGreen = 24.0/5*value - 0.6;}
			if (value >= 2.0/3.0 && value <= 7.0/8.0) {outGreen = -24.0/5*value + 4.2;}
			
			double outRed = 0;
			if (value >= 0.9) {outRed = -5*value +5.5;}
			if (value > 2.0/3.0 && value <= 0.9 ) {outRed = 1;}
			if (value >=1.0/2.0 && value <= 2.0/3.0 ) {outRed = 6*value -3;}
			
			jet[i] = new RGB((int)(outRed*255),
					(int)(outGreen*255),
					(int)(outBlue*255));

		}
		return new PaletteData(jet);
	}
	

	/**
	 * Make 256 rainbow color palette - Jean-Luc Pon's algorithm.
	 */
	public static PaletteData makeRainbow1Palette() {
		RGB rainbow[] = new RGB[256];
		RGB colorVal[] = new RGB[5];
		colorVal[0] = new RGB(200, 0, 250); // purple
		colorVal[1] = new RGB(40, 40, 255); // blue
		colorVal[2] = new RGB(40, 255, 40); // green
		colorVal[3] = new RGB(250, 250, 0); // yellow
		colorVal[4] = new RGB(255, 0, 0); // red
		double colorPos[] = new double[5];
		colorPos[0] = 0.0;
		colorPos[1] = 0.25;
		colorPos[2] = 0.50;
		colorPos[3] = 0.75;
		colorPos[4] = 1.0;
		int nb = 256;
		int colId;

		colId = 0;

		for (int i = 0; i < nb; i++) {

			double r1, g1, b1;
			double r2, g2, b2;

			double r = (double) i / (double) nb;
			if (colId < (colorPos.length - 2) && r >= colorPos[colId + 1])
				colId++;

			r1 = (double) colorVal[colId].red;
			g1 = (double) colorVal[colId].green;
			b1 = (double) colorVal[colId].blue;

			r2 = (double) colorVal[colId + 1].red;
			g2 = (double) colorVal[colId + 1].green;
			b2 = (double) colorVal[colId + 1].blue;

			double rr = (r - colorPos[colId])
					/ (colorPos[colId + 1] - colorPos[colId]);
			if (rr < 0.0)
				rr = 0.0;
			if (rr > 1.0)
				rr = 1.0;

			rainbow[i] = new RGB((int) (r1 + (r2 - r1) * rr),
					(int) (g1 + (g2 - g1) * rr), (int) (b1 + (b2 - b1) * rr));

		}
		return new PaletteData(rainbow);
	}

	/**
	 * Make 256 rainbow color palette - Ken Evans algorithm.
	 */
	public static PaletteData makeRainbow2Palette() {
		RGB rainbow[] = new RGB[256];
		double nGroups = 5, nMembers = 45, nTotal = nGroups * nMembers;
		double high = 1.000, medium = .375;
		int nColors = 256;
		for (int index = 0; index < 256; index++) {
			double h = (double) index / (double) nColors;
			double hx = h * nTotal;
			double deltax = (high - medium) / nMembers;
			double r, g, b;
			int gh = (int) Math.floor(hx / nMembers);
			int ih = (int) Math.floor(hx);
			switch (gh) {
			case 0:
				r = medium;
				g = medium + (ih - gh * nMembers) * deltax;
				b = high;
				break;
			case 1:
				r = medium;
				g = high;
				b = high - (ih - gh * nMembers) * deltax;
				break;
			case 2:
				r = medium + (ih - gh * nMembers) * deltax;
				g = high;
				b = medium;
				break;
			case 3:
				r = high;
				g = high - (ih - gh * nMembers) * deltax;
				b = medium;
				break;
			case 4:
				r = high;
				g = medium;
				b = medium + (ih - gh * nMembers) * deltax;
				break;
			default:
				r = high;
				g = medium;
				b = high;
				break;
			}
			int red = (int) (r * 255 + .5);
			if (red > 255)
				red = 255;
			int green = (int) (g * 255 + .5);
			if (green > 255)
				green = 255;
			int blue = (int) (b * 255 + .5);
			if (blue > 255)
				blue = 255;
			rainbow[index] = new RGB(red, green, blue);

		}
		return new PaletteData(rainbow);

	}
	
	// The following were generated from Matplotlib

	/**
	 * Make 256 level Blues palette.
	 */
	public static PaletteData makeBluesPalette() {
	  RGB blues[] = new RGB[256];
	  blues[0] = new RGB(247, 251, 255);
	  blues[1] = new RGB(246, 250, 254);
	  blues[2] = new RGB(245, 249, 254);
	  blues[3] = new RGB(244, 249, 254);
	  blues[4] = new RGB(243, 248, 253);
	  blues[5] = new RGB(243, 248, 253);
	  blues[6] = new RGB(242, 247, 253);
	  blues[7] = new RGB(241, 247, 253);
	  blues[8] = new RGB(240, 246, 252);
	  blues[9] = new RGB(239, 246, 252);
	  blues[10] = new RGB(239, 245, 252);
	  blues[11] = new RGB(238, 245, 252);
	  blues[12] = new RGB(237, 244, 251);
	  blues[13] = new RGB(236, 244, 251);
	  blues[14] = new RGB(236, 243, 251);
	  blues[15] = new RGB(235, 243, 251);
	  blues[16] = new RGB(234, 242, 250);
	  blues[17] = new RGB(233, 242, 250);
	  blues[18] = new RGB(232, 241, 250);
	  blues[19] = new RGB(232, 241, 250);
	  blues[20] = new RGB(231, 240, 249);
	  blues[21] = new RGB(230, 240, 249);
	  blues[22] = new RGB(229, 239, 249);
	  blues[23] = new RGB(228, 239, 249);
	  blues[24] = new RGB(228, 238, 248);
	  blues[25] = new RGB(227, 238, 248);
	  blues[26] = new RGB(226, 237, 248);
	  blues[27] = new RGB(225, 237, 248);
	  blues[28] = new RGB(225, 236, 247);
	  blues[29] = new RGB(224, 236, 247);
	  blues[30] = new RGB(223, 235, 247);
	  blues[31] = new RGB(222, 235, 247);
	  blues[32] = new RGB(221, 234, 246);
	  blues[33] = new RGB(221, 234, 246);
	  blues[34] = new RGB(220, 233, 246);
	  blues[35] = new RGB(219, 233, 246);
	  blues[36] = new RGB(218, 232, 245);
	  blues[37] = new RGB(218, 232, 245);
	  blues[38] = new RGB(217, 231, 245);
	  blues[39] = new RGB(216, 231, 245);
	  blues[40] = new RGB(215, 230, 244);
	  blues[41] = new RGB(215, 230, 244);
	  blues[42] = new RGB(214, 229, 244);
	  blues[43] = new RGB(213, 229, 244);
	  blues[44] = new RGB(212, 228, 243);
	  blues[45] = new RGB(212, 228, 243);
	  blues[46] = new RGB(211, 227, 243);
	  blues[47] = new RGB(210, 227, 243);
	  blues[48] = new RGB(209, 226, 242);
	  blues[49] = new RGB(209, 226, 242);
	  blues[50] = new RGB(208, 225, 242);
	  blues[51] = new RGB(207, 225, 242);
	  blues[52] = new RGB(206, 224, 241);
	  blues[53] = new RGB(206, 224, 241);
	  blues[54] = new RGB(205, 223, 241);
	  blues[55] = new RGB(204, 223, 241);
	  blues[56] = new RGB(203, 222, 240);
	  blues[57] = new RGB(203, 222, 240);
	  blues[58] = new RGB(202, 221, 240);
	  blues[59] = new RGB(201, 221, 240);
	  blues[60] = new RGB(200, 220, 239);
	  blues[61] = new RGB(200, 220, 239);
	  blues[62] = new RGB(199, 219, 239);
	  blues[63] = new RGB(198, 219, 239);
	  blues[64] = new RGB(197, 218, 238);
	  blues[65] = new RGB(196, 218, 238);
	  blues[66] = new RGB(195, 217, 238);
	  blues[67] = new RGB(193, 217, 237);
	  blues[68] = new RGB(192, 216, 237);
	  blues[69] = new RGB(191, 216, 236);
	  blues[70] = new RGB(190, 215, 236);
	  blues[71] = new RGB(188, 215, 235);
	  blues[72] = new RGB(187, 214, 235);
	  blues[73] = new RGB(186, 214, 234);
	  blues[74] = new RGB(185, 213, 234);
	  blues[75] = new RGB(183, 213, 234);
	  blues[76] = new RGB(182, 212, 233);
	  blues[77] = new RGB(181, 211, 233);
	  blues[78] = new RGB(180, 211, 232);
	  blues[79] = new RGB(178, 210, 232);
	  blues[80] = new RGB(177, 210, 231);
	  blues[81] = new RGB(176, 209, 231);
	  blues[82] = new RGB(175, 209, 230);
	  blues[83] = new RGB(173, 208, 230);
	  blues[84] = new RGB(172, 208, 230);
	  blues[85] = new RGB(171, 207, 229);
	  blues[86] = new RGB(170, 207, 229);
	  blues[87] = new RGB(168, 206, 228);
	  blues[88] = new RGB(167, 206, 228);
	  blues[89] = new RGB(166, 205, 227);
	  blues[90] = new RGB(165, 205, 227);
	  blues[91] = new RGB(163, 204, 227);
	  blues[92] = new RGB(162, 203, 226);
	  blues[93] = new RGB(161, 203, 226);
	  blues[94] = new RGB(160, 202, 225);
	  blues[95] = new RGB(158, 202, 225);
	  blues[96] = new RGB(157, 201, 224);
	  blues[97] = new RGB(155, 200, 224);
	  blues[98] = new RGB(154, 199, 224);
	  blues[99] = new RGB(152, 199, 223);
	  blues[100] = new RGB(151, 198, 223);
	  blues[101] = new RGB(149, 197, 223);
	  blues[102] = new RGB(147, 196, 222);
	  blues[103] = new RGB(146, 195, 222);
	  blues[104] = new RGB(144, 194, 222);
	  blues[105] = new RGB(143, 193, 221);
	  blues[106] = new RGB(141, 192, 221);
	  blues[107] = new RGB(139, 192, 221);
	  blues[108] = new RGB(138, 191, 220);
	  blues[109] = new RGB(136, 190, 220);
	  blues[110] = new RGB(135, 189, 220);
	  blues[111] = new RGB(133, 188, 219);
	  blues[112] = new RGB(131, 187, 219);
	  blues[113] = new RGB(130, 186, 219);
	  blues[114] = new RGB(128, 185, 218);
	  blues[115] = new RGB(127, 184, 218);
	  blues[116] = new RGB(125, 184, 217);
	  blues[117] = new RGB(123, 183, 217);
	  blues[118] = new RGB(122, 182, 217);
	  blues[119] = new RGB(120, 181, 216);
	  blues[120] = new RGB(119, 180, 216);
	  blues[121] = new RGB(117, 179, 216);
	  blues[122] = new RGB(115, 178, 215);
	  blues[123] = new RGB(114, 177, 215);
	  blues[124] = new RGB(112, 177, 215);
	  blues[125] = new RGB(111, 176, 214);
	  blues[126] = new RGB(109, 175, 214);
	  blues[127] = new RGB(107, 174, 214);
	  blues[128] = new RGB(106, 173, 213);
	  blues[129] = new RGB(105, 172, 213);
	  blues[130] = new RGB(103, 171, 212);
	  blues[131] = new RGB(102, 170, 212);
	  blues[132] = new RGB(101, 170, 211);
	  blues[133] = new RGB(99, 169, 211);
	  blues[134] = new RGB(98, 168, 210);
	  blues[135] = new RGB(97, 167, 210);
	  blues[136] = new RGB(96, 166, 209);
	  blues[137] = new RGB(94, 165, 209);
	  blues[138] = new RGB(93, 164, 208);
	  blues[139] = new RGB(92, 163, 208);
	  blues[140] = new RGB(90, 163, 207);
	  blues[141] = new RGB(89, 162, 207);
	  blues[142] = new RGB(88, 161, 206);
	  blues[143] = new RGB(87, 160, 206);
	  blues[144] = new RGB(85, 159, 205);
	  blues[145] = new RGB(84, 158, 205);
	  blues[146] = new RGB(83, 157, 204);
	  blues[147] = new RGB(81, 156, 204);
	  blues[148] = new RGB(80, 155, 203);
	  blues[149] = new RGB(79, 155, 203);
	  blues[150] = new RGB(78, 154, 202);
	  blues[151] = new RGB(76, 153, 202);
	  blues[152] = new RGB(75, 152, 201);
	  blues[153] = new RGB(74, 151, 201);
	  blues[154] = new RGB(72, 150, 200);
	  blues[155] = new RGB(71, 149, 200);
	  blues[156] = new RGB(70, 148, 199);
	  blues[157] = new RGB(69, 148, 199);
	  blues[158] = new RGB(67, 147, 198);
	  blues[159] = new RGB(66, 146, 198);
	  blues[160] = new RGB(65, 145, 197);
	  blues[161] = new RGB(64, 144, 197);
	  blues[162] = new RGB(63, 143, 196);
	  blues[163] = new RGB(62, 142, 196);
	  blues[164] = new RGB(61, 141, 195);
	  blues[165] = new RGB(60, 140, 195);
	  blues[166] = new RGB(59, 139, 194);
	  blues[167] = new RGB(58, 138, 193);
	  blues[168] = new RGB(57, 137, 193);
	  blues[169] = new RGB(56, 136, 192);
	  blues[170] = new RGB(55, 135, 192);
	  blues[171] = new RGB(53, 133, 191);
	  blues[172] = new RGB(52, 132, 191);
	  blues[173] = new RGB(51, 131, 190);
	  blues[174] = new RGB(50, 130, 190);
	  blues[175] = new RGB(49, 129, 189);
	  blues[176] = new RGB(48, 128, 189);
	  blues[177] = new RGB(47, 127, 188);
	  blues[178] = new RGB(46, 126, 188);
	  blues[179] = new RGB(45, 125, 187);
	  blues[180] = new RGB(44, 124, 187);
	  blues[181] = new RGB(43, 123, 186);
	  blues[182] = new RGB(42, 122, 185);
	  blues[183] = new RGB(41, 121, 185);
	  blues[184] = new RGB(40, 120, 184);
	  blues[185] = new RGB(39, 119, 184);
	  blues[186] = new RGB(38, 118, 183);
	  blues[187] = new RGB(37, 117, 183);
	  blues[188] = new RGB(36, 116, 182);
	  blues[189] = new RGB(35, 115, 182);
	  blues[190] = new RGB(34, 114, 181);
	  blues[191] = new RGB(33, 113, 181);
	  blues[192] = new RGB(32, 112, 180);
	  blues[193] = new RGB(31, 111, 179);
	  blues[194] = new RGB(30, 110, 178);
	  blues[195] = new RGB(30, 109, 178);
	  blues[196] = new RGB(29, 108, 177);
	  blues[197] = new RGB(28, 107, 176);
	  blues[198] = new RGB(27, 106, 175);
	  blues[199] = new RGB(26, 105, 174);
	  blues[200] = new RGB(26, 104, 174);
	  blues[201] = new RGB(25, 103, 173);
	  blues[202] = new RGB(24, 102, 172);
	  blues[203] = new RGB(23, 101, 171);
	  blues[204] = new RGB(23, 100, 171);
	  blues[205] = new RGB(22, 99, 170);
	  blues[206] = new RGB(21, 98, 169);
	  blues[207] = new RGB(20, 97, 168);
	  blues[208] = new RGB(19, 96, 167);
	  blues[209] = new RGB(19, 95, 167);
	  blues[210] = new RGB(18, 94, 166);
	  blues[211] = new RGB(17, 93, 165);
	  blues[212] = new RGB(16, 92, 164);
	  blues[213] = new RGB(15, 91, 163);
	  blues[214] = new RGB(15, 90, 163);
	  blues[215] = new RGB(14, 89, 162);
	  blues[216] = new RGB(13, 88, 161);
	  blues[217] = new RGB(12, 87, 160);
	  blues[218] = new RGB(12, 86, 160);
	  blues[219] = new RGB(11, 85, 159);
	  blues[220] = new RGB(10, 84, 158);
	  blues[221] = new RGB(9, 83, 157);
	  blues[222] = new RGB(8, 82, 156);
	  blues[223] = new RGB(8, 81, 156);
	  blues[224] = new RGB(8, 80, 154);
	  blues[225] = new RGB(8, 79, 153);
	  blues[226] = new RGB(8, 78, 151);
	  blues[227] = new RGB(8, 76, 150);
	  blues[228] = new RGB(8, 75, 148);
	  blues[229] = new RGB(8, 74, 146);
	  blues[230] = new RGB(8, 73, 145);
	  blues[231] = new RGB(8, 72, 143);
	  blues[232] = new RGB(8, 71, 142);
	  blues[233] = new RGB(8, 70, 140);
	  blues[234] = new RGB(8, 69, 139);
	  blues[235] = new RGB(8, 68, 137);
	  blues[236] = new RGB(8, 67, 136);
	  blues[237] = new RGB(8, 66, 134);
	  blues[238] = new RGB(8, 65, 133);
	  blues[239] = new RGB(8, 64, 131);
	  blues[240] = new RGB(8, 63, 130);
	  blues[241] = new RGB(8, 62, 128);
	  blues[242] = new RGB(8, 61, 126);
	  blues[243] = new RGB(8, 60, 125);
	  blues[244] = new RGB(8, 59, 123);
	  blues[245] = new RGB(8, 58, 122);
	  blues[246] = new RGB(8, 57, 120);
	  blues[247] = new RGB(8, 56, 119);
	  blues[248] = new RGB(8, 55, 117);
	  blues[249] = new RGB(8, 54, 116);
	  blues[250] = new RGB(8, 53, 114);
	  blues[251] = new RGB(8, 52, 113);
	  blues[252] = new RGB(8, 51, 111);
	  blues[253] = new RGB(8, 50, 110);
	  blues[254] = new RGB(8, 49, 108);
	  blues[255] = new RGB(8, 48, 107);
	  return new PaletteData(blues);
	}
	/**
	 * Make 256 level Greens palette.
	 */
	public static PaletteData makeGreensPalette() {
	  RGB greens[] = new RGB[256];
	  greens[0] = new RGB(247, 252, 245);
	  greens[1] = new RGB(246, 251, 244);
	  greens[2] = new RGB(245, 251, 243);
	  greens[3] = new RGB(245, 251, 243);
	  greens[4] = new RGB(244, 251, 242);
	  greens[5] = new RGB(244, 250, 241);
	  greens[6] = new RGB(243, 250, 241);
	  greens[7] = new RGB(243, 250, 240);
	  greens[8] = new RGB(242, 250, 239);
	  greens[9] = new RGB(241, 250, 239);
	  greens[10] = new RGB(241, 249, 238);
	  greens[11] = new RGB(240, 249, 237);
	  greens[12] = new RGB(240, 249, 237);
	  greens[13] = new RGB(239, 249, 236);
	  greens[14] = new RGB(239, 248, 235);
	  greens[15] = new RGB(238, 248, 235);
	  greens[16] = new RGB(237, 248, 234);
	  greens[17] = new RGB(237, 248, 233);
	  greens[18] = new RGB(236, 248, 233);
	  greens[19] = new RGB(236, 247, 232);
	  greens[20] = new RGB(235, 247, 231);
	  greens[21] = new RGB(235, 247, 231);
	  greens[22] = new RGB(234, 247, 230);
	  greens[23] = new RGB(234, 246, 229);
	  greens[24] = new RGB(233, 246, 229);
	  greens[25] = new RGB(232, 246, 228);
	  greens[26] = new RGB(232, 246, 227);
	  greens[27] = new RGB(231, 246, 227);
	  greens[28] = new RGB(231, 245, 226);
	  greens[29] = new RGB(230, 245, 225);
	  greens[30] = new RGB(230, 245, 225);
	  greens[31] = new RGB(229, 245, 224);
	  greens[32] = new RGB(228, 244, 223);
	  greens[33] = new RGB(227, 244, 222);
	  greens[34] = new RGB(227, 244, 221);
	  greens[35] = new RGB(226, 243, 220);
	  greens[36] = new RGB(225, 243, 219);
	  greens[37] = new RGB(224, 243, 218);
	  greens[38] = new RGB(223, 242, 217);
	  greens[39] = new RGB(222, 242, 216);
	  greens[40] = new RGB(221, 241, 215);
	  greens[41] = new RGB(220, 241, 214);
	  greens[42] = new RGB(219, 241, 213);
	  greens[43] = new RGB(218, 240, 212);
	  greens[44] = new RGB(217, 240, 211);
	  greens[45] = new RGB(216, 240, 210);
	  greens[46] = new RGB(215, 239, 209);
	  greens[47] = new RGB(214, 239, 208);
	  greens[48] = new RGB(213, 238, 207);
	  greens[49] = new RGB(212, 238, 206);
	  greens[50] = new RGB(211, 238, 205);
	  greens[51] = new RGB(211, 237, 204);
	  greens[52] = new RGB(210, 237, 203);
	  greens[53] = new RGB(209, 237, 202);
	  greens[54] = new RGB(208, 236, 201);
	  greens[55] = new RGB(207, 236, 200);
	  greens[56] = new RGB(206, 235, 199);
	  greens[57] = new RGB(205, 235, 198);
	  greens[58] = new RGB(204, 235, 197);
	  greens[59] = new RGB(203, 234, 196);
	  greens[60] = new RGB(202, 234, 195);
	  greens[61] = new RGB(201, 234, 194);
	  greens[62] = new RGB(200, 233, 193);
	  greens[63] = new RGB(199, 233, 192);
	  greens[64] = new RGB(198, 232, 191);
	  greens[65] = new RGB(197, 232, 190);
	  greens[66] = new RGB(196, 231, 189);
	  greens[67] = new RGB(195, 231, 188);
	  greens[68] = new RGB(193, 230, 187);
	  greens[69] = new RGB(192, 230, 185);
	  greens[70] = new RGB(191, 229, 184);
	  greens[71] = new RGB(190, 229, 183);
	  greens[72] = new RGB(189, 228, 182);
	  greens[73] = new RGB(187, 228, 181);
	  greens[74] = new RGB(186, 227, 180);
	  greens[75] = new RGB(185, 227, 178);
	  greens[76] = new RGB(184, 226, 177);
	  greens[77] = new RGB(183, 226, 176);
	  greens[78] = new RGB(182, 225, 175);
	  greens[79] = new RGB(180, 225, 174);
	  greens[80] = new RGB(179, 224, 173);
	  greens[81] = new RGB(178, 224, 171);
	  greens[82] = new RGB(177, 223, 170);
	  greens[83] = new RGB(176, 223, 169);
	  greens[84] = new RGB(174, 222, 168);
	  greens[85] = new RGB(173, 222, 167);
	  greens[86] = new RGB(172, 221, 166);
	  greens[87] = new RGB(171, 221, 165);
	  greens[88] = new RGB(170, 220, 163);
	  greens[89] = new RGB(168, 220, 162);
	  greens[90] = new RGB(167, 219, 161);
	  greens[91] = new RGB(166, 219, 160);
	  greens[92] = new RGB(165, 218, 159);
	  greens[93] = new RGB(164, 218, 158);
	  greens[94] = new RGB(162, 217, 156);
	  greens[95] = new RGB(161, 217, 155);
	  greens[96] = new RGB(160, 216, 154);
	  greens[97] = new RGB(159, 216, 153);
	  greens[98] = new RGB(157, 215, 152);
	  greens[99] = new RGB(156, 214, 151);
	  greens[100] = new RGB(154, 214, 149);
	  greens[101] = new RGB(153, 213, 148);
	  greens[102] = new RGB(152, 212, 147);
	  greens[103] = new RGB(150, 212, 146);
	  greens[104] = new RGB(149, 211, 145);
	  greens[105] = new RGB(147, 210, 144);
	  greens[106] = new RGB(146, 210, 142);
	  greens[107] = new RGB(144, 209, 141);
	  greens[108] = new RGB(143, 208, 140);
	  greens[109] = new RGB(142, 208, 139);
	  greens[110] = new RGB(140, 207, 138);
	  greens[111] = new RGB(139, 206, 137);
	  greens[112] = new RGB(137, 206, 135);
	  greens[113] = new RGB(136, 205, 134);
	  greens[114] = new RGB(135, 204, 133);
	  greens[115] = new RGB(133, 204, 132);
	  greens[116] = new RGB(132, 203, 131);
	  greens[117] = new RGB(130, 202, 130);
	  greens[118] = new RGB(129, 202, 129);
	  greens[119] = new RGB(128, 201, 127);
	  greens[120] = new RGB(126, 200, 126);
	  greens[121] = new RGB(125, 200, 125);
	  greens[122] = new RGB(123, 199, 124);
	  greens[123] = new RGB(122, 198, 123);
	  greens[124] = new RGB(120, 198, 122);
	  greens[125] = new RGB(119, 197, 120);
	  greens[126] = new RGB(118, 196, 119);
	  greens[127] = new RGB(116, 196, 118);
	  greens[128] = new RGB(115, 195, 117);
	  greens[129] = new RGB(113, 194, 116);
	  greens[130] = new RGB(112, 194, 116);
	  greens[131] = new RGB(110, 193, 115);
	  greens[132] = new RGB(108, 192, 114);
	  greens[133] = new RGB(107, 191, 113);
	  greens[134] = new RGB(105, 190, 112);
	  greens[135] = new RGB(104, 190, 112);
	  greens[136] = new RGB(102, 189, 111);
	  greens[137] = new RGB(100, 188, 110);
	  greens[138] = new RGB(99, 187, 109);
	  greens[139] = new RGB(97, 186, 108);
	  greens[140] = new RGB(96, 186, 108);
	  greens[141] = new RGB(94, 185, 107);
	  greens[142] = new RGB(92, 184, 106);
	  greens[143] = new RGB(91, 183, 105);
	  greens[144] = new RGB(89, 183, 105);
	  greens[145] = new RGB(88, 182, 104);
	  greens[146] = new RGB(86, 181, 103);
	  greens[147] = new RGB(84, 180, 102);
	  greens[148] = new RGB(83, 179, 101);
	  greens[149] = new RGB(81, 179, 101);
	  greens[150] = new RGB(80, 178, 100);
	  greens[151] = new RGB(78, 177, 99);
	  greens[152] = new RGB(76, 176, 98);
	  greens[153] = new RGB(75, 176, 98);
	  greens[154] = new RGB(73, 175, 97);
	  greens[155] = new RGB(72, 174, 96);
	  greens[156] = new RGB(70, 173, 95);
	  greens[157] = new RGB(68, 172, 94);
	  greens[158] = new RGB(67, 172, 94);
	  greens[159] = new RGB(65, 171, 93);
	  greens[160] = new RGB(64, 170, 92);
	  greens[161] = new RGB(63, 169, 91);
	  greens[162] = new RGB(62, 168, 91);
	  greens[163] = new RGB(61, 167, 90);
	  greens[164] = new RGB(60, 166, 89);
	  greens[165] = new RGB(59, 165, 88);
	  greens[166] = new RGB(58, 164, 88);
	  greens[167] = new RGB(57, 163, 87);
	  greens[168] = new RGB(56, 162, 86);
	  greens[169] = new RGB(55, 161, 85);
	  greens[170] = new RGB(55, 160, 85);
	  greens[171] = new RGB(54, 159, 84);
	  greens[172] = new RGB(53, 158, 83);
	  greens[173] = new RGB(52, 157, 82);
	  greens[174] = new RGB(51, 156, 81);
	  greens[175] = new RGB(50, 155, 81);
	  greens[176] = new RGB(49, 154, 80);
	  greens[177] = new RGB(48, 153, 79);
	  greens[178] = new RGB(47, 152, 78);
	  greens[179] = new RGB(46, 151, 78);
	  greens[180] = new RGB(45, 150, 77);
	  greens[181] = new RGB(44, 149, 76);
	  greens[182] = new RGB(43, 148, 75);
	  greens[183] = new RGB(42, 147, 75);
	  greens[184] = new RGB(41, 146, 74);
	  greens[185] = new RGB(40, 145, 73);
	  greens[186] = new RGB(39, 144, 72);
	  greens[187] = new RGB(39, 143, 72);
	  greens[188] = new RGB(38, 142, 71);
	  greens[189] = new RGB(37, 141, 70);
	  greens[190] = new RGB(36, 140, 69);
	  greens[191] = new RGB(35, 139, 69);
	  greens[192] = new RGB(34, 138, 68);
	  greens[193] = new RGB(33, 137, 67);
	  greens[194] = new RGB(31, 136, 66);
	  greens[195] = new RGB(30, 135, 66);
	  greens[196] = new RGB(29, 134, 65);
	  greens[197] = new RGB(28, 133, 64);
	  greens[198] = new RGB(27, 132, 63);
	  greens[199] = new RGB(26, 131, 62);
	  greens[200] = new RGB(25, 130, 62);
	  greens[201] = new RGB(24, 129, 61);
	  greens[202] = new RGB(23, 128, 60);
	  greens[203] = new RGB(22, 127, 59);
	  greens[204] = new RGB(21, 127, 59);
	  greens[205] = new RGB(19, 126, 58);
	  greens[206] = new RGB(18, 125, 57);
	  greens[207] = new RGB(17, 124, 56);
	  greens[208] = new RGB(16, 123, 55);
	  greens[209] = new RGB(15, 122, 55);
	  greens[210] = new RGB(14, 121, 54);
	  greens[211] = new RGB(13, 120, 53);
	  greens[212] = new RGB(12, 119, 52);
	  greens[213] = new RGB(11, 118, 51);
	  greens[214] = new RGB(10, 117, 51);
	  greens[215] = new RGB(8, 116, 50);
	  greens[216] = new RGB(7, 115, 49);
	  greens[217] = new RGB(6, 114, 48);
	  greens[218] = new RGB(5, 113, 48);
	  greens[219] = new RGB(4, 112, 47);
	  greens[220] = new RGB(3, 111, 46);
	  greens[221] = new RGB(2, 111, 45);
	  greens[222] = new RGB(1, 110, 44);
	  greens[223] = new RGB(0, 109, 44);
	  greens[224] = new RGB(0, 107, 43);
	  greens[225] = new RGB(0, 106, 43);
	  greens[226] = new RGB(0, 105, 42);
	  greens[227] = new RGB(0, 104, 41);
	  greens[228] = new RGB(0, 102, 41);
	  greens[229] = new RGB(0, 101, 40);
	  greens[230] = new RGB(0, 100, 40);
	  greens[231] = new RGB(0, 98, 39);
	  greens[232] = new RGB(0, 97, 39);
	  greens[233] = new RGB(0, 96, 38);
	  greens[234] = new RGB(0, 95, 38);
	  greens[235] = new RGB(0, 93, 37);
	  greens[236] = new RGB(0, 92, 37);
	  greens[237] = new RGB(0, 91, 36);
	  greens[238] = new RGB(0, 89, 36);
	  greens[239] = new RGB(0, 88, 35);
	  greens[240] = new RGB(0, 87, 35);
	  greens[241] = new RGB(0, 86, 34);
	  greens[242] = new RGB(0, 84, 33);
	  greens[243] = new RGB(0, 83, 33);
	  greens[244] = new RGB(0, 82, 32);
	  greens[245] = new RGB(0, 80, 32);
	  greens[246] = new RGB(0, 79, 31);
	  greens[247] = new RGB(0, 78, 31);
	  greens[248] = new RGB(0, 77, 30);
	  greens[249] = new RGB(0, 75, 30);
	  greens[250] = new RGB(0, 74, 29);
	  greens[251] = new RGB(0, 73, 29);
	  greens[252] = new RGB(0, 71, 28);
	  greens[253] = new RGB(0, 70, 28);
	  greens[254] = new RGB(0, 69, 27);
	  greens[255] = new RGB(0, 68, 27);
	  return new PaletteData(greens);
	}
	/**
	 * Make 256 level Reds palette.
	 */
	public static PaletteData makeRedsPalette() {
	  RGB reds[] = new RGB[256];
	  reds[0] = new RGB(255, 245, 240);
	  reds[1] = new RGB(254, 244, 239);
	  reds[2] = new RGB(254, 243, 238);
	  reds[3] = new RGB(254, 243, 237);
	  reds[4] = new RGB(254, 242, 236);
	  reds[5] = new RGB(254, 241, 235);
	  reds[6] = new RGB(254, 241, 234);
	  reds[7] = new RGB(254, 240, 233);
	  reds[8] = new RGB(254, 239, 232);
	  reds[9] = new RGB(254, 239, 231);
	  reds[10] = new RGB(254, 238, 230);
	  reds[11] = new RGB(254, 237, 229);
	  reds[12] = new RGB(254, 237, 228);
	  reds[13] = new RGB(254, 236, 227);
	  reds[14] = new RGB(254, 235, 226);
	  reds[15] = new RGB(254, 235, 225);
	  reds[16] = new RGB(254, 234, 224);
	  reds[17] = new RGB(254, 233, 224);
	  reds[18] = new RGB(254, 233, 223);
	  reds[19] = new RGB(254, 232, 222);
	  reds[20] = new RGB(254, 231, 221);
	  reds[21] = new RGB(254, 231, 220);
	  reds[22] = new RGB(254, 230, 219);
	  reds[23] = new RGB(254, 229, 218);
	  reds[24] = new RGB(254, 229, 217);
	  reds[25] = new RGB(254, 228, 216);
	  reds[26] = new RGB(254, 227, 215);
	  reds[27] = new RGB(254, 227, 214);
	  reds[28] = new RGB(254, 226, 213);
	  reds[29] = new RGB(254, 225, 212);
	  reds[30] = new RGB(254, 225, 211);
	  reds[31] = new RGB(254, 224, 210);
	  reds[32] = new RGB(253, 223, 209);
	  reds[33] = new RGB(253, 222, 208);
	  reds[34] = new RGB(253, 221, 206);
	  reds[35] = new RGB(253, 220, 205);
	  reds[36] = new RGB(253, 219, 203);
	  reds[37] = new RGB(253, 218, 202);
	  reds[38] = new RGB(253, 216, 200);
	  reds[39] = new RGB(253, 215, 199);
	  reds[40] = new RGB(253, 214, 197);
	  reds[41] = new RGB(253, 213, 195);
	  reds[42] = new RGB(253, 212, 194);
	  reds[43] = new RGB(253, 211, 192);
	  reds[44] = new RGB(253, 209, 191);
	  reds[45] = new RGB(253, 208, 189);
	  reds[46] = new RGB(253, 207, 188);
	  reds[47] = new RGB(253, 206, 186);
	  reds[48] = new RGB(252, 205, 185);
	  reds[49] = new RGB(252, 204, 183);
	  reds[50] = new RGB(252, 202, 182);
	  reds[51] = new RGB(252, 201, 180);
	  reds[52] = new RGB(252, 200, 179);
	  reds[53] = new RGB(252, 199, 177);
	  reds[54] = new RGB(252, 198, 175);
	  reds[55] = new RGB(252, 197, 174);
	  reds[56] = new RGB(252, 195, 172);
	  reds[57] = new RGB(252, 194, 171);
	  reds[58] = new RGB(252, 193, 169);
	  reds[59] = new RGB(252, 192, 168);
	  reds[60] = new RGB(252, 191, 166);
	  reds[61] = new RGB(252, 190, 165);
	  reds[62] = new RGB(252, 189, 163);
	  reds[63] = new RGB(252, 187, 162);
	  reds[64] = new RGB(252, 186, 160);
	  reds[65] = new RGB(252, 185, 159);
	  reds[66] = new RGB(252, 184, 157);
	  reds[67] = new RGB(252, 182, 156);
	  reds[68] = new RGB(252, 181, 154);
	  reds[69] = new RGB(252, 180, 153);
	  reds[70] = new RGB(252, 178, 151);
	  reds[71] = new RGB(252, 177, 150);
	  reds[72] = new RGB(252, 176, 148);
	  reds[73] = new RGB(252, 175, 147);
	  reds[74] = new RGB(252, 173, 145);
	  reds[75] = new RGB(252, 172, 144);
	  reds[76] = new RGB(252, 171, 142);
	  reds[77] = new RGB(252, 169, 141);
	  reds[78] = new RGB(252, 168, 139);
	  reds[79] = new RGB(252, 167, 138);
	  reds[80] = new RGB(252, 166, 137);
	  reds[81] = new RGB(252, 164, 135);
	  reds[82] = new RGB(252, 163, 134);
	  reds[83] = new RGB(252, 162, 132);
	  reds[84] = new RGB(252, 160, 131);
	  reds[85] = new RGB(252, 159, 129);
	  reds[86] = new RGB(252, 158, 128);
	  reds[87] = new RGB(252, 157, 126);
	  reds[88] = new RGB(252, 155, 125);
	  reds[89] = new RGB(252, 154, 123);
	  reds[90] = new RGB(252, 153, 122);
	  reds[91] = new RGB(252, 151, 120);
	  reds[92] = new RGB(252, 150, 119);
	  reds[93] = new RGB(252, 149, 117);
	  reds[94] = new RGB(252, 148, 116);
	  reds[95] = new RGB(252, 146, 114);
	  reds[96] = new RGB(251, 145, 113);
	  reds[97] = new RGB(251, 144, 112);
	  reds[98] = new RGB(251, 143, 111);
	  reds[99] = new RGB(251, 141, 109);
	  reds[100] = new RGB(251, 140, 108);
	  reds[101] = new RGB(251, 139, 107);
	  reds[102] = new RGB(251, 138, 106);
	  reds[103] = new RGB(251, 136, 104);
	  reds[104] = new RGB(251, 135, 103);
	  reds[105] = new RGB(251, 134, 102);
	  reds[106] = new RGB(251, 132, 100);
	  reds[107] = new RGB(251, 131, 99);
	  reds[108] = new RGB(251, 130, 98);
	  reds[109] = new RGB(251, 129, 97);
	  reds[110] = new RGB(251, 127, 95);
	  reds[111] = new RGB(251, 126, 94);
	  reds[112] = new RGB(251, 125, 93);
	  reds[113] = new RGB(251, 124, 92);
	  reds[114] = new RGB(251, 122, 90);
	  reds[115] = new RGB(251, 121, 89);
	  reds[116] = new RGB(251, 120, 88);
	  reds[117] = new RGB(251, 119, 87);
	  reds[118] = new RGB(251, 117, 85);
	  reds[119] = new RGB(251, 116, 84);
	  reds[120] = new RGB(251, 115, 83);
	  reds[121] = new RGB(251, 114, 82);
	  reds[122] = new RGB(251, 112, 80);
	  reds[123] = new RGB(251, 111, 79);
	  reds[124] = new RGB(251, 110, 78);
	  reds[125] = new RGB(251, 109, 77);
	  reds[126] = new RGB(251, 107, 75);
	  reds[127] = new RGB(251, 106, 74);
	  reds[128] = new RGB(250, 105, 73);
	  reds[129] = new RGB(250, 103, 72);
	  reds[130] = new RGB(250, 102, 71);
	  reds[131] = new RGB(249, 100, 70);
	  reds[132] = new RGB(249, 99, 69);
	  reds[133] = new RGB(248, 97, 68);
	  reds[134] = new RGB(248, 96, 67);
	  reds[135] = new RGB(248, 94, 66);
	  reds[136] = new RGB(247, 93, 66);
	  reds[137] = new RGB(247, 91, 65);
	  reds[138] = new RGB(247, 90, 64);
	  reds[139] = new RGB(246, 89, 63);
	  reds[140] = new RGB(246, 87, 62);
	  reds[141] = new RGB(245, 86, 61);
	  reds[142] = new RGB(245, 84, 60);
	  reds[143] = new RGB(245, 83, 59);
	  reds[144] = new RGB(244, 81, 58);
	  reds[145] = new RGB(244, 80, 57);
	  reds[146] = new RGB(244, 78, 56);
	  reds[147] = new RGB(243, 77, 55);
	  reds[148] = new RGB(243, 75, 54);
	  reds[149] = new RGB(242, 74, 53);
	  reds[150] = new RGB(242, 72, 52);
	  reds[151] = new RGB(242, 71, 51);
	  reds[152] = new RGB(241, 69, 50);
	  reds[153] = new RGB(241, 68, 50);
	  reds[154] = new RGB(241, 66, 49);
	  reds[155] = new RGB(240, 65, 48);
	  reds[156] = new RGB(240, 63, 47);
	  reds[157] = new RGB(239, 62, 46);
	  reds[158] = new RGB(239, 61, 45);
	  reds[159] = new RGB(239, 59, 44);
	  reds[160] = new RGB(238, 58, 43);
	  reds[161] = new RGB(237, 57, 43);
	  reds[162] = new RGB(236, 56, 42);
	  reds[163] = new RGB(234, 55, 42);
	  reds[164] = new RGB(233, 53, 41);
	  reds[165] = new RGB(232, 52, 41);
	  reds[166] = new RGB(231, 51, 40);
	  reds[167] = new RGB(230, 50, 40);
	  reds[168] = new RGB(229, 49, 39);
	  reds[169] = new RGB(228, 48, 39);
	  reds[170] = new RGB(227, 47, 39);
	  reds[171] = new RGB(225, 46, 38);
	  reds[172] = new RGB(224, 45, 38);
	  reds[173] = new RGB(223, 44, 37);
	  reds[174] = new RGB(222, 42, 37);
	  reds[175] = new RGB(221, 41, 36);
	  reds[176] = new RGB(220, 40, 36);
	  reds[177] = new RGB(219, 39, 35);
	  reds[178] = new RGB(217, 38, 35);
	  reds[179] = new RGB(216, 37, 34);
	  reds[180] = new RGB(215, 36, 34);
	  reds[181] = new RGB(214, 35, 33);
	  reds[182] = new RGB(213, 34, 33);
	  reds[183] = new RGB(212, 33, 32);
	  reds[184] = new RGB(211, 31, 32);
	  reds[185] = new RGB(210, 30, 31);
	  reds[186] = new RGB(208, 29, 31);
	  reds[187] = new RGB(207, 28, 31);
	  reds[188] = new RGB(206, 27, 30);
	  reds[189] = new RGB(205, 26, 30);
	  reds[190] = new RGB(204, 25, 29);
	  reds[191] = new RGB(203, 24, 29);
	  reds[192] = new RGB(202, 23, 28);
	  reds[193] = new RGB(200, 23, 28);
	  reds[194] = new RGB(199, 23, 28);
	  reds[195] = new RGB(198, 22, 28);
	  reds[196] = new RGB(197, 22, 27);
	  reds[197] = new RGB(196, 22, 27);
	  reds[198] = new RGB(194, 22, 27);
	  reds[199] = new RGB(193, 21, 27);
	  reds[200] = new RGB(192, 21, 26);
	  reds[201] = new RGB(191, 21, 26);
	  reds[202] = new RGB(190, 20, 26);
	  reds[203] = new RGB(188, 20, 26);
	  reds[204] = new RGB(187, 20, 25);
	  reds[205] = new RGB(186, 20, 25);
	  reds[206] = new RGB(185, 19, 25);
	  reds[207] = new RGB(184, 19, 25);
	  reds[208] = new RGB(183, 19, 24);
	  reds[209] = new RGB(181, 18, 24);
	  reds[210] = new RGB(180, 18, 24);
	  reds[211] = new RGB(179, 18, 24);
	  reds[212] = new RGB(178, 18, 23);
	  reds[213] = new RGB(177, 17, 23);
	  reds[214] = new RGB(175, 17, 23);
	  reds[215] = new RGB(174, 17, 23);
	  reds[216] = new RGB(173, 17, 22);
	  reds[217] = new RGB(172, 16, 22);
	  reds[218] = new RGB(171, 16, 22);
	  reds[219] = new RGB(169, 16, 22);
	  reds[220] = new RGB(168, 15, 21);
	  reds[221] = new RGB(167, 15, 21);
	  reds[222] = new RGB(166, 15, 21);
	  reds[223] = new RGB(165, 15, 21);
	  reds[224] = new RGB(163, 14, 20);
	  reds[225] = new RGB(161, 14, 20);
	  reds[226] = new RGB(159, 13, 20);
	  reds[227] = new RGB(157, 13, 20);
	  reds[228] = new RGB(155, 12, 19);
	  reds[229] = new RGB(153, 12, 19);
	  reds[230] = new RGB(151, 11, 19);
	  reds[231] = new RGB(149, 11, 19);
	  reds[232] = new RGB(147, 10, 18);
	  reds[233] = new RGB(145, 10, 18);
	  reds[234] = new RGB(143, 9, 18);
	  reds[235] = new RGB(141, 9, 18);
	  reds[236] = new RGB(139, 8, 17);
	  reds[237] = new RGB(138, 8, 17);
	  reds[238] = new RGB(136, 8, 17);
	  reds[239] = new RGB(134, 7, 17);
	  reds[240] = new RGB(132, 7, 16);
	  reds[241] = new RGB(130, 6, 16);
	  reds[242] = new RGB(128, 6, 16);
	  reds[243] = new RGB(126, 5, 16);
	  reds[244] = new RGB(124, 5, 15);
	  reds[245] = new RGB(122, 4, 15);
	  reds[246] = new RGB(120, 4, 15);
	  reds[247] = new RGB(118, 3, 15);
	  reds[248] = new RGB(116, 3, 14);
	  reds[249] = new RGB(114, 2, 14);
	  reds[250] = new RGB(112, 2, 14);
	  reds[251] = new RGB(110, 1, 14);
	  reds[252] = new RGB(108, 1, 13);
	  reds[253] = new RGB(106, 0, 13);
	  reds[254] = new RGB(104, 0, 13);
	  reds[255] = new RGB(103, 0, 13);
	  return new PaletteData(reds);
	}
	/**
	 * Make 256 level Pastel1 palette.
	 */
	public static PaletteData makePastel1Palette() {
	  RGB pastel1[] = new RGB[256];
	  pastel1[0] = new RGB(251, 180, 174);
	  pastel1[1] = new RGB(248, 180, 175);
	  pastel1[2] = new RGB(246, 181, 177);
	  pastel1[3] = new RGB(244, 182, 178);
	  pastel1[4] = new RGB(241, 183, 180);
	  pastel1[5] = new RGB(239, 183, 182);
	  pastel1[6] = new RGB(237, 184, 183);
	  pastel1[7] = new RGB(235, 185, 185);
	  pastel1[8] = new RGB(232, 186, 187);
	  pastel1[9] = new RGB(230, 187, 188);
	  pastel1[10] = new RGB(228, 187, 190);
	  pastel1[11] = new RGB(226, 188, 192);
	  pastel1[12] = new RGB(223, 189, 193);
	  pastel1[13] = new RGB(221, 190, 195);
	  pastel1[14] = new RGB(219, 190, 197);
	  pastel1[15] = new RGB(217, 191, 198);
	  pastel1[16] = new RGB(214, 192, 200);
	  pastel1[17] = new RGB(212, 193, 202);
	  pastel1[18] = new RGB(210, 194, 203);
	  pastel1[19] = new RGB(208, 194, 205);
	  pastel1[20] = new RGB(205, 195, 207);
	  pastel1[21] = new RGB(203, 196, 208);
	  pastel1[22] = new RGB(201, 197, 210);
	  pastel1[23] = new RGB(199, 198, 212);
	  pastel1[24] = new RGB(196, 198, 213);
	  pastel1[25] = new RGB(194, 199, 215);
	  pastel1[26] = new RGB(192, 200, 217);
	  pastel1[27] = new RGB(190, 201, 218);
	  pastel1[28] = new RGB(187, 201, 220);
	  pastel1[29] = new RGB(185, 202, 222);
	  pastel1[30] = new RGB(183, 203, 223);
	  pastel1[31] = new RGB(180, 204, 225);
	  pastel1[32] = new RGB(179, 205, 226);
	  pastel1[33] = new RGB(179, 206, 225);
	  pastel1[34] = new RGB(180, 207, 225);
	  pastel1[35] = new RGB(181, 207, 224);
	  pastel1[36] = new RGB(182, 208, 223);
	  pastel1[37] = new RGB(183, 209, 222);
	  pastel1[38] = new RGB(183, 210, 221);
	  pastel1[39] = new RGB(184, 211, 220);
	  pastel1[40] = new RGB(185, 212, 219);
	  pastel1[41] = new RGB(186, 213, 218);
	  pastel1[42] = new RGB(186, 214, 217);
	  pastel1[43] = new RGB(187, 215, 216);
	  pastel1[44] = new RGB(188, 216, 215);
	  pastel1[45] = new RGB(189, 217, 214);
	  pastel1[46] = new RGB(190, 218, 213);
	  pastel1[47] = new RGB(190, 219, 212);
	  pastel1[48] = new RGB(191, 220, 211);
	  pastel1[49] = new RGB(192, 221, 210);
	  pastel1[50] = new RGB(193, 222, 209);
	  pastel1[51] = new RGB(194, 223, 209);
	  pastel1[52] = new RGB(194, 223, 208);
	  pastel1[53] = new RGB(195, 224, 207);
	  pastel1[54] = new RGB(196, 225, 206);
	  pastel1[55] = new RGB(197, 226, 205);
	  pastel1[56] = new RGB(197, 227, 204);
	  pastel1[57] = new RGB(198, 228, 203);
	  pastel1[58] = new RGB(199, 229, 202);
	  pastel1[59] = new RGB(200, 230, 201);
	  pastel1[60] = new RGB(201, 231, 200);
	  pastel1[61] = new RGB(201, 232, 199);
	  pastel1[62] = new RGB(202, 233, 198);
	  pastel1[63] = new RGB(203, 234, 197);
	  pastel1[64] = new RGB(204, 234, 197);
	  pastel1[65] = new RGB(204, 233, 198);
	  pastel1[66] = new RGB(205, 232, 199);
	  pastel1[67] = new RGB(205, 231, 200);
	  pastel1[68] = new RGB(206, 230, 201);
	  pastel1[69] = new RGB(206, 229, 202);
	  pastel1[70] = new RGB(207, 228, 203);
	  pastel1[71] = new RGB(208, 227, 204);
	  pastel1[72] = new RGB(208, 226, 205);
	  pastel1[73] = new RGB(209, 225, 205);
	  pastel1[74] = new RGB(209, 224, 206);
	  pastel1[75] = new RGB(210, 223, 207);
	  pastel1[76] = new RGB(210, 222, 208);
	  pastel1[77] = new RGB(211, 221, 209);
	  pastel1[78] = new RGB(212, 220, 210);
	  pastel1[79] = new RGB(212, 219, 211);
	  pastel1[80] = new RGB(213, 218, 212);
	  pastel1[81] = new RGB(213, 217, 213);
	  pastel1[82] = new RGB(214, 216, 214);
	  pastel1[83] = new RGB(214, 215, 215);
	  pastel1[84] = new RGB(215, 214, 216);
	  pastel1[85] = new RGB(216, 213, 217);
	  pastel1[86] = new RGB(216, 212, 218);
	  pastel1[87] = new RGB(217, 211, 219);
	  pastel1[88] = new RGB(217, 210, 220);
	  pastel1[89] = new RGB(218, 209, 221);
	  pastel1[90] = new RGB(218, 208, 222);
	  pastel1[91] = new RGB(219, 207, 223);
	  pastel1[92] = new RGB(219, 206, 224);
	  pastel1[93] = new RGB(220, 205, 225);
	  pastel1[94] = new RGB(221, 204, 226);
	  pastel1[95] = new RGB(221, 203, 227);
	  pastel1[96] = new RGB(222, 203, 227);
	  pastel1[97] = new RGB(223, 203, 225);
	  pastel1[98] = new RGB(224, 204, 223);
	  pastel1[99] = new RGB(225, 204, 221);
	  pastel1[100] = new RGB(226, 204, 219);
	  pastel1[101] = new RGB(227, 205, 217);
	  pastel1[102] = new RGB(228, 205, 215);
	  pastel1[103] = new RGB(229, 206, 213);
	  pastel1[104] = new RGB(230, 206, 211);
	  pastel1[105] = new RGB(231, 207, 209);
	  pastel1[106] = new RGB(232, 207, 207);
	  pastel1[107] = new RGB(233, 207, 205);
	  pastel1[108] = new RGB(234, 208, 203);
	  pastel1[109] = new RGB(235, 208, 201);
	  pastel1[110] = new RGB(236, 209, 200);
	  pastel1[111] = new RGB(237, 209, 198);
	  pastel1[112] = new RGB(238, 210, 196);
	  pastel1[113] = new RGB(239, 210, 194);
	  pastel1[114] = new RGB(240, 211, 192);
	  pastel1[115] = new RGB(241, 211, 190);
	  pastel1[116] = new RGB(242, 211, 188);
	  pastel1[117] = new RGB(243, 212, 186);
	  pastel1[118] = new RGB(244, 212, 184);
	  pastel1[119] = new RGB(245, 213, 182);
	  pastel1[120] = new RGB(246, 213, 180);
	  pastel1[121] = new RGB(247, 214, 178);
	  pastel1[122] = new RGB(248, 214, 176);
	  pastel1[123] = new RGB(249, 215, 174);
	  pastel1[124] = new RGB(250, 215, 172);
	  pastel1[125] = new RGB(251, 215, 170);
	  pastel1[126] = new RGB(252, 216, 168);
	  pastel1[127] = new RGB(253, 216, 166);
	  pastel1[128] = new RGB(254, 217, 166);
	  pastel1[129] = new RGB(254, 218, 167);
	  pastel1[130] = new RGB(254, 219, 168);
	  pastel1[131] = new RGB(254, 221, 170);
	  pastel1[132] = new RGB(254, 222, 171);
	  pastel1[133] = new RGB(254, 223, 172);
	  pastel1[134] = new RGB(254, 224, 173);
	  pastel1[135] = new RGB(254, 225, 174);
	  pastel1[136] = new RGB(254, 227, 176);
	  pastel1[137] = new RGB(254, 228, 177);
	  pastel1[138] = new RGB(254, 229, 178);
	  pastel1[139] = new RGB(254, 230, 179);
	  pastel1[140] = new RGB(254, 231, 180);
	  pastel1[141] = new RGB(254, 233, 182);
	  pastel1[142] = new RGB(254, 234, 183);
	  pastel1[143] = new RGB(254, 235, 184);
	  pastel1[144] = new RGB(254, 236, 185);
	  pastel1[145] = new RGB(254, 237, 186);
	  pastel1[146] = new RGB(254, 239, 188);
	  pastel1[147] = new RGB(254, 240, 189);
	  pastel1[148] = new RGB(254, 241, 190);
	  pastel1[149] = new RGB(254, 242, 191);
	  pastel1[150] = new RGB(254, 243, 192);
	  pastel1[151] = new RGB(254, 245, 194);
	  pastel1[152] = new RGB(254, 246, 195);
	  pastel1[153] = new RGB(254, 247, 196);
	  pastel1[154] = new RGB(254, 248, 197);
	  pastel1[155] = new RGB(254, 249, 198);
	  pastel1[156] = new RGB(254, 250, 199);
	  pastel1[157] = new RGB(254, 252, 201);
	  pastel1[158] = new RGB(254, 253, 202);
	  pastel1[159] = new RGB(254, 254, 203);
	  pastel1[160] = new RGB(254, 254, 203);
	  pastel1[161] = new RGB(253, 253, 203);
	  pastel1[162] = new RGB(252, 251, 202);
	  pastel1[163] = new RGB(252, 250, 202);
	  pastel1[164] = new RGB(251, 249, 201);
	  pastel1[165] = new RGB(250, 248, 201);
	  pastel1[166] = new RGB(249, 246, 200);
	  pastel1[167] = new RGB(248, 245, 200);
	  pastel1[168] = new RGB(247, 244, 199);
	  pastel1[169] = new RGB(247, 243, 199);
	  pastel1[170] = new RGB(246, 242, 199);
	  pastel1[171] = new RGB(245, 240, 198);
	  pastel1[172] = new RGB(244, 239, 198);
	  pastel1[173] = new RGB(243, 238, 197);
	  pastel1[174] = new RGB(243, 237, 197);
	  pastel1[175] = new RGB(242, 235, 196);
	  pastel1[176] = new RGB(241, 234, 196);
	  pastel1[177] = new RGB(240, 233, 195);
	  pastel1[178] = new RGB(239, 232, 195);
	  pastel1[179] = new RGB(238, 230, 194);
	  pastel1[180] = new RGB(238, 229, 194);
	  pastel1[181] = new RGB(237, 228, 193);
	  pastel1[182] = new RGB(236, 227, 193);
	  pastel1[183] = new RGB(235, 226, 192);
	  pastel1[184] = new RGB(234, 224, 192);
	  pastel1[185] = new RGB(234, 223, 191);
	  pastel1[186] = new RGB(233, 222, 191);
	  pastel1[187] = new RGB(232, 221, 191);
	  pastel1[188] = new RGB(231, 219, 190);
	  pastel1[189] = new RGB(230, 218, 190);
	  pastel1[190] = new RGB(230, 217, 189);
	  pastel1[191] = new RGB(229, 216, 189);
	  pastel1[192] = new RGB(229, 216, 190);
	  pastel1[193] = new RGB(230, 216, 191);
	  pastel1[194] = new RGB(231, 216, 193);
	  pastel1[195] = new RGB(231, 216, 194);
	  pastel1[196] = new RGB(232, 216, 196);
	  pastel1[197] = new RGB(233, 216, 197);
	  pastel1[198] = new RGB(234, 216, 198);
	  pastel1[199] = new RGB(234, 216, 200);
	  pastel1[200] = new RGB(235, 216, 201);
	  pastel1[201] = new RGB(236, 216, 203);
	  pastel1[202] = new RGB(237, 216, 204);
	  pastel1[203] = new RGB(237, 216, 206);
	  pastel1[204] = new RGB(238, 216, 207);
	  pastel1[205] = new RGB(239, 216, 209);
	  pastel1[206] = new RGB(240, 216, 210);
	  pastel1[207] = new RGB(240, 216, 212);
	  pastel1[208] = new RGB(241, 217, 213);
	  pastel1[209] = new RGB(242, 217, 215);
	  pastel1[210] = new RGB(243, 217, 216);
	  pastel1[211] = new RGB(243, 217, 218);
	  pastel1[212] = new RGB(244, 217, 219);
	  pastel1[213] = new RGB(245, 217, 221);
	  pastel1[214] = new RGB(246, 217, 222);
	  pastel1[215] = new RGB(246, 217, 224);
	  pastel1[216] = new RGB(247, 217, 225);
	  pastel1[217] = new RGB(248, 217, 226);
	  pastel1[218] = new RGB(249, 217, 228);
	  pastel1[219] = new RGB(249, 217, 229);
	  pastel1[220] = new RGB(250, 217, 231);
	  pastel1[221] = new RGB(251, 217, 232);
	  pastel1[222] = new RGB(252, 217, 234);
	  pastel1[223] = new RGB(252, 217, 235);
	  pastel1[224] = new RGB(252, 218, 236);
	  pastel1[225] = new RGB(252, 219, 236);
	  pastel1[226] = new RGB(252, 220, 236);
	  pastel1[227] = new RGB(251, 220, 236);
	  pastel1[228] = new RGB(251, 221, 236);
	  pastel1[229] = new RGB(250, 222, 237);
	  pastel1[230] = new RGB(250, 223, 237);
	  pastel1[231] = new RGB(250, 223, 237);
	  pastel1[232] = new RGB(249, 224, 237);
	  pastel1[233] = new RGB(249, 225, 237);
	  pastel1[234] = new RGB(249, 226, 238);
	  pastel1[235] = new RGB(248, 226, 238);
	  pastel1[236] = new RGB(248, 227, 238);
	  pastel1[237] = new RGB(248, 228, 238);
	  pastel1[238] = new RGB(247, 229, 238);
	  pastel1[239] = new RGB(247, 229, 238);
	  pastel1[240] = new RGB(247, 230, 239);
	  pastel1[241] = new RGB(246, 231, 239);
	  pastel1[242] = new RGB(246, 232, 239);
	  pastel1[243] = new RGB(246, 232, 239);
	  pastel1[244] = new RGB(245, 233, 239);
	  pastel1[245] = new RGB(245, 234, 240);
	  pastel1[246] = new RGB(245, 235, 240);
	  pastel1[247] = new RGB(244, 235, 240);
	  pastel1[248] = new RGB(244, 236, 240);
	  pastel1[249] = new RGB(244, 237, 240);
	  pastel1[250] = new RGB(243, 238, 241);
	  pastel1[251] = new RGB(243, 238, 241);
	  pastel1[252] = new RGB(243, 239, 241);
	  pastel1[253] = new RGB(242, 240, 241);
	  pastel1[254] = new RGB(242, 241, 241);
	  pastel1[255] = new RGB(242, 242, 242);
	  return new PaletteData(pastel1);
	}
	/**
	 * Make 256 level Spring palette.
	 */
	public static PaletteData makeSpringPalette() {
	  RGB spring[] = new RGB[256];
	  spring[0] = new RGB(255, 0, 255);
	  spring[1] = new RGB(255, 1, 254);
	  spring[2] = new RGB(255, 2, 253);
	  spring[3] = new RGB(255, 3, 252);
	  spring[4] = new RGB(255, 4, 251);
	  spring[5] = new RGB(255, 5, 250);
	  spring[6] = new RGB(255, 6, 249);
	  spring[7] = new RGB(255, 7, 248);
	  spring[8] = new RGB(255, 8, 247);
	  spring[9] = new RGB(255, 9, 246);
	  spring[10] = new RGB(255, 10, 245);
	  spring[11] = new RGB(255, 11, 244);
	  spring[12] = new RGB(255, 12, 243);
	  spring[13] = new RGB(255, 13, 242);
	  spring[14] = new RGB(255, 14, 241);
	  spring[15] = new RGB(255, 15, 240);
	  spring[16] = new RGB(255, 16, 239);
	  spring[17] = new RGB(255, 17, 238);
	  spring[18] = new RGB(255, 18, 237);
	  spring[19] = new RGB(255, 19, 236);
	  spring[20] = new RGB(255, 20, 235);
	  spring[21] = new RGB(255, 21, 234);
	  spring[22] = new RGB(255, 22, 233);
	  spring[23] = new RGB(255, 23, 232);
	  spring[24] = new RGB(255, 24, 231);
	  spring[25] = new RGB(255, 25, 230);
	  spring[26] = new RGB(255, 26, 229);
	  spring[27] = new RGB(255, 27, 228);
	  spring[28] = new RGB(255, 28, 227);
	  spring[29] = new RGB(255, 29, 226);
	  spring[30] = new RGB(255, 30, 225);
	  spring[31] = new RGB(255, 31, 224);
	  spring[32] = new RGB(255, 32, 223);
	  spring[33] = new RGB(255, 33, 222);
	  spring[34] = new RGB(255, 34, 221);
	  spring[35] = new RGB(255, 35, 220);
	  spring[36] = new RGB(255, 36, 219);
	  spring[37] = new RGB(255, 37, 218);
	  spring[38] = new RGB(255, 38, 217);
	  spring[39] = new RGB(255, 39, 216);
	  spring[40] = new RGB(255, 40, 215);
	  spring[41] = new RGB(255, 41, 214);
	  spring[42] = new RGB(255, 42, 213);
	  spring[43] = new RGB(255, 43, 211);
	  spring[44] = new RGB(255, 44, 211);
	  spring[45] = new RGB(255, 45, 210);
	  spring[46] = new RGB(255, 46, 209);
	  spring[47] = new RGB(255, 47, 208);
	  spring[48] = new RGB(255, 48, 207);
	  spring[49] = new RGB(255, 49, 206);
	  spring[50] = new RGB(255, 50, 205);
	  spring[51] = new RGB(255, 51, 204);
	  spring[52] = new RGB(255, 52, 203);
	  spring[53] = new RGB(255, 53, 202);
	  spring[54] = new RGB(255, 54, 201);
	  spring[55] = new RGB(255, 55, 200);
	  spring[56] = new RGB(255, 56, 199);
	  spring[57] = new RGB(255, 57, 198);
	  spring[58] = new RGB(255, 58, 197);
	  spring[59] = new RGB(255, 59, 195);
	  spring[60] = new RGB(255, 60, 195);
	  spring[61] = new RGB(255, 61, 194);
	  spring[62] = new RGB(255, 62, 193);
	  spring[63] = new RGB(255, 63, 192);
	  spring[64] = new RGB(255, 64, 191);
	  spring[65] = new RGB(255, 65, 190);
	  spring[66] = new RGB(255, 66, 189);
	  spring[67] = new RGB(255, 67, 188);
	  spring[68] = new RGB(255, 68, 187);
	  spring[69] = new RGB(255, 69, 186);
	  spring[70] = new RGB(255, 70, 185);
	  spring[71] = new RGB(255, 71, 184);
	  spring[72] = new RGB(255, 72, 183);
	  spring[73] = new RGB(255, 73, 182);
	  spring[74] = new RGB(255, 74, 180);
	  spring[75] = new RGB(255, 75, 179);
	  spring[76] = new RGB(255, 76, 179);
	  spring[77] = new RGB(255, 77, 178);
	  spring[78] = new RGB(255, 78, 177);
	  spring[79] = new RGB(255, 79, 176);
	  spring[80] = new RGB(255, 80, 175);
	  spring[81] = new RGB(255, 81, 174);
	  spring[82] = new RGB(255, 82, 173);
	  spring[83] = new RGB(255, 83, 172);
	  spring[84] = new RGB(255, 84, 171);
	  spring[85] = new RGB(255, 85, 170);
	  spring[86] = new RGB(255, 86, 169);
	  spring[87] = new RGB(255, 87, 168);
	  spring[88] = new RGB(255, 88, 167);
	  spring[89] = new RGB(255, 89, 166);
	  spring[90] = new RGB(255, 90, 164);
	  spring[91] = new RGB(255, 91, 163);
	  spring[92] = new RGB(255, 92, 163);
	  spring[93] = new RGB(255, 93, 162);
	  spring[94] = new RGB(255, 94, 161);
	  spring[95] = new RGB(255, 95, 160);
	  spring[96] = new RGB(255, 96, 159);
	  spring[97] = new RGB(255, 97, 158);
	  spring[98] = new RGB(255, 98, 157);
	  spring[99] = new RGB(255, 99, 156);
	  spring[100] = new RGB(255, 100, 155);
	  spring[101] = new RGB(255, 101, 154);
	  spring[102] = new RGB(255, 102, 153);
	  spring[103] = new RGB(255, 103, 152);
	  spring[104] = new RGB(255, 104, 151);
	  spring[105] = new RGB(255, 105, 150);
	  spring[106] = new RGB(255, 106, 148);
	  spring[107] = new RGB(255, 107, 147);
	  spring[108] = new RGB(255, 108, 147);
	  spring[109] = new RGB(255, 109, 146);
	  spring[110] = new RGB(255, 110, 145);
	  spring[111] = new RGB(255, 111, 144);
	  spring[112] = new RGB(255, 112, 143);
	  spring[113] = new RGB(255, 113, 142);
	  spring[114] = new RGB(255, 114, 141);
	  spring[115] = new RGB(255, 115, 140);
	  spring[116] = new RGB(255, 116, 139);
	  spring[117] = new RGB(255, 117, 138);
	  spring[118] = new RGB(255, 118, 137);
	  spring[119] = new RGB(255, 119, 136);
	  spring[120] = new RGB(255, 120, 135);
	  spring[121] = new RGB(255, 121, 134);
	  spring[122] = new RGB(255, 122, 132);
	  spring[123] = new RGB(255, 123, 131);
	  spring[124] = new RGB(255, 124, 131);
	  spring[125] = new RGB(255, 125, 130);
	  spring[126] = new RGB(255, 126, 129);
	  spring[127] = new RGB(255, 127, 128);
	  spring[128] = new RGB(255, 128, 127);
	  spring[129] = new RGB(255, 129, 126);
	  spring[130] = new RGB(255, 130, 125);
	  spring[131] = new RGB(255, 131, 124);
	  spring[132] = new RGB(255, 132, 122);
	  spring[133] = new RGB(255, 133, 121);
	  spring[134] = new RGB(255, 134, 121);
	  spring[135] = new RGB(255, 135, 120);
	  spring[136] = new RGB(255, 136, 119);
	  spring[137] = new RGB(255, 137, 118);
	  spring[138] = new RGB(255, 138, 117);
	  spring[139] = new RGB(255, 139, 116);
	  spring[140] = new RGB(255, 140, 114);
	  spring[141] = new RGB(255, 141, 113);
	  spring[142] = new RGB(255, 142, 113);
	  spring[143] = new RGB(255, 143, 112);
	  spring[144] = new RGB(255, 144, 111);
	  spring[145] = new RGB(255, 145, 110);
	  spring[146] = new RGB(255, 146, 109);
	  spring[147] = new RGB(255, 147, 108);
	  spring[148] = new RGB(255, 148, 106);
	  spring[149] = new RGB(255, 149, 105);
	  spring[150] = new RGB(255, 150, 105);
	  spring[151] = new RGB(255, 151, 104);
	  spring[152] = new RGB(255, 152, 103);
	  spring[153] = new RGB(255, 153, 102);
	  spring[154] = new RGB(255, 154, 101);
	  spring[155] = new RGB(255, 155, 100);
	  spring[156] = new RGB(255, 156, 98);
	  spring[157] = new RGB(255, 157, 97);
	  spring[158] = new RGB(255, 158, 97);
	  spring[159] = new RGB(255, 159, 96);
	  spring[160] = new RGB(255, 160, 95);
	  spring[161] = new RGB(255, 161, 94);
	  spring[162] = new RGB(255, 162, 93);
	  spring[163] = new RGB(255, 163, 92);
	  spring[164] = new RGB(255, 164, 90);
	  spring[165] = new RGB(255, 165, 89);
	  spring[166] = new RGB(255, 166, 89);
	  spring[167] = new RGB(255, 167, 88);
	  spring[168] = new RGB(255, 168, 87);
	  spring[169] = new RGB(255, 169, 86);
	  spring[170] = new RGB(255, 170, 85);
	  spring[171] = new RGB(255, 171, 84);
	  spring[172] = new RGB(255, 172, 82);
	  spring[173] = new RGB(255, 173, 81);
	  spring[174] = new RGB(255, 174, 81);
	  spring[175] = new RGB(255, 175, 80);
	  spring[176] = new RGB(255, 176, 79);
	  spring[177] = new RGB(255, 177, 78);
	  spring[178] = new RGB(255, 178, 77);
	  spring[179] = new RGB(255, 179, 76);
	  spring[180] = new RGB(255, 180, 74);
	  spring[181] = new RGB(255, 181, 73);
	  spring[182] = new RGB(255, 182, 73);
	  spring[183] = new RGB(255, 183, 72);
	  spring[184] = new RGB(255, 184, 71);
	  spring[185] = new RGB(255, 185, 70);
	  spring[186] = new RGB(255, 186, 69);
	  spring[187] = new RGB(255, 187, 68);
	  spring[188] = new RGB(255, 188, 66);
	  spring[189] = new RGB(255, 189, 65);
	  spring[190] = new RGB(255, 190, 65);
	  spring[191] = new RGB(255, 191, 64);
	  spring[192] = new RGB(255, 192, 63);
	  spring[193] = new RGB(255, 193, 62);
	  spring[194] = new RGB(255, 194, 61);
	  spring[195] = new RGB(255, 195, 60);
	  spring[196] = new RGB(255, 196, 58);
	  spring[197] = new RGB(255, 197, 57);
	  spring[198] = new RGB(255, 198, 56);
	  spring[199] = new RGB(255, 199, 56);
	  spring[200] = new RGB(255, 200, 55);
	  spring[201] = new RGB(255, 201, 54);
	  spring[202] = new RGB(255, 202, 53);
	  spring[203] = new RGB(255, 203, 52);
	  spring[204] = new RGB(255, 204, 50);
	  spring[205] = new RGB(255, 205, 49);
	  spring[206] = new RGB(255, 206, 48);
	  spring[207] = new RGB(255, 207, 48);
	  spring[208] = new RGB(255, 208, 47);
	  spring[209] = new RGB(255, 209, 46);
	  spring[210] = new RGB(255, 210, 45);
	  spring[211] = new RGB(255, 211, 44);
	  spring[212] = new RGB(255, 212, 42);
	  spring[213] = new RGB(255, 213, 41);
	  spring[214] = new RGB(255, 214, 40);
	  spring[215] = new RGB(255, 215, 40);
	  spring[216] = new RGB(255, 216, 39);
	  spring[217] = new RGB(255, 217, 38);
	  spring[218] = new RGB(255, 218, 37);
	  spring[219] = new RGB(255, 219, 36);
	  spring[220] = new RGB(255, 220, 34);
	  spring[221] = new RGB(255, 221, 33);
	  spring[222] = new RGB(255, 222, 32);
	  spring[223] = new RGB(255, 223, 32);
	  spring[224] = new RGB(255, 224, 31);
	  spring[225] = new RGB(255, 225, 30);
	  spring[226] = new RGB(255, 226, 29);
	  spring[227] = new RGB(255, 227, 28);
	  spring[228] = new RGB(255, 228, 26);
	  spring[229] = new RGB(255, 229, 25);
	  spring[230] = new RGB(255, 230, 24);
	  spring[231] = new RGB(255, 231, 24);
	  spring[232] = new RGB(255, 232, 23);
	  spring[233] = new RGB(255, 233, 22);
	  spring[234] = new RGB(255, 234, 21);
	  spring[235] = new RGB(255, 235, 20);
	  spring[236] = new RGB(255, 236, 18);
	  spring[237] = new RGB(255, 237, 17);
	  spring[238] = new RGB(255, 238, 16);
	  spring[239] = new RGB(255, 239, 16);
	  spring[240] = new RGB(255, 240, 15);
	  spring[241] = new RGB(255, 241, 14);
	  spring[242] = new RGB(255, 242, 13);
	  spring[243] = new RGB(255, 243, 12);
	  spring[244] = new RGB(255, 244, 10);
	  spring[245] = new RGB(255, 245, 9);
	  spring[246] = new RGB(255, 246, 8);
	  spring[247] = new RGB(255, 247, 8);
	  spring[248] = new RGB(255, 248, 7);
	  spring[249] = new RGB(255, 249, 6);
	  spring[250] = new RGB(255, 250, 5);
	  spring[251] = new RGB(255, 251, 4);
	  spring[252] = new RGB(255, 252, 2);
	  spring[253] = new RGB(255, 253, 1);
	  spring[254] = new RGB(255, 254, 0);
	  spring[255] = new RGB(255, 255, 0);
	  return new PaletteData(spring);
	}
	/**
	 * Make 256 level Summer palette.
	 */
	public static PaletteData makeSummerPalette() {
	  RGB summer[] = new RGB[256];
	  summer[0] = new RGB(0, 127, 102);
	  summer[1] = new RGB(1, 128, 102);
	  summer[2] = new RGB(2, 128, 102);
	  summer[3] = new RGB(3, 129, 102);
	  summer[4] = new RGB(4, 129, 102);
	  summer[5] = new RGB(5, 130, 102);
	  summer[6] = new RGB(6, 130, 102);
	  summer[7] = new RGB(7, 131, 102);
	  summer[8] = new RGB(8, 131, 102);
	  summer[9] = new RGB(9, 132, 102);
	  summer[10] = new RGB(10, 132, 102);
	  summer[11] = new RGB(11, 133, 102);
	  summer[12] = new RGB(12, 133, 102);
	  summer[13] = new RGB(13, 134, 102);
	  summer[14] = new RGB(14, 134, 102);
	  summer[15] = new RGB(15, 135, 102);
	  summer[16] = new RGB(16, 135, 102);
	  summer[17] = new RGB(17, 136, 102);
	  summer[18] = new RGB(18, 136, 102);
	  summer[19] = new RGB(19, 137, 102);
	  summer[20] = new RGB(20, 137, 102);
	  summer[21] = new RGB(21, 138, 102);
	  summer[22] = new RGB(22, 138, 102);
	  summer[23] = new RGB(23, 139, 102);
	  summer[24] = new RGB(24, 139, 102);
	  summer[25] = new RGB(25, 140, 102);
	  summer[26] = new RGB(26, 140, 102);
	  summer[27] = new RGB(27, 141, 102);
	  summer[28] = new RGB(28, 141, 102);
	  summer[29] = new RGB(29, 142, 102);
	  summer[30] = new RGB(30, 142, 102);
	  summer[31] = new RGB(31, 143, 102);
	  summer[32] = new RGB(32, 143, 102);
	  summer[33] = new RGB(33, 144, 102);
	  summer[34] = new RGB(34, 144, 102);
	  summer[35] = new RGB(35, 145, 102);
	  summer[36] = new RGB(36, 145, 102);
	  summer[37] = new RGB(37, 146, 102);
	  summer[38] = new RGB(38, 146, 102);
	  summer[39] = new RGB(39, 147, 102);
	  summer[40] = new RGB(40, 147, 102);
	  summer[41] = new RGB(41, 148, 102);
	  summer[42] = new RGB(42, 148, 102);
	  summer[43] = new RGB(43, 149, 102);
	  summer[44] = new RGB(44, 149, 102);
	  summer[45] = new RGB(45, 150, 102);
	  summer[46] = new RGB(46, 150, 102);
	  summer[47] = new RGB(47, 151, 102);
	  summer[48] = new RGB(48, 151, 102);
	  summer[49] = new RGB(49, 152, 102);
	  summer[50] = new RGB(50, 152, 102);
	  summer[51] = new RGB(51, 153, 102);
	  summer[52] = new RGB(52, 153, 102);
	  summer[53] = new RGB(53, 154, 102);
	  summer[54] = new RGB(54, 154, 102);
	  summer[55] = new RGB(55, 155, 102);
	  summer[56] = new RGB(56, 155, 102);
	  summer[57] = new RGB(57, 156, 102);
	  summer[58] = new RGB(58, 156, 102);
	  summer[59] = new RGB(59, 157, 102);
	  summer[60] = new RGB(60, 157, 102);
	  summer[61] = new RGB(61, 158, 102);
	  summer[62] = new RGB(62, 158, 102);
	  summer[63] = new RGB(63, 159, 102);
	  summer[64] = new RGB(64, 159, 102);
	  summer[65] = new RGB(65, 160, 102);
	  summer[66] = new RGB(66, 160, 102);
	  summer[67] = new RGB(67, 161, 102);
	  summer[68] = new RGB(68, 161, 102);
	  summer[69] = new RGB(69, 162, 102);
	  summer[70] = new RGB(70, 162, 102);
	  summer[71] = new RGB(71, 163, 102);
	  summer[72] = new RGB(72, 163, 102);
	  summer[73] = new RGB(73, 163, 102);
	  summer[74] = new RGB(74, 164, 102);
	  summer[75] = new RGB(75, 165, 102);
	  summer[76] = new RGB(76, 165, 102);
	  summer[77] = new RGB(77, 166, 102);
	  summer[78] = new RGB(78, 166, 102);
	  summer[79] = new RGB(79, 167, 102);
	  summer[80] = new RGB(80, 167, 102);
	  summer[81] = new RGB(81, 168, 102);
	  summer[82] = new RGB(82, 168, 102);
	  summer[83] = new RGB(83, 169, 102);
	  summer[84] = new RGB(84, 169, 102);
	  summer[85] = new RGB(85, 170, 102);
	  summer[86] = new RGB(86, 170, 102);
	  summer[87] = new RGB(87, 171, 102);
	  summer[88] = new RGB(88, 171, 102);
	  summer[89] = new RGB(89, 172, 102);
	  summer[90] = new RGB(90, 172, 102);
	  summer[91] = new RGB(91, 173, 102);
	  summer[92] = new RGB(92, 173, 102);
	  summer[93] = new RGB(93, 174, 102);
	  summer[94] = new RGB(94, 174, 102);
	  summer[95] = new RGB(95, 175, 102);
	  summer[96] = new RGB(96, 175, 102);
	  summer[97] = new RGB(97, 176, 102);
	  summer[98] = new RGB(98, 176, 102);
	  summer[99] = new RGB(99, 177, 102);
	  summer[100] = new RGB(100, 177, 102);
	  summer[101] = new RGB(101, 178, 102);
	  summer[102] = new RGB(102, 178, 102);
	  summer[103] = new RGB(103, 179, 102);
	  summer[104] = new RGB(104, 179, 102);
	  summer[105] = new RGB(105, 179, 102);
	  summer[106] = new RGB(106, 180, 102);
	  summer[107] = new RGB(107, 181, 102);
	  summer[108] = new RGB(108, 181, 102);
	  summer[109] = new RGB(109, 182, 102);
	  summer[110] = new RGB(110, 182, 102);
	  summer[111] = new RGB(111, 183, 102);
	  summer[112] = new RGB(112, 183, 102);
	  summer[113] = new RGB(113, 184, 102);
	  summer[114] = new RGB(114, 184, 102);
	  summer[115] = new RGB(115, 185, 102);
	  summer[116] = new RGB(116, 185, 102);
	  summer[117] = new RGB(117, 186, 102);
	  summer[118] = new RGB(118, 186, 102);
	  summer[119] = new RGB(119, 187, 102);
	  summer[120] = new RGB(120, 187, 102);
	  summer[121] = new RGB(121, 188, 102);
	  summer[122] = new RGB(122, 188, 102);
	  summer[123] = new RGB(123, 189, 102);
	  summer[124] = new RGB(124, 189, 102);
	  summer[125] = new RGB(125, 190, 102);
	  summer[126] = new RGB(126, 190, 102);
	  summer[127] = new RGB(127, 191, 102);
	  summer[128] = new RGB(128, 191, 102);
	  summer[129] = new RGB(129, 192, 102);
	  summer[130] = new RGB(130, 192, 102);
	  summer[131] = new RGB(131, 193, 102);
	  summer[132] = new RGB(132, 193, 102);
	  summer[133] = new RGB(133, 194, 102);
	  summer[134] = new RGB(134, 194, 102);
	  summer[135] = new RGB(135, 195, 102);
	  summer[136] = new RGB(136, 195, 102);
	  summer[137] = new RGB(137, 195, 102);
	  summer[138] = new RGB(138, 196, 102);
	  summer[139] = new RGB(139, 196, 102);
	  summer[140] = new RGB(140, 197, 102);
	  summer[141] = new RGB(141, 198, 102);
	  summer[142] = new RGB(142, 198, 102);
	  summer[143] = new RGB(143, 199, 102);
	  summer[144] = new RGB(144, 199, 102);
	  summer[145] = new RGB(145, 200, 102);
	  summer[146] = new RGB(146, 200, 102);
	  summer[147] = new RGB(147, 201, 102);
	  summer[148] = new RGB(148, 201, 102);
	  summer[149] = new RGB(149, 202, 102);
	  summer[150] = new RGB(150, 202, 102);
	  summer[151] = new RGB(151, 203, 102);
	  summer[152] = new RGB(152, 203, 102);
	  summer[153] = new RGB(153, 204, 102);
	  summer[154] = new RGB(154, 204, 102);
	  summer[155] = new RGB(155, 205, 102);
	  summer[156] = new RGB(156, 205, 102);
	  summer[157] = new RGB(157, 206, 102);
	  summer[158] = new RGB(158, 206, 102);
	  summer[159] = new RGB(159, 207, 102);
	  summer[160] = new RGB(160, 207, 102);
	  summer[161] = new RGB(161, 208, 102);
	  summer[162] = new RGB(162, 208, 102);
	  summer[163] = new RGB(163, 209, 102);
	  summer[164] = new RGB(164, 209, 102);
	  summer[165] = new RGB(165, 210, 102);
	  summer[166] = new RGB(166, 210, 102);
	  summer[167] = new RGB(167, 211, 102);
	  summer[168] = new RGB(168, 211, 102);
	  summer[169] = new RGB(169, 211, 102);
	  summer[170] = new RGB(170, 212, 102);
	  summer[171] = new RGB(171, 212, 102);
	  summer[172] = new RGB(172, 213, 102);
	  summer[173] = new RGB(173, 214, 102);
	  summer[174] = new RGB(174, 214, 102);
	  summer[175] = new RGB(175, 215, 102);
	  summer[176] = new RGB(176, 215, 102);
	  summer[177] = new RGB(177, 216, 102);
	  summer[178] = new RGB(178, 216, 102);
	  summer[179] = new RGB(179, 217, 102);
	  summer[180] = new RGB(180, 217, 102);
	  summer[181] = new RGB(181, 218, 102);
	  summer[182] = new RGB(182, 218, 102);
	  summer[183] = new RGB(183, 219, 102);
	  summer[184] = new RGB(184, 219, 102);
	  summer[185] = new RGB(185, 220, 102);
	  summer[186] = new RGB(186, 220, 102);
	  summer[187] = new RGB(187, 221, 102);
	  summer[188] = new RGB(188, 221, 102);
	  summer[189] = new RGB(189, 222, 102);
	  summer[190] = new RGB(190, 222, 102);
	  summer[191] = new RGB(191, 223, 102);
	  summer[192] = new RGB(192, 223, 102);
	  summer[193] = new RGB(193, 224, 102);
	  summer[194] = new RGB(194, 224, 102);
	  summer[195] = new RGB(195, 225, 102);
	  summer[196] = new RGB(196, 225, 102);
	  summer[197] = new RGB(197, 226, 102);
	  summer[198] = new RGB(198, 226, 102);
	  summer[199] = new RGB(199, 227, 102);
	  summer[200] = new RGB(200, 227, 102);
	  summer[201] = new RGB(201, 227, 102);
	  summer[202] = new RGB(202, 228, 102);
	  summer[203] = new RGB(203, 228, 102);
	  summer[204] = new RGB(204, 229, 102);
	  summer[205] = new RGB(205, 230, 102);
	  summer[206] = new RGB(206, 230, 102);
	  summer[207] = new RGB(207, 231, 102);
	  summer[208] = new RGB(208, 231, 102);
	  summer[209] = new RGB(209, 232, 102);
	  summer[210] = new RGB(210, 232, 102);
	  summer[211] = new RGB(211, 233, 102);
	  summer[212] = new RGB(212, 233, 102);
	  summer[213] = new RGB(213, 234, 102);
	  summer[214] = new RGB(214, 234, 102);
	  summer[215] = new RGB(215, 235, 102);
	  summer[216] = new RGB(216, 235, 102);
	  summer[217] = new RGB(217, 236, 102);
	  summer[218] = new RGB(218, 236, 102);
	  summer[219] = new RGB(219, 237, 102);
	  summer[220] = new RGB(220, 237, 102);
	  summer[221] = new RGB(221, 238, 102);
	  summer[222] = new RGB(222, 238, 102);
	  summer[223] = new RGB(223, 239, 102);
	  summer[224] = new RGB(224, 239, 102);
	  summer[225] = new RGB(225, 240, 102);
	  summer[226] = new RGB(226, 240, 102);
	  summer[227] = new RGB(227, 241, 102);
	  summer[228] = new RGB(228, 241, 102);
	  summer[229] = new RGB(229, 242, 102);
	  summer[230] = new RGB(230, 242, 102);
	  summer[231] = new RGB(231, 243, 102);
	  summer[232] = new RGB(232, 243, 102);
	  summer[233] = new RGB(233, 243, 102);
	  summer[234] = new RGB(234, 244, 102);
	  summer[235] = new RGB(235, 244, 102);
	  summer[236] = new RGB(236, 245, 102);
	  summer[237] = new RGB(237, 246, 102);
	  summer[238] = new RGB(238, 246, 102);
	  summer[239] = new RGB(239, 247, 102);
	  summer[240] = new RGB(240, 247, 102);
	  summer[241] = new RGB(241, 248, 102);
	  summer[242] = new RGB(242, 248, 102);
	  summer[243] = new RGB(243, 249, 102);
	  summer[244] = new RGB(244, 249, 102);
	  summer[245] = new RGB(245, 250, 102);
	  summer[246] = new RGB(246, 250, 102);
	  summer[247] = new RGB(247, 251, 102);
	  summer[248] = new RGB(248, 251, 102);
	  summer[249] = new RGB(249, 252, 102);
	  summer[250] = new RGB(250, 252, 102);
	  summer[251] = new RGB(251, 253, 102);
	  summer[252] = new RGB(252, 253, 102);
	  summer[253] = new RGB(253, 254, 102);
	  summer[254] = new RGB(254, 254, 102);
	  summer[255] = new RGB(255, 255, 102);
	  return new PaletteData(summer);
	}
	/**
	 * Make 256 level Autumn palette.
	 */
	public static PaletteData makeAutumnPalette() {
	  RGB autumn[] = new RGB[256];
	  autumn[0] = new RGB(255, 0, 0);
	  autumn[1] = new RGB(255, 1, 0);
	  autumn[2] = new RGB(255, 2, 0);
	  autumn[3] = new RGB(255, 3, 0);
	  autumn[4] = new RGB(255, 4, 0);
	  autumn[5] = new RGB(255, 5, 0);
	  autumn[6] = new RGB(255, 6, 0);
	  autumn[7] = new RGB(255, 7, 0);
	  autumn[8] = new RGB(255, 8, 0);
	  autumn[9] = new RGB(255, 9, 0);
	  autumn[10] = new RGB(255, 10, 0);
	  autumn[11] = new RGB(255, 11, 0);
	  autumn[12] = new RGB(255, 12, 0);
	  autumn[13] = new RGB(255, 13, 0);
	  autumn[14] = new RGB(255, 14, 0);
	  autumn[15] = new RGB(255, 15, 0);
	  autumn[16] = new RGB(255, 16, 0);
	  autumn[17] = new RGB(255, 17, 0);
	  autumn[18] = new RGB(255, 18, 0);
	  autumn[19] = new RGB(255, 19, 0);
	  autumn[20] = new RGB(255, 20, 0);
	  autumn[21] = new RGB(255, 21, 0);
	  autumn[22] = new RGB(255, 22, 0);
	  autumn[23] = new RGB(255, 23, 0);
	  autumn[24] = new RGB(255, 24, 0);
	  autumn[25] = new RGB(255, 25, 0);
	  autumn[26] = new RGB(255, 26, 0);
	  autumn[27] = new RGB(255, 27, 0);
	  autumn[28] = new RGB(255, 28, 0);
	  autumn[29] = new RGB(255, 29, 0);
	  autumn[30] = new RGB(255, 30, 0);
	  autumn[31] = new RGB(255, 31, 0);
	  autumn[32] = new RGB(255, 32, 0);
	  autumn[33] = new RGB(255, 33, 0);
	  autumn[34] = new RGB(255, 34, 0);
	  autumn[35] = new RGB(255, 35, 0);
	  autumn[36] = new RGB(255, 36, 0);
	  autumn[37] = new RGB(255, 37, 0);
	  autumn[38] = new RGB(255, 38, 0);
	  autumn[39] = new RGB(255, 39, 0);
	  autumn[40] = new RGB(255, 40, 0);
	  autumn[41] = new RGB(255, 41, 0);
	  autumn[42] = new RGB(255, 42, 0);
	  autumn[43] = new RGB(255, 43, 0);
	  autumn[44] = new RGB(255, 44, 0);
	  autumn[45] = new RGB(255, 45, 0);
	  autumn[46] = new RGB(255, 46, 0);
	  autumn[47] = new RGB(255, 47, 0);
	  autumn[48] = new RGB(255, 48, 0);
	  autumn[49] = new RGB(255, 49, 0);
	  autumn[50] = new RGB(255, 50, 0);
	  autumn[51] = new RGB(255, 51, 0);
	  autumn[52] = new RGB(255, 52, 0);
	  autumn[53] = new RGB(255, 53, 0);
	  autumn[54] = new RGB(255, 54, 0);
	  autumn[55] = new RGB(255, 55, 0);
	  autumn[56] = new RGB(255, 56, 0);
	  autumn[57] = new RGB(255, 57, 0);
	  autumn[58] = new RGB(255, 58, 0);
	  autumn[59] = new RGB(255, 59, 0);
	  autumn[60] = new RGB(255, 60, 0);
	  autumn[61] = new RGB(255, 61, 0);
	  autumn[62] = new RGB(255, 62, 0);
	  autumn[63] = new RGB(255, 63, 0);
	  autumn[64] = new RGB(255, 64, 0);
	  autumn[65] = new RGB(255, 65, 0);
	  autumn[66] = new RGB(255, 66, 0);
	  autumn[67] = new RGB(255, 67, 0);
	  autumn[68] = new RGB(255, 68, 0);
	  autumn[69] = new RGB(255, 69, 0);
	  autumn[70] = new RGB(255, 70, 0);
	  autumn[71] = new RGB(255, 71, 0);
	  autumn[72] = new RGB(255, 72, 0);
	  autumn[73] = new RGB(255, 73, 0);
	  autumn[74] = new RGB(255, 74, 0);
	  autumn[75] = new RGB(255, 75, 0);
	  autumn[76] = new RGB(255, 76, 0);
	  autumn[77] = new RGB(255, 77, 0);
	  autumn[78] = new RGB(255, 78, 0);
	  autumn[79] = new RGB(255, 79, 0);
	  autumn[80] = new RGB(255, 80, 0);
	  autumn[81] = new RGB(255, 81, 0);
	  autumn[82] = new RGB(255, 82, 0);
	  autumn[83] = new RGB(255, 83, 0);
	  autumn[84] = new RGB(255, 84, 0);
	  autumn[85] = new RGB(255, 85, 0);
	  autumn[86] = new RGB(255, 86, 0);
	  autumn[87] = new RGB(255, 87, 0);
	  autumn[88] = new RGB(255, 88, 0);
	  autumn[89] = new RGB(255, 89, 0);
	  autumn[90] = new RGB(255, 90, 0);
	  autumn[91] = new RGB(255, 91, 0);
	  autumn[92] = new RGB(255, 92, 0);
	  autumn[93] = new RGB(255, 93, 0);
	  autumn[94] = new RGB(255, 94, 0);
	  autumn[95] = new RGB(255, 95, 0);
	  autumn[96] = new RGB(255, 96, 0);
	  autumn[97] = new RGB(255, 97, 0);
	  autumn[98] = new RGB(255, 98, 0);
	  autumn[99] = new RGB(255, 99, 0);
	  autumn[100] = new RGB(255, 100, 0);
	  autumn[101] = new RGB(255, 101, 0);
	  autumn[102] = new RGB(255, 102, 0);
	  autumn[103] = new RGB(255, 103, 0);
	  autumn[104] = new RGB(255, 104, 0);
	  autumn[105] = new RGB(255, 105, 0);
	  autumn[106] = new RGB(255, 106, 0);
	  autumn[107] = new RGB(255, 107, 0);
	  autumn[108] = new RGB(255, 108, 0);
	  autumn[109] = new RGB(255, 109, 0);
	  autumn[110] = new RGB(255, 110, 0);
	  autumn[111] = new RGB(255, 111, 0);
	  autumn[112] = new RGB(255, 112, 0);
	  autumn[113] = new RGB(255, 113, 0);
	  autumn[114] = new RGB(255, 114, 0);
	  autumn[115] = new RGB(255, 115, 0);
	  autumn[116] = new RGB(255, 116, 0);
	  autumn[117] = new RGB(255, 117, 0);
	  autumn[118] = new RGB(255, 118, 0);
	  autumn[119] = new RGB(255, 119, 0);
	  autumn[120] = new RGB(255, 120, 0);
	  autumn[121] = new RGB(255, 121, 0);
	  autumn[122] = new RGB(255, 122, 0);
	  autumn[123] = new RGB(255, 123, 0);
	  autumn[124] = new RGB(255, 124, 0);
	  autumn[125] = new RGB(255, 125, 0);
	  autumn[126] = new RGB(255, 126, 0);
	  autumn[127] = new RGB(255, 127, 0);
	  autumn[128] = new RGB(255, 128, 0);
	  autumn[129] = new RGB(255, 129, 0);
	  autumn[130] = new RGB(255, 130, 0);
	  autumn[131] = new RGB(255, 131, 0);
	  autumn[132] = new RGB(255, 132, 0);
	  autumn[133] = new RGB(255, 133, 0);
	  autumn[134] = new RGB(255, 134, 0);
	  autumn[135] = new RGB(255, 135, 0);
	  autumn[136] = new RGB(255, 136, 0);
	  autumn[137] = new RGB(255, 137, 0);
	  autumn[138] = new RGB(255, 138, 0);
	  autumn[139] = new RGB(255, 139, 0);
	  autumn[140] = new RGB(255, 140, 0);
	  autumn[141] = new RGB(255, 141, 0);
	  autumn[142] = new RGB(255, 142, 0);
	  autumn[143] = new RGB(255, 143, 0);
	  autumn[144] = new RGB(255, 144, 0);
	  autumn[145] = new RGB(255, 145, 0);
	  autumn[146] = new RGB(255, 146, 0);
	  autumn[147] = new RGB(255, 147, 0);
	  autumn[148] = new RGB(255, 148, 0);
	  autumn[149] = new RGB(255, 149, 0);
	  autumn[150] = new RGB(255, 150, 0);
	  autumn[151] = new RGB(255, 151, 0);
	  autumn[152] = new RGB(255, 152, 0);
	  autumn[153] = new RGB(255, 153, 0);
	  autumn[154] = new RGB(255, 154, 0);
	  autumn[155] = new RGB(255, 155, 0);
	  autumn[156] = new RGB(255, 156, 0);
	  autumn[157] = new RGB(255, 157, 0);
	  autumn[158] = new RGB(255, 158, 0);
	  autumn[159] = new RGB(255, 159, 0);
	  autumn[160] = new RGB(255, 160, 0);
	  autumn[161] = new RGB(255, 161, 0);
	  autumn[162] = new RGB(255, 162, 0);
	  autumn[163] = new RGB(255, 163, 0);
	  autumn[164] = new RGB(255, 164, 0);
	  autumn[165] = new RGB(255, 165, 0);
	  autumn[166] = new RGB(255, 166, 0);
	  autumn[167] = new RGB(255, 167, 0);
	  autumn[168] = new RGB(255, 168, 0);
	  autumn[169] = new RGB(255, 169, 0);
	  autumn[170] = new RGB(255, 170, 0);
	  autumn[171] = new RGB(255, 171, 0);
	  autumn[172] = new RGB(255, 172, 0);
	  autumn[173] = new RGB(255, 173, 0);
	  autumn[174] = new RGB(255, 174, 0);
	  autumn[175] = new RGB(255, 175, 0);
	  autumn[176] = new RGB(255, 176, 0);
	  autumn[177] = new RGB(255, 177, 0);
	  autumn[178] = new RGB(255, 178, 0);
	  autumn[179] = new RGB(255, 179, 0);
	  autumn[180] = new RGB(255, 180, 0);
	  autumn[181] = new RGB(255, 181, 0);
	  autumn[182] = new RGB(255, 182, 0);
	  autumn[183] = new RGB(255, 183, 0);
	  autumn[184] = new RGB(255, 184, 0);
	  autumn[185] = new RGB(255, 185, 0);
	  autumn[186] = new RGB(255, 186, 0);
	  autumn[187] = new RGB(255, 187, 0);
	  autumn[188] = new RGB(255, 188, 0);
	  autumn[189] = new RGB(255, 189, 0);
	  autumn[190] = new RGB(255, 190, 0);
	  autumn[191] = new RGB(255, 191, 0);
	  autumn[192] = new RGB(255, 192, 0);
	  autumn[193] = new RGB(255, 193, 0);
	  autumn[194] = new RGB(255, 194, 0);
	  autumn[195] = new RGB(255, 195, 0);
	  autumn[196] = new RGB(255, 196, 0);
	  autumn[197] = new RGB(255, 197, 0);
	  autumn[198] = new RGB(255, 198, 0);
	  autumn[199] = new RGB(255, 199, 0);
	  autumn[200] = new RGB(255, 200, 0);
	  autumn[201] = new RGB(255, 201, 0);
	  autumn[202] = new RGB(255, 202, 0);
	  autumn[203] = new RGB(255, 203, 0);
	  autumn[204] = new RGB(255, 204, 0);
	  autumn[205] = new RGB(255, 205, 0);
	  autumn[206] = new RGB(255, 206, 0);
	  autumn[207] = new RGB(255, 207, 0);
	  autumn[208] = new RGB(255, 208, 0);
	  autumn[209] = new RGB(255, 209, 0);
	  autumn[210] = new RGB(255, 210, 0);
	  autumn[211] = new RGB(255, 211, 0);
	  autumn[212] = new RGB(255, 212, 0);
	  autumn[213] = new RGB(255, 213, 0);
	  autumn[214] = new RGB(255, 214, 0);
	  autumn[215] = new RGB(255, 215, 0);
	  autumn[216] = new RGB(255, 216, 0);
	  autumn[217] = new RGB(255, 217, 0);
	  autumn[218] = new RGB(255, 218, 0);
	  autumn[219] = new RGB(255, 219, 0);
	  autumn[220] = new RGB(255, 220, 0);
	  autumn[221] = new RGB(255, 221, 0);
	  autumn[222] = new RGB(255, 222, 0);
	  autumn[223] = new RGB(255, 223, 0);
	  autumn[224] = new RGB(255, 224, 0);
	  autumn[225] = new RGB(255, 225, 0);
	  autumn[226] = new RGB(255, 226, 0);
	  autumn[227] = new RGB(255, 227, 0);
	  autumn[228] = new RGB(255, 228, 0);
	  autumn[229] = new RGB(255, 229, 0);
	  autumn[230] = new RGB(255, 230, 0);
	  autumn[231] = new RGB(255, 231, 0);
	  autumn[232] = new RGB(255, 232, 0);
	  autumn[233] = new RGB(255, 233, 0);
	  autumn[234] = new RGB(255, 234, 0);
	  autumn[235] = new RGB(255, 235, 0);
	  autumn[236] = new RGB(255, 236, 0);
	  autumn[237] = new RGB(255, 237, 0);
	  autumn[238] = new RGB(255, 238, 0);
	  autumn[239] = new RGB(255, 239, 0);
	  autumn[240] = new RGB(255, 240, 0);
	  autumn[241] = new RGB(255, 241, 0);
	  autumn[242] = new RGB(255, 242, 0);
	  autumn[243] = new RGB(255, 243, 0);
	  autumn[244] = new RGB(255, 244, 0);
	  autumn[245] = new RGB(255, 245, 0);
	  autumn[246] = new RGB(255, 246, 0);
	  autumn[247] = new RGB(255, 247, 0);
	  autumn[248] = new RGB(255, 248, 0);
	  autumn[249] = new RGB(255, 249, 0);
	  autumn[250] = new RGB(255, 250, 0);
	  autumn[251] = new RGB(255, 251, 0);
	  autumn[252] = new RGB(255, 252, 0);
	  autumn[253] = new RGB(255, 253, 0);
	  autumn[254] = new RGB(255, 254, 0);
	  autumn[255] = new RGB(255, 255, 0);
	  return new PaletteData(autumn);
	}
	/**
	 * Make 256 level Winter palette.
	 */
	public static PaletteData makeWinterPalette() {
	  RGB winter[] = new RGB[256];
	  winter[0] = new RGB(0, 0, 255);
	  winter[1] = new RGB(0, 1, 254);
	  winter[2] = new RGB(0, 2, 254);
	  winter[3] = new RGB(0, 3, 253);
	  winter[4] = new RGB(0, 4, 253);
	  winter[5] = new RGB(0, 5, 252);
	  winter[6] = new RGB(0, 6, 252);
	  winter[7] = new RGB(0, 7, 251);
	  winter[8] = new RGB(0, 8, 251);
	  winter[9] = new RGB(0, 9, 250);
	  winter[10] = new RGB(0, 10, 250);
	  winter[11] = new RGB(0, 11, 249);
	  winter[12] = new RGB(0, 12, 249);
	  winter[13] = new RGB(0, 13, 248);
	  winter[14] = new RGB(0, 14, 248);
	  winter[15] = new RGB(0, 15, 247);
	  winter[16] = new RGB(0, 16, 247);
	  winter[17] = new RGB(0, 17, 246);
	  winter[18] = new RGB(0, 18, 246);
	  winter[19] = new RGB(0, 19, 245);
	  winter[20] = new RGB(0, 20, 245);
	  winter[21] = new RGB(0, 21, 244);
	  winter[22] = new RGB(0, 22, 244);
	  winter[23] = new RGB(0, 23, 243);
	  winter[24] = new RGB(0, 24, 243);
	  winter[25] = new RGB(0, 25, 242);
	  winter[26] = new RGB(0, 26, 242);
	  winter[27] = new RGB(0, 27, 241);
	  winter[28] = new RGB(0, 28, 241);
	  winter[29] = new RGB(0, 29, 240);
	  winter[30] = new RGB(0, 30, 240);
	  winter[31] = new RGB(0, 31, 239);
	  winter[32] = new RGB(0, 32, 239);
	  winter[33] = new RGB(0, 33, 238);
	  winter[34] = new RGB(0, 34, 238);
	  winter[35] = new RGB(0, 35, 237);
	  winter[36] = new RGB(0, 36, 237);
	  winter[37] = new RGB(0, 37, 236);
	  winter[38] = new RGB(0, 38, 236);
	  winter[39] = new RGB(0, 39, 235);
	  winter[40] = new RGB(0, 40, 235);
	  winter[41] = new RGB(0, 41, 234);
	  winter[42] = new RGB(0, 42, 234);
	  winter[43] = new RGB(0, 43, 233);
	  winter[44] = new RGB(0, 44, 233);
	  winter[45] = new RGB(0, 45, 232);
	  winter[46] = new RGB(0, 46, 232);
	  winter[47] = new RGB(0, 47, 231);
	  winter[48] = new RGB(0, 48, 231);
	  winter[49] = new RGB(0, 49, 230);
	  winter[50] = new RGB(0, 50, 230);
	  winter[51] = new RGB(0, 51, 229);
	  winter[52] = new RGB(0, 52, 229);
	  winter[53] = new RGB(0, 53, 228);
	  winter[54] = new RGB(0, 54, 228);
	  winter[55] = new RGB(0, 55, 227);
	  winter[56] = new RGB(0, 56, 227);
	  winter[57] = new RGB(0, 57, 226);
	  winter[58] = new RGB(0, 58, 226);
	  winter[59] = new RGB(0, 59, 225);
	  winter[60] = new RGB(0, 60, 225);
	  winter[61] = new RGB(0, 61, 224);
	  winter[62] = new RGB(0, 62, 224);
	  winter[63] = new RGB(0, 63, 223);
	  winter[64] = new RGB(0, 64, 223);
	  winter[65] = new RGB(0, 65, 222);
	  winter[66] = new RGB(0, 66, 222);
	  winter[67] = new RGB(0, 67, 221);
	  winter[68] = new RGB(0, 68, 221);
	  winter[69] = new RGB(0, 69, 220);
	  winter[70] = new RGB(0, 70, 220);
	  winter[71] = new RGB(0, 71, 219);
	  winter[72] = new RGB(0, 72, 219);
	  winter[73] = new RGB(0, 73, 218);
	  winter[74] = new RGB(0, 74, 218);
	  winter[75] = new RGB(0, 75, 217);
	  winter[76] = new RGB(0, 76, 217);
	  winter[77] = new RGB(0, 77, 216);
	  winter[78] = new RGB(0, 78, 216);
	  winter[79] = new RGB(0, 79, 215);
	  winter[80] = new RGB(0, 80, 215);
	  winter[81] = new RGB(0, 81, 214);
	  winter[82] = new RGB(0, 82, 214);
	  winter[83] = new RGB(0, 83, 213);
	  winter[84] = new RGB(0, 84, 213);
	  winter[85] = new RGB(0, 85, 212);
	  winter[86] = new RGB(0, 86, 211);
	  winter[87] = new RGB(0, 87, 211);
	  winter[88] = new RGB(0, 88, 211);
	  winter[89] = new RGB(0, 89, 210);
	  winter[90] = new RGB(0, 90, 210);
	  winter[91] = new RGB(0, 91, 209);
	  winter[92] = new RGB(0, 92, 209);
	  winter[93] = new RGB(0, 93, 208);
	  winter[94] = new RGB(0, 94, 208);
	  winter[95] = new RGB(0, 95, 207);
	  winter[96] = new RGB(0, 96, 207);
	  winter[97] = new RGB(0, 97, 206);
	  winter[98] = new RGB(0, 98, 206);
	  winter[99] = new RGB(0, 99, 205);
	  winter[100] = new RGB(0, 100, 205);
	  winter[101] = new RGB(0, 101, 204);
	  winter[102] = new RGB(0, 102, 204);
	  winter[103] = new RGB(0, 103, 203);
	  winter[104] = new RGB(0, 104, 203);
	  winter[105] = new RGB(0, 105, 202);
	  winter[106] = new RGB(0, 106, 202);
	  winter[107] = new RGB(0, 107, 201);
	  winter[108] = new RGB(0, 108, 201);
	  winter[109] = new RGB(0, 109, 200);
	  winter[110] = new RGB(0, 110, 200);
	  winter[111] = new RGB(0, 111, 199);
	  winter[112] = new RGB(0, 112, 199);
	  winter[113] = new RGB(0, 113, 198);
	  winter[114] = new RGB(0, 114, 198);
	  winter[115] = new RGB(0, 115, 197);
	  winter[116] = new RGB(0, 116, 197);
	  winter[117] = new RGB(0, 117, 196);
	  winter[118] = new RGB(0, 118, 195);
	  winter[119] = new RGB(0, 119, 195);
	  winter[120] = new RGB(0, 120, 195);
	  winter[121] = new RGB(0, 121, 194);
	  winter[122] = new RGB(0, 122, 194);
	  winter[123] = new RGB(0, 123, 193);
	  winter[124] = new RGB(0, 124, 193);
	  winter[125] = new RGB(0, 125, 192);
	  winter[126] = new RGB(0, 126, 192);
	  winter[127] = new RGB(0, 127, 191);
	  winter[128] = new RGB(0, 128, 191);
	  winter[129] = new RGB(0, 129, 190);
	  winter[130] = new RGB(0, 130, 190);
	  winter[131] = new RGB(0, 131, 189);
	  winter[132] = new RGB(0, 132, 189);
	  winter[133] = new RGB(0, 133, 188);
	  winter[134] = new RGB(0, 134, 188);
	  winter[135] = new RGB(0, 135, 187);
	  winter[136] = new RGB(0, 136, 187);
	  winter[137] = new RGB(0, 137, 186);
	  winter[138] = new RGB(0, 138, 186);
	  winter[139] = new RGB(0, 139, 185);
	  winter[140] = new RGB(0, 140, 185);
	  winter[141] = new RGB(0, 141, 184);
	  winter[142] = new RGB(0, 142, 184);
	  winter[143] = new RGB(0, 143, 183);
	  winter[144] = new RGB(0, 144, 183);
	  winter[145] = new RGB(0, 145, 182);
	  winter[146] = new RGB(0, 146, 182);
	  winter[147] = new RGB(0, 147, 181);
	  winter[148] = new RGB(0, 148, 180);
	  winter[149] = new RGB(0, 149, 180);
	  winter[150] = new RGB(0, 150, 179);
	  winter[151] = new RGB(0, 151, 179);
	  winter[152] = new RGB(0, 152, 179);
	  winter[153] = new RGB(0, 153, 178);
	  winter[154] = new RGB(0, 154, 178);
	  winter[155] = new RGB(0, 155, 177);
	  winter[156] = new RGB(0, 156, 177);
	  winter[157] = new RGB(0, 157, 176);
	  winter[158] = new RGB(0, 158, 176);
	  winter[159] = new RGB(0, 159, 175);
	  winter[160] = new RGB(0, 160, 175);
	  winter[161] = new RGB(0, 161, 174);
	  winter[162] = new RGB(0, 162, 174);
	  winter[163] = new RGB(0, 163, 173);
	  winter[164] = new RGB(0, 164, 173);
	  winter[165] = new RGB(0, 165, 172);
	  winter[166] = new RGB(0, 166, 172);
	  winter[167] = new RGB(0, 167, 171);
	  winter[168] = new RGB(0, 168, 171);
	  winter[169] = new RGB(0, 169, 170);
	  winter[170] = new RGB(0, 170, 170);
	  winter[171] = new RGB(0, 171, 169);
	  winter[172] = new RGB(0, 172, 169);
	  winter[173] = new RGB(0, 173, 168);
	  winter[174] = new RGB(0, 174, 168);
	  winter[175] = new RGB(0, 175, 167);
	  winter[176] = new RGB(0, 176, 167);
	  winter[177] = new RGB(0, 177, 166);
	  winter[178] = new RGB(0, 178, 166);
	  winter[179] = new RGB(0, 179, 165);
	  winter[180] = new RGB(0, 180, 164);
	  winter[181] = new RGB(0, 181, 164);
	  winter[182] = new RGB(0, 182, 163);
	  winter[183] = new RGB(0, 183, 163);
	  winter[184] = new RGB(0, 184, 163);
	  winter[185] = new RGB(0, 185, 162);
	  winter[186] = new RGB(0, 186, 162);
	  winter[187] = new RGB(0, 187, 161);
	  winter[188] = new RGB(0, 188, 161);
	  winter[189] = new RGB(0, 189, 160);
	  winter[190] = new RGB(0, 190, 160);
	  winter[191] = new RGB(0, 191, 159);
	  winter[192] = new RGB(0, 192, 159);
	  winter[193] = new RGB(0, 193, 158);
	  winter[194] = new RGB(0, 194, 158);
	  winter[195] = new RGB(0, 195, 157);
	  winter[196] = new RGB(0, 196, 157);
	  winter[197] = new RGB(0, 197, 156);
	  winter[198] = new RGB(0, 198, 156);
	  winter[199] = new RGB(0, 199, 155);
	  winter[200] = new RGB(0, 200, 155);
	  winter[201] = new RGB(0, 201, 154);
	  winter[202] = new RGB(0, 202, 154);
	  winter[203] = new RGB(0, 203, 153);
	  winter[204] = new RGB(0, 204, 153);
	  winter[205] = new RGB(0, 205, 152);
	  winter[206] = new RGB(0, 206, 152);
	  winter[207] = new RGB(0, 207, 151);
	  winter[208] = new RGB(0, 208, 151);
	  winter[209] = new RGB(0, 209, 150);
	  winter[210] = new RGB(0, 210, 150);
	  winter[211] = new RGB(0, 211, 149);
	  winter[212] = new RGB(0, 212, 148);
	  winter[213] = new RGB(0, 213, 148);
	  winter[214] = new RGB(0, 214, 147);
	  winter[215] = new RGB(0, 215, 147);
	  winter[216] = new RGB(0, 216, 147);
	  winter[217] = new RGB(0, 217, 146);
	  winter[218] = new RGB(0, 218, 146);
	  winter[219] = new RGB(0, 219, 145);
	  winter[220] = new RGB(0, 220, 145);
	  winter[221] = new RGB(0, 221, 144);
	  winter[222] = new RGB(0, 222, 144);
	  winter[223] = new RGB(0, 223, 143);
	  winter[224] = new RGB(0, 224, 143);
	  winter[225] = new RGB(0, 225, 142);
	  winter[226] = new RGB(0, 226, 142);
	  winter[227] = new RGB(0, 227, 141);
	  winter[228] = new RGB(0, 228, 141);
	  winter[229] = new RGB(0, 229, 140);
	  winter[230] = new RGB(0, 230, 140);
	  winter[231] = new RGB(0, 231, 139);
	  winter[232] = new RGB(0, 232, 139);
	  winter[233] = new RGB(0, 233, 138);
	  winter[234] = new RGB(0, 234, 138);
	  winter[235] = new RGB(0, 235, 137);
	  winter[236] = new RGB(0, 236, 137);
	  winter[237] = new RGB(0, 237, 136);
	  winter[238] = new RGB(0, 238, 136);
	  winter[239] = new RGB(0, 239, 135);
	  winter[240] = new RGB(0, 240, 135);
	  winter[241] = new RGB(0, 241, 134);
	  winter[242] = new RGB(0, 242, 134);
	  winter[243] = new RGB(0, 243, 133);
	  winter[244] = new RGB(0, 244, 132);
	  winter[245] = new RGB(0, 245, 132);
	  winter[246] = new RGB(0, 246, 131);
	  winter[247] = new RGB(0, 247, 131);
	  winter[248] = new RGB(0, 248, 131);
	  winter[249] = new RGB(0, 249, 130);
	  winter[250] = new RGB(0, 250, 130);
	  winter[251] = new RGB(0, 251, 129);
	  winter[252] = new RGB(0, 252, 129);
	  winter[253] = new RGB(0, 253, 128);
	  winter[254] = new RGB(0, 254, 128);
	  winter[255] = new RGB(0, 255, 127);
	  return new PaletteData(winter);
	}
	
	/**
	 * Uses the users preferred choice for the palette
	 * @return
	 */
	public static PaletteData getPalette() throws Exception {
		
		final ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, "org.dawb.fable.imageviewer");
		final int index = store.getInt("palettePreference");
		return getPalette(index);
	}

	public static PaletteData getPalette(int index) throws Exception{
        return PaletteFactory.getPalette(index, false);
	}
	
	public static PaletteData getPalette(int index, final boolean createCopy) throws Exception {
		if (index < 0 || index >= PALETTES.size()) {
			return null;
		}
		if (palettes[index] == null) {
			switch (index) {
			case PaletteValues.PALETTE_GREY:
				palettes[index] = PaletteFactory.makeGrayScalePalette();
				break;
			case PaletteValues.PALETTE_GREY_INVERTED:
				palettes[index] = PaletteFactory.makeGrayScalePaletteInverted();
				break;
			case PaletteValues.PALETTE_COLOR:
				palettes[index] = PaletteFactory.makeColorPalette();
				break;
			case PaletteValues.PALETTE_RAINBOW1:
				palettes[index] = PaletteFactory.makeRainbow1Palette();
				break;
			case PaletteValues.PALETTE_RAINBOW2:
				palettes[index] = PaletteFactory.makeRainbow2Palette();
				break;
			case PaletteValues.PALETTE_BLUES:
				palettes[index] = PaletteFactory.makeBluesPalette();
				break;
			case PaletteValues.PALETTE_GREENS:
				palettes[index] = PaletteFactory.makeGreensPalette();
				break;
			case PaletteValues.PALETTE_REDS:
				palettes[index] = PaletteFactory.makeRedsPalette();
				break;
			case PaletteValues.PALETTE_PASTEL1:
				palettes[index] = PaletteFactory.makePastel1Palette();
				break;
			case PaletteValues.PALETTE_SPRING:
				palettes[index] = PaletteFactory.makeSpringPalette();
				break;
			case PaletteValues.PALETTE_SUMMER:
				palettes[index] = PaletteFactory.makeSummerPalette();
				break;
			case PaletteValues.PALETTE_AUTUMN:
				palettes[index] = PaletteFactory.makeAutumnPalette();
				break;
			case PaletteValues.PALETTE_WINTER:
				palettes[index] = PaletteFactory.makeWinterPalette();
				break;
			case PaletteValues.PALETTE_JET:
				palettes[index] = PaletteFactory.makeJetPalette();
				break;
			}
		}
		PaletteData ret = palettes[index];
		if (createCopy) {
			ret = new PaletteData((RGB[])ObjectUtils.deepCopy(ret.colors, RGB.class.getClassLoader()));
		}
		return ret;
	}
	
	public static Map<String, Integer> getPaletteNames() {

		Map<String,Integer> PALETTES = new LinkedHashMap<String,Integer>(11);

		PALETTES.put( "Grayscale", PaletteValues.PALETTE_GREY);
		PALETTES.put( "Grayscale Inverted", PaletteValues.PALETTE_GREY_INVERTED);
		PALETTES.put( "Color", PaletteValues.PALETTE_COLOR);
		PALETTES.put( "Rainbow 1", PaletteValues.PALETTE_RAINBOW1);
		PALETTES.put( "Rainbow 2", PaletteValues.PALETTE_RAINBOW2);
		PALETTES.put( "Blues", PaletteValues.PALETTE_BLUES);
		PALETTES.put( "Greens", PaletteValues.PALETTE_GREENS);
		PALETTES.put( "Reds", PaletteValues.PALETTE_REDS);
		PALETTES.put( "Pastel 1", PaletteValues.PALETTE_PASTEL1);
		PALETTES.put( "Spring", PaletteValues.PALETTE_SPRING);
		PALETTES.put( "Summer", PaletteValues.PALETTE_SUMMER);
		PALETTES.put( "Autumn", PaletteValues.PALETTE_AUTUMN);
		PALETTES.put( "Winter", PaletteValues.PALETTE_WINTER);
		PALETTES.put( "Jet", PaletteValues.PALETTE_JET);

		return PALETTES;
	}

	/**
	 * Returns an array of colors in a smooth palette from <code>start</code> to <code>end</code>.
	 * <p>
	 * The returned array has size <code>steps</code>, and the color at index 0 is <code>start</code>, the color
	 * at index <code>steps&nbsp;-&nbsp;1</code> is <code>end</code>.
	 * 
	 * @see org.eclipse.jface.internal.text.revisions.Colors
	 *
	 * @param start the start color of the palette
	 * @param end the end color of the palette
	 * @param steps the requested size, must be &gt; 0
	 * @return an array of <code>steps</code> colors in the palette from <code>start</code> to <code>end</code>
	 */
	private static RGB[] palette(RGB start, RGB end, int steps) {
		Assert.isLegal(start != null);
		Assert.isLegal(end != null);
		Assert.isLegal(steps > 0);

		if (steps == 1)
			return new RGB[] { start };

		float step= 1.0f / (steps - 1);
		RGB[] gradient= new RGB[steps];
		for (int i= 0; i < steps; i++)
			gradient[i]= blend(start, end, step * i);

		return gradient;
	}

	/**
	 * Returns an RGB that lies between the given foreground and background
	 * colors using the given mixing factor. A <code>factor</code> of 1.0 will produce a
	 * color equal to <code>fg</code>, while a <code>factor</code> of 0.0 will produce one
	 * equal to <code>bg</code>.
	 * @param bg the background color
	 * @param fg the foreground color
	 * @param factor the mixing factor, must be in [0,&nbsp;1]
	 *
	 * @return the interpolated color
	 */
	private static RGB blend(RGB bg, RGB fg, float factor) {
		Assert.isLegal(bg != null);
		Assert.isLegal(fg != null);
		Assert.isLegal(factor >= 0f && factor <= 1f);

		float complement= 1f - factor;
		return new RGB(
				(int) (complement * bg.red + factor * fg.red),
				(int) (complement * bg.green + factor * fg.green),
				(int) (complement * bg.blue + factor * fg.blue)
		);
	}
	
	/**
	 * Returns an array of colors with hues evenly distributed on the hue wheel defined by the <a
	 * href="http://en.wikipedia.org/wiki/HSV_color_space">HSB color space</a>. The returned array
	 * has size <code>steps</code>. The distance <var>d</var> between two successive colors is
	 * in [120&#176;,&nbsp;180&#176;].
	 * <p>
	 * The color at a given <code>index</code> has the hue returned by
	 * {@linkplain #computeHue(int) computeHue(index)}; i.e. the computed hues are not equidistant,
	 * but adaptively distributed on the color wheel.
	 * </p>
	 * <p>
	 * The first six colors returned correspond to the following {@link SWT} color constants:
	 * {@link SWT#COLOR_RED red}, {@link SWT#COLOR_GREEN green}, {@link SWT#COLOR_BLUE blue},
	 * {@link SWT#COLOR_YELLOW yellow}, {@link SWT#COLOR_CYAN cyan},
	 * {@link SWT#COLOR_MAGENTA magenta}.
	 * </p>
	 *
	 * @param steps the requested size, must be &gt;= 2
	 * @return an array of <code>steps</code> colors evenly distributed on the color wheel
	 */
	public static RGB[] rainbow(int steps) {
		Assert.isLegal(steps >= 2);

		RGB[] rainbow= new RGB[steps];
		for (int i= 0; i < steps; i++)
			rainbow[i]= new RGB(computeHue(i), 1f, 1f);

		return rainbow;
	}

	/**
	 * Returns an indexed hue in [0&#176;,&nbsp;360&#176;), distributing the hues evenly on the hue wheel
	 * defined by the <a href="http://en.wikipedia.org/wiki/HSV_color_space">HSB (or HSV) color
	 * space</a>. The distance <var>d</var> between two successive colors is in [120&#176;,&nbsp;180&#176;].
	 * <p>
	 * The first six colors returned correspond to the following {@link SWT} color constants:
	 * {@link SWT#COLOR_RED red}, {@link SWT#COLOR_GREEN green}, {@link SWT#COLOR_BLUE blue},
	 * {@link SWT#COLOR_YELLOW yellow}, {@link SWT#COLOR_CYAN cyan},
	 * {@link SWT#COLOR_MAGENTA magenta}.
	 * </p>
	 *
	 * @param index the index of the color, must be &gt;= 0
	 * @return a color hue in [0&#176;,&nbsp;360&#176;)
	 * @see RGB#RGB(float, float, float)
	 */
	public static float computeHue(final int index) {
		Assert.isLegal(index >= 0);
		/*
		 * Base 3 gives a nice partitioning for RGB colors with red, green, blue being the colors
		 * 0,1,2, and yellow, cyan, magenta colors 3,4,5.
		 */
		final int base= 3;
		final float range= 360f;

		// partition the baseRange by using the least significant bit to select one half of the
		// partitioning
		int baseIndex= index / base;
		float baseRange= range / base;
		float baseOffset= 0f;
		while (baseIndex > 0) {
			baseRange /= 2;
			int lsb= baseIndex % 2;
			baseOffset += lsb * baseRange;
			baseIndex >>= 1;
		}

		final int baseMod= index % base;
		final float hue= baseOffset + baseMod * range / base;
		Assert.isTrue(hue >= 0 && hue < 360);
		return hue;
	}
}
