package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import org.opencv.aruco.Aruco;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.aruco.Dictionary;
import org.opencv.calib3d.Calib3d;
/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee.
 */

public class YourService extends KiboRpcService {
    private final String TAG = this.getClass().getSimpleName();
    @Override
    protected void runPlan1(){
        // The mission starts.
        Log.i(TAG, "GAME START");
        api.startMission();

        // Move to a point.
        Point point = new Point(10.42f, -10.58f, 4.82f);
        Quaternion quaternion = new Quaternion(0f, 0f, -0.707f, 0.707f);
        Result result=api.moveTo(point, quaternion, false);
        final int MAX = 10;
        int loop_Count=0;
        while (!result.hasSucceeded()&&loop_Count<MAX){
            api.moveTo(point, quaternion, false);
            ++loop_Count;
        }
        // Get a camera image
        Mat image = api.getMatNavCam();
        Dictionary dictionary =Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        List<Mat> corners = new ArrayList<>();
        Mat markerIds = new Mat();
        Aruco.detectMarkers(image,dictionary,corners,markerIds);

        Mat cameraMatrix = new Mat( 3, 3, CvType.CV_64F);
        cameraMatrix.put(0,0,api.getNavCamIntrinsics()[0]);
        Mat cameraCoefficients = new Mat(1,5,CvType.CV_64F);
        cameraCoefficients.put(0,0,api.getNavCamIntrinsics()[1]);
        cameraCoefficients.convertTo(cameraCoefficients,CvType.CV_64F);

        Mat undistorting = new Mat();
        Calib3d.undistort(image,undistorting,cameraMatrix,cameraCoefficients);

        api.saveMatImage(image,  "Try1.png");
        /* *********************************************************************** */
        /* Write your code to recognize type and number of items in the each area! */
        /* *********************************************************************** */

        // When you recognize items, let’s set the type and number.
        api.setAreaInfo(1, "item_name", 1);

        /* **************************************************** */
        /* Let's move to the each area and recognize the items. */
        /* **************************************************** */

        // When you move to the front of the astronaut, report the rounding completion.
        api.reportRoundingCompletion();

        /* ********************************************************** */
        /* Write your code to recognize which item the astronaut has. */
        /* ********************************************************** */

        // Let's notify the astronaut when you recognize it.
        api.notifyRecognitionItem();

        /* ******************************************************************************************************* */
        /* Write your code to move Astrobee to the location of the target item (what the astronaut is looking for) */
        /* ******************************************************************************************************* */

        // Take a snapshot of the target item.
        api.takeTargetItemSnapshot();
    }

    @Override
    protected void runPlan2(){
       // write your plan 2 here.
    }

    @Override
    protected void runPlan3(){
        // write your plan 3 here.
    }

    // You can add your method.
    private String yourMethod(){
        return "your method";
    }
}
