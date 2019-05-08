
public class cardPic {

	private int id;
	private boolean played;
	
	cardPic(int ID, boolean status){
	
		this.id = ID;
		this.played = status;
		
	}
	
	public int getId() {
		return this.id;
	}
	public boolean getStatus() {
		return this.played;
	}
	public void setStatus(boolean status) {
		this.played = status;
	}
	
	
}
