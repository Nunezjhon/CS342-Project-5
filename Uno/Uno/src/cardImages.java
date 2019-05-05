import java.util.HashMap;

public class cardImages {

	private HashMap<Integer, String> idToPic;
	
	public cardImages() {
		
		idToPic = new HashMap<>();
		
		//Add Blue Cards
		idToPic.put(0, "blue_0_large");
		idToPic.put(1, "blue_1_large");
		idToPic.put(2, "blue_2_large");
		idToPic.put(3, "blue_3_large");
		idToPic.put(4, "blue_4_large");
		idToPic.put(5, "blue_5_large");
		idToPic.put(6, "blue_6_large");
		idToPic.put(7, "blue_7_large");
		idToPic.put(8, "blue_8_large");
		idToPic.put(9, "blue_9_large");
		
		//Add Green Cards
		idToPic.put(10, "green_0_large");
		idToPic.put(11, "green_1_large");
		idToPic.put(12, "green_2_large");
		idToPic.put(13, "green_3_large");
		idToPic.put(14, "green_4_large");
		idToPic.put(15, "green_5_large");
		idToPic.put(16, "green_6_large");
		idToPic.put(17, "green_7_large");
		idToPic.put(18, "green_8_large");
		idToPic.put(19, "green_9_large");
		
		//Add Yellow Cards
		idToPic.put(20, "yellow_0_large");
		idToPic.put(21, "yellow_1_large");
		idToPic.put(22, "yellow_2_large");
		idToPic.put(23, "yellow_3_large");
		idToPic.put(24, "yellow_4_large");
		idToPic.put(25, "yellow_5_large");
		idToPic.put(26, "yellow_6_large");
		idToPic.put(27, "yellow_7_large");
		idToPic.put(28, "yellow_8_large");
		idToPic.put(29, "yellow_9_large");
		
		//Add Red Cards
		idToPic.put(30, "red_0_large");
		idToPic.put(31, "red_1_large");
		idToPic.put(32, "red_2_large");
		idToPic.put(33, "red_3_large");
		idToPic.put(34, "red_4_large");
		idToPic.put(35, "red_5_large");
		idToPic.put(36, "red_6_large");
		idToPic.put(37, "red_7_large");
		idToPic.put(38, "red_8_large");
		idToPic.put(39, "red_9_large");

	}
	
	public String getCardPicture( int n ) {
		
		return idToPic.get(n);
	}
	
	
}
