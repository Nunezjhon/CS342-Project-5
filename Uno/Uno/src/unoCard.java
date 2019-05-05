
public class unoCard {

	private int id;
	private int number;
	private String color;
	private String image;
	
	//initialize card
	public unoCard() {
		id = -1;
		number = -1;
		color = null;
		image = null;
	}
	
	public int getId(){
		return id;
	}
	public int getNumber(){
		return number;
	}
	public String getColor() {
		return color;
	}
	public String getImage() {
		return image;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public void setNumber(int num) {
		this.number = num;
	}
	public void setColor(String col) {
		this.color = col;
	}
	public void setImage(String image) {
		this.image = image;
	}
}
