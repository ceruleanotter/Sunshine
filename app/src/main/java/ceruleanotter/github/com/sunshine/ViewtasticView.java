package ceruleanotter.github.com.sunshine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.graphics.drawable.shapes.OvalShape;
import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

/**
 * Created by lyla on 8/15/14.
 */
public class ViewtasticView extends View {
    private ShapeDrawable mCircle_outer;
    private ShapeDrawable mEye_L;
    private ShapeDrawable mEye_R;
    private ShapeDrawable mMouth;


    private static final double DESIRE_TEMP_HIGH = 27.5;
    private static final double DESIRE_TEMP_LOW = 18;

    private static final int TOTAL_WIDTH = 200;
    private static final int TOTAL_HEIGHT = 200;
    private static final int EYE_DIM = 25;
    private static final int MOUTH_DIM = 40;

    public ViewtasticView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ViewtasticView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewtasticView(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.isInEditMode();
        int x = 0;
        int y = 0;

        mCircle_outer = new ShapeDrawable(new OvalShape());
        mCircle_outer.getPaint().setColor(getResources().getColor(R.color.sunshine_blue));
        mCircle_outer.setBounds(x, y, x + TOTAL_WIDTH, y + TOTAL_HEIGHT);
        int eyeLevel = TOTAL_WIDTH/3;
        int edgeDistance = TOTAL_WIDTH/5;

        mEye_L = new ShapeDrawable(new OvalShape());
        mEye_L.getPaint().setColor(getResources().getColor(R.color.grey));
        mEye_L.setBounds(edgeDistance, eyeLevel, edgeDistance + EYE_DIM, eyeLevel + EYE_DIM);

        mEye_R = new ShapeDrawable(new OvalShape());
        mEye_R.getPaint().setColor(getResources().getColor(R.color.grey));
        int rx = TOTAL_WIDTH-edgeDistance-EYE_DIM;
        int ry = eyeLevel;

        mEye_R.setBounds(rx, ry, rx+EYE_DIM, ry+EYE_DIM);

        mMouth = new ShapeDrawable(new ArcShape(0,180));

        int mx = (TOTAL_WIDTH/2)-(MOUTH_DIM/2);
        int my = TOTAL_HEIGHT - eyeLevel-MOUTH_DIM;
        mMouth.getPaint().setColor(getResources().getColor(R.color.grey));
        mMouth.setBounds(mx, my, mx+MOUTH_DIM, my+MOUTH_DIM);


    }

    public void setEmotion(boolean test) {
        AccessibilityManager accessibilityManager =
                (AccessibilityManager) this.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);

        if (accessibilityManager.isEnabled()) sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);

        double weatherMod = 0;
        double humidTempMod = 0;
        double extremeTempMod = 0;
//        switch(drawable) {
//            case R.drawable.art_light_clouds:
//            case R.drawable.art_clear:
//                weatherMod = 0.5;
//                break;
//            default:
//                break;
//
//        }
        sadHappy(test);

    }


    private void sadHappy(boolean happy) {
        if (happy) {
            mMouth = new ShapeDrawable(new ArcShape(0,180));
        } else {
            mMouth = new ShapeDrawable(new ArcShape(0,-180));
        }
        int eyeLevel = TOTAL_WIDTH/3;
        int mx = (TOTAL_WIDTH/2)-(MOUTH_DIM/2);
        int my = TOTAL_HEIGHT - eyeLevel-MOUTH_DIM;
        mMouth.getPaint().setColor(getResources().getColor(R.color.grey));
        mMouth.setBounds(mx, my, mx+MOUTH_DIM, my+MOUTH_DIM);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCircle_outer.draw(canvas);
        mEye_L.draw(canvas);
        mEye_R.draw(canvas);
        mMouth.draw(canvas);
        super.onDraw(canvas);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int myHeight = MeasureSpec.getSize(heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int myWidth = MeasureSpec.getSize(widthMeasureSpec);


        setMeasuredDimension(myWidth, myHeight);


    }




    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.getText().add("hello");

        return super.dispatchPopulateAccessibilityEvent(event);
    }
}
