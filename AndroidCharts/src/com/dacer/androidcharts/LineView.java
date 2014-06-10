package com.dacer.androidcharts;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by Dacer on 11/4/13.
 * Edited by Lee youngchan 21/1/14
 */
public class LineView extends View {
    private int mViewHeight;
    //drawBackground
    private boolean autoSetDataOfGird = true;
    private boolean autoSetGridWidth = true;
    private int dataOfAGird = 10;
    private int bottomTextHeight = 0;
    private ArrayList<String> bottomTextList;
    
    private ArrayList<ArrayList<Double>> dataLists;
    private ArrayList<Double> dataList;
    
    private ArrayList<Double> xCoordinateList = new ArrayList<Double>();
    private ArrayList<Double> yCoordinateList = new ArrayList<Double>();
    
    private ArrayList<ArrayList<Dot>> drawDotLists = new ArrayList<ArrayList<Dot>>();
    private ArrayList<Dot> drawDotList = new ArrayList<Dot>();
    
    private Paint bottomTextPaint = new Paint();
    private int bottomTextDescent;

    //popup
    private Paint popupTextPaint = new Paint();
    private final int bottomTriangleHeight = 12;
    public boolean showPopup = true; 

	private Dot selectedDot;

    private int topLineLength = MyUtils.dip2px(getContext(), 12);
    
    // His original code
    //private int sideLineLength = MyUtils.dip2px(getContext(),45)/3*2;
    private int sideLineLength = MyUtils.dip2px(getContext(), 18);      // <-- FOR 30 ENTRIES
    
    // This is the width between the grids
    //Original was 45 in call to dip2px                                             
    private int backgroundGridWidth = MyUtils.dip2px(getContext(), 5);

    //Constants
    private final int popupTopPadding = MyUtils.dip2px(getContext(), 5);
    private final int popupBottomMargin = MyUtils.dip2px(getContext(),5);
    private final int bottomTextTopMargin = MyUtils.sp2px(getContext(),5);
    private final int bottomLineLength = MyUtils.sp2px(getContext(), 22);
    private final int DOT_INNER_CIR_RADIUS = MyUtils.dip2px(getContext(), 2);
    private final int DOT_OUTER_CIR_RADIUS = MyUtils.dip2px(getContext(),5);
    private final int MIN_TOP_LINE_LENGTH = MyUtils.dip2px(getContext(),12);
    private final int MIN_VERTICAL_GRID_NUM = 4;
    private final int MIN_HORIZONTAL_GRID_NUM = 1;
    private final int BACKGROUND_LINE_COLOR = Color.parseColor("#EEEEEE");
    private final int BOTTOM_TEXT_COLOR = Color.parseColor("#9B9A9B");
    
    public static final int SHOW_POPUPS_All = 1;
    public static final int SHOW_POPUPS_MAXMIN_ONLY = 2;
    public static final int SHOW_POPUPS_NONE = 3;

    private int showPopupType = SHOW_POPUPS_NONE;
    public void setShowPopup(int popupType) {
		this.showPopupType = popupType;
	}

	//
    private Boolean drawDotLine = false;
    //
    private String[] colorArray = {"#e74c3c","#2980b9","#1abc9c"};
    //popup 
    private int[] popupColorArray = {R.drawable.popup_red,R.drawable.popup_blue,R.drawable.popup_green};
    
	public void setDrawDotLine(Boolean drawDotLine) {
		this.drawDotLine = drawDotLine;
	}

	private Runnable animator = new Runnable() {
        @Override
        public void run() {
            boolean needNewFrame = false;
            for(ArrayList<Dot> data : drawDotLists){
            	for(Dot dot : data){
                    dot.update();
                    if(!dot.isAtRest()){
                        needNewFrame = true;
                    }
                }
            }
            if (needNewFrame) {
                postDelayed(this, 25);
            }
            invalidate();
        }
    };

