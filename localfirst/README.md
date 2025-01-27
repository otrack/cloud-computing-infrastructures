# The (local-first) Bachelor Party

You are the best man / woman of a friend and need to prepare his / her bachelor-ette party.
For this, you decide to write a local-first application that permits the party participants to collaboratively submit proposals of activities.
Participants can also upvote or downvote proposals.

## 1. First steps

To write the application, we will use the [Yjs](https://github.com/yjs/yjs) library.
This library implements various CRDTs that can be persisted locally and synchronized among peers.

**[Q11]** Before we start, make sure that you have [npm](https://www.npmjs.com/) (or [pnpm](https://pnpm.io/)) installed on your machine. 
In addition, you should also have [NodeJS](https://nodejs.org/en) available. 
We will use this to run the application (either on the command line, or in the browser).
Execute `npm install` in the application directory to install its dependencies.

Yjs offers multiple bindings.
Hereafter, we will use Javascript (JS) ones.
For this practical , it is highly recommended to use an IDE that supports and understand JS.
If you are familiar with TypeScript, feel free to adjust the provided code base and use TypeScript instead.

**[Q12]** Install Yjs and y-websocket following the guidelines provided in the [Getting Started]( https://github.com/yjs/yjs#getting-started) section. Start a [websocket](https://en.wikipedia.org/wiki/WebSocket) server. Run `node observer.js` in the terminal, using the following command: `PORT=1234 node ./node_modules/y-websocket/bin/server.cjs`. The program should display `connected`, indicating that it managed to connect to the server.

We will store the proposals in a [YMap](https://github.com/yjs/yjs?tab=readme-ov-file#api) of the form `(String id, String text)`, where `id` is a unique identifier of the proposal and `text` a description.

**[Q13]** The file `observer.js` contains some provided code. 
First, you should add the map of proposal by calling `ydoc.getMap`. 
Name the map `todos`. 
In addition, create a listener for this map that logs some information in the console when a new entry is added. 
For this, you can use the pattern `todos.observe(event => {..})`. 
Test that your code is correct by adding a new proposal once the listener is properly registered.
You run the code using the `node observer.js` command.

To store the votes of the participants, we use a second map named `votes`. 
For each proposal identifier, this map stores a YArray. 
This array contains integers of the form `1` for an upvote and `-1` for a downvote. 
To obtain the score of the proposal, we simply sum all its votes.

**[Q14]** Create the `votes` map. 
Add a listener to the map that display in the console the score of each proposal. 
Check the correctness of your code by adding proposals for the bachelor party.

**[Q15]** What is the invariant in the application binding variables `todos` and `votes`? 
Explain why transactions in Yjs can help to maintain this invariant. 
Amend your code to use a transaction when a new proposal is added to the application.

## 2. Interface

We are now moving to write down the web interface of the application.
The `bachelor.html`, `bachelor.css` and `bachelor.js` files provide  a skeleton.
The first two files hold the static content of the application, that is an HTML file for presentation and a CSS file for decoration.
The third file contains the logic of the application.

To execute the application, we run the commands below.
``` shell
	npm run dist
	npm start
```
Before doing so, take care to remove the line `"type" : "module"` from `package.json`.
This comes from the fact that, from now on, we do not execute the script using NodeJS but instead use the web browser.
Please also take care of restarting the websocket server to empty the content of the Yjs documents which were created earlier.

The logic of the above lines is as follows:
The first line packages the script in a suitable format for your browser.
The second line creates an HTTP server that will make the application available locally.
This code should open a tab in your browser in which the application is executed.
For the moment, submitting a proposal has no effect.

**[Q21]** Complete the code of the `addProposalButton` listener.
This code should retrieve the input of the user using `document.getElementById('proposal-input').value` then create a transaction that updates `todos` and `votes`.

Next, we need to add the list of proposals and their scores to the HTML document.
For this, we complete the second listener, the one attached to `votes`.

**[Q22]** In a first step, iterate over the list of proposals stored in `todos`and display each proposal together with its score on the console.

**[Q23]** In a second step, amend the list in the HTML document to add the proposals.
This means that you have to add dynamically new elements to the list.
This is done using the following code, where `id` and `score` are respectively the identifier and score of a proposal.

``` javascript
	const newProposal = document.createElement('li');
	newProposal.innerHTML = `
		${v}
		<div>
		<button id="upvote-${id}">Upvote</button>
		<button id="downvote-${id}">Downvote</button>
		</div>
		&nbsp;&nbsp;&nbsp; Score ${score}
	`;

```

**[Q24]** Add a listener to each of the upvote and downvote buttons. Be careful that when receiving an updates on `votes`, you must use a deep observer to be notified of modifications happening on the maps storing the score of each proposal. That is, the code of the form (`votes.observeDeep(event => {..})`).

**[Q25]** Test now your application.
In particular, verify that when the websocket server is down, the application still works locally.
Here you may even use two tabs or two different browsers.
Once the websocket server is up again, the two instances should reconcile automatically.

**[Q26]** The list of proposals is more readable when they are sorted according to their scores.
Change the application accordingly.

**[OPT]** Deploy your application in a JavaScript runtime in GCP CloudFunctions and make it accessible publicly on the web.
You can now add a nice favicon and invite some friends to participate :)
