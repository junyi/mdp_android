package com.example.mdptool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Maze extends SurfaceView implements SurfaceHolder.Callback {
	
	private static final int row = 15;
	private static final int column = 20;
	int leftCoord, topCoord, rightCoord, botCoord;
	RectF rect;
	
	SurfaceHolder surfaceHolder;
	MazeThread mazeThread = null;
	
	private Paint rectColor =  new Paint(Paint.ANTI_ALIAS_FLAG);
	
	public Maze(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		getHolder().addCallback(this);
		
	}
	public Maze(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		getHolder().addCallback(this);
		
	}
	public Maze(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		getHolder().addCallback(this);
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		   
		//Create and start background Thread
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(mazeThread == null){
			return;
		}
        boolean retry = true;
        mazeThread.setRunning(false);
        while (retry) {
            try {
                mazeThread.join();
                retry = false;
            } catch (InterruptedException e) {
                // we will try it again and again...
            }
        }
    }

	protected void onDraw(Canvas canvas, int[][] map){
		//super.onDraw(canvas);
		int mGridSize = Math.min((canvas.getHeight() / row), (canvas.getWidth() / column));
		int xOffset = (canvas.getHeight() - (mGridSize * row)) / 2;
		int yOffset = (canvas.getWidth() - (mGridSize * column)) / 2;
		for(int x = 0; x < row; x++){
            for(int y = 0; y < column; y++){
            	leftCoord = yOffset + mGridSize * y;
            	topCoord = xOffset + mGridSize * x;
            	rightCoord = yOffset + mGridSize * (y + 1);
            	botCoord = xOffset + mGridSize * (x + 1);
            	rect = new RectF(leftCoord, topCoord, rightCoord, botCoord);
            	
	            rectColor.setColor(Color.WHITE);
	            if(((x==0 || x==1 || x==2 ) && (y==0 || y==1 || y==2)) || ((x==14 || x==13 || x==12 ) && (y==19 || y==18 || y==17))){
	            	rectColor.setColor(Color.GREEN);
	            }
	            if((x==6 || x==7 ||x ==8) && (y==8 || y==9 ||  y==10)){
	            	rectColor.setColor(Color.BLUE);
	            }
	            if(map[x][y] == 1){
	        		rectColor.setColor(Color.LTGRAY);
	        	}else if(map[x][y] == 2){
	        		rectColor.setColor(Color.BLACK);
	        	}
	        	rectColor.setStyle(Paint.Style.FILL);
	        	canvas.drawRect(rect, rectColor);
	        	rectColor.setColor(Color.BLACK);
	        	rectColor.setStrokeWidth(2);
	        	rectColor.setStyle(Paint.Style.STROKE);
	        	canvas.drawRect(rect, rectColor);
	        	
            }
		}
	}
	public void startMaze(MapDescriptor MapDesc){
		mazeThread = new MazeThread(surfaceHolder,this, MapDesc);
		mazeThread.setRunning(true);
		mazeThread.start();
	}

}
