package com.flatironschool.javacs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;
import java.util.Scanner;

public class WikiPhilosophy {

	final static WikiFetcher wf = new WikiFetcher();
	final static String philosophyUrl = "https://en.wikipedia.org/wiki/Philosophy";

	/**
	 * Tests a conjecture about Wikipedia and Philosophy.
	 *
	 * https://en.wikipedia.org/wiki/Wikipedia:Getting_to_Philosophy
	 *
	 * 1. Clicking on the first non-parenthesized, non-italicized link
   * 2. Ignoring external links, links to the current page, or red links
   * 3. Stopping when reaching "Philosophy", a page with no links or a page
   *    that does not exist, or when a loop occurs
	 *
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		//Failing Tests:
		//	https://en.wikipedia.org/wiki/Pieretta_Dawn
		//  https://en.wikipedia.org/wiki/MacBook
		//  https://en.wikipedia.org/wiki/Nintendo

		//Passing Tests:
		//  https://en.wikipedia.org/wiki/Java_(programming_language)

		String url = "https://en.wikipedia.org/wiki/Pieretta_Dawn";
		Elements paras = wf.fetchWikipedia(url);
		/*
		Stack<Character> parenthesisStack = new Stack<Character>();
		//A list to keep track of the visited pages
		List<String> viewedPages = new ArrayList<String>();
		viewedPages.add(url);


		System.out.println("Just looked at " + url);
		String nextUrl = visitPage(viewedPages, url);
		System.out.println("Visiting " + nextUrl);

		int count = 1;
		while ( nextUrl != null && !nextUrl.equals(philosophyUrl)) {
			nextUrl = visitPage(viewedPages, nextUrl);
			System.out.println("Visiting " + nextUrl);
			count++;
		}

		if (nextUrl == null) {
			System.out.println("ERROR: Something went wrong!");
		} else {
			System.out.println("Success! Took " + count + " clicks to get to Philosophy.");
		}
		*/
	}

	/**
	 * Visits the valid link by returning the next link.
	 *
	 * @param viewedPages, a list keeping track of visited urls
	 * @param link, a url
	 * @return string representation of the next url
	 */
	public static String visitPage(List viewedPages, String link) throws IOException {
		Elements paras = wf.fetchWikipedia(link);
		Element firstPara = paras.get(0);

		System.out.println(firstPara.text());
		//System.out.println("The parent tag to this first paragraph is " + firstPara.parent().parent());

		Iterable<Node> iter = new WikiNodeIterable(firstPara);

		Stack<Character> parenthesisStack = new Stack<Character>();

		for (Node node: iter) {
			if (node instanceof TextNode) {
				parenthesisUpdate(parenthesisStack, ((TextNode)node).text());
			}

			if (node instanceof Element) {
				//Cast the node into an Element object to access the tag name
				Element nodeElement = (Element) node;

				//Assemble the new link - check for 'a' tag representing link
				if (nodeElement.tagName().equals("a") && linkCheck(nodeElement)) {
					String nextLink = "https://en.wikipedia.org" + nodeElement.attr("href");

					//verify that this link is allowed
					if (allowVisit(viewedPages, parenthesisStack, nextLink)) {
						viewedPages.add(nextLink);
						return nextLink;
					}

				}
			}
    }

		return null;
	}

	/**
	 * Tests whether a url has been visited before or in parenthesis.
	 *
	 * Possible improvement: link should not begin with an uppercase letter
	 *
	 * @param viewedPages, a list keeping track of visited urls
	 * @param parenthesisStack, a stack keeping track of open parenthesis
	 * @param nextLink, the next constructed link as a string
	 * @return true or false depending on if link is not visited and not in parenthesis
	 */
	public static boolean allowVisit(List viewedPages, Stack parenthesisStack, String nextLink){
		if (viewedPages.contains(nextLink) || !parenthesisStack.empty()) {
			return false;
		}

		return true;
	}

	/**
	 * Tests whether the link text is in bold, italics, ciation, or red.
	 *
	 * @param linkText, a Element object with tags and attributes
	 * @return true if the link isn't bold/italic/red/outside
	 */
	public static boolean linkCheck(Element linkText) {
		if (linkText.tagName().equals("b") || linkText.tagName().equals("i") || linkText.tagName().equals("em")){
			return false;
		}

		//Ignore red links
		String title = linkText.attr("title");
	  if (title.contains("(page does not exist)")) {
			return false;
		}

		//Ignore links that are citations
		if (linkText.attr("href").contains("cite_")) {
			return false;
		}

		return true;
	}

	/**
	 * Updates the parenthesis stack.
	 *
	 * @param parenthesisStack, a stack keeping track of open parenthesis
	 * @param text
	 */
	public static void parenthesisUpdate(Stack parenthesisStack, String text) {
		for (char letter : text.toCharArray()){
			if (letter == '(') {
				parenthesisStack.push('o');
			}

			if (letter == ')') {
				parenthesisStack.pop();
			}
		}
	}

}
