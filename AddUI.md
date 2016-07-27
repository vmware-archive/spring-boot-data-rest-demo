# Spring Data Rest Demo: Add a JavaScript UI

While it's great to see data in JSON format, it's even more fun to put a UI on it and see if we can drive some more interesting behavior.  In this part of the demo you will add a JavaScript UI to the application.

For this app we're going to use the Google Polymer JavaScript library.  This is a powerful framework and set of components that is based on the WebComponents specs from W3C.  If you're not familiar with Polymer or WebComonents that's OKay, this is going to be all copy/past, but the code will give you a sense of what it does.

Why Polymer?  See <a href="#WhyPolymer">here</a>

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

The script tag includes the basics for a webcomponents implementation for browsers that don't support it natively.  The link imports the greeting-display custom component that we'll create in the next sections, and we add it to the page as you do any other component.


## Add the JavaScript libraries

1. Go back to the Terminal, and navigate to the static directory.  EG: ~/S1P2016/workspace/(your_project)/src/main/resources/static
2. Install the appropriate Polymer libraries using bower

```bash
bower init
(enter information, you can leave it all blank)
bower install --save Polymer/polymer
bower install --save PolymerElements/iron-ajax
bower install --save PolymerElements/paper-button
```

> Important!  Eclipse will not see the files until you tell it to refresh its view.  In the Package Explorer Right-click on the static directory and select "Refresh".  You will now see the created bower.json and bower_components directory, but more importantly, so will the webserver embeded in the app.

## Create the Display Component

1. Right-click on /src/main/resources/static and select New -> Folder.  Name it elements.
2. Right-click on the elements directory and select New -> File.  Name it greeting-display.html
3. Copy the contents below into the file.

```html
<link rel="import" href="../bower_components/polymer/polymer.html">
<link rel="import" href="../bower_components/iron-ajax/iron-ajax.html">
<link rel="import" href="../bower_components/paper-button/paper-button.html">

<dom-module id="greeting-display">

  <style>
  div.bubble {
    margin: 20px;
    padding: 20px 20px;
    border-radius: 20px;
    background-color: #f1f1f1;
    width: 15em;
  }
  table, th, td {
    border: 1px solid black;
    border-collapse: collapse;
  }
  </style>
  
  <template>
    <iron-ajax id="getGreetingsService"
               url="/greetings"
               on-response="updateGreetings"></iron-ajax>

    <div class="bubble">
    <center><paper-button on-click="getGreetings" raised>Get The Greetings</paper-button><center>

    <p>
    <table style="width:100%">
	    <tr><th>Greeting</th><th>Link</th></tr>
	    <template is="dom-repeat" items="[[greetings]]">
	        <tr>
	            <td style="padding:0px 8px">[[item.text]]</td>
	            <td style="text-align:center"><a href='[[item._links.self.href]]'>raw data</a></span></td>
	        </tr>
	    </template>
    </table>
    </p>
    </div>
  </template>

  <script>
	Polymer({
		is : "greeting-display",
		ready : function() {
			this.greetings = [];
		},
		getGreetings : function() {
			this.$.getGreetingsService.generateRequest();
		},
		updateGreetings : function(theResponse) {
			this.greetings = theResponse.detail.response._embedded.greetings;
		}
	});
  </script>
</dom-module>
```

Brief description:  This collection of html, css, and JavaScript is used by Polymer to define a custom WebComponent named greeting-display.  The imports bring in other components we want to use, for fun we define and use a local style, the template section is what gets put in the DOM, and the script tells the Polymer framwork to define the component and some additional functions, including overriding the default ready() function to define the greetings data element.  In the template there is a sub-template for the repeating section, and the parts in the square brackets ```[[foo]]``` are data elements with Polymer managed bindings.  Square brackets mean it's "one way".  We'll see "two way" binding later.

Now go look a the page.  No need to restart the app if it's already running, just go to your browser and navigate to http://localhost:8080  Click on the button, and the greetings will load into a table.  There is also a link, extracted from the data, that will take you to the raw JSON version of the individual greetings.

The rest of the tutorial builds on this component.  If you want to see the finished version you can find it here: [greeting-display](greeting-display.html)

