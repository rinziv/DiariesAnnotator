package viz;

import java.awt.Color;

public class ActivityColors {
//	; ; ; ; ; ; ; ; ; 
	public static int HOME = 0;
	public static int WORK = 1;
	public static int SHOPPING = 2;
	public static int SOCIAL = 3;
//	public static int HOME = 0;
	
	
	
	public static Color[] colors = {
		new Color(166, 206, 227),
		new Color(31, 120, 180),
		new Color(178, 223, 138),
		new Color(51, 160, 44),
		new Color(251, 154, 153),
		new Color(227, 26, 28),
		new Color(253, 191, 111),
		new Color(255, 127, 0),
		new Color(202, 178, 214)
		};

	public static Color getColor(int activityType){
		Color activityColor;
		if(activityType>-1){
			activityColor= colors[activityType%colors.length];
		}else{
			activityColor = Color.GRAY;
		}
		return activityColor;
	}
	
	
}
