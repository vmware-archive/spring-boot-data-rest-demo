# Spring Data Rest Demo: Add a JavaScript UI

While it's great to see data in JSON format, it's even more fun to put a UI on it and see if we can drive some more interesting behavior.  In this part of the demo you will add a JavaScript UI to the application.

## Add the Single Page

This will be a SPA (Single Page Application), so start by creating the single page.  This is nothing but an index.html that will eventually hold our Web Components.

1. Right-click on the directory /src/main/resources/static and select New -> File.  Name it index.html
2. Add the following HTML

```html
<!DOCTYPE html>
<html>
  <head>
    <script src="bower_components/webcomponentsjs/webcomponents-lite.min.js"></script>
    <link rel="import" href="elements/greeting-display.html">
  </head>
  <body>
    <h1>Greeting Display App</h1>
    <greeting-display></greeting-display>
  </body>
</html>
```

The script tag includes the basics for a webcomponents implementation.  The link imports the greeting display that we'll create in the next section.  In the body you can see that we simply use a custom component.


### Add the JavaScript libraries

For this page we're going to use the Google Polymer JavaScript library.  This is a powerful set of components that is based on the set of WebComponents specs from W3C.  If you're not familiar with Polymer or WebComonents that's OKay, this is going to be all copy/past, but you can browse the code to get a sense of what it does.

1. Go back to the Terminal, and navigate to the static directory.  EG: ~/S1P2016/workspace/<your_project>/src/main/resources/static
2. Install the appropriate Polymer libraries

```bash
bower init
(enter information, it doesn't matter if you leave it all blank)
bower install --save Polymer/polymer
bower install --save PolymerElements/iron-ajax
bower install --save PolymerElements/paper-button
```

## Create the Display Component

1. Right-click on /src/main/resources/static and select New -> Folder.  Name it elements.
2. Right-click on the elements directory and select New -> File.  Name it greeting-display.html
3. Open the link below, and copy the contents from the link below into the file.

[Use this Content](greeting-display.html)


Now go look a the page.  No need to restart the app if it's already running, just go to your browser and navigate to http://localhost:8080  Click on the button, and the greetings will load into a table.  There is also a link, extracted from the data, that will take you to the raw JSON version of the individual greetings.