## Add Search

Since the service supports a simple search, you can go ahead and add that to the UI.  Start with getting the appropriate Polymer component.

```
bower install --save PolymerElements/paper-input
```

> Important!  Refresh the eclipse view as above (right-click static, Refresh) or the webserver will not see the new files.  You'll know this because the control won't show up.

Add the import for the paper-input control at the top next to the other ones.

```html
<link rel="import" href="../bower_components/paper-input/paper-input.html">
```

So the input fields don't get too big, add a style to the list in the ```<style>``` block.

```css
  paper-input { width: 10em; }
```

Add a new hidden ajax control.  It doesn't matter where, so long as it's in the ```<template>``` block, but I put it right under the other one.  The end point is the findByText method we defined in the repository.  The params field is a JSON Key/Value object that is added to the GET request when the component is triggered.  In this case we are binding it to a data element called searchString (defined below).  The square brackets tell Polymer that it's a "one way" binding.  Notice we take advantage of the same updateGreetings method as used in the getGreetings service.  The "auto" property will tell the component to call the service when ever the params (or url) property changes, in this case when ever the searchString data changes.

```html
    <iron-ajax id="getGreetingsSearchService"
               url="/greetings/search/findByText"
               params='[[searchString]]'
               on-response="updateGreetings" auto></iron-ajax>
```

Add the input control below the ```paper-button```.  The value is bound to the value of the key "text" in the searchString data element.  The curly brackes tell Polymer that it's "two way" so that when the value of the input control changes the value of the data element changes as well.

```html
    <paper-input label="Enter Search Word" value="{{searchString.text}}"></paper-input>
```

Finally, modify the ready() method to include the creations of the searchString data element.  The text key/value will be added to the GET request like <url>?text=value.

```javascript
		ready : function() {
			this.greetings = [];
			this.searchString = {"text" : ""};  //add this
		},
```

Now go see what happened.  Again, no need to restart the app, just save the file and reload the page in the browser.  In the new input field type in the word "Hello".  Once you hit a match the data should appear in the table.

## How About Adding Data?

For this final section you will take advantage of a feature we haven't yet explored, but that comes alone automatically with the RestRepository which is the ability to add data via POSTs.  Todo this we'll use another ajax control and an input field.  You'll take advantage of the on-change event of the input field, and when the response comes back we'll call the getGreetings service to refresh the table with the entire list.

Start with the ajax call.  In this case we have set the method to POST and bound the body to the soon to be created data element newGreeting.  When the response comes back we'll capture it with the greetingAdded method.

```html
    <iron-ajax id="addGreetingService"
               url="/greetings"
               method="POST"
               content-type="application/json"
               body="[[newGreeting]]"
               on-response="greetingAdded"></iron-ajax>
```

Add another input field to gather the new Greeting.  Put it below the other ```paper-input```.  You are binding the on-change even to another method called addGreeting which will tell the new service component to send the data to the server.

```html
    <paper-input label="Add Greeting" value="{{newGreeting.text}}" on-change="addGreeting"></paper-input>
```

Finally, add the new data element to the ready() function and create some handler methods.  The first method is the one bound to the input control, the second gets the response and simply calls the service to get all the Greetings.

```javascript
		ready : function() {
			this.greetings = [];
			this.searchString = {"text" : ""};
			this.newGreeting  = {"text" : ""}; //add this
		},
		addGreeting: function() { //add this function
			this.$.addGreetingService.generateRequest();
		},
		greetingAdded: function(theResponse) { //add this function
			this.$.getGreetingsService.generateRequest();
		},
```

That's it!  Save, refresh the page, and try it out.  You should be able to type in a new Greeting and when you hit Enter it'll be added to the database.

<a name="WhyPolymer"/>
## Why Polymer?

There are lots of great choices for JavaScript libraries out there, and Pivotal doen't express a preference.  Angular is well known, and React.js has lots of good buzz around it.  So why use the lesser known Polymer?  If you have done the tutorial then you know that Polymer has some nice components available for it, good encapsulation for custom WebComponents, support for repeting sections, and very useful data binding features.  These come together to bring a lot of functionality for a small amount of JavaScript that's relatively easy to understand.
