[![Release](https://jitpack.io/v/ralfstuckert/pdfbox-layout.svg)](https://jitpack.io/#ralfstuckert/pdfbox-layout)

# pdfbox-layout
A tiny layout library on top of pdfbox. Main features are

* word wrapping
* text alignment
* paragraphs 
* pagination

See the examples for more information on the usage. 

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