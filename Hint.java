package com.genesislabs.pixelspace;


public class Hint {
	private int duration;
	private long startTime;
	public Hint(int duration) {
		this.duration = duration;
	}
	public void setStartTime(long startTime){
		this.startTime = startTime;
	}

	public void start(){}
	public void render(){
	}
}
