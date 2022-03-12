package processing;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import java.util.ArrayList;
import org.opencv.core.KeyPoint; 
import org.opencv.core.MatOfKeyPoint;

public class MatOfKeyPoint2MatOfPoint { public static MatOfPoint toMatOfPoint(MatOfKeyPoint mokp) { 
    KeyPoint[] keyPoints = mokp.toArray(); 
    ArrayList<Point> arrayOfPoints = new ArrayList<Point>();

    for(int i = 0; i < keyPoints.length; i++) {
        arrayOfPoints.add(keyPoints[i].pt);         
    }   

    MatOfPoint matOfPoint = new MatOfPoint();
    matOfPoint.fromList(arrayOfPoints);

    return matOfPoint;
}
}