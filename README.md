[![Release](https://jitpack.io/v/ralfstuckert/pdfbox-layout.svg)](https://jitpack.io/#ralfstuckert/pdfbox-layout)

# pdfbox-layout
A tiny layout library on top of pdfbox. Main features are

* word wrapping
* text alignment
* paragraphs 
* pagination

See the examples for more information on the usage. 

## Supports pdfbox 1.8.x and 2.x
The library comes in two flavors: one for Apache pdfbox 1.8.x, and the other for pdfbox 2.x

artifactId | pdfbox version
---------- | -------------
pdfbox1-layout | pdfbox 1.8.x
pdfbox2-layout | pdfbox 2.x


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
        <version>0.2.0</version>
    </dependency>
```

Gradle:

```repositories { 
        jcenter()
        maven { url "https://jitpack.io" }
   }
   dependencies {
         compile 'com.github.ralfstuckert.pdfbox-layout:pdfbox2-layout:0.2.0'
   }
```