package com.example.mdptool;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class MazeThread extends Thread{

	boolean running = false;
	Maze maze;
	SurfaceHolder surfaceHolder;
	MapDescriptor mapDesc;
	
	public MazeThread(SurfaceHolder surfaceHolder, Maze maze, MapDescriptor mapDesc){
		this.maze=maze;
		this.surfaceHolder = surfaceHolder;
		this.mapDesc = mapDesc;
	}
	public void setRunning(boolean r){
		running = r;
	}
	@Override
	public void run(){
		while(running){
                Canvas c = null;
                try {
                       c = surfaceHolder.lockCanvas();
                       if(c != null){
	                       synchronized (surfaceHolder) {
	                              maze.onDraw(c,mapDesc.getMap(),mapDesc.getRobotPos());
	                       }
                       }
                } finally {
                       if (c != null) {
                              surfaceHolder.unlockCanvasAndPost(c);
                       }

                }

         }				
	}
}
