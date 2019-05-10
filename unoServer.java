
/*
 *	Author(s): 
 *	Joseph Canning (jec2) 
 *	Christian Dominguez (cdomin26)
 *	Jhon Nunez (jnunez34)
 *  
 *  Description:
 *      This file is part of an implementation of Mattel's card game Uno
*/

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class unoServer extends Application {

// signals
public static final String WEL_SIG = "welcome";           // client has connected to server
public static final String WAIT_SIG = "wait";             // client is the first to connect to server
public static final String TURN_SIG = "your move";        // client's turn
public static final String PLAY_SIG = "client played";    // client made a play; server needs to read/process it
public static final String LOSE_SIG = "lose";             // client lost round/game
public static final String OP_SIG = "op:";                // opponent's score
public static final String END_SIG = "over";              // game is over
public static final String YES_SIG = "yes:";              // client wants re-match
public static final String NO_SIG = "no:";                // client doesn't want re-match
public static final String FORCE_SIG = "force:";          // force client disconnect
public static final String UPDATE_SIG = "update list"; 	  // update list of clients
public static final String CLEAR_SIG = "clear list";	  // clears list of clients
public static final String CHALLENGE_SIG = "challenge:";  // challenge an opponent from our list
public static final String REJECT_SIG = "reject"; 		  // reject challenge
public static final String DISABLE_SIG = "disable";		  // disable ability to challenge another client
public static final String CARD_SIG = "card:";			  // signal for cards given by the server
public static final String SCREEN_SIG = "update screen";  // update card pictures displayed
public static final String PLAYED_SIG = "played:";    	  // card client played
public static final String WINNER_SIG = "winner:";        // client won game
public static final String PASS_SIG = "pass";			  // player passed
public static final String TIE_SIG = "tie";               // both clients tied
public static final String WIN_SIG = "win";               // client won round/game
public static final String QUIT_SIG = "quit";             // close window signal

// reference types
private ServerSocket server;						// connects clients
private HashMap<Integer, InputListener> listeners; 	// IO for connected clients; keyed by client ID
private Scene host; 								// host scene
private Button on,off,setPort;
private TextField port;
private Label clientNumber, gameTitle; 
private ArrayList<Integer> clientList;

// primitives
private int portNumber;
private int nextID;									// next client ID to be assigned
private unoDeck deck;

public static void main(String[] args) throws IOException{
	launch(args);	
}

@Override
public void start(Stage primaryStage) {

	primaryStage.setTitle("Uno Card Game Server By: Team 13");
	//HOST SCENE---------------------------------------------------------------------------------------
	gameTitle = new Label("Welcome to Uno! Enter Port Number!");
	gameTitle.setFont(new Font("Arial",12) );
	Label clients = new Label("Number of Clients");
	clientNumber = new Label("0");
	clientNumber.setFont(new Font("Arial", 12));
	clientNumber.setStyle("-fx-font-weight: bold");
	clientNumber.setTextFill(Color.DARKRED);
	
	clientList = new ArrayList<Integer>();
	
	BorderPane gamePane = new BorderPane(); //create pane for card game
	gamePane.setPadding(new Insets(50)); //add padding
	
	on = new Button("Turn On");
	on.setMaxWidth(200);
	off = new Button("Turn Off");
	off.setMaxWidth(200);
	setPort = new Button("Create");
	setPort.setMaxWidth(200);
	port = new TextField();
	port.setPromptText("Enter Port Number to listen to");
	port.setMaxWidth(200);	
	port.setText("7777");
	
	HBox top = new HBox(gameTitle); //title
	top.setAlignment(Pos.TOP_CENTER);

	VBox center = new VBox(port,setPort, on, off,clients,clientNumber);
	center.setAlignment(Pos.TOP_CENTER);
	
	gamePane.setTop(top);
	gamePane.setCenter(center);
	
	host = new Scene(gamePane, 380,250);//create scene for host
	primaryStage.setScene(host); //set Scene
	primaryStage.show(); //show Scene
	
	//DISABLE ON and OFF
	on.setDisable(true);
	off.setDisable(true);
	
	setPort.setOnAction(e -> { // assign portNumber to value entered by user
		try {
			portNumber = Integer.parseInt(port.getText());
			on.setDisable(false);
			setPort.setDisable(true);
			port.setDisable(true);
			System.out.println("Port set!");
		} catch (NumberFormatException x) {
			x.printStackTrace();
			portNumber = 0;
		}
	});
	
	off.setOnAction(e -> { // disconnect the server when off is pressed
		disconnect();
		setPort.setDisable(false);
		off.setDisable(true);
		on.setDisable(false);
	});
	
	on.setOnAction(e -> { // start the server
		acceptClients();
		setPort.setDisable(true);
		on.setDisable(true);
		off.setDisable(false);
	});

} // end of start

private void disconnect() { // close all client connections; disable server

	for (InputListener l : listeners.values()) {
		Socket s = l.getClient();
		try {
			l.send(FORCE_SIG + -1); // send client force disconnect signal with server tag
			s.close();
		} catch (Exception x) {
			x.printStackTrace();

			if (x instanceof IOException) {
				System.err.println("\nPlayer already disconnected\n");
			} else {
				System.err.println("\nNo player to disconnect\n");
			}
		}
	}
	try {
		server.close();
	} catch (Exception x) {
		x.printStackTrace();
		if (x instanceof IOException) {
			System.err.println("\nServer could not be disconnected\n");
		} else {
			System.err.println("\nUnknown error\n");
		}
	}

}

@Override
public void stop() {

	disconnect();
	System.exit(0);

}

// creates a new thread to that runs forever, accepting any clients that wish to join the server; this method should be run immediately after the server starts
private synchronized void acceptClients() {

	new Thread(() -> { // wait for players to connect

		try {

			listeners = new HashMap<>();
			server = new ServerSocket(portNumber);
			InputListener clientIO;

			while (true) {

				listeners.put(nextID, new InputListener(server.accept())); // accept client connection, establish IO
				clientIO = listeners.get(listeners.size() - 1); // get the client that was just connected
				clientIO.start(); // start IO thread
				clientIO.send(WEL_SIG + ++nextID);

				Platform.runLater(() -> clientNumber.setText(""+nextID)  ); //update number of clients on server
				Platform.runLater(() -> clientNumber.setTextFill(Color.GREEN)  );
				
				clientList.add(nextID); //add list 
				updateList(); //update list to all clients 

			}
		} catch (IOException x) {
			x.printStackTrace();
			disconnect(); // tear down server
			System.err.println("\nFailed to set up server\n");

		} finally {
			System.gc();
		}
	}).start();

}

public void updateList() { //update list of all clients 
	for (InputListener l : listeners.values()) {
		try {
			l.send( CLEAR_SIG ); // clear client's previous list
			for(int i =0; i < clientList.size(); i++) {
				l.send(UPDATE_SIG + clientList.get(i)); // send client update list command
			}
		} catch (Exception x) {x.printStackTrace();}
	}
}

//four player game
private void startGame(InputListener one, InputListener two, InputListener three, InputListener four) {

	deck = new unoDeck(); //create and shuffle deck
	deck.shuffle();
	
	
	//deal cards
	for (int i = 0; i < 4; i++) {

		ArrayList<unoCard> hand = deck.dealHand(7);
		
		for( int j = 0; j < 7; j++) {
			listeners.get(i).send(CARD_SIG + hand.get(j).getId()); //send card id
		}
		
		
	}
	
	//update cards
	for(int i = 0; i<4 ; i++) {
		listeners.get(i).send(SCREEN_SIG);
	}

	
	new Thread(() -> {

		
		one.send(TURN_SIG);
		two.send(DISABLE_SIG);
		three.send(DISABLE_SIG);
		four.send(DISABLE_SIG);
		
		///OUTER_LOOP:
		while(!one.last.equals(WIN_SIG)) {//until winner
		
	
			while (!one.last.equals(PLAY_SIG) ) {}
			while (one.last.equals(PLAY_SIG) ) {}

			try {
				Thread.sleep(200);
			} catch (Exception e) {
				e.printStackTrace();
			}

			//Second player goes
			two.send(TURN_SIG);
		
			while (!two.last.equals(PLAY_SIG) ) {}
			while (two.last.equals(PLAY_SIG) ) {}
			
			//Third player goes
			three.send(TURN_SIG);
			
			while (!three.last.equals(PLAY_SIG) ) {}
			while (three.last.equals(PLAY_SIG) ) {}
			
			//Fourth player goes
			four.send(TURN_SIG);
			
			while (!four.last.equals(PLAY_SIG) ) {}
			while (four.last.equals(PLAY_SIG) ) {}
			
			//First player goes
			one.send(TURN_SIG);
		}
		
	}).start();

}

public class InputListener extends Thread {

	private Scanner in;             // reads input
	private PrintWriter out;        // writes output
	private Socket client;          // client this listener is responsible for
	private volatile String last;   // last signal received from client

    public InputListener(Socket client) { // constructor initializes in and out

        this.client = client;

        try {
            in = new Scanner(client.getInputStream());
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (Exception e) {{
        	
        }
            e.printStackTrace();
            System.err.println("\nUnable to read from client IO streams");
        }
    }

    @Override
    public void run() {

        // if statements check for signals and execute appropriate responses; String.contains() should be used if an ID may follow the signal, otherwise use String.equals()
        while (in.hasNextLine()) {
            last = in.nextLine();
            if (last.contains(NO_SIG)) { // client has quit the game; close their Socket
                try {
                    
                    System.out.println("client disconnected");
                    
                    //for(int i = 0; i<4 ; i++) {
                	//	listeners.get(i).send(QUIT_SIG);
                	//}
                    
                    client.close();
                    nextID--; //remove client
                    Platform.runLater(() -> clientNumber.setText(""+nextID)  ); //update number of clients on server
    				//Platform.runLater(() -> clientNumber.setTextFill(Color.GREEN)  );
    				
    				
                   
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (last.contains(QUIT_SIG)) {//add play
            
            	
            	// primaryStage.close();
           	
            }
            else if (last.contains(PLAYED_SIG)) {//add play
            	
            	int x = Integer.parseInt(last.substring(PLAYED_SIG.length()) ); 
            	//update card played
            	for(int i = 0; i<4 ; i++) {
            		listeners.get(i).send(PLAYED_SIG+x);
            	}
           	
            }
            else if (last.contains(WINNER_SIG)) {//pass
            	
            	int y = Integer.parseInt(last.substring(WINNER_SIG.length()) );  
            	
            	//pass = true;
            	System.out.println("Win signal received!");
            	for(int i = 0; i<4 ; i++) {
            		
            		if (i == y-1) {
            			listeners.get(i).send(WIN_SIG);
            		}
            		else {
            			listeners.get(i).send(LOSE_SIG);
            		}
            	
            	}
            	
            	
            }
            else if (last.equals(TIE_SIG)) {
            	
            	System.out.println("Everyone passed!");
            	for(int i = 0; i<4 ; i++) {
            		listeners.get(i).send(TIE_SIG);
            	}
            	
            }
            else if (last.equals(PASS_SIG)) {
            	
            	System.out.println("player passed!");
            	for(int i = 0; i<4 ; i++) {
            		listeners.get(i).send(PASS_SIG); //increase the pass counter
            	}
            	
            }
            
            
            else if (last.contains(CHALLENGE_SIG)) {
            	
            	
            	//InputListener a = listeners.get(id1-1);
                InputListener a = listeners.get(0);
            	InputListener b = listeners.get(1);
            	InputListener c = listeners.get(2);
            	InputListener d = listeners.get(3);
            	startGame(a,b,c,d);	//start game for two clients

            }
            
            
        }
        // close IO once Socket is closed
        in.close();
        out.close();
    }

    public void send(String message) { // sends message to client's input stream
        out.println(message);
        out.flush();
    }

    // getters/setters
    public Socket getClient() { return client; }

}



}
