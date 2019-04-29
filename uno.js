/*
 *  uno.js
 *
 *  Author(s): Joseph Canning (jec2)
 *  Description:
 *      This file is part of an implementation of Mattel's card game Uno and runs a server on a hard-coded port that accepts up to ten players.
*/

// non-function variables
const app = require('express')();
const server = require('http').createServer(app);
const io = require('socket.io')(server);
const port = 5555;
const colors = ['red', 'blue', 'yellow', 'green'];
const nums = [1, 2, 3, 4, 5, 6, 7, 8, 9]; // zero is excluded for convenience; see initCards
const actions = ['draw2', 'draw4', 'skip', 'reverse', 'wild'];
const playerCap = 10;
let players = []; // array of clients on server
let cards = []; // array of all cards; effectively final after initialization
let deck = []; // shuffled deck of cards that are dealt to the players
let turn = 0; // player whose turn it is
let reverse = false; // turn order is reversed
let gameStarted = false; // game is in progress
var match; // card whose number, color, or action must be matched

// functions
function getPlayerNum(client) { // convert index of client in players to player number visible to clients and return it

    let playerNum = players.findIndex(player => player.socket.id === client.id);

    if (playerNum === -1) {
        return -1;
    } else {
        return playerNum + 1;
    }

}

function Card(num, color, action) { // Card constructor

    this.num = num; // number
    this.color = color; // face color
    this.action = action; // draw two, skip, etc.
    this.imgID = '' + num + color + action; // name of image representing this card

}

function Player(socket, cards, num) { // Player constructor

    this.socket = socket; // player's web socket
    this.cards = cards; // number of cards
    this.num = num; // player number

}

function initCards() { // creates the 108 Uno cards

    colors.forEach(color => {

        nums.forEach(num => {

            cards.push(new Card(num, color, null));
            cards.push(new Card(num, color, null));

        });

        actions.forEach(action => cards.push(new Card(null, color, action)));
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

    if (players.length + 1 > playerCap) { // server is full; reject further connections

        client.emit('reject', 'Server is full');
        client.disconnect();
        console.log(`Client ${client.id} rejected: server is full`);
        return;

    }

    if (gameStarted) { // first player started the game; reject further connections

        client.emit('reject', 'Game in progress');
        client.disconnect();
        console.log(`Client ${client.id} rejected: game in progress`);
        return;

    }

    client.on('join', msg => { // client has joined the server; give them seven cards and let others know

        players.push(new Player(client, 7, players.length + 1));
        client.emit('assign', players.length);
        client.emit('take cards', JSON.stringify(dealCards(7)));
        client.broadcast.emit('op update', JSON.stringify(players));
        console.log(msg);
        console.log(`\n${players.length} clients now connected:`);

        if (players.length === 1) {
            client.emit('host');
        }

    });
    
    client.on('clicked', msg => console.log(msg + ` (client ${client.id})`)); // TODO: remove once actual GUI is being built; this is just an example event

    client.on('disconnect', msg => {  // client has left the server
        
        players.splice(players.indexOf(players.findIndex(player => player.socket.id === client.id)), 1);
        client.broadcast.emit('op update', JSON.stringify(players));
        console.log(msg);
        console.log(`\n${players.length} clients still connected:`);
        let i = 0;
        players.forEach(player => console.log('\t' + ++i + ': ' + player.socket.id));
    
    });

    // TODO: implement game transmissions below in client
    client.on('played', card => { // when client plays a card, relay sent card JSON string to all other clients; tell next player to go

        match = card;
        client.broadcast.emit('match', JSON.stringify(match));

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

        io.to(`${players[turn].socket.id}`).emit('play');

    });

    client.on('draw', num => { // when client needs to draw cards, send them a card array JSON string; inform all other clients who took how many cards

        client.emit('take cards', JSON.stringify(dealCards(num)));
        client.broadcast.emit('op draw', getPlayerNum(client), num);

    });

    client.on('win', () => io.broadcast.emit('lose', getPlayerNum(client))); // player has won the game; tell others who they lost to

    client.on('start', () => { // when first player starts game, tell all other clients

        client.broadcast.emit('ready', 'The game has been started');
        gameStarted = true;


    });

});

server.listen(port);
