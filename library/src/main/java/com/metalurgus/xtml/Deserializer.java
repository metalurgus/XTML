package com.metalurgus.xtml;

import org.jsoup.nodes.Element;

public interface Deserializer {
    void deserialize(Element element);
}
