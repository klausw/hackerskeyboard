package org.pocketworkstation.pckeyboard;

/**
 * Global current settings for the keyboard.
 * 
 * <p>
 * Yes, globals are evil. But the persisted shared preferences are global data
 * by definition, and trying to hide this by propagating the current manually
 * just adds a lot of complication. This is especially annoying due to Views
 * getting constructed in a way that doesn't support adding additional
 * constructor arguments, requiring post-construction method calls, which is
 * error-prone and fragile.
 * 
 * <p>
 * The comments below indicate which class is responsible for updating the
 * value, and for recreating keyboards or views as necessary. Other classes
 * MUST treat the fields as read-only values, and MUST NOT attempt to save
 * these values or results derived from them across re-initializations.

 * 
 * @author klaus.weidner@gmail.com
 */
public final class GlobalKeyboardSettings {
	/* Updated by LatinIME */
	//
	// Read by KeyboardSwitcher
	public boolean wantFullInPortrait = false;
	public boolean isPortrait = false;
	//
	// Read by LatinKeyboardView
	public float rowHeightPercent = 10.0f; // percent of screen height
	public boolean showTouchPos = false;
	//
	// Read by LatinKeyboardBaseView
	public int hintMode = 0;
	//
	// Read by PointerTracker
	public boolean sendSlideKeys = false;
	public int longpressTimeout = 400;
	//
	// Read by LatinIMESettings
	// These are cached values for informational display, don't use for other purposes
	public String editorPackageName; 
        public String editorFieldName; 
        public int editorFieldId; 
        public int editorInputType; 

	/* Updated by KeyboardSwitcher */
	//
	// Used by LatinKeyboardBaseView and LatinIME
	public float labelScalePref = 1.0f;
	public float labelScale = 1.0f;
}
