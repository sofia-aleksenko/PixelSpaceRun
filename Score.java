package com.genesislabs.pixelspace;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class Score extends TextField {
	int score;

	public Score(String text, TextField.TextFieldStyle style) {
		super(text, style);
		score = 0;
	}
}
