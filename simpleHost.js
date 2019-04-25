const app = require('express')();
const server = require('http').createServer(app);
const io = require('socket.io')(server);
const port = 5555;
let players = [];

console.log(`Starting server on port ${port}`);
app.get('/', (req, res) => res.sendFile(__dirname + '/page.html'));

io.on('connection', client => {

    client.on('join', msg => {

        players.push(client);
        console.log(msg);
        console.log(`\n${players.length} clients now connected:`);
        let i = 0;
        players.forEach(client => console.log('\t' + i++ + ': ' + client.id));

    });
    client.on('clicked', msg => console.log(msg + ` (client ${client.id})`));
    client.on('disconnect', msg => { 
        
        players.pop(client);
        console.log(msg);
        console.log(`\n${players.length} clients still connected:`);
        let i = 0;
        players.forEach(client => console.log('\t' + ++i + ': ' + client.id));
    
    });

});

server.listen(port);
