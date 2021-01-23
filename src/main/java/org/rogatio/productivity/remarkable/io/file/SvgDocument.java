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

import static java.lang.String.format;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.model.content.Layer;
import org.rogatio.productivity.remarkable.model.content.Page;
import org.rogatio.productivity.remarkable.model.content.PencilType;
import org.rogatio.productivity.remarkable.model.content.Segment;
import org.rogatio.productivity.remarkable.model.content.Stroke;
import org.rogatio.productivity.remarkable.model.content.StrokeColor;

/**
 * The Class SvgDocument.
 */
public class SvgDocument {

	/** The Constant logger. */
	protected static final Logger logger = LogManager.getLogger(SvgDocument.class);

	/**
	 * Creates the.
	 *
	 * @param page the page
	 */
	public static void createPortrait(Page page) {
		String name = Util.getFilename(page, "svg");

		logger.info("Create '" + name + "'");

		File exportDir = new File(name);
		exportDir.getParentFile().mkdirs();

		createPortrait(page, name);
	}

	public static void createLandscape(Page page) {
		String name = Util.getFilename(page, "svg");

		logger.info("Create '" + name + "'");

		File exportDir = new File(name);
		exportDir.getParentFile().mkdirs();

		createLandscape(page, name);
	}

	/**
	 * Adds the polyline start.
	 *
	 * @param writer      the writer
	 * @param color       the color
	 * @param strokeWidth the stroke width
	 * @param opacity     the opacity
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void addPolylineStart(Writer writer, String color, float strokeWidth, double opacity)
			throws IOException {
		writer.write(format(Locale.US,
				"<polyline stroke-linejoin=\"round\" stroke-linecap=\"round\" shape-rendering=\"geometricPrecision\" style=\"fill:none;stroke:%s;stroke-width:%.2f;opacity:%.2f\" points=\"",
				color, strokeWidth, (float) opacity));
	}

	/**
	 * Adds the stroke.
	 *
	 * @param writer    the writer
	 * @param stroke    the stroke
	 * @param pecilType the pecil type
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void addStroke(Writer writer, Stroke stroke, PencilType pecilType) throws IOException {

		String color = stroke.getStrokeColor().getName();
		if (PencilType.HIGHLIGHTER == pecilType) {
			color = StrokeColor.HIGHLIGHT.getName();
		}

		if (pecilType == stroke.getPencilType()) {

			addPolylineStart(writer, color, stroke.getFirstSegment().getStrokeWidth(),
					stroke.getPencilType().getOpacity());

			for (Segment segment : stroke.getSegments()) {

				String xy = format(Locale.US, "%.2f, %.2f ", segment.getHorizontalAxis(), segment.getVertikalAxis());
				double opacity = ((double) Math
						.round(stroke.getPencilType().getOpacity() * (segment.getPenPressure()) * 100.0)) / 100.0;
				double strokeWidth = 0.9 * (segment.getStrokeWidth() + 0.6 * segment.getPenSpeed());

				if (segment.getSegmentNumber() % 8 == 0) {
					writer.write(xy + "\"/>\n");
					addPolylineStart(writer, color, (float) strokeWidth, opacity);
					writer.write(xy);
				} else {
					writer.write(xy);
				}
			}

			writer.write("\"/>\n");
		}
	}

	public static void createLandscape(Page page, String fileName) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

			writer.write(format(
					"<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"100%%\" width=\"100%%\" viewBox=\"0 0 %d %d\">",
					page.getVerticalWidth(), page.getHorizontalWidth()));

			int delta = - Math.abs((page.getVerticalWidth() - page.getHorizontalWidth()) / 2);

			writer.write(
					format("<g id=\"%s\" style=\"display:inline\" transform=\"rotate(90 %d %d) translate(%d %d)\">",
							UUID.randomUUID().toString(), page.getHorizontalWidth() / 2, page.getVerticalWidth() / 2,
							delta, delta));

			// Set highlighterStrokes beneath all other penStrokes
			for (Layer layer : page.getLayers()) {
				for (Stroke stroke : layer.getStrokes()) {
					addStroke(writer, stroke, PencilType.HIGHLIGHTER);
				}
			}

			// Set all other penStrokes
			for (Layer layer : page.getLayers()) {
				for (Stroke stroke : layer.getStrokes()) {
					if (stroke.getPencilType() != PencilType.HIGHLIGHTER) {
						addStroke(writer, stroke, stroke.getPencilType());
					}
				}
			}

			writer.write(format("<rect x=\"0\" y=\"0\" width=\"%d\" height=\"%d\" fill-opacity=\"0\"/>",
					page.getHorizontalWidth(), page.getVerticalWidth()));
			writer.write("</g>");
			writer.write("</svg>");

			writer.close();
		} catch (IOException e) {
		}
	}

	/**
	 * See https://github.com/reHackable/maxio/blob/master/tools/rM2svg
	 * 
	 * See https://remarkablewiki.com/tech/filesystem
	 *
	 * @param page     the page
	 * @param fileName the file name
	 */
	public static void createPortrait(Page page, String fileName) {

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

			writer.write(format(
					"<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"100%%\" width=\"100%%\" viewBox=\"0 0 %d %d\">",
					page.getHorizontalWidth(), page.getVerticalWidth()));

			writer.write(format("<g id=\"%s\" style=\"display:inline\">", UUID.randomUUID().toString()));

			// Set highlighterStrokes beneath all other penStrokes
			for (Layer layer : page.getLayers()) {
				for (Stroke stroke : layer.getStrokes()) {
					addStroke(writer, stroke, PencilType.HIGHLIGHTER);
				}
			}

			// Set all other penStrokes
			for (Layer layer : page.getLayers()) {
				for (Stroke stroke : layer.getStrokes()) {
					if (stroke.getPencilType() != PencilType.HIGHLIGHTER) {
						addStroke(writer, stroke, stroke.getPencilType());
					}
				}
			}

			writer.write(format("<rect x=\"0\" y=\"0\" width=\"%d\" height=\"%d\" fill-opacity=\"0\"/>",
					page.getHorizontalWidth(), page.getVerticalWidth()));
			writer.write("</g>");
			writer.write("</svg>");

			writer.close();
		} catch (IOException e) {
		}

	}

}
