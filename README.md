[![Release](https://jitpack.io/v/ralfstuckert/pdfbox-layout.svg)](https://jitpack.io/#ralfstuckert/pdfbox-layout)
[![MIT licensed](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE)

# pdfbox-layout
A tiny layout library on top of pdfbox. Main features are

* word wrapping
* text alignment
* paragraphs 
* pagination

See the [Wiki](https://github.com/ralfstuckert/pdfbox-layout/wiki) for more information on the usage, or browse the [javadoc](https://jitpack.io/com/github/ralfstuckert/pdfbox-layout/pdfbox2-layout/0.7.1/javadoc/).

These two articles may also be useful as a short introduction:
* [PDF text layout made easy with PDFBox-Layout](https://hardmockcafe.blogspot.de/2016/04/pdf-text-layout-made-easy-with-pdfbox_17.html)
* [Creating Lists with PDFBox-Layout](https://hardmockcafe.blogspot.de/2016/06/creating-lists-with-pdfbox-layout.html)

## Supports pdfbox 1.8.x and 2.x
The library comes in two flavors: one for Apache pdfbox 1.8.x, and the other for pdfbox 2.x

artifactId | pdfbox version
---------- | -------------
pdfbox**1**-layout | pdfbox **1**.8.x
pdfbox**2**-layout | pdfbox **2**.x


#### Get it:

As of 0.2.0 pdfbox-layout is available from [jitpack.io](https://jitpack.io/#ralfstuckert/pdfbox-layout). 

Maven:

```xml
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    ...
    <dependency>
        <groupId>com.github.ralfstuckert.pdfbox-layout</groupId>
        <artifactId>pdfbox2-layout</artifactId>
        <version>0.7.1</version>
    </dependency>
```

Gradle:

```gradle
   repositories { 
        jcenter()
        maven { url "https://jitpack.io" }
   }
   dependencies {
         compile 'com.github.ralfstuckert.pdfbox-layout:pdfbox2-layout:0.7.1'
   }
```
