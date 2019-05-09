import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class unoDeck {

	private ArrayList<unoCard> deck;
	private HashMap<Integer,unoCard> idToCard;
	
	public unoDeck (){
	
		deck = new ArrayList<unoCard>();
		idToCard = new HashMap<Integer, unoCard>();
		
		int counter = 0;
		cardImages images = new cardImages();
		
		for(int i = 0; i <= 39; i++) {
			
			unoCard card = new unoCard();
			
			
			card.setId(i); //add id
			
			card.setImage( images.getCardPicture(i) ); //add image
			
			
			if(counter == 10) {//reset counter at 10
				counter = 0;
			}
			
			//initiate card color
			if (i < 10) {
				card.setColor("Blue");
				card.setNumber(counter); //add number
				
			}
			else if (i >= 10 && i < 20) {
				card.setColor("Green");
				card.setNumber(counter); //add number
			}
			else if (i >= 20 && i < 30) {
				card.setColor("Yellow");
				card.setNumber(counter); //add number
			}
			else if (i >= 30 && i < 40) {
				card.setColor("Red");
				card.setNumber(counter); //add number
			}
			
			//add card
			deck.add(card);
			
			//add card to map
			idToCard.put(card.getId(), card);
			
			counter++;
	
		}
		
	}
	
	public void shuffle() { //shuffle cards
		Collections.shuffle(deck);
	}
	
	public ArrayList<unoCard> dealHand(int num){//deal cards
		
		ArrayList<unoCard> hand = new ArrayList<unoCard>();
		
		for(int i = 0; i<7; i++) {
			hand.add(deck.get(i));
			deck.remove(i);
		}
		
		return hand;
	}
	
	public unoCard idToCard(int id) { //return card object based on id
		
		return idToCard.get(id);
	}
	
}
