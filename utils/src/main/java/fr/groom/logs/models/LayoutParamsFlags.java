package fr.groom.logs.models;

public enum LayoutParamsFlags {
	FLAG_ALLOW_LOCK_WHILE_SCREEN_ON("0x00000001"),
	FLAG_ALT_FOCUSABLE_IM("0x00020000"),
	FLAG_BLUR_BEHIND("0x00000004"),
	FLAG_DIM_BEHIND("0x00000002"),
	FLAG_DISMISS_KEYGUARD("0x00400000"),
	FLAG_DITHER("0x00001000"),
	FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS("0x80000000"),
	FLAG_FORCE_NOT_FULLSCREEN("0x00000800"),
	FLAG_FULLSCREEN("0x00000400"),
	FLAG_HARDWARE_ACCELERATED("0x01000000"),
	FLAG_IGNORE_CHEEK_PRESSES("0x00008000"),
	FLAG_KEEP_SCREEN_ON("0x00000080"),
	FLAG_LAYOUT_ATTACHED_IN_DECOR("0x40000000"),
	FLAG_LAYOUT_INSET_DECOR("0x00010000"),
	FLAG_LAYOUT_IN_OVERSCAN("0x02000000"),
	FLAG_LAYOUT_IN_SCREEN("0x00000100"),
	FLAG_LAYOUT_NO_LIMITS("0x00000200"),
	FLAG_LOCAL_FOCUS_MODE("0x10000000"),
	FLAG_NOT_FOCUSABLE("0x00000008"),
	FLAG_NOT_TOUCHABLE("0x00000010"),
	FLAG_NOT_TOUCH_MODAL("0x00000020"),
	FLAG_SCALED("0x00004000"),
	FLAG_SECURE("0x00002000"),
	FLAG_SHOW_WALLPAPER("0x00100000"),
	FLAG_SHOW_WHEN_LOCKED("0x00080000"),
	FLAG_SPLIT_TOUCH("0x00800000"),
	FLAG_TOUCHABLE_WHEN_WAKING("0x00000040"),
	FLAG_TRANSLUCENT_NAVIGATION("0x08000000"),
	FLAG_TRANSLUCENT_STATUS("0x04000000"),
	FLAG_TURN_SCREEN_ON("0x00200000"),
	FLAG_WATCH_OUTSIDE_TOUCH("0x00040000");
	private String hexString;

	LayoutParamsFlags(String hexString) {
		this.hexString = hexString;
	}

	public String getHexString() {
		return hexString;
	}

}
