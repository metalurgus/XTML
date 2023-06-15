package com.metalurgus.xtml;

import org.jsoup.nodes.Element;

public interface PreProcessor {
    Element preProcess(Element input);
}