    public LineView(Context context){
        this(context,null);
    }
    public LineView(Context context, AttributeSet attrs){
        super(context, attrs);
        popupTextPaint.setAntiAlias(true);
        popupTextPaint.setColor(Color.WHITE);
        popupTextPaint.setTextSize(MyUtils.sp2px(getContext(), 11));
        popupTextPaint.setStrokeWidth(5);
        popupTextPaint.setTextAlign(Paint.Align.CENTER);

        bottomTextPaint.setAntiAlias(true);
        bottomTextPaint.setTextSize(MyUtils.sp2px(getContext(), 9));  // ORIGINAL = 12. DATES = 5
        bottomTextPaint.setTextAlign(Paint.Align.CENTER);
        bottomTextPaint.setStyle(Paint.Style.FILL);
        bottomTextPaint.setColor(BOTTOM_TEXT_COLOR);
    }

    /**
     * dataList will be reset when called is method.
     * @param bottomTextList The String ArrayList in the bottom.
     */
    public void setBottomTextList(ArrayList<String> bottomTextList){
        this.dataList = null;
        this.bottomTextList = bottomTextList;

        Rect r = new Rect();
        int longestWidth = 0;
        String longestStr = "";
        bottomTextDescent = 0;
        for(String s:bottomTextList){
            bottomTextPaint.getTextBounds(s,0,s.length(),r);
            if(bottomTextHeight<r.height()){
                bottomTextHeight = r.height();
            }
            if(autoSetGridWidth&&(longestWidth<r.width())){
                longestWidth = r.width();
                longestStr = s;
            }
            if(bottomTextDescent<(Math.abs(r.bottom))){
                bottomTextDescent = Math.abs(r.bottom);
            }
        }

        if(autoSetGridWidth){
        	// I COMMENTED THIS OUT - This makes it nice formatted.
            /*if(backgroundGridWidth<longestWidth){
                backgroundGridWidth = longestWidth+(int)bottomTextPaint.measureText(longestStr,0,1);
            }*/
        	
        	// I ADDED THIS
        	if(backgroundGridWidth < longestWidth){
        		backgroundGridWidth = longestWidth + 5;
        	}
        	
            if(sideLineLength<longestWidth/2){
                sideLineLength = longestWidth/2;
            }
        }

        refreshXCoordinateList(getHorizontalGridNum());
    }

    /**
     *
     * @param dataLists The Double ArrayLists for showing,
     *                 dataList.size() must < bottomTextList.size()
     */
    public void setDataList(ArrayList<ArrayList<Double>> dataLists){
    	selectedDot = null;
        this.dataLists = dataLists;
        for(ArrayList<Double> list : dataLists){
        	if(list.size() > bottomTextList.size()){
                throw new RuntimeException("dacer.LineView error:" +
                        " dataList.size() > bottomTextList.size() !!!");
            }
        }
        Double biggestData = 0.0;
        for(ArrayList<Double> list : dataLists){
        	if(autoSetDataOfGird){
                for(Double i:list){
                    if(biggestData<i){
                        biggestData = i;
                    }
                }
        	}
        	dataOfAGird = 1;
        	while(biggestData/10 > dataOfAGird){
        		dataOfAGird *= 10;
        	}
        }
        
        refreshAfterDataChanged();
        showPopup = true;
        setMinimumWidth(0); // It can help the LineView reset the Width,
                                // I don't know the better way..
        postInvalidate();
    }

    
    private void refreshAfterDataChanged(){
        double verticalGridNum = getVerticalGridlNum();
        refreshTopLineLength(verticalGridNum);
        refreshYCoordinateList(verticalGridNum);
        refreshDrawDotList(verticalGridNum);
    }

    private double getVerticalGridlNum(){
        double verticalGridNum = MIN_VERTICAL_GRID_NUM;
        if(dataLists != null && !dataLists.isEmpty()){
        	for(ArrayList<Double> list : dataLists){
	        	for(Double doub:list){
	        		if(verticalGridNum<(doub+1)){
	        			verticalGridNum = doub+1;
	        		}
	        	}
        	}
        }
        return verticalGridNum;
    }

    private int getHorizontalGridNum(){
    	int horizontalGridNum = 30-1;
        if(horizontalGridNum<MIN_HORIZONTAL_GRID_NUM){
            horizontalGridNum = MIN_HORIZONTAL_GRID_NUM;
        }
        return horizontalGridNum;
    }

