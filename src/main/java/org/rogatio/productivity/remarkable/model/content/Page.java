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
package org.rogatio.productivity.remarkable.model.content;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.io.file.SvGTemplateLoader;
import org.rogatio.productivity.remarkable.io.file.Util;

/**
 * The Class Page.
 * 
 * @author Matthias Wegner
 */
public class Page {

	/** The Constant logger. */
	protected static final Logger logger = LogManager.getLogger(Page.class);

	/** The header. */
	private final String HEADER = "reMarkable .lines file, version=X";

	/** The page number. */
	private int pageNumber;

	/** The horizontal width. */
	private int horizontalWidth = 1404;

	/** The vertical width. */
	private int verticalWidth = 1872;

	/** The version. */
	private int version;

	/** The layers. */
	private List<Layer> layers = new ArrayList<Layer>();

	/**
	 * Gets the horizontal width.
	 *
	 * @return the horizontal width
	 */
	public int getHorizontalWidth() {
		return horizontalWidth;
	}

	/**
	 * Gets the vertical width.
	 *
	 * @return the vertical width
	 */
	public int getVerticalWidth() {
		return verticalWidth;
	}

	/**
	 * Extract header.
	 *
	 * @param byteArray the byte array
	 * @return the byte[]
	 */
	private byte[] extractHeader(byte[] byteArray) {
		List<Byte> tmpByteArray = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			if (byteArray[i] >= 32) {
				tmpByteArray.add(byteArray[i]);
			} else {
				break;
			}
		}

		Byte[] bytes = tmpByteArray.toArray(new Byte[tmpByteArray.size()]);

