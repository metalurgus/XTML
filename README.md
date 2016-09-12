# XTML
GSON analog for serialization/deserialization of Java objects into HTML code and back

[![](https://jitpack.io/v/metalurgus/XTML.svg)](https://jitpack.io/#metalurgus/XTML)

Work in progress, a basic data is supported so far.

For the sample see 
[TestClass.java](https://github.com/metalurgus/XTML/blob/master/app/src/main/java/com/metalurgus/xtml/app/TestClass.java)

Contribution is welcome!

## How to use?
Add dependency (`XTML` is available through jitpack.io):
```
repositories {
    maven { url "https://jitpack.io" }
}
dependencies {
    compile 'com.github.metalurgus:XTML:0.0.1.1'
}
```

Sample HTML to deserialize:
```html
 "<div test1 = '111'>" +
    "<div id='testId' test2='222'/>" +
    "<div test2='333'/>" +
    "<div id='testId2'>" +
        "<div test2='333'/>" +
        "<div test2='333'/>" +
        "<div test2='333'/>" +
    "</div>" +
    "<div id='testId3'>" +
        "<div test2='333'>222.22</div>" +
        "<div test2='333'>333.33</div>" +
        "<div test2='333'>444.44</div>" +
    "</div>" +
"</div>";
```

Annotate your model class with `@XTMLClass` annotation:
```java
@XTMLClass
public class TestClass {
...
}
```
Annotate every field to be deserialized with `@XTMLMapping` annotation:
```java
@XTMLMapping(type = XTMLMapping.Type.ATTRIBUTE, name = "test1")
public int field1;
//Non-siple data types are supported as well.
@XTMLMapping(type = XTMLMapping.Type.TAG, select = "#testId")
public InnerTestClass innerTestClass1;
@XTMLMapping(type = XTMLMapping.Type.TAG, index = 1)
public InnerTestClass innerTestClass2;
//as well as collections
@XTMLMapping(type = XTMLMapping.Type.COLLECTION, select = "#testId2 > *")
public List<InnerTestClass> innerTestClassList;
@XTMLMapping(type = XTMLMapping.Type.COLLECTION, select = "#testId3 > *")
public List<Double> integerList;

@XTMLClass
class InnerTestClass {
    @XTMLMapping(type = XTMLMapping.Type.ATTRIBUTE, name = "test2")
    public String field3;
}
```
Call `XTML.fromHTML()`:
```java
TestClass testClass = XTML.fromHTML(Jsoup.parse(TestClass.HTML).body().child(0), TestClass.class);
```
`type` attribute:
 `@XTMLMapping.Type.ATTRIBUTE` will pick an attribute lavue into the annitated field
 `@XTMLMapping.Type.TAG` will parse an HTML tag into annotated field, doing it recursively, if field class is annotated with `@XTMLClass`, and trying to assign the attribute text to the field if not annotated.
 `@XTMLMapping.Type.COLLECTION` will parse  a list of elements, provided with a `select` attribute.
 
 `name` attribute - specifies HTML attribute name to be parsed with `@XTMLMapping.Type.ATTRIBUTE` annotation, or an element with specified `name`(`<div name='someName'/>`)
 
 `id` attribute - specifies `id` of an element to be parsed into the annotated field
 
 `index` attribute - specifies an index of the element inside a list, provided by `select` attribute, or an index of a child element to be parsed into an annotated field
 
 `select` attribute - a CSS selector to select a list of nodes for collection, or a single node (using `index`, or picking `st element) for parsing.
 
 
 