    private void refreshXCoordinateList(int horizontalGridNum){
        xCoordinateList.clear();
        for(int i=0;i<(horizontalGridNum+1);i++){
            xCoordinateList.add((double)sideLineLength + backgroundGridWidth*i);
        }

    }

    private void refreshYCoordinateList(double verticalGridNum){
        yCoordinateList.clear();
        for(int i=0;i<(verticalGridNum+1);i++){
            yCoordinateList.add((double)topLineLength +
                    ((mViewHeight-topLineLength-bottomTextHeight-bottomTextTopMargin-
                            bottomLineLength-bottomTextDescent)*i/(verticalGridNum)));
        }
    }

    private void refreshDrawDotList(double verticalGridNum){
        if(dataLists != null && !dataLists.isEmpty()){
    		if(drawDotLists.size() == 0){
    			for(int k = 0; k < dataLists.size(); k++){
    				drawDotLists.add(new ArrayList<LineView.Dot>());
    			}
    		}
        	for(int k = 0; k < dataLists.size(); k++){
        		int drawDotSize = drawDotLists.get(k).isEmpty()? 0:drawDotLists.get(k).size();
        		
        		for(int i=0;i<dataLists.get(k).size();i++){
                    Double x = xCoordinateList.get(i);
                    double y = yCoordinateList.get((int) (verticalGridNum - dataLists.get(k).get(i)));
                    if(i>drawDotSize-1){
                    	//
                        drawDotLists.get(k).add(new Dot(x.intValue(), 0, x.intValue(), (int)y, dataLists.get(k).get(i),k));
                    }else{
                    	//
                        drawDotLists.get(k).set(i, drawDotLists.get(k).get(i).setTargetData(x.intValue(),(int)y,dataLists.get(k).get(i),k));
                    }
                }
        		
        		int temp = drawDotLists.get(k).size() - dataLists.get(k).size();
        		for(int i=0; i<temp; i++){
        			drawDotLists.get(k).remove(drawDotLists.get(k).size()-1);
        		}
        	}
        }
        removeCallbacks(animator);
        post(animator);
    }

