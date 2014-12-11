org.eclipsescout.rt.ui.fx
=========================

This is a community effort to provide a JavaFX rendering engine for the [eclipse scout](http://eclipse.org/scout/) project.

To ensure compatibility with Eclipse Scout, this project uses a similar branching concept. Please switch to one of this branch to get the code:

* Luna: [releases/4.0.x](http://github.com/jmini/org.eclipsescout.rt.ui.fx/tree/releases/4.0.x)
* Mars: TBD

Description
-----------

Branch **releases/4.0.x** contains the JavaFx renderer for the Luna version. 

### org.eclipsescout.rt.ui.fx.target

Project containing the target file required to work on the JavaFx rendering layer.
Set `scout_fx_luna.target` as Target Platform.

### org.eclipsescout.rt.ui.fx

Main RT plugin containing the JavaFx rendering layer for eclipse scout.


### org.eclipsescout.rt.ui.fx.test

Tests code for the main plugin.


### org.eclipsescout.demo.widgets.ui.fx

Test application: additional ui plugin for the [Eclipse Scout Widgets Demo Application](http://wiki.eclipse.org/Scout/Demo#Widgets) in order to start the JavaFx UI.
You will need to import the plugins from the [GitHub project](https://github.com/BSI-Business-Systems-Integration-AG/org.eclipsescout.demo/) (take the `4.0` branch):
* `org.eclipsescout.demo.widgets.client`
* `org.eclipsescout.demo.widgets.shared`


Get in touch / bug tracker
--------------------------

Use the [org.eclipsescout.rt.ui.fx issue tracker](http://github.com/jmini/org.eclipsescout.rt.ui.fx/issues) on GitHub.


Download
--------

There is no download for this branch for the moment.


License
-------

[Eclipse Public License - v 1.0](http://www.eclipse.org/legal/epl-v10.html)


