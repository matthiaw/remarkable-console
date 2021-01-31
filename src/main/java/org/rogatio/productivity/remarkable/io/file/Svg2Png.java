/*
 * Remarkable Console - Copyright (C) 2021 Matthias Wegner
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.rogatio.productivity.remarkable.io.file;

import java.awt.RenderingHints;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.model.content.Page;

/**
 * The Class Svg2Png.
 */
public class Svg2Png {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(Svg2Png.class);

	public static void createThumbnail(Page page) throws TranscoderException, IOException {
		createPng(page, "_thumbnail", 0.1);
	}

	public static void createPng(Page page, double scale) throws TranscoderException, IOException {
		createPng(page, null, scale);
	}

	/**
	 * 
	 * @param page
	 * @param scale
	 * @throws TranscoderException
	 * @throws IOException
	 */
	public static void createPng(Page page, String suffix, double scale) throws TranscoderException, IOException {

		if (scale <= 0.0) {
			scale = 1.0;
		}

		String svg = Util.getFilename(page, "svg");

		String orientation = page.getNotebook().getContentData().getOrientation();

		if (!Util.fileExists(svg)) {
			if (orientation.equals("portrait")) {
				SvgDocument.createPortrait(page);
			} else {
				SvgDocument.createLandscape(page);
			}
		}

		String png = Util.getFilename(page, suffix, "png");

		if (suffix != null) {
			logger.info("Create '" + png + "'");
		} else {
			logger.info("Create '" + png + "'");
		}

		// https://stackoverflow.com/questions/45239099/apache-batik-no-writeadapter-is-available
		// Read the input SVG document into Transcoder Input
		String svg_URI_input = Paths.get(svg).toUri().toURL().toString();
		TranscoderInput input_svg_image = new TranscoderInput(svg_URI_input);
		// Define OutputStream to PNG Image and attach to TranscoderOutput
		OutputStream png_ostream = new FileOutputStream(png);
		TranscoderOutput output_png_image = new TranscoderOutput(png_ostream);

		// Create PNGTranscoder and define hints
		PNGTranscoder transcoder = new PNGTranscoder() {
			@Override
			protected ImageRenderer createRenderer() {
				ImageRenderer r = super.createRenderer();

				RenderingHints rh = r.getRenderingHints();
				rh.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION,
						RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
				rh.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BICUBIC));
				rh.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
				rh.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING,
						RenderingHints.VALUE_COLOR_RENDER_QUALITY));
				rh.add(new RenderingHints(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE));
				rh.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
				rh.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE));
				rh.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS,
						RenderingHints.VALUE_FRACTIONALMETRICS_ON));
				rh.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_OFF));

				r.setRenderingHints(rh);

				return r;
			}
		};

		// set target size of png
		if (orientation.equals("portrait")) {
			transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(page.getHorizontalWidth() * scale));
			transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(page.getVerticalWidth() * scale));
		} else {
			transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(page.getVerticalWidth() * scale));
			transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(page.getHorizontalWidth() * scale));
		}

		// Convert and Write output
		transcoder.transcode(input_svg_image, output_png_image);
		// Close / flush Output Stream
		png_ostream.flush();
		png_ostream.close();
	}
}
