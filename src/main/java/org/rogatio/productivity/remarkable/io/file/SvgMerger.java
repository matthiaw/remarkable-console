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

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.rogatio.productivity.remarkable.io.PropertiesCache;
import org.rogatio.productivity.remarkable.model.content.Page;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

/**
 * The Class SvgMerger.
 */
public class SvgMerger {

	/** The Constant logger. */
	protected static final Logger logger = LogManager.getLogger(SvgMerger.class);

	/** The Constant COLOR. */
	private static final String COLOR = PropertiesCache.getInstance().getProperty(PropertiesCache.SVGGRIDCOLOR);

	/**
	 * Merge.
	 *
	 * @param page     the page
	 * @param template the template
	 * @param target   the target
	 */
	public static void merge(Page page, String template, File target) {
		File templateFile = SvGTemplateLoader.getInstance().getFile(template);
		File pageFile = new File(Util.getFilename(page, "svg"));
		String no = String.format("%03d", page.getPageNumber());
		String title = page.getNotebook().getName() + " - Page " + no;
		mergeSvg(templateFile, pageFile, target, title);
	}

	/**
	 * Replace color.
	 *
	 * @param node the node
	 */
	private static void replaceColor(Node node) {
		if (node.getNodeName().equalsIgnoreCase("style")) {
			String content = node.getTextContent();
			content = content.replace("stroke:#000000", "stroke:" + COLOR);
			content = content.replace("stroke:rgb(0%,0%,0%)", "stroke:" + COLOR);
			node.setTextContent(content);
			// logger.debug("Color replaced to '" + COLOR + "'");
		}

		if (node.hasAttributes()) {
			NamedNodeMap attribs = node.getAttributes();
			if (attribs != null) {
				for (int j = 0; j < attribs.getLength(); j++) {
					Node namedNode = attribs.item(j);
					if (namedNode.getNodeName().equalsIgnoreCase("style")) {
						String content = namedNode.getNodeValue();

						content = content.replace("stroke:#000000", "stroke:" + COLOR);
						content = content.replace("stroke:rgb(0%,0%,0%)", "stroke:" + COLOR);
						namedNode.setNodeValue(content);
					}
				}
			}
		}

		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				// calls this method for all the children which is Element
				replaceColor(currentNode);
			}
		}
	}

	/**
	 * Merge svg.
	 *
	 * @param backgroundFile the background file
	 * @param foregroundFile the foreground file
	 * @param targetFile     the target file
	 * @param title          the title
	 */
	private static void mergeSvg(File backgroundFile, File foregroundFile, File targetFile, String title) {
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory sax = new SAXSVGDocumentFactory(parser);

		if (backgroundFile == null) {
			logger.debug("Skip merging because no template is found.");
			return;
		}

		if (backgroundFile.getName().equalsIgnoreCase("Blank.svg")) {
			// logger.debug("Skip merging because template is blank.");
			return;
		}

		logger.debug("Merge SVG: " + backgroundFile.getName() + " + " + foregroundFile.getName() + " -> "
				+ targetFile.getName());

		try {
			SVGDocument docTemplate = sax.createSVGDocument(backgroundFile.toURI().toString());
			SVGDocument docImage = sax.createSVGDocument(foregroundFile.toURI().toString());

			NodeList nList = docTemplate.getElementsByTagName("title");
			if (nList.getLength() == 1) {
				Node node = nList.item(0);
				node.setTextContent(title);
			}

			replaceColor(docTemplate.getDocumentElement());

			Element g = docImage.getDocumentElement();
			Node mergedNode = docTemplate.importNode(g, true);
			docTemplate.getDocumentElement().appendChild(mergedNode);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(targetFile);
			Source input = new DOMSource(docTemplate);
			transformer.transform(input, output);
		} catch (IOException e) {
		} catch (TransformerConfigurationException e) {
		} catch (TransformerFactoryConfigurationError e) {
		} catch (TransformerException e) {
		}

	}

}
