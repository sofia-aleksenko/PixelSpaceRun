package com.genesislabs.pixelspace;

public interface GoogleServices {
	void signInSilently();
	void startSignInIntent();
	void signOut();
	void unlockAchievement(String id);
	void incrementAchievement(String id);
	void submitScore(int highScore);
	void showAchievement();
	void showScore();
	boolean isSignedIn();
	String getResourceString(String name);

	void showAd();
	void showInterstitialAd (Runnable then);
}