		return ArrayUtils.toPrimitive(bytes);
	}

	/**
	 * Gets the template file.
	 *
	 * @return the template file
	 */
	public File getTemplateFile() {
		return SvGTemplateLoader.getInstance().getFile(getTemplateName());
	}

	/**
	 * Gets the template name.
	 *
	 * @return the template name
	 */
	public String getTemplateName() {

		String templateName = notebook.getTemplateNames().get(this.getPageNumber());
		if (templateName != null) {
			if (!templateName.equals("")) {
				return templateName;
			}
		}

		return notebook.getDefaultTemplate();
	}

	/**
	 * Cut bytes.
	 *
	 * @param start     the start
	 * @param end       the end
	 * @param byteArray the byte array
	 * @return the byte[]
	 */
	private byte[] cutBytes(int start, int end, byte[] byteArray) {
		byte[] tmpByteArray = new byte[end - start];
		for (int i = 0; i < tmpByteArray.length; i++) {
			tmpByteArray[i] = byteArray[start + i];
		}
		return tmpByteArray;
	}

	/**
	 * Parses the version.
	 *
	 * @param bytes the bytes
	 * @return the int
	 */
	private int parseVersion(byte[] bytes) {
		byte[] headerBytes = extractHeader(bytes);

		int endOfHeader = headerBytes.length;

		String headerLine = new String(headerBytes).trim();

		if (headerLine.startsWith(HEADER.substring(0, HEADER.length() - 1))) {
			try {
				version = Integer.decode(headerLine.substring(headerLine.length() - 1));
			} catch (NumberFormatException e) {
				version = 0;
			}
		} else {
			version = 0;
		}

		return endOfHeader;
	}

	/**
	 * Gets the notebook.
	 *
	 * @return the notebook
	 */
	public Content getNotebook() {
		return notebook;
	}

	/**
	 * Instantiates a new page.
	 *
	 * @param pageNumber the page number
	 * @param bytes the bytes
	 */
	public Page(int pageNumber, byte[] bytes) {
		this(pageNumber, bytes, null);
	}

	/**
	 * Sets the notebook.
	 *
	 * @param notebook the new notebook
	 */
	public void setNotebook(Content notebook) {
		this.notebook = notebook;
	}

	/**
	 * See https://remarkablewiki.com/tech/filesystem
	 * 
	 * An alternative for this parsing could be found at
	 * https://github.com/raydac/java-binary-block-parser/blob/master/jbbp/src/test/java/com/igormaznitsa/jbbp/it/RemarkableLinesParsingTest.java
	 *
	 * @param pageNumber the page number
	 * @param bytes      the bytes
	 * @param notebook   the notebook
	 */
	@SuppressWarnings("unused")
	public Page(int pageNumber, byte[] bytes, Content notebook) {

		this.notebook = notebook;
		this.pageNumber = pageNumber;

		int endOfHeader = parseVersion(bytes);

		byte[] content = this.cutBytes(endOfHeader, bytes.length, bytes);
		ByteBuffer bb = ByteBuffer.wrap(content);
		bb.order(ByteOrder.LITTLE_ENDIAN);

		int numberOfLayers = bb.getInt();

		for (int layerNo = 1; layerNo <= numberOfLayers; layerNo++) {
			Layer layer = new Layer(layerNo);
			int numberOfStrokes = bb.getInt();

			for (int strokeNo = 1; strokeNo <= numberOfStrokes; strokeNo++) {
				int pencilType = bb.getInt();
				int strokeColor = bb.getInt();
				int unknown = bb.getInt();
				float penwidth = bb.getFloat();
				unknown = bb.getInt();
				int noOfSegments = bb.getInt();

				List<Segment> segments = new ArrayList<Segment>();
				for (int segmentNo = 1; segmentNo <= noOfSegments; segmentNo++) {
					float horizontalAxis = bb.getFloat();
					float vertikalAxis = bb.getFloat();
					float penSpeed = bb.getFloat();
					float strokeDirection = bb.getFloat();
					float strokeWidth = bb.getFloat();
					float penPressure = bb.getFloat();

					Segment segment = new Segment(segmentNo, horizontalAxis, vertikalAxis, penSpeed, strokeDirection,
							strokeWidth, penPressure);
					segments.add(segment);
				}

				Stroke stroke = new Stroke(strokeNo, pencilType, strokeColor, penwidth, segments);
				layer.add(stroke);
			}

			layers.add(layer);
		}

	}

	/**
	 * Gets the layers.
	 *
	 * @return the layers
	 */
	public List<Layer> getLayers() {
		return layers;
	}

	/** The notebook. */
	private Content notebook;

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * Gets the page number.
	 *
	 * @return the page number
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	public String toString() {
		return "Page (Version=" + this.getVersion() + ", Number=" + pageNumber + ") [Layers (" + this.getLayers().size()
				+ "): " + this.getLayers() + "]";
	}

	/**
	 * Gets the png.
	 *
	 * @return the png
	 */
	public File getPng() {
		String f = Util.getFilename(this, "png");
		if (new File(f).exists()) {
			return new File(f);
		} else {
			logger.debug("Image of " + new File(f).getAbsolutePath() + " not exists. Use export.");
		}
		return null;
	}
	
	/**
	 * Gets the svg.
	 *
	 * @return the svg
	 */
	public File getSvg() {
		String f = Util.getFilename(this, "svg");
		if (new File(f).exists()) {
			return new File(f);
		} else {
			logger.debug("Image of " + new File(f).getAbsolutePath() + " not exists. Use export.");
		}
		return null;
	}

	/**
	 * Gets the thumbnail.
	 *
	 * @return the thumbnail
	 */
	public File getThumbnail() {
		String f = Util.getFilename(this, "_thumbnail", "png");
		if (new File(f).exists()) {
			return new File(f);
		} else {
			logger.debug("Thumbnail image of " + new File(f).getAbsolutePath() + " not exists. Use export.");
		}
		return null;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((layers == null) ? 0 : layers.hashCode());
		result = prime * result + version;
		return result;
	}

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Page other = (Page) obj;
		if (layers == null) {
			if (other.layers != null)
				return false;
		} else if (!layers.equals(other.layers))
			return false;
		if (version != other.version)
			return false;
		return true;
	}

}
