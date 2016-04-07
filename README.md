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
pdfbox**1**-layout | pdfbox **1**.8.x
pdfbox**2**-layout | pdfbox **2**.x


#### Get it:

As of 0.2.0 pdfbox-layout is available from [jitpack.io](https://jitpack.io/#ralfstuckert/pdfbox-layout). 
The latest release is <span class="latest_release">...</span>.

Maven:

<div class="highlight highlight-text-xml"><pre>    &lt;<span class="pl-ent">repositories</span>&gt;
        &lt;<span class="pl-ent">repository</span>&gt;
            &lt;<span class="pl-ent">id</span>&gt;jitpack.io&lt;/<span class="pl-ent">id</span>&gt;
            &lt;<span class="pl-ent">url</span>&gt;https://jitpack.io&lt;/<span class="pl-ent">url</span>&gt;
        &lt;/<span class="pl-ent">repository</span>&gt;
    &lt;/<span class="pl-ent">repositories</span>&gt;
    ...
    &lt;<span class="pl-ent">dependency</span>&gt;
        &lt;<span class="pl-ent">groupId</span>&gt;com.github.ralfstuckert.pdfbox-layout&lt;/<span class="pl-ent">groupId</span>&gt;
        &lt;<span class="pl-ent">artifactId</span>&gt;pdfbox2-layout&lt;/<span class="pl-ent">artifactId</span>&gt;
        &lt;<span class="pl-ent">version</span>&gt;<span class="latest_release">...</span>&lt;/<span class="pl-ent">version</span>&gt;
    &lt;/<span class="pl-ent">dependency</span>&gt;</pre></div>

Gradle:

<div class="highlight highlight-source-groovy-gradle"><pre>   <span class="pl-en">repositories</span> { 
        jcenter()
        maven { url <span class="pl-s"><span class="pl-pds">"</span>https://jitpack.io<span class="pl-pds">"</span></span> }
   }
   <span class="pl-en">dependencies</span> {
         compile <span class="pl-s"><span class="pl-pds">'</span>com.github.ralfstuckert.pdfbox-layout:pdfbox2-layout:<span class="latest_release">...</span><span class="pl-pds">'</span></span>
   }</pre></div>
</article>
  </div>


<!-- Add this script to update "latest_release" span to latest version -->
<script>
      var user = 'ralfstuckert'; 
      var repo = 'pdfbox-layout'
      
      var xmlhttp = new XMLHttpRequest();
      xmlhttp.onreadystatechange = function() {
          if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
              var myArr = JSON.parse(xmlhttp.responseText);
              populateRelease(myArr);
          }
      }
      xmlhttp.open("GET", "https://api.github.com/repos/" user + "/" + repo + "/releases", true);
      xmlhttp.send();
      
      function populateRelease(arr) {
          var release = arr[0].tag_name;
          document.getElementsByClassName ("latest_release").innerHTML = release;
      }
</script>

