package Team467Vision2022;

import java.util.Scanner;
public class Distancefinderfinalteam467{
    
    public static void main(String args[]){

       Scanner scanner = new Scanner(System.in);

       System.out.println("Enter in the x coordinate for the center of the bounding box: ");

       double x = scanner.nextDouble();

       System.out.println("Enter in the y coordinate for the center of the bounding box: ");

       double y = scanner.nextDouble();

       double horizontalMiddleOfBoundingBox = (x-640);//Middle point of the bounding box in terms of x (horizontal) //(INTEGREATION)
		   
       double verticalMiddleOfBoundingBox = (y-360);//Middle point of the bounding box in terms of y (vertical) //(INTEGREATION)
       
       double horizontalMiddleOfCamera = 640; //Middle point of the Camera frame in terms of horizontal
		   
       double verticalMiddleOfCamera = 360; //Middle point of the Camera frame in terms of vertical
		   
       double differenceOftowerHeightandCameraHeight = 56;//towerHeight-camHeight (opposite side of camera angle)
		   
       double HalfVerticalAngleOfCameraFov = 17.15; 
       
       double verticalPixelToAngleRatio = verticalMiddleOfCamera/HalfVerticalAngleOfCameraFov;//*in degreees* 20.991253644314869
		   
       double angleofTargetOfEquator = verticalMiddleOfBoundingBox/verticalPixelToAngleRatio;//Pixel-Angle ratio that determines the angle of bbMidy from camMidy
       //VertVarAngle = angleofTargetOffHorizontalMid
           
       //Measurement scale: 38 (w)*38 (d)* 23.75 (h)

       double tanValue = Math.tan(angleofTargetOfEquator); // 
		
       double distanceOfRobotFromTarget = (differenceOftowerHeightandCameraHeight/tanValue); // oppCamAng/tan(VertVarAngle)
 
       double HalfHorizontalAngleOfCameraFov = 30.5;//*in degrees* 20.983606557377049

       double horizontalPixelToAngleRatio = horizontalMiddleOfCamera/HalfHorizontalAngleOfCameraFov;
		   
       double angleofTargetOffPrimeMeridian = horizontalMiddleOfBoundingBox/horizontalPixelToAngleRatio; //using pixel-angle ratio that determines the angle of bbMidx from camMidx

       System.out.println("The Vertical Angle Degree is: " + angleofTargetOfEquator);

       System.out.println("The Horizontal Angle Degree is: " + angleofTargetOffPrimeMeridian);
       
       System.out.println("The distance of the robot from the target is: " + distanceOfRobotFromTarget);

       scanner.close();
    }
}