    private void refreshTopLineLength(double verticalGridNum){
        // For prevent popup can't be completely showed when backgroundGridHeight is too small.
        // But this code not so good.
        if((mViewHeight-topLineLength-bottomTextHeight-bottomTextTopMargin)/
                (verticalGridNum+2)<getPopupHeight()){
            topLineLength = getPopupHeight()+DOT_OUTER_CIR_RADIUS+DOT_INNER_CIR_RADIUS+2;
        }else{
            topLineLength = MIN_TOP_LINE_LENGTH;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackgroundLines(canvas);
        drawLines(canvas);
        drawDots(canvas);
        int minFlag = 0;    // Only show one min value if there are more than one
        int maxFlag = 0;    // Only show one max value if there are more than one
        
        for(int k=0; k < drawDotLists.size(); k++){
        	double MaxValue = Collections.max(dataLists.get(k));
        	double MinValue = Collections.min(dataLists.get(k));
        	for(Dot d: drawDotLists.get(k)){
        		if(showPopupType == SHOW_POPUPS_All)
        			drawPopup(canvas, String.valueOf(d.data), d.getPoint(),popupColorArray[k%3]);
        		else if(showPopupType == SHOW_POPUPS_MAXMIN_ONLY){
        			if(d.data == MaxValue && maxFlag == 0)
        			{
        				drawPopup(canvas, String.valueOf(d.data), d.getPoint(),popupColorArray[k%3]);
        				maxFlag++;
        			}
        			if(d.data == MinValue && minFlag == 0)
        			{
        				drawPopup(canvas, String.valueOf(d.data), d.getPoint(),popupColorArray[k%3]);
        				minFlag++;
        			}
        		}
        	}
        }
//    
        if(showPopup && selectedDot != null){
            drawPopup(canvas,
                    String.valueOf(selectedDot.data),
                    selectedDot.getPoint(),popupColorArray[selectedDot.linenumber%3]);
        }
    }

    /**
     *
     * @param canvas  The canvas you need to draw on.
     * @param point   The Point consists of the x y coordinates from left bottom to right top.
     *                Like is
     *                
     *                3
     *                2
     *                1
     *                0 1 2 3 4 5
     */
    private void drawPopup(Canvas canvas,String num, Point point,int PopupColor){
        boolean singularNum = (num.length() == 1);
        int sidePadding = MyUtils.dip2px(getContext(),singularNum? 8:5);
        int x = point.x;
        int y = point.y-MyUtils.dip2px(getContext(),5);
        Rect popupTextRect = new Rect();
        popupTextPaint.getTextBounds(num,0,num.length(),popupTextRect);
        Rect r = new Rect(x-popupTextRect.width()/2-sidePadding,
                y - popupTextRect.height()-bottomTriangleHeight-popupTopPadding*2-popupBottomMargin,
                x + popupTextRect.width()/2+sidePadding,
                y+popupTopPadding-popupBottomMargin);

        NinePatchDrawable popup = (NinePatchDrawable)getResources().getDrawable(PopupColor);
        popup.setBounds(r);
        popup.draw(canvas);
        //Original code:
        //canvas.drawText(num, x, y-bottomTriangleHeight-popupBottomMargin, popupTextPaint);
        canvas.drawText(num, x, y-bottomTriangleHeight-popupBottomMargin - 20, popupTextPaint);
    }

    private int getPopupHeight(){
        Rect popupTextRect = new Rect();
        popupTextPaint.getTextBounds("9",0,1,popupTextRect);
        Rect r = new Rect(-popupTextRect.width()/2,
                 - popupTextRect.height()-bottomTriangleHeight-popupTopPadding*2-popupBottomMargin,
                 + popupTextRect.width()/2,
                +popupTopPadding-popupBottomMargin);
        return r.height();
    }

    //
    private void drawDots(Canvas canvas){
        Paint bigCirPaint = new Paint();
        bigCirPaint.setAntiAlias(true);
        Paint smallCirPaint = new Paint(bigCirPaint);
        smallCirPaint.setColor(Color.parseColor("#FFFFFF"));
        if(drawDotLists!=null && !drawDotLists.isEmpty()){
        	for(int k=0; k < drawDotLists.size(); k++){	
        		bigCirPaint.setColor(Color.parseColor(colorArray[k%3]));
        		for(Dot dot : drawDotLists.get(k)){
                	canvas.drawCircle(dot.x,dot.y,DOT_OUTER_CIR_RADIUS,bigCirPaint);
                	canvas.drawCircle(dot.x,dot.y,DOT_INNER_CIR_RADIUS,smallCirPaint);
            	}
        	}
        }
    }

    //
    private void drawLines(Canvas canvas){
        Paint linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(MyUtils.dip2px(getContext(), 2));
        for(int k = 0; k<drawDotLists.size(); k ++){
        	linePaint.setColor(Color.parseColor(colorArray[k%3]));
	        for(int i=0; i<drawDotLists.get(k).size()-1; i++){
	            canvas.drawLine(drawDotLists.get(k).get(i).x,
	                    drawDotLists.get(k).get(i).y,
	                    drawDotLists.get(k).get(i+1).x,
	                    drawDotLists.get(k).get(i+1).y,
	                    linePaint);
	        }
        }
    }
    
    
     private void drawBackgroundLines(Canvas canvas){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(MyUtils.dip2px(getContext(),1f));
        paint.setColor(BACKGROUND_LINE_COLOR);
        PathEffect effects = new DashPathEffect(
                new float[]{10,5,10,5}, 1);

        //draw vertical lines
        for(int i=0;i<xCoordinateList.size();i++){
            canvas.drawLine(xCoordinateList.get(i).floatValue(),
                    0,
                    xCoordinateList.get(i).floatValue(),
                    mViewHeight - bottomTextTopMargin - bottomTextHeight-bottomTextDescent,
                    paint);
        }
        
        //draw dotted lines
      paint.setPathEffect(effects);
      Path dottedPath = new Path();
      for(int i=0;i<yCoordinateList.size();i++){
          if((yCoordinateList.size()-1-i)%dataOfAGird == 0){
              dottedPath.moveTo((float)0, yCoordinateList.get(i).floatValue());
              dottedPath.lineTo(getWidth(), yCoordinateList.get(i).floatValue());
              canvas.drawPath(dottedPath, paint);
          }
      }
    	  //draw bottom text
      if(bottomTextList != null){
    	  for(int i=0;i<bottomTextList.size();i++){
    		  canvas.drawText(bottomTextList.get(i), sideLineLength+backgroundGridWidth*i, mViewHeight-bottomTextDescent, bottomTextPaint);
    	  }
      }
      
      if(!drawDotLine){
    	//draw solid lines
          for(int i=0;i<yCoordinateList.size();i++){
              if((yCoordinateList.size()-1-i)%dataOfAGird == 0){
                  canvas.drawLine(0,yCoordinateList.get(i).floatValue(),getWidth(),yCoordinateList.get(i).floatValue(),paint);
              }
          }
      }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mViewWidth = measureWidth(widthMeasureSpec);
        mViewHeight = measureHeight(heightMeasureSpec);
        refreshAfterDataChanged();
        setMeasuredDimension(mViewWidth,mViewHeight);
    }

    private int measureWidth(int measureSpec){
        int horizontalGridNum = getHorizontalGridNum();
        int preferred = backgroundGridWidth*horizontalGridNum+sideLineLength*2;
        return getMeasurement(measureSpec, preferred);
    }

    private int measureHeight(int measureSpec){
        int preferred = 0;
        return getMeasurement(measureSpec, preferred);
    }

    private int getMeasurement(int measureSpec, int preferred){
        int specSize = MeasureSpec.getSize(measureSpec);
        int measurement;
        switch(MeasureSpec.getMode(measureSpec)){
            case MeasureSpec.EXACTLY:
                measurement = specSize;
                break;
            case MeasureSpec.AT_MOST:
                measurement = Math.min(preferred, specSize);
                break;
            default:
                measurement = preferred;
                break;
        }
        return measurement;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();
        Region r = new Region();
        int width = backgroundGridWidth/2;
        if(drawDotLists != null || !drawDotLists.isEmpty()){
	        for(ArrayList<Dot> data : drawDotLists){
	        	for(Dot dot : data){
	        		r.set(dot.x-width,dot.y-width,dot.x+width,dot.y+width);
	                if (r.contains(point.x,point.y) && event.getAction() == MotionEvent.ACTION_DOWN){
	                    selectedDot = dot;
	                }else if (event.getAction() == MotionEvent.ACTION_UP){
	                    if (r.contains(point.x,point.y)){
	                        showPopup = true;
	                    }
	                }
	            }
	        }
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN ||
                event.getAction() == MotionEvent.ACTION_UP){
            postInvalidate();
        }
        return true;
    }


    
    class Dot{
        int x;
        int y;
        double data;
        int targetX;
        int targetY;
        int linenumber;
        int velocity = MyUtils.dip2px(getContext(),18);

        Dot(int x,int y,int targetX,int targetY,Double data,int linenumber){
            this.x = x;
            this.y = y;
            this.linenumber = linenumber;
            setTargetData(targetX, targetY,data,linenumber);
        }

        Point getPoint(){
            return new Point(x,y);
        }

        Dot setTargetData(int targetX,int targetY,Double data,int linenumber){
            this.targetX = targetX;
            this.targetY = targetY;
            this.data = roundTwoDecimals(data);
            this.linenumber = linenumber;
            return this;
        }

        boolean isAtRest(){
            return (x==targetX)&&(y==targetY);
        }

        void update(){
            x = updateSelf(x, targetX, velocity);
            y = updateSelf(y, targetY, velocity);
        }

        private int updateSelf(int origin, int target, int velocity){
            if (origin < target) {
                origin += velocity;
            } else if (origin > target){
                origin-= velocity;
            }
            if(Math.abs(target-origin)<velocity){
                origin = target;
            }
            return origin;
        }
        
        public double roundTwoDecimals(double d) {
            DecimalFormat twoDForm = new DecimalFormat("#.##");
            return Double.valueOf(twoDForm.format(d));
        }

    }
    
    
}



