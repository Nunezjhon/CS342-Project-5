/*
 *  uno.js
 *
 *  Author(s): Joseph Canning (jec2)
 *  Description:
 *      This file is part of an implementation of Mattel's card game Uno and runs a server on a hard-coded port that accepts four players.
*/

// non-function variables
const app = require('express')();
const server = require('http').createServer(app);
const io = require('socket.io')(server);
const port = 5555;
const colors = ['red', 'blue', 'yellow', 'green'];
const nums = [1, 2, 3, 4, 5, 6, 7, 8, 9]; // zero is excluded for convenience; see initCards
const actions = ['draw2', 'draw4', 'skip', 'reverse', 'wild'];
let players = []; // array of clients on server
let cards = []; // array of all cards; effectively final after initialization
let deck = []; // shuffled deck of cards that are dealt to the players
let turn = 0; // player whose turn it is
let reverse = false; // turn order is reversed
var match; // card whose number, color, or action must be matched

// functions
const getPlayerNum = client => players.indexOf(client) + 1; // convert index of client in players to player number visible to clients and return it

function Card(num, color, action) { // Card constructor

    this.num = num; // number
    this.color = color; // face color
    this.action = action; // draw two, skip, etc.
    this.imgID = '' + num + color + action; // name of image representing this card

}

function initCards() { // creates the 108 Uno cards

    colors.forEach(color => {

        nums.forEach(num => {

            cards.push(new Card(num, color, null));
            cards.push(new Card(num, color, null));

        });

        actions.forEach(action => cards.push(new Card(null, color, action)));

    });

    colors.forEach(color => {

        cards.push(new Card(0, color, null));
        cards.push(new Card(null, color, 'draw2'));
        cards.push(new Card(null, color, 'skip'));
        cards.push(new Card(null, color, 'reverse'));

    });

    console.log(`${cards.length} cards instantiated`);

}

function makeDeck() { // assign deck a Fischer-Yates shuffle of cards

    deck = JSON.parse(JSON.stringify(cards)); // deep copy

    for (let i = 0; i < deck.length - 1; i++) {

        let j = Math.floor(Math.random() * deck.length);
        let temp = deck[i];
        deck[i] = deck[j];
        deck[j] = temp;

    }

    console.log(`${deck.length} cards in deck`);

}

function dealCards(num) { // returns array of cards from deck

    let dealt = [];

    if (deck.length < num) {
        makeDeck();
    }

    for (let i = 0; i < num; i++) {
        dealt.push(deck.pop());
    }

    return dealt;

}

// initialize server
initCards();
makeDeck();
match = deck.pop(); // get top of deck as initial match card
console.log('Match card:\n' + JSON.stringify(match));
console.log('\n------------------------------------------------------\n');
console.log(`Starting server on port ${port}`);
app.get('/', (req, res) => res.sendFile(__dirname + '/page.html'));
console.log('Server is online; waiting for players');

// server responses to client transmissions
io.on('connection', client => {

    client.on('join', msg => { // client has joined the server; give them seven cards

        players.push(client);
        io.to(players[turn].id).emit('take cards', JSON.stringify(dealCards(7)));
        console.log(msg);
        console.log(`\n${players.length} clients now connected:`);
        let i = 0;
        players.forEach(client => console.log('\t' + i++ + ': ' + client.id));

    });

    
    client.on('clicked', msg => console.log(msg + ` (client ${client.id})`)); // TODO: remove once actual GUI is being built; this is just an example event

    client.on('disconnect', msg => {  // client has left the server
        
        players.splice(players.indexOf(client), 1);
        console.log(msg);
        console.log(`\n${players.length} clients still connected:`);
        let i = 0;
        players.forEach(client => console.log('\t' + ++i + ': ' + client.id));
    
    });

    // TODO: implement game transmissions below in client
    client.on('played', card => { // when client plays a card, relay sent card JSON string to all other clients; tell next player to go

        match = card;
        io.broadcast.emit('match', JSON.stringify(match));

        if (reverse) {

            if (turn === 0) {
                turn = players.length - 1;
            } else {
                turn--;
            }

        } else {

            if (turn === players.length - 1) {
                turn = 0;
            } else {
                turn++;
            }

        }

        io.to(players[turn].id).emit('play');

    });

    client.on('draw', num => { // when client needs to draw cards, send them a card array JSON string; inform all other clients who took how many cards

        io.to(players[turn].id).emit('take cards', JSON.stringify(dealCards(num)));
        io.broadcast.emit('op draw', getPlayerNum(client), num);

    });

    client.on('win', () => io.broadcast.emit('lose', getPlayerNum(client))); // player has won the game; tell others who they lost to

});

server.listen(port);
