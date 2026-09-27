# Binary Tree Visualisation 

This Visualisation was developed by Ankit Sharma as
LearnTrees. [See the repo](https://github.com/beingmartinbmc/LearnTrees.git)

The Logic is being developed by Agha Muhammad Aslam and Team. It is being intended for the Portfolio project of Algorithm and Data Structure. 

## Overview:

This project is a tool for visualizing binary trees effectively. It provides insight into how binary trees are
constructed, modified, and traversed, offering a detailed view of their structure. This tool is particularly helpful for
learning and teaching purposes, as it includes a feature set tailored to guide users through the complexities of binary
tree operations.

--------------------------------------

## Key Features:

* Visualize binary trees with clear and interactive diagrams.
* Track history changes to see how nodes are added and removed incrementally.
* Provides full modification history for analyzing the evolution of a tree structure. 
* Track individual node changes step by step. 
* Replay modifications to learn tree operations visually and intuitively.

## What Differentiates This Project:

This project introduces **real-time interaction with a history-tracking feature.**

--------------------------------------

## Optional Features in the Future Development:

+ Animation for each step in the history of tree changes. This would enable a fully interactive and visually engaging
  experience for users.
+ Dynamic resizing of the pane and nodes to prevent overlaps and maintain a clear layout, regardless of tree size.

## Requirements and running from Terminal

- JDK 23 (not only a Java Runtime Environment). The Maven compiler configuration targets Java 23; Java 27 does not work with this project's JavaFX 23 WebView dependency.
- Apache Maven.
- Graphviz (`dot`) for tree-image rendering. On macOS, install it with `brew install graphviz` if rendering reports that no Graphviz engine could be initialized.

On macOS, select JDK 23 in the Terminal session (if you installed a JDK archive manually, set `JAVA_HOME` to its `Contents/Home` directory instead):

~~~bash
export JAVA_HOME=$(/usr/libexec/java_home -v 23)
export PATH="$JAVA_HOME/bin:$PATH"
java -version
mvn -version
~~~

Clone and run the application:

~~~bash
git clone https://github.com/MusLead/BinaryTreeHSF_Visualisation.git
cd BinaryTreeHSF_Visualisation
mvn clean javafx:run
~~~

Run Maven from the directory containing `pom.xml`. Maven downloads the JavaFX dependencies on the first run.

## Building a macOS app (Apple Silicon)

These commands build an `.app` using JDK 23 and the Maven dependencies. Run them from a local project directory outside iCloud Drive: Finder metadata on an app generated inside iCloud Drive can make macOS code signing fail. Confirm `"$JAVA_HOME/bin/java" -version` reports 23 before building.

~~~bash
mvn clean package dependency:copy-dependencies \
  -DincludeScope=runtime \
  -DoutputDirectory=target/app-input

mkdir -p target/app-mods
cp target/BinaryTreeVis-1.0-SNAPSHOT.jar target/app-mods/
find target/app-input -maxdepth 1 -type f -name '*.jar' \
  ! -name 'javafx-*.jar' -exec cp {} target/app-mods/ \;
cp target/app-input/javafx-*-mac-aarch64.jar target/app-mods/

"$JAVA_HOME/bin/jpackage" \
  --type app-image \
  --name BinaryTreeVis \
  --app-version 1.0.0 \
  --module-path "$PWD/target/app-mods" \
  --module de.hsfd.binarytreevis/de.hsfd.binarytreevis.Main \
  --runtime-image "$JAVA_HOME" \
  --dest target
~~~

The app will be at `target/BinaryTreeVis.app`. It includes a Java runtime, so users of the packaged app do not need to install a JDK. This build is for Apple Silicon Macs; build separately for other platforms. Test the launcher and tree rendering before sharing:

~~~bash
./target/BinaryTreeVis.app/Contents/MacOS/BinaryTreeVis
~~~

If Finder opens no window, this Terminal command displays the launch error. The app bundle has not yet been verified as a distributable release.

## Sharing a compiled app

After the app works, compress the entire `.app` bundle (it is a directory) into a ZIP file:

~~~bash
ditto -c -k --keepParent target/BinaryTreeVis.app BinaryTreeVis-macos-arm64-v1.0.0.zip
~~~

Create a GitHub **Release** for a version tag such as `v1.0.0` and attach the ZIP as a release asset. Share the release link; GitHub's automatically generated “Source code” ZIP contains the source, not the compiled app. Do not commit `target/` or the app bundle to the Git repository (`target/` is already ignored). Compressing the app reduces download size and keeps its bundle together, but you still need to compile once per version or whenever the code changes. Users can download the ZIP and run that built version without compiling it.

An ad hoc signed app may be stopped by macOS Gatekeeper after download. For smooth public distribution, sign with an Apple Developer ID and notarize the app before publishing the ZIP.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Reference
- [SLF4J](https://www.slf4j.org/codes.html#StaticLoggerBinder), [SLF4J-NOP MavenRep](https://mvnrepository.com/artifact/org.slf4j/slf4j-nop/2.1.0-alpha1) solving the Warning because using the WebView 
- [Graphviz](https://github.com/nidi3/graphviz-java/blob/master/README.md) for showing the tree image in the WebView
- [Markdown](https://central.sonatype.com/artifact/com.github.rjeschke/txtmark) based for java dependency. [txtmark GitHub](https://github.com/rjeschke/txtmark)
- [Markdown-pd-fx](https://github.com/eugener/markdown-pad-fx/blob/master/markdown-pad-fx/src/main/java/org/oxbow/markdownfx/DocumentEditor.java) is an example how to implement the markdown dependency in the WebView.
- [StackOverflow idea](https://stackoverflow.com/questions/17725377/add-hyperlink-inside-a-textarea-in-javafx), for showing image in the WebView rather than using the TextArea
- [BinaryTreeHSF](https://github.com/MusLead/BinaryTreeHSF.git) the logic behind the Binary Tree Implementations such as RB-Tree, AVL-Tree, and Binary Search Tree